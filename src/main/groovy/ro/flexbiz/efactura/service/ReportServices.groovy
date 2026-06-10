package ro.flexbiz.efactura.service

import com.github.scribejava.core.model.OAuth2AccessToken
import org.moqui.context.ExecutionContext
import org.moqui.entity.EntityValue
import org.moqui.service.ServiceException
import ro.flexbiz.efactura.pojo.Invoice
import ro.flexbiz.efactura.pojo.anaf.AnafResponseError
import ro.flexbiz.efactura.pojo.anaf.AnafUploadResponseHeader

import java.util.stream.Collectors

class ReportServices {
    static Map<String, Object> reportInvoice(ExecutionContext ec) {
        Invoice invoice = ec.context.invoice
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
        reportToSave.set("invoiceId", invoice.getId())
        return [reportedInvoice: reportToSave.store()]
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
}
