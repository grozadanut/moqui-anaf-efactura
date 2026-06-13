package ro.flexbiz.efactura.service

import com.helger.ubl21.codelist.*
import org.moqui.Moqui
import org.moqui.context.ExecutionContext
import ro.flexbiz.efactura.pojo.*
import ro.flexbiz.efactura.pojo.anaf.*
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

import static org.assertj.core.api.Assertions.assertThat

class AnafApiIntegrationTests extends Specification {
    @Shared
    ExecutionContext ec

    private Invoice invoice 
    private String token 

    def setupSpec() {
        ec = Moqui.getExecutionContext()
        ec.user.loginAnonymousIfNoUser()
    }

    def cleanupSpec() {
        ec.destroy()
    }

    def setup() {
        ec.artifactExecution.disableAuthz()
        ec.message.clearAll()
        token = "tokenValue"

        invoice = new Invoice() 
        invoice.setId(1L) 
        invoice.setInvoiceNumber("LIND100") 
        invoice.setIssueDate(LocalDate.of(2023, 10, 20).atStartOfDay().toInstant(ZoneOffset.UTC)) 
        invoice.setDueDate(LocalDate.of(2023, 11, 19).atStartOfDay().toInstant(ZoneOffset.UTC)) 
        invoice.setDocumentCurrencyCode(ECurrencyCode21.RON.getID()) 
        invoice.setPaymentMeansCode(EPaymentMeansCode21._30.getID()) 
        invoice.setPaymentId("FF_LIND100/2023-10-20") 
        invoice.setNote("Textual note") 

        final FinancialAccount payeeFinancialAccount = new FinancialAccount() 
        payeeFinancialAccount.setId("RO0123456xxx") 
        payeeFinancialAccount.setName("LINIC SRL") 
        payeeFinancialAccount.setFinancialInstitutionBranch("BT") 
        payeeFinancialAccount.setCurrency(ECurrencyCode21.RON.getID()) 
        invoice.setPayeeFinancialAccount(payeeFinancialAccount) 

        invoice.setTaxCurrencyCode(ECurrencyCode21.RON.getID()) 
        invoice.setTaxAmount(new BigDecimal("19")) 

        final TaxSubtotal taxSubtotal = new TaxSubtotal() 
        taxSubtotal.setTaxableAmount(new BigDecimal("100")) 
        taxSubtotal.setTaxAmount(new BigDecimal("19")) 
        final TaxCategory taxCategory = new TaxCategory() 
        taxCategory.setCode("S") 
        taxCategory.setPercent(new BigDecimal("0.19")) 
        taxCategory.setTaxScheme("VAT") 
        taxSubtotal.setTaxCategory(taxCategory) 
        invoice.setTaxSubtotals(List.of(taxSubtotal)) 

        final AllowanceCharge charge = new AllowanceCharge() 
        charge.setChargeIndicator(true) 
        charge.setAllowanceChargeReasonCode(EAllowanceChargeReasonCode21.ZZZ.getID()) 
        charge.setAllowanceChargeReason(EAllowanceChargeReasonCode21.ZZZ.getDisplayName()) 
        charge.setMultiplierFactorNumeric(new BigDecimal("0.1")) 
        charge.setBaseAmount(new BigDecimal("100")) 
        charge.setAmount(new BigDecimal("10")) 
        final AllowanceCharge allowance = new AllowanceCharge() 
        allowance.setChargeIndicator(false) 
        allowance.setAllowanceChargeReasonCode(EAllowanceChargeReasonCode21._19.getID()) 
        allowance.setAllowanceChargeReason(EAllowanceChargeReasonCode21._19.getDisplayName()) 
        allowance.setMultiplierFactorNumeric(new BigDecimal("0.1")) 
        allowance.setBaseAmount(new BigDecimal("100")) 
        allowance.setAmount(new BigDecimal("10")) 
        invoice.setAllowanceCharges(List.of(charge, allowance)) 

        invoice.setLineExtensionAmount(new BigDecimal("100")) 
        invoice.setTaxExclusiveAmount(new BigDecimal("100")) 
        invoice.setTaxInclusiveAmount(new BigDecimal("119")) 
        invoice.setAllowanceTotalAmount(new BigDecimal("10")) 
        invoice.setChargeTotalAmount(new BigDecimal("10")) 
        invoice.setPrepaidAmount(new BigDecimal("50")) 
        invoice.setPayableAmount(new BigDecimal("79")) 

        final Party supplier = new Party() 
        supplier.setTaxId("RO1485236") 
        supplier.setBusinessName("Colibri - Linic") 
        supplier.setRegistrationName("LINIC SRL") 
        supplier.setRegistrationId("J05/1001/2002") 
        supplier.setCompanyLegalForm("Capital social 200 RON") 
        supplier.setContactName("Groza Danut") 
        supplier.setTelephone("0259xxx") 
        supplier.setElectronicMail("linic@gmail.com") 
        final Address supplierAddress = new Address() 
        supplierAddress.setCountry(ECountryIdentificationCode21.RO.getID()) 
        supplierAddress.setCountrySubentity("RO-BH") 
        supplierAddress.setCity("MARGHITA") 
        supplierAddress.setPostalZone("415300") 
        supplierAddress.setPrimaryLine("Balcescu 51") 
        supplierAddress.setSecondaryLine("Vladimirescu 59") 
        supplier.setPostalAddress(supplierAddress) 
        invoice.setAccountingSupplier(supplier) 
        invoice.setPayeeParty(supplier) 

        final Party customer = new Party() 
        customer.setTaxId("RO148") 
        customer.setRegistrationName("CLIENT SRL") 
        customer.setRegistrationId("J05/10/2023") 
        customer.setContactName("Delegat") 
        customer.setTelephone("0745154xxx") 
        final Address customerAddress = new Address() 
        customerAddress.setCountry(ECountryIdentificationCode21.RO.getID()) 
        customerAddress.setCountrySubentity("RO-BH") 
        customerAddress.setCity("MARGHITA") 
        customerAddress.setPostalZone("415300") 
        customerAddress.setPrimaryLine("Revolutiei") 
        customer.setPostalAddress(customerAddress) 
        invoice.setAccountingCustomer(customer) 

        final InvoiceLine invoiceLine = new InvoiceLine() 
        invoiceLine.setId(1L) 
        invoiceLine.setSellersItemIdentification("399") 
        invoiceLine.setBuyersItemIdentification("598751225812") 
        invoiceLine.setNote("Test note") 
        invoiceLine.setDescription("Super fluffly paper") 
        invoiceLine.setName("Toilet paper 3ply") 
        invoiceLine.setQuantity(new BigDecimal("11")) 
        invoiceLine.setUom(EUnitOfMeasureCode21.C62.getID()) 
        invoiceLine.setClassifiedTaxCategory(taxCategory) 
        invoiceLine.setPrice(new BigDecimal("10")) 
        invoiceLine.setBaseQuantity(new BigDecimal("1")) 
        invoiceLine.setLineExtensionAmount(new BigDecimal("100")) 
        invoiceLine.setTaxAmount(new BigDecimal("19")) 
        invoiceLine.setAllowanceCharges(List.of(allowance)) 
        invoice.setLines(List.of(invoiceLine)) 
    }

