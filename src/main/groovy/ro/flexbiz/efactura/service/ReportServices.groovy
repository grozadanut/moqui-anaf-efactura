package ro.flexbiz.efactura.service

import org.moqui.context.ExecutionContext
import org.moqui.entity.EntityCondition
import org.moqui.entity.EntityValue
import org.moqui.impl.context.ContextJavaUtil
import org.moqui.service.ServiceException
import org.moqui.util.ObjectUtilities
import org.moqui.util.SystemBinding
import ro.flexbiz.efactura.pojo.Invoice
import ro.flexbiz.efactura.pojo.InvoiceWrapper
import ro.flexbiz.efactura.pojo.anaf.AnafResponseError
import ro.flexbiz.efactura.pojo.anaf.AnafUploadResponseHeader
import ro.flexbiz.efactura.pojo.anaf.AnafUploadStateResponseHeader
import ro.flexbiz.efactura.util.StringUtils

import java.text.MessageFormat
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

class ReportServices {
    static Map<String, Object> reportInvoice(ExecutionContext ec) {
        Invoice invoice
        try {
            invoice = ContextJavaUtil.jacksonMapper.readValue(ec.web.requestBodyText, InvoiceWrapper.class)
                    .invoice()
        } catch (Exception e) {
            ec.logger.error(e.getMessage(), e)
            invoice = ec.context.invoice
        }
        if (invoice.getId() == null)
            throw new ServiceException("ID-ul facturii lipseste!")

        final EntityValue reportedInvoice = ec.entity.fastFindOne("ro.flexbiz.efactura.ReportedInvoice",
                false, true, invoice.getId())
        if (reportedInvoice != null)
            validateReportState(reportedInvoice.statusId)

        String accessToken = ec.service.sync()
                .name("ro.flexbiz.efactura.AuthServices.find#AnafAccessToken")
                .call().accessToken
        if (accessToken == null || accessToken.isEmpty())
            throw new ServiceException("Nu aveti un token de acces la ANAF!")

        final Map<String, Object> uploadResult = ec.service.sync()
                .name("AnafServices.upload#Invoice")
                .parameters([accessToken: accessToken, invoice: invoice])
                .call()
        final EntityValue reportToSave = mapResult(ec, uploadResult)
        reportToSave.set("invoiceId", invoice.getId()+"")
        return reportToSave.store()
    }

    private static void validateReportState(final String statusId) {
        if ("AnafRepInvSent" == statusId)
            throw new ServiceException("Factura a fost deja raportata la ANAF!")
        else if ("AnafRepInvWaitingValidation" == statusId)
            throw new ServiceException("Factura asteapta validare din partea ANAF!")
    }

    private static EntityValue mapResult(ExecutionContext ec, final Map<String, Object> uploadResult) {
        AnafUploadResponseHeader responseHeader = uploadResult?.anafUploadResponseHeader
        EntityValue report = ec.entity.makeValue("ro.flexbiz.efactura.ReportedInvoice")
        if (responseHeader != null) {
            if (responseHeader.isExecutionStatusOk()) {
                report.set("statusId", "AnafRepInvWaitingValidation")
                report.set("uploadIndex", responseHeader.getUploadIndex())
            } else {
                report.set("statusId", "AnafRepInvUploadError")
                report.set("errorMessage", responseHeader.getErrors().stream()
                        .map(AnafResponseError::getMessage)
                        .collect(Collectors.joining(System.lineSeparator())))
            }
        } else {
            report.set("statusId", "AnafRepInvUploadError")
            report.set("errorMessage", uploadResult.statusCode)
        }
        return report
    }

    static Map<String, Object> findById(ExecutionContext ec) {
        String invoiceId = ec.context.invoiceId
        return [reportedInvoice: ec.entity.fastFindOne("ro.flexbiz.efactura.ReportedInvoice",
                false, true, invoiceId)]
    }

    static Map<String, Object> findAllById(ExecutionContext ec) {
        Collection<String> ids = ec.context.ids
        return [resultList: ec.entity.find("ro.flexbiz.efactura.ReportedInvoice")
                .condition("invoiceId", EntityCondition.ComparisonOperator.IN, ids)
                .list()]
    }

