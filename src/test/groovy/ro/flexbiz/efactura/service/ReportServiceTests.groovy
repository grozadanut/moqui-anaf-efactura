package ro.flexbiz.efactura.service

import org.moqui.Moqui
import org.moqui.context.ExecutionContext
import org.moqui.entity.EntityValue
import ro.flexbiz.efactura.TestData
import ro.flexbiz.efactura.pojo.Invoice
import spock.lang.Shared
import spock.lang.Specification

class ReportServiceTests extends Specification {
    @Shared
    ExecutionContext ec

    private String accessToken
    EntityValue credential
    EntityValue accessTokenField
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

        accessToken = "ReportServiceTests"

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
        credential.delete()
    }

    def "givenNullInvoiceId_whenReportInvoice_thenThrowException"() {
        given:
        final Invoice invoice = new Invoice()
        invoice.setId(null)

        when:
        ec.service.sync().name("EFacturaServices.report#Invoice")
                .parameters([invoice: invoice])
                .call()
        then:
        ec.message.errorsString.contains("ID-ul facturii lipseste!")
    }

    def "givenInvoiceIsReported_whenReportInvoice_thenThrowException"() {
        given:
        final Invoice invoice = new Invoice()
        invoice.setId(1L)
        EntityValue reportedInvoice = ec.entity.makeValue("ro.flexbiz.efactura.ReportedInvoice")
        reportedInvoice.set("invoiceId", "1")
        reportedInvoice.set("statusId", "AnafRepInvSent")
        reportedInvoice.store()

        when:
        ec.service.sync().name("EFacturaServices.report#Invoice")
                .parameters([invoice: invoice])
                .call()
        then:
        ec.message.errorsString.contains("Factura a fost deja raportata la ANAF!")

        cleanup:
        reportedInvoice.delete()
    }

    def "givenWaitingValidation_whenReportInvoice_thenThrowException"() {
        given:
        final Invoice invoice = new Invoice()
        invoice.setId(1L)
        EntityValue reportedInvoice = ec.entity.makeValue("ro.flexbiz.efactura.ReportedInvoice")
        reportedInvoice.set("invoiceId", "1")
        reportedInvoice.set("statusId", "AnafRepInvWaitingValidation")
        reportedInvoice.store()

        when:
        ec.service.sync().name("EFacturaServices.report#Invoice")
                .parameters([invoice: invoice])
                .call()
        then:
        ec.message.errorsString.contains("Factura asteapta validare din partea ANAF!")

        cleanup:
        reportedInvoice.delete()
    }

    def "givenCompanyOAuthTokenIsMissing_whenReportInvoice_thenThrowException"() {
        given:
        final Invoice invoice = new Invoice()
        invoice.setId(1L)

        credentialUser.delete()
        accessTokenField.delete()
        credential.delete()

        when:
        ec.service.sync().name("EFacturaServices.report#Invoice")
                .parameters([invoice: invoice])
                .call()
        then:
        ec.message.errorsString.contains("Nu aveti un token de acces la ANAF!")
    }

    def "givenHasUploadError_whenReportInvoice_thenResendAndSaveResult"() {
        given:
        EntityValue reportedInvoice = ec.entity.makeValue("ro.flexbiz.efactura.ReportedInvoice")
        reportedInvoice.set("invoiceId", "1")
        reportedInvoice.set("statusId", "AnafRepInvUploadError")
        reportedInvoice.store()

//        AnafUploadResponseHeader anafResponseHeader = new AnafUploadResponseHeader()
//        anafResponseHeader.setExecutionStatus("0")
//        anafResponseHeader.setUploadIndex("1234")

        when:
        final Map<String, Object> report = ec.service.sync().name("EFacturaServices.report#Invoice")
                .parameters([invoice: TestData.invoice])
                .call()

        then:
        report.get("invoiceId") == "1"
        report.get("statusId") == "AnafRepInvWaitingValidation"
        report.get("uploadIndex") == "1234"
        report.get("errorMessage") == null

        cleanup:
        reportedInvoice.delete()
    }

    def "givenHasRejectedInvalid_whenReportInvoice_thenResendAndSaveResult"() {
        given:
        final EntityValue reportedInvoice = ec.entity.makeValue("ro.flexbiz.efactura.ReportedInvoice")
        reportedInvoice.set("invoiceId", "1")
        reportedInvoice.set("statusId", "AnafRepInvRejectedInvalid")
        reportedInvoice.store()

//        final AnafUploadResponseHeader anafResponseHeader = new AnafUploadResponseHeader()
//        anafResponseHeader.setExecutionStatus("0")
//        anafResponseHeader.setUploadIndex("1234")

        when:
        final Map<String, Object> report = ec.service.sync().name("EFacturaServices.report#Invoice")
                .parameters([invoice: TestData.invoice])
                .call()

        then:
        report.get("invoiceId") == "1"
        report.get("statusId") == "AnafRepInvWaitingValidation"
        report.get("uploadIndex") == "1234"
        report.get("errorMessage") == null

        cleanup:
        reportedInvoice.delete()
    }

    def "givenInvoiceNotReported_whenReportInvoice_thenReportAndSaveResult"() {
        given:
//        final AnafUploadResponseHeader anafResponseHeader = new AnafUploadResponseHeader()
//        anafResponseHeader.setExecutionStatus("0")
//        anafResponseHeader.setUploadIndex("1234")

        when:
        final Map<String, Object> report = ec.service.sync().name("EFacturaServices.report#Invoice")
                .parameters([invoice: TestData.invoice])
                .call()

        then:
        report.get("invoiceId") == "1"
        report.get("statusId") == "AnafRepInvWaitingValidation"
        report.get("uploadIndex") == "1234"
        report.get("errorMessage") == null

        cleanup:
        ec.service.sync().name("delete#ro.flexbiz.efactura.ReportedInvoice")
                .parameter("invoiceId", "1")
                .call()
    }

    def "givenAnafUploadFails_whenReportInvoice_thenSaveErrorMessage"() {
        given:
        final Invoice invoice = new Invoice()
        invoice.setId(1L)

        accessTokenField.set("value", "returnError")
        accessTokenField.update()

        when:
        final Map<String, Object> report = ec.service.sync().name("EFacturaServices.report#Invoice")
                .parameters([invoice: TestData.invoice])
                .call()

        then:
        report.get("invoiceId") == "1"
        report.get("statusId") == "AnafRepInvUploadError"
        report.get("uploadIndex") == null
        report.get("errorMessage") == 500

        cleanup:
        ec.service.sync().name("delete#ro.flexbiz.efactura.ReportedInvoice")
                .parameter("invoiceId", "1")
                .call()
    }

    def "givenAnafUploadReturnsErrors_whenReportInvoice_thenSaveErrorMessage"() {
        given:
        accessTokenField.set("value", "notPerm")
        accessTokenField.update()
//        final AnafUploadResponseHeader anafResponseHeader = new AnafUploadResponseHeader()
//        anafResponseHeader.setExecutionStatus("1")
//        anafResponseHeader.setErrors(List.of(new AnafResponseError("Not permitted")))

        when:
        final Map<String, Object> report = ec.service.sync().name("EFacturaServices.report#Invoice")
                .parameters([invoice: TestData.invoice])
                .call()

        then:
        report.get("invoiceId") == "1"
        report.get("statusId") == "AnafRepInvUploadError"
        report.get("uploadIndex") == null
        report.get("errorMessage") == "Not permitted"

        cleanup:
        ec.service.sync().name("delete#ro.flexbiz.efactura.ReportedInvoice")
                .parameter("invoiceId", "1")
                .call()
    }

    def "givenCompanyOAuthTokenIsMissing_whenCheckRepInvState_thenThrowException"() {
        given:
        credentialUser.delete()
        accessTokenField.delete()
        credential.delete()

        when:
        ec.service.sync().name("EFacturaServices.check#ReportedInvoicesState").call()
        then:
        ec.message.errorsString.contains("Nu aveti un token de acces la ANAF!")
    }

    def "givenAnafStatusCodeIsNotOk_whenCheckRepInvState_thenSkipCheck"() {
        given:
        final EntityValue dbRepInv = ec.entity.makeValue("ro.flexbiz.efactura.ReportedInvoice")
        dbRepInv.set("invoiceId", "1")
        dbRepInv.set("statusId", "AnafRepInvWaitingValidation")
        dbRepInv.set("uploadIndex", "5000")
        dbRepInv.store()

        when:
        ec.service.sync().name("EFacturaServices.check#ReportedInvoicesState").call()

        then:
        dbRepInv.refresh()
        dbRepInv.get("invoiceId") == "1"
        dbRepInv.get("statusId") == "AnafRepInvWaitingValidation"
        dbRepInv.get("uploadIndex") == "5000"
        ec.entity.find("ro.flexbiz.efactura.ReportedInvoice").count() == 1

        cleanup:
        dbRepInv.delete()
    }

    def "givenValidInvoice_whenCheckRepInvState_thenSaveDownloadIdAndState"() {
        given:
        final EntityValue dbRepInv = ec.entity.makeValue("ro.flexbiz.efactura.ReportedInvoice")
        dbRepInv.set("invoiceId", "1")
        dbRepInv.set("statusId", "AnafRepInvWaitingValidation")
        dbRepInv.set("uploadIndex", "5001")
        dbRepInv.store()

//        final AnafUploadStateResponseHeader anafResponseHeader = new AnafUploadStateResponseHeader()
//        anafResponseHeader.setState("ok")
//        anafResponseHeader.setDownloadId("1234")

        when:
        ec.service.sync().name("EFacturaServices.check#ReportedInvoicesState").call()

        then:
        dbRepInv.refresh()
        dbRepInv.get("invoiceId") == "1"
        dbRepInv.get("statusId") == "AnafRepInvSent"
        dbRepInv.get("uploadIndex") == "5001"
        dbRepInv.get("downloadId") == "1234"
        dbRepInv.get("errorMessage") == null
        ec.entity.find("ro.flexbiz.efactura.ReportedInvoice").count() == 1

        cleanup:
        dbRepInv.delete()
    }

    def "givenInvalidInvoice_whenCheckRepInvState_thenSaveDownloadIdAndState"() {
        given:
        final EntityValue dbRepInv = ec.entity.makeValue("ro.flexbiz.efactura.ReportedInvoice")
        dbRepInv.set("invoiceId", "1")
        dbRepInv.set("statusId", "AnafRepInvWaitingValidation")
        dbRepInv.set("uploadIndex", "5002")
        dbRepInv.store()

//        final AnafUploadStateResponseHeader anafResponseHeader = new AnafUploadStateResponseHeader()
//        anafResponseHeader.setState("nok")
//        anafResponseHeader.setDownloadId("4321")

        when:
        ec.service.sync().name("EFacturaServices.check#ReportedInvoicesState").call()

        then:
        dbRepInv.refresh()
        dbRepInv.get("invoiceId") == "1"
        dbRepInv.get("statusId") == "AnafRepInvRejectedInvalid"
        dbRepInv.get("uploadIndex") == "5002"
        dbRepInv.get("downloadId") == "4321"
        dbRepInv.get("errorMessage") == "Validarea facturii a esuat. Descarcati fisierul de erori cu id-ul 4321"
        ec.entity.find("ro.flexbiz.efactura.ReportedInvoice").count() == 1

        cleanup:
        dbRepInv.delete()
    }

    def "givenValidationIsPending_whenCheckRepInvState_thenSkipCheck"() {
        given:
        final EntityValue dbRepInv = ec.entity.makeValue("ro.flexbiz.efactura.ReportedInvoice")
        dbRepInv.set("invoiceId", "1")
        dbRepInv.set("statusId", "AnafRepInvWaitingValidation")
        dbRepInv.set("uploadIndex", "5003")
        dbRepInv.store()

//        final AnafUploadStateResponseHeader anafResponseHeader = new AnafUploadStateResponseHeader()
//        anafResponseHeader.setState("in prelucrare")

        when:
        ec.service.sync().name("EFacturaServices.check#ReportedInvoicesState").call()

        then:
        dbRepInv.refresh()
        dbRepInv.get("invoiceId") == "1"
        dbRepInv.get("statusId") == "AnafRepInvWaitingValidation"
        dbRepInv.get("uploadIndex") == "5003"
        ec.entity.find("ro.flexbiz.efactura.ReportedInvoice").count() == 1

        cleanup:
        dbRepInv.delete()
    }

    def "givenXmlInvalid_whenCheckRepInvState_thenSaveErrorState"() {
        given:
        final EntityValue dbRepInv = ec.entity.makeValue("ro.flexbiz.efactura.ReportedInvoice")
        dbRepInv.set("invoiceId", "1")
        dbRepInv.set("statusId", "AnafRepInvWaitingValidation")
        dbRepInv.set("uploadIndex", "5004")
        dbRepInv.store()

//        final AnafUploadStateResponseHeader anafResponseHeader = new AnafUploadStateResponseHeader()
//        anafResponseHeader.setState("XML cu erori nepreluat de sistem")

        when:
        ec.service.sync().name("EFacturaServices.check#ReportedInvoicesState").call()

        then:
        dbRepInv.refresh()
        dbRepInv.get("invoiceId") == "1"
        dbRepInv.get("statusId") == "AnafRepInvRejectedInvalid"
        dbRepInv.get("uploadIndex") == "5004"
        dbRepInv.get("downloadId") == null
        dbRepInv.get("errorMessage") == "XML cu erori nepreluat de sistem"
        ec.entity.find("ro.flexbiz.efactura.ReportedInvoice").count() == 1

        cleanup:
        dbRepInv.delete()
    }

    def "givenUnauthorizedIndex_whenCheckRepInvState_thenSkipCheck"() {
        given:
        final EntityValue dbRepInv = ec.entity.makeValue("ro.flexbiz.efactura.ReportedInvoice")
        dbRepInv.set("invoiceId", "1")
        dbRepInv.set("statusId", "AnafRepInvWaitingValidation")
        dbRepInv.set("uploadIndex", "5005")
        dbRepInv.store()

        //"Nu aveti dreptul de inteorgare pentru id_incarcare=3842"

        when:
        ec.service.sync().name("EFacturaServices.check#ReportedInvoicesState").call()

        then:
        dbRepInv.refresh()
        dbRepInv.get("invoiceId") == "1"
        dbRepInv.get("statusId") == "AnafRepInvWaitingValidation"
        dbRepInv.get("uploadIndex") == "5005"
        dbRepInv.get("downloadId") == null
        dbRepInv.get("errorMessage") == null
        ec.entity.find("ro.flexbiz.efactura.ReportedInvoice").count() == 1

        cleanup:
        dbRepInv.delete()
    }

    def "givenUnauthorizedTaxId_whenCheckRepInvState_thenSkipCheck"() {
        given:
        final EntityValue dbRepInv = ec.entity.makeValue("ro.flexbiz.efactura.ReportedInvoice")
        dbRepInv.set("invoiceId", "1")
        dbRepInv.set("statusId", "AnafRepInvWaitingValidation")
        dbRepInv.set("uploadIndex", "5006")
        dbRepInv.store()

//        AnafResponseError("Nu exista niciun CIF petru care sa aveti drept")

        when:
        ec.service.sync().name("EFacturaServices.check#ReportedInvoicesState").call()

        then:
        dbRepInv.refresh()
        dbRepInv.get("invoiceId") == "1"
        dbRepInv.get("statusId") == "AnafRepInvWaitingValidation"
        dbRepInv.get("uploadIndex") == "5006"
        dbRepInv.get("downloadId") == null
        dbRepInv.get("errorMessage") == null
        ec.entity.find("ro.flexbiz.efactura.ReportedInvoice").count() == 1

        cleanup:
        dbRepInv.delete()
    }

    def "givenInvalidUploadIndex_whenCheckRepInvState_thenSkipCheck"() {
        given:
        final EntityValue dbRepInv = ec.entity.makeValue("ro.flexbiz.efactura.ReportedInvoice")
        dbRepInv.set("invoiceId", "1")
        dbRepInv.set("statusId", "AnafRepInvWaitingValidation")
        dbRepInv.set("uploadIndex", "bbb")
        dbRepInv.store()

//        ("Id_incarcare introdus= bbb nu este un numar intreg")

        when:
        ec.service.sync().name("EFacturaServices.check#ReportedInvoicesState").call()

        then:
        dbRepInv.refresh()
        dbRepInv.get("invoiceId") == "1"
        dbRepInv.get("statusId") == "AnafRepInvWaitingValidation"
        dbRepInv.get("uploadIndex") == "bbb"
        dbRepInv.get("downloadId") == null
        dbRepInv.get("errorMessage") == null
        ec.entity.find("ro.flexbiz.efactura.ReportedInvoice").count() == 1

        cleanup:
        dbRepInv.delete()
    }

    def "givenInvoiceMissing_whenCheckRepInvState_thenSkipCheck"() {
        given:
        final EntityValue dbRepInv = ec.entity.makeValue("ro.flexbiz.efactura.ReportedInvoice")
        dbRepInv.set("invoiceId", "1")
        dbRepInv.set("statusId", "AnafRepInvWaitingValidation")
        dbRepInv.set("uploadIndex", "5007")
        dbRepInv.store()

//        AnafResponseError("Nu exista factura cu id_incarcare= 5007")

        when:
        ec.service.sync().name("EFacturaServices.check#ReportedInvoicesState").call()

        then:
        dbRepInv.refresh()
        dbRepInv.get("invoiceId") == "1"
        dbRepInv.get("statusId") == "AnafRepInvWaitingValidation"
        dbRepInv.get("uploadIndex") == "5007"
        dbRepInv.get("downloadId") == null
        dbRepInv.get("errorMessage") == null
        ec.entity.find("ro.flexbiz.efactura.ReportedInvoice").count() == 1

        cleanup:
        dbRepInv.delete()
    }

    def "givenQuotaExceeded_whenCheckRepInvState_thenSkipCheck"() {
        given:
        final EntityValue dbRepInv = ec.entity.makeValue("ro.flexbiz.efactura.ReportedInvoice")
        dbRepInv.set("invoiceId", "1")
        dbRepInv.set("statusId", "AnafRepInvWaitingValidation")
        dbRepInv.set("uploadIndex", "5008")
        dbRepInv.store()

//        AnafResponseError("S-au facut deja 20 descarcari de mesaj in cursul zilei")

        when:
        ec.service.sync().name("EFacturaServices.check#ReportedInvoicesState").call()

        then:
        dbRepInv.refresh()
        dbRepInv.get("invoiceId") == "1"
        dbRepInv.get("statusId") == "AnafRepInvWaitingValidation"
        dbRepInv.get("uploadIndex") == "5008"
        dbRepInv.get("downloadId") == null
        dbRepInv.get("errorMessage") == null
        ec.entity.find("ro.flexbiz.efactura.ReportedInvoice").count() == 1

        cleanup:
        dbRepInv.delete()
    }
}