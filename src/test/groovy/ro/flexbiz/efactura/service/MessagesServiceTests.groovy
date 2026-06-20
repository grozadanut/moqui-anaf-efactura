package ro.flexbiz.efactura.service

import org.moqui.Moqui
import org.moqui.context.ExecutionContext
import org.moqui.entity.EntityValue
import ro.flexbiz.efactura.TestData
import ro.flexbiz.efactura.entity.ReceivedMessage
import ro.flexbiz.efactura.pojo.anaf.AnafReceivedMessage
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDateTime

import static org.assertj.core.api.Assertions.assertThat

class MessagesServiceTests extends Specification {
    @Shared
    ExecutionContext ec

    private String taxId
    private String accessToken
    EntityValue credential
    EntityValue accessTokenField
    EntityValue taxIdField
    EntityValue credentialUser

    def setupSpec() {
        ec = Moqui.getExecutionContext()
        ec.user.loginUser("john.doe", "moqui")
    }

    def cleanupSpec() {
        ec.destroy()
    }

    def setup() {
        TestData.init()
        ec.artifactExecution.disableAuthz()

        taxId = "1485236"
        accessToken = "MessagesServiceTests"

        credential = ec.entity.makeValue("ro.flexbiz.security.Credential")
        credential.set("credentialId", "ANAF_EFACTURA_OAUTH_TOKEN")
        credential.set("credentialTypeEnumId", "CtLogin")
        credential.store()

        accessTokenField = ec.entity.makeValue("ro.flexbiz.security.CredentialField")
        accessTokenField.set("credentialId", "ANAF_EFACTURA_OAUTH_TOKEN")
        accessTokenField.set("name", "accessToken")
        accessTokenField.set("fromDate", "2026-06-01T00:00:00Z")
        accessTokenField.set("value", accessToken)
        accessTokenField.store()

        taxIdField = ec.entity.makeValue("ro.flexbiz.security.CredentialField")
        taxIdField.set("credentialId", "ANAF_EFACTURA_OAUTH_TOKEN")
        taxIdField.set("name", "taxId")
        taxIdField.set("fromDate", "2026-06-01T00:00:00Z")
        taxIdField.set("value", taxId)
        taxIdField.store()

        credentialUser = ec.entity.makeValue("ro.flexbiz.security.CredentialUser")
        credentialUser.set("credentialId", "ANAF_EFACTURA_OAUTH_TOKEN")
        credentialUser.set("userId", ec.user.userId)
        credentialUser.set("fromDate", "2026-06-01T00:00:00Z")
        credentialUser.set("authzActionEnumId", "AUTHZA_ALL")
        credentialUser.store()
    }

    def cleanup() {
        ec.message.clearAll()
        credentialUser.delete()
        accessTokenField.delete()
        taxIdField.delete()
        credential.delete()
    }

