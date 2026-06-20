package ro.flexbiz.efactura.service

import org.moqui.Moqui
import org.moqui.context.ExecutionContext
import org.moqui.entity.EntityValue
import ro.flexbiz.efactura.TestData
import ro.flexbiz.efactura.entity.ReceivedMessage
import ro.flexbiz.efactura.pojo.anaf.AnafReceivedMessage
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate
import java.time.LocalDateTime

class ReceivedInvoiceServiceTests extends Specification {
    @Shared
    ExecutionContext ec

    def setupSpec() {
        ec = Moqui.getExecutionContext()
        ec.user.loginAnonymousIfNoUser()
        TestData.init()
    }

    def cleanupSpec() {
        ec.destroy()
    }

    def setup() {
        ec.artifactExecution.disableAuthz()
        ec.message.clearAll()
    }

    def "givenWrongMessageType_whenBillReceived_thenThrowException"() {
        given:
        final ReceivedMessage billSent = new ReceivedMessage("3001503294", LocalDateTime.of(2022, 11, 1, 13, 36),
                TestData.taxId, "5001131297", "Factura cu id_incarcare=5001131297 emisa de cif_emitent="+TestData.taxId+" pentru cif_beneficiar=3",
                AnafReceivedMessage.AnafReceivedMessageType.BILL_SENT)
        when:
        ec.service.sync().name("MessagesServices.bill#Received")
                .parameters([accessToken: TestData.accessToken, receivedMessage: billSent.convert(ec)])
                .call()
        then:
        ec.message.errorsString.contains("Only AnafRecMsgBillReceived status allowed")
    }

    def "givenInvoiceExists_whenBillReceived_thenDoNothing"() {
        given:
        final ReceivedMessage billReceived = new ReceivedMessage("3009239535", LocalDateTime.now().minusDays(10),
                TestData.taxId, "5006514680", "Factura cu id_incarcare=5006514680 emisa de cif_emitent=1485236 pentru cif_beneficiar="+TestData.taxId,
                AnafReceivedMessage.AnafReceivedMessageType.BILL_RECEIVED)
        EntityValue receivedInvoice = ec.entity.makeValue("ro.flexbiz.efactura.ReceivedInvoice")
        receivedInvoice.set("id", billReceived.getId())
        receivedInvoice.set("uploadIndex", billReceived.getUploadIndex())
        receivedInvoice.store()

        when:
        ec.service.sync().name("MessagesServices.bill#Received")
                .parameters([accessToken: TestData.accessToken, receivedMessage: billReceived.convert(ec)])
                .call()

        then:
        ec.message.errors.isEmpty()
        ec.entity.find("ro.flexbiz.efactura.ReceivedInvoice").count() == 1

        cleanup:
        receivedInvoice.delete()
    }

    def "givenNewInvoice_whenBillReceived_thenSaveInvoiceWithDownloadIdAndRawXml"() {
        given:
        final ReceivedMessage billReceived = new ReceivedMessage("3009239535", LocalDateTime.now().minusDays(10),
                TestData.taxId, "5006514680", "Factura cu id_incarcare=5006514680 emisa de cif_emitent=1485236 pentru cif_beneficiar="+TestData.taxId,
                AnafReceivedMessage.AnafReceivedMessageType.BILL_RECEIVED)

        final Path testInvoiceZipPath = Paths.get("src","test","resources", "3009239535.zip")
        final Path testInvoiceXmlPath = Paths.get("src","test","resources", "5006514680.xml")

        when:
        ec.service.sync().name("MessagesServices.bill#Received")
                .parameters([accessToken: TestData.accessToken, receivedMessage: billReceived.convert(ec)])
                .call()

        then:
        ec.message.errors.isEmpty()
        ec.entity.find("ro.flexbiz.efactura.ReceivedInvoice").count() == 1
        EntityValue capturedInvoice = ec.entity.find("ro.flexbiz.efactura.ReceivedInvoice")
                .list().first

        capturedInvoice.get("id") == billReceived.getId()
        capturedInvoice.get("uploadIndex") == billReceived.getUploadIndex()
        capturedInvoice.get("downloadId") == billReceived.getId()
        capturedInvoice.get("xmlRaw") == Files.readString(testInvoiceXmlPath)
        capturedInvoice.getTimestamp("issueDate").toLocalDateTime().toLocalDate() ==
                LocalDate.of(2024, 1, 25)

        cleanup:
        capturedInvoice.delete()
    }

    def "givenDownloadZipInternalError_whenBillReceived_thenPropagateException"() {
        given:
        final ReceivedMessage billReceived = new ReceivedMessage("400", LocalDateTime.now().minusDays(10),
                TestData.taxId, "5006514680", "Factura cu id_incarcare=5006514680 emisa de cif_emitent=1485236 pentru cif_beneficiar="+TestData.taxId,
                AnafReceivedMessage.AnafReceivedMessageType.BILL_RECEIVED)

        when:
        ec.service.sync().name("MessagesServices.bill#Received")
                .parameters([accessToken: TestData.accessToken, receivedMessage: billReceived.convert(ec)])
                .call()

        then:
        ec.message.errorsString.contains("Anaf response: 400")
    }

    def "givenNewCreditNote_whenBillReceived_thenSaveCreditNoteWithDownloadIdAndRawXml"() {
        given:
        final ReceivedMessage billReceived = new ReceivedMessage("123", LocalDateTime.now().minusDays(10),
                TestData.taxId, "4680", "Factura cu id_incarcare=4680 emisa de cif_emitent=RO7568475 pentru cif_beneficiar="+TestData.taxId,
                AnafReceivedMessage.AnafReceivedMessageType.BILL_RECEIVED)

        final Path testInvoiceZipPath = Paths.get("src","test","resources", "credit_note.zip")
        final Path testInvoiceXmlPath = Paths.get("src","test","resources", "credit_note.xml")

        when:
        ec.service.sync().name("MessagesServices.bill#Received")
                .parameters([accessToken: TestData.accessToken, receivedMessage: billReceived.convert(ec)])
                .call()

        then:
        ec.message.errors.isEmpty()
        ec.entity.find("ro.flexbiz.efactura.ReceivedCreditNote").count() == 1
        EntityValue capturedCreditNote = ec.entity.find("ro.flexbiz.efactura.ReceivedCreditNote")
                .list().first

        capturedCreditNote.get("id") == billReceived.getId()
        capturedCreditNote.get("uploadIndex") == billReceived.getUploadIndex()
        capturedCreditNote.get("downloadId") == billReceived.getId()
        capturedCreditNote.getString("xmlRaw").replaceAll("\\s", "") ==
                Files.readString(testInvoiceXmlPath).replaceAll("\\s", "")
        capturedCreditNote.getTimestamp("issueDate").toLocalDateTime().toLocalDate() ==
                LocalDate.of(2024, 1, 31)

        cleanup:
        capturedCreditNote.delete()
    }
}