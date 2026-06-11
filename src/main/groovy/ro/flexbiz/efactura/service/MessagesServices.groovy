package ro.flexbiz.efactura.service

import com.helger.ubl21.UBL21Marshaller
import jdk.internal.org.xml.sax.SAXException
import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType
import org.moqui.context.ExecutionContext
import org.moqui.entity.EntityCondition
import org.moqui.entity.EntityValue
import org.moqui.service.ServiceException
import org.w3c.dom.Document
import ro.flexbiz.efactura.mapper.AnafMessageMapper
import ro.flexbiz.efactura.pojo.anaf.AnafReceivedMessages
import ro.flexbiz.efactura.util.StringUtils

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import java.time.LocalTime
import java.util.stream.Collectors
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class MessagesServices {
    static Map<String, Object> receivedMessagesBetween(ExecutionContext ec) {
        return [resultList: ec.entity.find("ro.flexbiz.efactura.ReceivedMessage")
                .condition("creationDate", EntityCondition.ComparisonOperator.BETWEEN, [ec.context.from, ec.context.thru])
                .condition("statusId", EntityCondition.ComparisonOperator.IN, ["AnafRecMsgBillReceived", "AnafRecMsgBillSent"])
                .list()]
    }

    static Map<String, Object> checkForReceivedMessages(ExecutionContext ec) {
        int days = ec.context.days
        final Map<String, Object> anafToken = ec.service.sync()
                .name("ro.flexbiz.efactura.AuthServices.find#AnafAccessToken")
                .call()
        final String accessToken = anafToken.accessToken
        final String taxId = anafToken.taxId
        if (StringUtils.isBlank(accessToken))
            throw new ServiceException("Nu aveti un token de acces la ANAF!")
        if (StringUtils.isBlank(taxId))
            throw new ServiceException("Setati codul fiscal folosit pentru eFactura!")

        final Map<String, Object> anafResponse = ec.service.sync()
                .name("AnafServices.received#Messages")
                .parameters([accessToken: accessToken, taxId: taxId, days: days])
                .call()
        AnafReceivedMessages receivedMessages = anafResponse.anafReceivedMessages
        String responseStatus = anafResponse.statusCode

        if (!responseStatus?.startsWith("2"))
            throw new ServiceException("Anaf response: "+responseStatus)

        if (StringUtils.isNotEmpty(receivedMessages.getError())) {
            if (receivedMessages.getError().equalsIgnoreCase(AnafReceivedMessages.NO_MESSAGES_ERROR))
                return [:]
            else if (receivedMessages.getError().equalsIgnoreCase(AnafReceivedMessages.TOO_MANY_MESSAGES_ERROR))
                throw new ServiceException(receivedMessages.getError())

            throw new ServiceException(receivedMessages.getError())
        }

        return [resultList: receivedMessages.getMessages().stream()
                .map(AnafMessageMapper.INSTANCE::toEntity)
                .map(msg -> msg.save(ec))
                .map(msg -> afterMessageSaved(accessToken, msg))
                .collect(Collectors.toList())]
    }

    private static EntityValue afterMessageSaved(ExecutionContext ec, final String anafAccessToken, final EntityValue receivedMessage) {
        if (receivedMessage.get("statusId") == "AnafRecMsgBillReceived")
            ec.service.sync().name("MessagesServices.bill#Received")
                    .parameters([accessToken: anafAccessToken, receivedMessage: receivedMessage])
                    .call()
        return receivedMessage
    }

    static Map<String, Object> billReceived(ExecutionContext ec) {
        final String accessToken = ec.context.accessToken
        Map<String, Object> message = ec.context.receivedMessage

        if (!message.get("statusId") == "AnafRecMsgBillReceived")
            throw new ServiceException("Only AnafRecMsgBillReceived status allowed")

        if (ec.entity.fastFindOne("ro.flexbiz.efactura.ReceivedInvoice",
                false, true, message.getId()) != null)
            return

        final String downloadId = message.get("id")
        Map<String, Object> downloadZipResponse = ec.service.sync()
                .name("AnafServices.download#Response")
                .parameters([accessToken: accessToken, downloadId: downloadId])
                .call()
        String responseStatus = downloadZipResponse.statusCode
        if (!responseStatus?.startsWith("2"))
            throw new ServiceException("Anaf response: "+responseStatus)

        final byte[] zipFile = downloadZipResponse.result
        final String rawXml = zipToXml(ec, zipFile)

        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance()
            final DocumentBuilder db = dbf.newDocumentBuilder()
            final Document doc = db.parse(new ByteArrayInputStream(rawXml.getBytes("UTF-8")))
            final String docType = doc.getDocumentElement().getNodeName()

            if (StringUtils.equalsIgnoreCase(docType, "Invoice")) {
                final InvoiceType readInvoice = UBL21Marshaller.invoice().read(rawXml)
                EntityValue recInv = ec.entity.makeValue("ro.flexbiz.efactura.ReceivedInvoice")
                recInv.set("id", message.getId())
                recInv.set("uploadIndex", message.getUploadIndex())
                recInv.set("downloadId", downloadId)
                recInv.set("xmlRaw", rawXml)
                recInv.set("issueDate", readInvoice.issueDateValue.toEpochSecond(LocalTime.MIN))
                recInv.store()
            } else if (StringUtils.equalsIgnoreCase(docType, "CreditNote")) {
                final CreditNoteType readCreditNote = UBL21Marshaller.creditNote().read(rawXml)
                EntityValue recCreditNote = ec.entity.makeValue("ro.flexbiz.efactura.ReceivedCreditNote")
                recCreditNote.set("id", message.getId())
                recCreditNote.set("uploadIndex", message.getUploadIndex())
                recCreditNote.set("downloadId", downloadId)
                recCreditNote.set("xmlRaw", rawXml)
                recCreditNote.set("issueDate", readCreditNote.issueDateValue.toEpochSecond(LocalTime.MIN))
                recCreditNote.store()
            } else
                throw new ServiceException(docType + " document type not supported")

        } catch (final ParserConfigurationException | SAXException | IOException e) {
            ec.logger.error("Error parsing XML", e)
            ec.logger.error("Message: "+message)
            ec.logger.error("downloadId: "+downloadId)
            ec.logger.error(rawXml)
        }
    }

    private static String zipToXml(ExecutionContext ec, final byte[] zipData) {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipData))) {
            ZipEntry zipEntry;
            int read;
            final byte[] buffer = new byte[1024];
            final StringBuilder sb = new StringBuilder();

            while ((zipEntry = zis.getNextEntry()) != null) {
                if (!zipEntry.getName().toLowerCase().endsWith(".xml") ||
                        zipEntry.getName().toLowerCase().startsWith("semnatura"))
                    continue;

                while ((read = zis.read(buffer, 0, 1024)) >= 0)
                    sb.append(new String(buffer, 0, read));
            }
            return sb.toString();
        } catch (final IOException e) {
            ec.logger.error( "Extracting zip contents failed", e);
            return null;
        }
    }
}
