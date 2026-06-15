package ro.flexbiz.efactura

import com.helger.ubl21.codelist.EAllowanceChargeReasonCode21
import com.helger.ubl21.codelist.ECountryIdentificationCode21
import com.helger.ubl21.codelist.ECurrencyCode21
import com.helger.ubl21.codelist.EPaymentMeansCode21
import com.helger.ubl21.codelist.EUnitOfMeasureCode21
import ro.flexbiz.efactura.pojo.Address
import ro.flexbiz.efactura.pojo.AllowanceCharge
import ro.flexbiz.efactura.pojo.FinancialAccount
import ro.flexbiz.efactura.pojo.Invoice
import ro.flexbiz.efactura.pojo.InvoiceLine
import ro.flexbiz.efactura.pojo.Party
import ro.flexbiz.efactura.pojo.TaxCategory
import ro.flexbiz.efactura.pojo.TaxSubtotal

import java.time.LocalDate
import java.time.ZoneOffset

public class TestData {
    public static String accessToken
    public static String taxId
    public static Invoice invoice

    public static void init() {
        accessTokenInit()
        invoiceInit()
    }

    private static void accessTokenInit() {
        taxId = "15487754"
        accessToken = "abc123"
    }

    private static void invoiceInit() {
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
}