    def "givenValidInvoice_whenUpload_thenReturnUploadIndex"() throws URISyntaxException {
        when:
        final AnafUploadResponseHeader response = ec.service.sync().name("AnafServices.upload#Invoice")
                .parameters([accessToken: token, invoice: invoice])
                .call().anafUploadResponseHeader as AnafUploadResponseHeader

        then:
        response.getUploadIndex() == "3828"
        response.isExecutionStatusOk() == true
        response.getErrors().isEmpty()
    }

    def "givenTokenIsNull_whenUpload_thenThrowNPE"() {
        when:
        Map<String, Object> response = ec.service.sync().name("AnafServices.upload#Invoice")
                .parameters([accessToken: null, invoice: invoice])
                .call()
        then:
        response == null
        ec.message.errorsString.trim() == "Field cannot be empty(for field Access Token of service Anaf Services Upload Invoice"
    }

    def "givenInvoiceIsNull_whenUpload_thenThrowNPE"() {
        when:
        Map<String, Object> response = ec.service.sync().name("AnafServices.upload#Invoice")
                .parameters([accessToken: token, invoice: null])
                .call()
        then:
        response == null
        ec.message.errorsString.trim() == "Field cannot be empty(for field Invoice of service Anaf Services Upload Invoice"
    }

    def "givenInvalidAccessPermission_whenUpload_thenReturnError"() {
        when:
        final AnafUploadResponseHeader response = ec.service.sync().name("AnafServices.upload#Invoice")
                .parameters([accessToken: token, invoice: invoice])
                .call().anafUploadResponseHeader as AnafUploadResponseHeader

        then:
        response.getUploadIndex() == null
        response.isExecutionStatusOk() == false
        assertThat(response.getErrors()).singleElement().extracting(AnafResponseError::getMessage)
                .isEqualTo("Nu exista niciun CIF petru care sa aveti drept in SPV") 
    }