    static Map<String, Object> checkReportedInvoicesState(ExecutionContext ec) {
        final String accessToken = ec.service.sync()
                .name("ro.flexbiz.efactura.AuthServices.find#AnafAccessToken")
                .call().accessToken
        if (accessToken == null || accessToken.isEmpty())
            throw new ServiceException("Nu aveti un token de acces la ANAF!")

        ec.entity.find("ro.flexbiz.efactura.ReportedInvoice")
                .condition("statusId", "AnafRepInvWaitingValidation")
                .list().forEach(awaitingInvoice -> {
            saveReportedInvoiceResult(ec, accessToken, awaitingInvoice)
            try {
                // throttle requests to limit ANAF quota as per official specs:
                // 100 Requests / 1 minute
                // 50 Spike arrest / 10 seconds
                TimeUnit.SECONDS.sleep(Long.valueOf(SystemBinding.getPropOrEnv('anaf.request.sleep-delay.seconds')))
            } catch (final InterruptedException ie) {
                Thread.currentThread().interrupt()
            }
        })
        return [:]
    }

    private static void saveReportedInvoiceResult(ExecutionContext ec, final String anafAccessToken, final EntityValue awaitingInvoice) {
        final Map<String, Object> anafResponse = ec.service.sync()
                .name("AnafServices.check#InvoiceState")
                .parameters([accessToken: anafAccessToken, uploadIndex: awaitingInvoice.get("uploadIndex")])
                .call()
        AnafUploadStateResponseHeader responseHeader = anafResponse.anafUploadStateResponseHeader
        String responseStatus = anafResponse.statusCode

        if (!responseStatus?.startsWith("2")) {
            ec.logger.error(MessageFormat.format("Check ANAF state failed for invoice {0} with returned status code {1}",
                    awaitingInvoice.get("invoiceId"), responseStatus))
            return
        }

        if (responseHeader.isStateOk()) {
            awaitingInvoice.set("statusId", "AnafRepInvSent")
            awaitingInvoice.set("downloadId", responseHeader.getDownloadId())
            awaitingInvoice.set("errorMessage", null)
            awaitingInvoice.store()
        } else if (responseHeader.isStateNok()) {
            awaitingInvoice.set("statusId", "AnafRepInvRejectedInvalid")
            awaitingInvoice.set("downloadId", responseHeader.getDownloadId())
            awaitingInvoice.set("errorMessage", MessageFormat.format("Validarea facturii a esuat. Descarcati fisierul de erori cu id-ul {0}",
                    responseHeader.getDownloadId()))
            awaitingInvoice.store()
        } else if (responseHeader.isStatePending()) {
            return
        } else if (StringUtils.isNotEmpty(responseHeader.getState())) {
            awaitingInvoice.set("statusId", "AnafRepInvRejectedInvalid")
            awaitingInvoice.set("errorMessage", responseHeader.prettyErrorMessage())
            awaitingInvoice.store()
        } else {
            ec.logger.error(MessageFormat.format("Check ANAF state failed for invoice {0} with error {1}",
                    awaitingInvoice.get("invoiceId"), responseHeader.prettyErrorMessage()))
        }
    }

    static Map<String, Object> downloadResponse(ExecutionContext ec) {
        String downloadId = ec.context.downloadId
        final String accessToken = ec.service.sync()
                .name("ro.flexbiz.efactura.AuthServices.find#AnafAccessToken")
                .call().accessToken
        if (accessToken == null || accessToken.isEmpty())
            throw new ServiceException("Nu aveti un token de acces la ANAF!")

        byte[] r = ec.service.sync()
                .name("AnafServices.download#Response")
                .parameters([accessToken: accessToken, downloadId: downloadId])
                .call().result
        ec.web.response.setContentType("application/octet-stream")
        ec.web.response.setContentLength(r.length)
        try (ByteArrayInputStream bais = new ByteArrayInputStream(r)) {
            ObjectUtilities.copyStream(bais, ec.web.response.outputStream)
        } finally {
            ec.web.response.outputStream.close()
        }
        return [:]
    }
}
