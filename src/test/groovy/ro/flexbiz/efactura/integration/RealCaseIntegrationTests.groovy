package ro.flexbiz.efactura.integration

import com.fasterxml.jackson.core.JsonParser
import org.moqui.Moqui
import org.moqui.context.ExecutionContext
import org.moqui.entity.EntityValue
import org.moqui.impl.context.ContextJavaUtil
import org.moqui.screen.ScreenTest
import ro.flexbiz.efactura.pojo.Invoice
import spock.lang.Shared
import spock.lang.Specification

class RealCaseIntegrationTests extends Specification {
    @Shared
    ExecutionContext ec
    @Shared
    ScreenTest screenTest

    static final String token = 'TestSessionToken'

    private String accessToken
    private String taxId
    EntityValue credential
    EntityValue accessTokenField
    EntityValue taxIdField
    EntityValue credentialUser

    def setupSpec() {
        ec = Moqui.getExecutionContext()
        ec.user.loginUser("john.doe", "moqui")
        screenTest = ec.screen.makeTest().baseScreenPath("rest")
    }

    def cleanupSpec() {
        ec.destroy()
    }

    def setup() {
        ec.artifactExecution.disableAuthz()

        accessToken = "RealCaseIntegrationTests"
        taxId = "1485236"

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
        ec.artifactExecution.enableAuthz()
    }