    def "givenInvalidInvoice_whenUpload_thenThrowException"() throws URISyntaxException {
        given:
        final Invoice invoice = new Invoice()
        invoice.setId(1L)
        invoice.setInvoiceNumber("LIND100")
        invoice.setIssueDate(LocalDate.of(2023, 10, 20).atStartOfDay().toInstant(ZoneOffset.UTC))

        when:
        Map<String, Object> response = ec.service.sync().name("AnafServices.upload#Invoice")
                .parameters([accessToken: token, invoice: invoice])
                .call()
        then:
        response.isEmpty()
        ec.message.errorsString.contains("cvc-complex-type.2.4.a: Invalid content was found starting with element")
    }


    def "givenValidInvoice_whenCheckInvoiceState_thenReturnInvoiceDownloadId"() throws URISyntaxException {
        given:
        final String uploadIndex = "3828"
        when:
        final AnafUploadStateResponseHeader response = ec.service.sync().name("AnafServices.check#InvoiceState")
                .parameters([accessToken: token, uploadIndex: uploadIndex])
                .call().anafUploadStateResponseHeader as AnafUploadStateResponseHeader

        then:
        response.getDownloadId() == "1234"
        assertThat(response.isStateOk()).isTrue()
        assertThat(response.isStateNok()).isFalse()
        assertThat(response.isStatePending()).isFalse()
        assertThat(response.getErrors()).isEmpty()
        assertThat(response.prettyErrorMessage()).isNullOrEmpty()
    }

    def "givenInvalidInvoice_whenCheckInvoiceState_thenReturnErrorDownloadId"() throws URISyntaxException {
        given:
        final String uploadIndex = "3829"
        when:
        final AnafUploadStateResponseHeader response = ec.service.sync().name("AnafServices.check#InvoiceState")
                .parameters([accessToken: token, uploadIndex: uploadIndex])
                .call().anafUploadStateResponseHeader as AnafUploadStateResponseHeader

        then:
        assertThat(response.getDownloadId()).isEqualTo("123")
        assertThat(response.isStateOk()).isFalse()
        assertThat(response.isStateNok()).isTrue()
        assertThat(response.isStatePending()).isFalse()
        assertThat(response.getErrors()).isEmpty()
        assertThat(response.prettyErrorMessage()).isEqualTo("nok")
    }

    def "givenValidationIsPending_whenCheckInvoiceState_thenReturnResponse"() throws URISyntaxException {
        given:
        final String uploadIndex = "3830"
        when:
        final AnafUploadStateResponseHeader response = ec.service.sync().name("AnafServices.check#InvoiceState")
                .parameters([accessToken: token, uploadIndex: uploadIndex])
                .call().anafUploadStateResponseHeader as AnafUploadStateResponseHeader

        then:
        assertThat(response.getDownloadId()).isNull()
        assertThat(response.isStateOk()).isFalse()
        assertThat(response.isStateNok()).isFalse()
        assertThat(response.isStatePending()).isTrue()
        assertThat(response.getErrors()).isEmpty()
        assertThat(response.prettyErrorMessage()).isEqualTo("in prelucrare")
    }