    def "givenNoMessagesSaved_whenCheckForReceivedMessages_thenSaveAllMessages"() {
//        given:
        final AnafReceivedMessage billSent = new AnafReceivedMessage("3001503294", LocalDateTime.of(2022, 11, 1, 13, 36),
                taxId, "5001131297", "Factura cu id_incarcare=5001131297 emisa de cif_emitent="+taxId+" pentru cif_beneficiar=3",
                AnafReceivedMessage.AnafReceivedMessageType.BILL_SENT)
        final AnafReceivedMessage billReceived = new AnafReceivedMessage("3009239535", LocalDateTime.of(2024, 1, 25, 14, 36),
                taxId, "5006514680", "Factura cu id_incarcare=5006514680 emisa de cif_emitent=1485236 pentru cif_beneficiar="+taxId,
                AnafReceivedMessage.AnafReceivedMessageType.BILL_RECEIVED)
        final AnafReceivedMessage billErrors = new AnafReceivedMessage("3001293434", LocalDateTime.of(2022, 11, 1, 14, 15),
                taxId, "5001130147", "Erori de validare identificate la factura primita cu id_incarcare=5001130147",
                AnafReceivedMessage.AnafReceivedMessageType.BILL_ERRORS)

        when:
        final List<EntityValue> savedMessages = ec.service.sync().name("MessagesServices.check#ForReceivedMessages")
                .parameter("days", 20)
                .call().resultList

        then:
        final EntityValue savedBillSent = new ReceivedMessage(billSent.getId(), billSent.getCreationDate(),
                taxId, billSent.getUploadIndex(), billSent.getDetails(), AnafReceivedMessage.AnafReceivedMessageType.BILL_SENT)
                .convert(ec)
        savedBillSent.set("lastUpdatedStamp", savedMessages.first.get("lastUpdatedStamp"))
        final EntityValue savedBillReceived = new ReceivedMessage(billReceived.getId(), billReceived.getCreationDate(),
                taxId, billReceived.getUploadIndex(), billReceived.getDetails(), AnafReceivedMessage.AnafReceivedMessageType.BILL_RECEIVED)
                .convert(ec)
        savedBillReceived.set("lastUpdatedStamp", savedMessages.first.get("lastUpdatedStamp"))
        final EntityValue savedBillErrors = new ReceivedMessage(billErrors.getId(), billErrors.getCreationDate(),
                taxId, billErrors.getUploadIndex(), billErrors.getDetails(), AnafReceivedMessage.AnafReceivedMessageType.BILL_ERRORS)
                .convert(ec)
        savedBillErrors.set("lastUpdatedStamp", savedMessages.first.get("lastUpdatedStamp"))

        assertThat(savedMessages).containsExactlyInAnyOrder(savedBillSent, savedBillReceived, savedBillErrors)

        cleanup:
        savedBillSent.delete()
        savedBillReceived.delete()
        savedBillErrors.delete()
    }

    def "givenSomeMessagesSaved_whenCheckForReceivedMessages_thenSaveAndUpdateMessages"() {
        given:
        final AnafReceivedMessage billSent = new AnafReceivedMessage("3001503294", LocalDateTime.of(2022, 11, 1, 13, 36),
                taxId, "5001131297", "Factura cu id_incarcare=5001131297 emisa de cif_emitent="+taxId+" pentru cif_beneficiar=3",
                AnafReceivedMessage.AnafReceivedMessageType.BILL_SENT)
        final AnafReceivedMessage billReceived = new AnafReceivedMessage("3009239535", LocalDateTime.of(2024, 1, 25, 14, 36),
                taxId, "5006514680", "Factura cu id_incarcare=5006514680 emisa de cif_emitent=1485236 pentru cif_beneficiar="+taxId,
                AnafReceivedMessage.AnafReceivedMessageType.BILL_RECEIVED)
        final AnafReceivedMessage billErrors = new AnafReceivedMessage("3001293434", LocalDateTime.of(2022, 11, 1, 14, 15),
                taxId, "5001130147", "Erori de validare identificate la factura primita cu id_incarcare=5001130147",
                AnafReceivedMessage.AnafReceivedMessageType.BILL_ERRORS)

        final EntityValue savedBillSent = new ReceivedMessage(billSent.getId(), billSent.getCreationDate(),
                taxId, "000", "to be replaced by service call", AnafReceivedMessage.AnafReceivedMessageType.BILL_SENT)
                .save(ec)
        final EntityValue savedBillReceived = new ReceivedMessage(billReceived.getId(), billReceived.getCreationDate(),
                taxId, "000", "to be replaced by service call", AnafReceivedMessage.AnafReceivedMessageType.BILL_RECEIVED)
                .save(ec)
        final EntityValue savedBillErrors = new ReceivedMessage(billErrors.getId(), billErrors.getCreationDate(),
                taxId, "000", "to be replaced by service call", AnafReceivedMessage.AnafReceivedMessageType.BILL_ERRORS)
                .save(ec)

        when:
        final List<EntityValue> savedMessages = ec.service.sync().name("MessagesServices.check#ForReceivedMessages")
                .call().resultList

        then:
        savedBillSent.refresh()
        savedBillReceived.refresh()
        savedBillErrors.refresh()
        assertThat(savedMessages).containsExactlyInAnyOrder(savedBillSent, savedBillReceived, savedBillErrors)

        cleanup:
        savedBillSent.delete()
        savedBillReceived.delete()
        savedBillErrors.delete()
    }
}