    def "givenRealDataCase1_whenUploadInvoice_thenNoValidationErrors"() {
        /**
         * Real test data taken from an Invoice to Primaria Marghita
         * The errors returned from Anaf when sending the Invoice from the attached json are:
         * 1. The Buyer vat identifier shall have the country 2 digit code prefix
         * 2. TaxTotal not permitted at InvoiceLine level
         * 3. Issue date format error(yyyy-mm-dd): 2023-12-18+02:00
         * 4. Due date format error(yyyy-mm-dd): 2023-12-22+02:00
         * 5. Customer partyLegalEntity companyId: attribute present but void not allowed
         * 6. Customer contact electronicMail: attribute present but void not allowed
         * These errors are fixed if the Anaf xml request matches the expected xml below
         */
        when:
        JsonParser jsonParser = ContextJavaUtil.jacksonMapper.createParser("""
                    {"id":284135,"lines":[{"id":444744,"sellersItemIdentification":"3734","buyersItemIdentification":null,"note":null,"description":null,"name":"CAPAC WC K2 ALB SANOBI INCHIDERE LENTA","quantity":1.0000,"uom":"C62","classifiedTaxCategory":{"code":"S","percent":0.19,"taxExemptionReason":null,"taxScheme":"VAT"},"price":74.79,"baseQuantity":1,"allowanceCharges":null,"lineExtensionAmount":74.79,"taxAmount":14.21},{"id":444745,"sellersItemIdentification":"5947041000291","buyersItemIdentification":null,"note":null,"description":null,"name":"REZERVOR WC BETA EXPORT","quantity":1.0000,"uom":"C62","classifiedTaxCategory":{"code":"S","percent":0.19,"taxExemptionReason":null,"taxScheme":"VAT"},"price":96.64,"baseQuantity":1,"allowanceCharges":null,"lineExtensionAmount":96.64,"taxAmount":18.36},{"id":444748,"sellersItemIdentification":"2092","buyersItemIdentification":null,"note":null,"description":null,"name":"VAS WC CIV CERSANIT PREZIDENT","quantity":1.0000,"uom":"C62","classifiedTaxCategory":{"code":"S","percent":0.19,"taxExemptionReason":null,"taxScheme":"VAT"},"price":243.70,"baseQuantity":1,"allowanceCharges":null,"lineExtensionAmount":243.70,"taxAmount":46.30},{"id":444743,"sellersItemIdentification":"5949052855150","buyersItemIdentification":null,"note":null,"description":null,"name":"CAPAC WC ALB AMORTIZARE GEHLER DUROPLAST","quantity":1.0000,"uom":"C62","classifiedTaxCategory":{"code":"S","percent":0.19,"taxExemptionReason":null,"taxScheme":"VAT"},"price":138.66,"baseQuantity":1,"allowanceCharges":null,"lineExtensionAmount":138.66,"taxAmount":26.34},{"id":444746,"sellersItemIdentification":"5411183185364","buyersItemIdentification":null,"note":null,"description":null,"name":"SOUDAL SILICON SANITAR TRANSP 310ML 160357","quantity":1.0000,"uom":"C62","classifiedTaxCategory":{"code":"S","percent":0.19,"taxExemptionReason":null,"taxScheme":"VAT"},"price":21.85,"baseQuantity":1,"allowanceCharges":null,"lineExtensionAmount":21.85,"taxAmount":4.15},{"id":444749,"sellersItemIdentification":"795","buyersItemIdentification":null,"note":null,"description":null,"name":"DISCOUNT IESIRI","quantity":-1.0000,"uom":"C62","classifiedTaxCategory":{"code":"S","percent":0.19,"taxExemptionReason":null,"taxScheme":"VAT"},"price":164.71,"baseQuantity":1,"allowanceCharges":null,"lineExtensionAmount":-164.71,"taxAmount":-31.29},{"id":444747,"sellersItemIdentification":"5411183185357","buyersItemIdentification":null,"note":null,"description":null,"name":"SOUDAL SILICON UNIVERSAL ALB 310ML 160359","quantity":1.0000,"uom":"C62","classifiedTaxCategory":{"code":"S","percent":0.19,"taxExemptionReason":null,"taxScheme":"VAT"},"price":21.01,"baseQuantity":1,"allowanceCharges":null,"lineExtensionAmount":21.01,"taxAmount":3.99}],"invoiceNumber":"LINDL1-1136","issueDate":"2023-12-12T13:58:10.615159Z","dueDate":"2023-12-19T22:00:00Z","accountingSupplier":{"taxId":"RO14998343","businessName":null,"registrationName":"SC LINIC SRL","registrationId":"J05/1111/2002","companyLegalForm":"Capital social 100,000.00 RON","postalAddress":{"country":"RO","countrySubentity":"RO-BH","city":"MARGINE","postalZone":null,"primaryLine":"Str Principala nr 218A","secondaryLine":null},"contactName":"GROZA MIRCEA","telephone":"Colibri - 0787577227, Linic - 0259362437","electronicMail":"colibridepot@gmail.com, sclinicsrl@gmail.com"},"accountingCustomer":{"taxId":"4348947","businessName":null,"registrationName":"PRIMARIA MARGHITA","registrationId":"","companyLegalForm":null,"postalAddress":{"country":"RO","countrySubentity":"RO-BH","city":"Marghita","postalZone":null,"primaryLine":"STR REPUBLICII JUD","secondaryLine":null},"contactName":"HECZI ALEXANDRU","telephone":"","electronicMail":""},"payeeParty":null,"documentCurrencyCode":"RON","paymentMeansCode":"30","paymentId":"FF_1136/2023-12-12","payeeFinancialAccount":{"id":"RO48BTRL00501202K65277XX-RON","name":"SC LINIC SRL","financialInstitutionBranch":null,"currency":null},"taxCurrencyCode":"RON","taxAmount":82.06,"taxSubtotals":[{"taxableAmount":431.94,"taxAmount":82.06,"taxCategory":{"code":"S","percent":0.19,"taxExemptionReason":null,"taxScheme":"VAT"}}],"allowanceCharges":null,"lineExtensionAmount":431.94,"taxExclusiveAmount":431.94,"taxInclusiveAmount":514.00,"allowanceTotalAmount":null,"chargeTotalAmount":null,"prepaidAmount":0,"payableRoundingAmount":null,"payableAmount":514.00,"note":null}
                    """)
        Invoice invoice = jsonParser.readValueAs(Invoice.class)
        Map<String, Object> params = [:]
        params.put("moquiSessionToken", token)
        params.put("invoice", invoice)
        ScreenTest.ScreenTestRender str = screenTest.render("s1/moqui-anaf-efactura/report", params, "put")

        then:
        ec.logger.info("${str.output}")
        str.assertContains("uploadIndex\" : \"3828")
        str.assertContains("statusId\" : \"AnafRepInvWaitingValidation")
        str.assertContains("errorMessage\" : null")
        str.assertContains("downloadId\" : null")
        str.assertContains("invoiceId\" : \"284135")
        ec.entity.find("ro.flexbiz.efactura.ReportedInvoice").count() == 1

        cleanup:
        ec.service.sync().name("delete#ro.flexbiz.efactura.ReportedInvoice")
                .parameter("invoiceId", "284135")
                .call()
    }
}