    def "givenXmlInvalid_whenCheckInvoiceState_thenReturnResponse"() throws URISyntaxException {
        given:
        final String uploadIndex = "3831"
        when:
        final AnafUploadStateResponseHeader response = ec.service.sync().name("AnafServices.check#InvoiceState")
                .parameters([accessToken: token, uploadIndex: uploadIndex])
                .call().anafUploadStateResponseHeader as AnafUploadStateResponseHeader

        then:
        assertThat(response.getDownloadId()).isNull()
        assertThat(response.isStateOk()).isFalse()
        assertThat(response.isStateNok()).isFalse()
        assertThat(response.isStatePending()).isFalse()
        assertThat(response.getErrors()).isEmpty()
        assertThat(response.prettyErrorMessage()).isEqualTo("XML cu erori nepreluat de sistem")
    }

    def "givenUnauthorizedIndex_whenCheckInvoiceState_thenReturnResponse"() throws URISyntaxException {
        given:
        final String uploadIndex = "3832"
        when:
        final AnafUploadStateResponseHeader response = ec.service.sync().name("AnafServices.check#InvoiceState")
                .parameters([accessToken: token, uploadIndex: uploadIndex])
                .call().anafUploadStateResponseHeader as AnafUploadStateResponseHeader

        then:
        assertThat(response.getDownloadId()).isNull()
        assertThat(response.isStateOk()).isFalse()
        assertThat(response.isStateNok()).isFalse()
        assertThat(response.isStatePending()).isFalse()
        assertThat(response.getErrors()).singleElement().extracting(AnafResponseError::getMessage)
                .isEqualTo("Nu aveti dreptul de inteorgare pentru id_incarcare=3828")
        assertThat(response.prettyErrorMessage()).isEqualTo("Nu aveti dreptul de inteorgare pentru id_incarcare=3828")
    }

    def "givenUnauthorizedTaxId_whenCheckInvoiceState_thenReturnResponse"() throws URISyntaxException {
        given:
        final String uploadIndex = "3833"
        when:
        final AnafUploadStateResponseHeader response = ec.service.sync().name("AnafServices.check#InvoiceState")
                .parameters([accessToken: token, uploadIndex: uploadIndex])
                .call().anafUploadStateResponseHeader as AnafUploadStateResponseHeader

        then:
        assertThat(response.getDownloadId()).isNull()
        assertThat(response.isStateOk()).isFalse()
        assertThat(response.isStateNok()).isFalse()
        assertThat(response.isStatePending()).isFalse()
        assertThat(response.getErrors()).singleElement().extracting(AnafResponseError::getMessage)
                .isEqualTo("Nu exista niciun CIF petru care sa aveti drept")
        assertThat(response.prettyErrorMessage()).isEqualTo("Nu exista niciun CIF petru care sa aveti drept")
    }

    def "givenInvalidUploadIndex_whenCheckInvoiceState_thenReturnResponse"() throws URISyntaxException {
        given:
        final String uploadIndex = "aaa"
        when:
        final AnafUploadStateResponseHeader response = ec.service.sync().name("AnafServices.check#InvoiceState")
                .parameters([accessToken: token, uploadIndex: uploadIndex])
                .call().anafUploadStateResponseHeader as AnafUploadStateResponseHeader

        then:
        assertThat(response.getDownloadId()).isNull()
        assertThat(response.isStateOk()).isFalse()
        assertThat(response.isStateNok()).isFalse()
        assertThat(response.isStatePending()).isFalse()
        assertThat(response.getErrors()).singleElement().extracting(AnafResponseError::getMessage)
                .isEqualTo("Id_incarcare introdus= aaa nu este un numar intreg")
        assertThat(response.prettyErrorMessage()).isEqualTo("Id_incarcare introdus= aaa nu este un numar intreg")
    }

