package ro.flexbiz.efactura.service

import com.fasterxml.jackson.core.JsonParser
import com.helger.commons.error.list.IErrorList
import com.helger.ubl21.UBL21Marshaller
import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.Unmarshaller
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType
import org.eclipse.jetty.http.HttpHeader
import org.moqui.context.ExecutionContext
import org.moqui.impl.context.ContextJavaUtil
import org.moqui.service.ServiceException
import org.moqui.util.RestClient
import org.moqui.util.SystemBinding
import ro.flexbiz.efactura.mapper.InvoiceMapper
import ro.flexbiz.efactura.pojo.Invoice
import ro.flexbiz.efactura.pojo.Party
import ro.flexbiz.efactura.pojo.anaf.AnafReceivedMessages
import ro.flexbiz.efactura.pojo.anaf.AnafUploadResponseHeader
import ro.flexbiz.efactura.pojo.anaf.AnafUploadStateResponseHeader
import ro.flexbiz.efactura.util.StringUtils

class AnafServices {
    private static void validateInvoice(ExecutionContext ec, final InvoiceType ublInvoice) {
        final IErrorList invoiceValidationResult = UBL21Marshaller.invoice().validate(ublInvoice)
        if (invoiceValidationResult.containsAtLeastOneError())
            throw new ServiceException(invoiceValidationResult.getAllTexts(ec.user.locale).toString())
    }

    static Map<String, Object> uploadInvoice(ExecutionContext ec) {
        Invoice invoice = ec.context.invoice
        String accessToken = ec.context.accessToken
        final String taxNumber = Optional.ofNullable(invoice.getAccountingSupplier())
                .map(Party::getTaxId)
                .map(taxId -> StringUtils.removeStartIgnoreCase(taxId, "RO"))
                .orElse("")

        RestClient rest = new RestClient()
        rest.uri(SystemBinding.getPropOrEnv('anaf.api.base.url')+"/rest/upload?"+
                RestClient.parametersMapToString([standard: "UBL", cif: taxNumber]))

        createAuthHeader(rest, accessToken)
        final InvoiceType ublInvoice = InvoiceMapper.INSTANCE.toUblInvoice(invoice)
        validateInvoice(ec, ublInvoice)
        final String ublInvoiceXml = UBL21Marshaller.invoice().getAsString(ublInvoice)
        ec.logger.info(ublInvoiceXml)
        rest.text(ublInvoiceXml)
        RestClient.RestResponse anafResult = rest.method(RestClient.Method.POST).call()
        JAXBContext jc = JAXBContext.newInstance(AnafUploadResponseHeader.class)
        Unmarshaller u = jc.createUnmarshaller();
        return [anafUploadResponseHeader: u.unmarshal(new StringReader(anafResult.text())),
                statusCode: anafResult.statusCode]
    }

    private static void createAuthHeader(RestClient rest, final String accessToken) {
        rest.addHeader(HttpHeader.AUTHORIZATION.asString(), "Bearer " + accessToken)
    }

    static Map<String, Object> checkInvoiceState(ExecutionContext ec) {
        String accessToken = ec.context.accessToken
        String uploadIndex = ec.context.uploadIndex
        RestClient rest = new RestClient()
        rest.uri(SystemBinding.getPropOrEnv('anaf.api.base.url')+"/rest/stareMesaj?"+
                RestClient.parametersMapToString([id_incarcare: uploadIndex]))

        createAuthHeader(rest, accessToken)
        RestClient.RestResponse anafResult = rest.method(RestClient.Method.GET).call()
        JAXBContext jc = JAXBContext.newInstance(AnafUploadStateResponseHeader.class)
        Unmarshaller u = jc.createUnmarshaller()
        return [anafUploadStateResponseHeader: u.unmarshal(new StringReader(anafResult.text())),
                statusCode: anafResult.statusCode]
    }

    static Map<String, Object> downloadResponse(ExecutionContext ec) {
        String accessToken = ec.context.accessToken
        String downloadId = ec.context.downloadId
        RestClient rest = new RestClient()
        rest.uri(SystemBinding.getPropOrEnv('anaf.api.base.url')+"/rest/descarcare?"+
                RestClient.parametersMapToString([id: downloadId]))

        createAuthHeader(rest, accessToken)
        RestClient.RestResponse anafResult = rest.method(RestClient.Method.GET).call()
        return [statusCode: anafResult.statusCode, result: anafResult.bytes()]
    }

    static Map<String, Object> receivedMessages(ExecutionContext ec) {
        String accessToken = ec.context.accessToken
        String taxId = ec.context.taxId
        int days = ec.context.days

        if (days < 1 || days > 60)
            throw new ServiceException("Numarul de zile pentru care se face interogarea trebuie sa fie intre 1 si 60!")

        RestClient rest = new RestClient()
        rest.uri(SystemBinding.getPropOrEnv('anaf.api.base.url')+"/rest/listaMesajeFactura?"+
                RestClient.parametersMapToString([zile: days, cif: taxId]))

        createAuthHeader(rest, accessToken)
        RestClient.RestResponse anafResult = rest.method(RestClient.Method.GET).call()
        JsonParser jsonParser = ContextJavaUtil.jacksonMapper.createParser(anafResult.bytes())
        return [anafReceivedMessages: jsonParser.readValueAs(AnafReceivedMessages.class),
                statusCode: anafResult.statusCode]
    }
}