    def "givenInvoiceMissing_whenCheckInvoiceState_thenReturnResponse"() throws URISyntaxException {
        given:
        final String uploadIndex = "15000"
        when:
        final AnafUploadStateResponseHeader response = ec.service.sync().name("AnafServices.check#InvoiceState")
                .parameters([accessToken: token, uploadIndex: uploadIndex])
                .call().anafUploadStateResponseHeader as AnafUploadStateResponseHeader

        then:
        assertThat(response.getDownloadId()).isNull()
        assertThat(response.isStateOk()).isFalse()
        assertThat(response.isStateNok()).isFalse()
        assertThat(response.isStatePending()).isFalse()
        assertThat(response.getErrors()).singleElement().extracting(AnafResponseError::getMessage)
                .isEqualTo("Nu exista factura cu id_incarcare= 15000")
        assertThat(response.prettyErrorMessage()).isEqualTo("Nu exista factura cu id_incarcare= 15000")
    }

    def "givenQuotaExceeded_whenCheckInvoiceState_thenReturnResponse"() throws URISyntaxException {
        given:
        final String uploadIndex = "3838"
        when:
        final AnafUploadStateResponseHeader response = ec.service.sync().name("AnafServices.check#InvoiceState")
                .parameters([accessToken: token, uploadIndex: uploadIndex])
                .call().anafUploadStateResponseHeader as AnafUploadStateResponseHeader

        then:
        assertThat(response.getDownloadId()).isNull()
        assertThat(response.isStateOk()).isFalse()
        assertThat(response.isStateNok()).isFalse()
        assertThat(response.isStatePending()).isFalse()
        assertThat(response.getErrors()).singleElement().extracting(AnafResponseError::getMessage)
                .isEqualTo("S-au facut deja 20 descarcari de mesaj in cursul zilei")
        assertThat(response.prettyErrorMessage()).isEqualTo("S-au facut deja 20 descarcari de mesaj in cursul zilei")
    }

    def "givenValidCall_whenReceivedMessages_thenReturnParsedMessages"() throws URISyntaxException {
        given:
        final String cif = "1485236"
        final int days = 60

        when:
        final AnafReceivedMessages response = ec.service.sync().name("AnafServices.received#Messages")
                .parameters([accessToken: token, taxId: cif, days: days])
                .call().anafReceivedMessages as AnafReceivedMessages

        then:
        final AnafReceivedMessage billSent = new AnafReceivedMessage("3001503294", LocalDateTime.of(2022, 11, 1, 13, 36),
                "1485236", "5001131297", "Factura cu id_incarcare=5001131297 emisa de cif_emitent=1485236 pentru cif_beneficiar=3",
                AnafReceivedMessage.AnafReceivedMessageType.BILL_SENT)
        final AnafReceivedMessage billReceived = new AnafReceivedMessage("3009239535", LocalDateTime.of(2024, 1, 25, 14, 36),
                "1485236", "5006514680", "Factura cu id_incarcare=5006514680 emisa de cif_emitent=1485236 pentru cif_beneficiar=1485236",
                AnafReceivedMessage.AnafReceivedMessageType.BILL_RECEIVED)
        final AnafReceivedMessage validationErrors = new AnafReceivedMessage("3001293434", LocalDateTime.of(2022, 11, 1, 14, 15),
                "1485236", "5001130147", "Erori de validare identificate la factura primita cu id_incarcare=5001130147",
                AnafReceivedMessage.AnafReceivedMessageType.BILL_ERRORS)

        response.getError() == null
        assertThat(response.getMessages()).hasSize(3)
        assertThat(response.getMessages()).containsExactlyInAnyOrder(billSent, billReceived, validationErrors)
    }

    def "givenTaxIdIsNonNumeric_whenReceivedMessages_thenReturnError"() throws URISyntaxException {
        given:
        final String cif = "aaa"
        final int days = 60

        when:
        final AnafReceivedMessages response = ec.service.sync().name("AnafServices.received#Messages")
                .parameters([accessToken: token, taxId: cif, days: days])
                .call().anafReceivedMessages as AnafReceivedMessages

        then:
        assertThat(response.getError()).isEqualTo("CIF introdus= aaa nu este un numar")
        assertThat(response.getMessages()).isEmpty()
    }

    def "givenDayGreaterThan60_whenReceivedMessages_thenThrowException"() throws URISyntaxException {
        given:
        final String cif = "1485236"
        final int days = 61

        when:
        final AnafReceivedMessages response = ec.service.sync().name("AnafServices.received#Messages")
                .parameters([accessToken: token, taxId: cif, days: days])
                .call().anafReceivedMessages as AnafReceivedMessages
        then:
        response == null
        ec.message.errorsString.trim().contains("Numarul de zile pentru care se face interogarea trebuie sa fie intre 1 si 60!")
    }

    def "givenDaySmallerThan1_whenReceivedMessages_thenThrowException"() throws URISyntaxException {
        given:
        final String cif = "1485236"
        final int days = 0

        when:
        final AnafReceivedMessages response = ec.service.sync().name("AnafServices.received#Messages")
                .parameters([accessToken: token, taxId: cif, days: days])
                .call().anafReceivedMessages as AnafReceivedMessages
        then:
        response == null
        ec.message.errorsString.trim().contains("Numarul de zile pentru care se face interogarea trebuie sa fie intre 1 si 60!")
    }

    def "givenTaxIdNotAllowed_whenReceivedMessages_thenReturnError"() throws URISyntaxException {
        given:
        final String cif = "8000000000"
        final int days = 60

        when:
        final AnafReceivedMessages response = ec.service.sync().name("AnafServices.received#Messages")
                .parameters([accessToken: token, taxId: cif, days: days])
                .call().anafReceivedMessages as AnafReceivedMessages

        then:
        assertThat(response.getError()).isEqualTo("Nu aveti drept in SPV pentru CIF=8000000000")
        assertThat(response.getMessages()).isEmpty()
    }

    def "givenNotAllowed_whenReceivedMessages_thenReturnError"() throws URISyntaxException {
        given:
        final String cif = "1485237"
        final int days = 60

        when:
        final AnafReceivedMessages response = ec.service.sync().name("AnafServices.received#Messages")
                .parameters([accessToken: token, taxId: cif, days: days])
                .call().anafReceivedMessages as AnafReceivedMessages

        then:
        assertThat(response.getError()).isEqualTo("Nu exista niciun CIF petru care sa aveti drept in SPV")
        assertThat(response.getMessages()).isEmpty()
    }

    def "givenNoMessages_whenReceivedMessages_thenReturnSpecialErrorCode"() throws URISyntaxException {
        given:
        final String cif = "1485238"
        final int days = 60

        when:
        final AnafReceivedMessages response = ec.service.sync().name("AnafServices.received#Messages")
                .parameters([accessToken: token, taxId: cif, days: days])
                .call().anafReceivedMessages as AnafReceivedMessages

        then:
        assertThat(response.getError()).isEqualTo(AnafReceivedMessages.NO_MESSAGES_ERROR)
        assertThat(response.getMessages()).isEmpty()
    }

    def "givenTooManyMessages_whenReceivedMessages_thenReturnSpecialErrorCode"() throws URISyntaxException {
        given:
        final String cif = "1485239"
        final int days = 60

        when:
        final AnafReceivedMessages response = ec.service.sync().name("AnafServices.received#Messages")
                .parameters([accessToken: token, taxId: cif, days: days])
                .call().anafReceivedMessages as AnafReceivedMessages

        then:
        assertThat(response.getError()).isEqualTo(AnafReceivedMessages.TOO_MANY_MESSAGES_ERROR)
        assertThat(response.getMessages()).isEmpty()
    }

    def "givenDailyQuotaReached_whenReceivedMessages_thenReturnError"() throws URISyntaxException {
        given:
        final String cif = "1485240"
        final int days = 60

        when:
        final AnafReceivedMessages response = ec.service.sync().name("AnafServices.received#Messages")
                .parameters([accessToken: token, taxId: cif, days: days])
                .call().anafReceivedMessages as AnafReceivedMessages

        then:
        assertThat(response.getError()).isEqualTo("S-au facut deja 1000 interogari de lista mesaje de catre utilizator in cursul zilei")
        assertThat(response.getMessages()).isEmpty()
    }
}
