package ro.flexbiz.efactura.mapper

import com.helger.ubl21.UBL21Marshaller
import com.helger.ubl21.codelist.EAllowanceChargeReasonCode21
import com.helger.ubl21.codelist.ECountryIdentificationCode21
import com.helger.ubl21.codelist.ECurrencyCode21
import com.helger.ubl21.codelist.EPaymentMeansCode21
import com.helger.ubl21.codelist.EUnitOfMeasureCode21
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType
import ro.flexbiz.efactura.pojo.Address
import ro.flexbiz.efactura.pojo.AllowanceCharge
import ro.flexbiz.efactura.pojo.FinancialAccount
import ro.flexbiz.efactura.pojo.Invoice
import ro.flexbiz.efactura.pojo.InvoiceLine
import ro.flexbiz.efactura.pojo.Party
import ro.flexbiz.efactura.pojo.TaxCategory
import ro.flexbiz.efactura.pojo.TaxSubtotal
import spock.lang.Specification

import java.time.LocalDate
import java.time.ZoneOffset

public class InvoiceMapperTests extends Specification {

    def "whenToUblInvoice_thenMapAllInvoiceFields"() {
        // given
        given:
        final Invoice invoice = new Invoice();
        invoice.setId(1L);
        invoice.setInvoiceNumber("LIND100");
        invoice.setIssueDate(LocalDate.of(2023, 10, 20).atStartOfDay().toInstant(ZoneOffset.UTC));
        invoice.setDueDate(LocalDate.of(2023, 11, 19).atStartOfDay().toInstant(ZoneOffset.UTC));
        invoice.setDocumentCurrencyCode(ECurrencyCode21.RON.getID());
        invoice.setPaymentMeansCode(EPaymentMeansCode21._30.getID());
        invoice.setPaymentId("FF_LIND100/2023-10-20");
        invoice.setNote("Textual note");

        final FinancialAccount payeeFinancialAccount = new FinancialAccount();
        payeeFinancialAccount.setId("RO0123456xxx");
        payeeFinancialAccount.setName("LINIC SRL");
        payeeFinancialAccount.setFinancialInstitutionBranch("BT");
        payeeFinancialAccount.setCurrency(ECurrencyCode21.RON.getID());
        invoice.setPayeeFinancialAccount(payeeFinancialAccount );

        invoice.setTaxCurrencyCode(ECurrencyCode21.RON.getID());
        invoice.setTaxAmount(new BigDecimal("19"));

        final TaxSubtotal taxSubtotal = new TaxSubtotal();
        taxSubtotal.setTaxableAmount(new BigDecimal("100"));
        taxSubtotal.setTaxAmount(new BigDecimal("19"));
        final TaxCategory taxCategory = new TaxCategory();
        taxCategory.setCode("S");
        taxCategory.setPercent(new BigDecimal("0.19"));
        taxCategory.setTaxScheme("VAT");
        taxSubtotal.setTaxCategory(taxCategory);
        invoice.setTaxSubtotals(List.of(taxSubtotal));

        final AllowanceCharge charge = new AllowanceCharge();
        charge.setChargeIndicator(true);
        charge.setAllowanceChargeReasonCode(EAllowanceChargeReasonCode21.ZZZ.getID());
        charge.setAllowanceChargeReason(EAllowanceChargeReasonCode21.ZZZ.getDisplayName());
        charge.setMultiplierFactorNumeric(new BigDecimal("0.1"));
        charge.setBaseAmount(new BigDecimal("100"));
        charge.setAmount(new BigDecimal("10"));
        final AllowanceCharge allowance = new AllowanceCharge();
        allowance.setChargeIndicator(false);
        allowance.setAllowanceChargeReasonCode(EAllowanceChargeReasonCode21._19.getID());
        allowance.setAllowanceChargeReason(EAllowanceChargeReasonCode21._19.getDisplayName());
        allowance.setMultiplierFactorNumeric(new BigDecimal("0.1"));
        allowance.setBaseAmount(new BigDecimal("100"));
        allowance.setAmount(new BigDecimal("10"));
        invoice.setAllowanceCharges(List.of(charge, allowance));

        invoice.setLineExtensionAmount(new BigDecimal("100"));
        invoice.setTaxExclusiveAmount(new BigDecimal("100"));
        invoice.setTaxInclusiveAmount(new BigDecimal("119"));
        invoice.setAllowanceTotalAmount(new BigDecimal("10"));
        invoice.setChargeTotalAmount(new BigDecimal("10"));
        invoice.setPrepaidAmount(new BigDecimal("50"));
        invoice.setPayableAmount(new BigDecimal("79"));

        final Party supplier = new Party();
        supplier.setTaxId("RO1485236");
        supplier.setBusinessName("Colibri - Linic");
        supplier.setRegistrationName("LINIC SRL");
        supplier.setRegistrationId("J05/1001/2002");
        supplier.setCompanyLegalForm("Capital social 200 RON");
        supplier.setContactName("Groza Danut");
        supplier.setTelephone("0259xxx");
        supplier.setElectronicMail("linic@gmail.com");
        final Address supplierAddress = new Address();
        supplierAddress.setCountry(ECountryIdentificationCode21.RO.getID());
        supplierAddress.setCountrySubentity("RO-BH");
        supplierAddress.setCity("MARGHITA");
        supplierAddress.setPostalZone("415300");
        supplierAddress.setPrimaryLine("Balcescu 51");
        supplierAddress.setSecondaryLine("Vladimirescu 59");
        supplier.setPostalAddress(supplierAddress);
        invoice.setAccountingSupplier(supplier);
        invoice.setPayeeParty(supplier);

        final Party customer = new Party();
        customer.setTaxId("RO148");
        customer.setRegistrationName("CLIENT SRL");
        customer.setRegistrationId("J05/10/2023");
        customer.setContactName("Delegat");
        customer.setTelephone("0745154xxx");
        final Address customerAddress = new Address();
        customerAddress.setCountry(ECountryIdentificationCode21.RO.getID());
        customerAddress.setCountrySubentity("RO-BH");
        customerAddress.setCity("MARGHITA");
        customerAddress.setPostalZone("415300");
        customerAddress.setPrimaryLine("Revolutiei");
        customer.setPostalAddress(customerAddress);
        invoice.setAccountingCustomer(customer);

        final InvoiceLine invoiceLine = new InvoiceLine();
        invoiceLine.setId(1L);
        invoiceLine.setSellersItemIdentification("399");
        invoiceLine.setBuyersItemIdentification("598751225812");
        invoiceLine.setNote("Test note");
        invoiceLine.setDescription("Super fluffly paper");
        invoiceLine.setName("Toilet paper 3ply");
        invoiceLine.setQuantity(new BigDecimal("11"));
        invoiceLine.setUom(EUnitOfMeasureCode21.C62.getID());
        invoiceLine.setClassifiedTaxCategory(taxCategory);
        invoiceLine.setPrice(new BigDecimal("10"));
        invoiceLine.setBaseQuantity(new BigDecimal("1"));
        invoiceLine.setLineExtensionAmount(new BigDecimal("100"));
        invoiceLine.setTaxAmount(new BigDecimal("19"));
        invoiceLine.setAllowanceCharges(List.of(allowance));
        invoice.setLines(List.of(invoiceLine));

        // when
        when:
        final InvoiceType ublInvoice = InvoiceMapper.INSTANCE.toUblInvoice(invoice);

        // then
        then:
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getCustomizationIDValue()).isEqualTo("urn:cen.eu:en16931:2017#compliant#urn:efactura.mfinante.ro:CIUS-RO:1.0.1");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceTypeCodeValue()).isEqualTo("380");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getIDValue()).isEqualTo("LIND100");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getIssueDateValueLocal()).isEqualTo(LocalDate.of(2023, 10, 20));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getDueDateValueLocal()).isEqualTo(LocalDate.of(2023, 11, 19));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getDocumentCurrencyCodeValue()).isEqualTo(ECurrencyCode21.RON.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getNoteCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getNoteAtIndex(0).getValue()).isEqualTo("Textual note");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPaymentMeansCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPaymentMeansAtIndex(0).getPaymentMeansCodeValue()).isEqualTo(EPaymentMeansCode21._30.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPaymentMeansAtIndex(0).getPaymentIDCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPaymentMeansAtIndex(0).getPaymentIDAtIndex(0).getValue()).isEqualTo("FF_LIND100/2023-10-20");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPaymentMeansAtIndex(0).getPayeeFinancialAccount().getIDValue()).isEqualTo("RO0123456xxx");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPaymentMeansAtIndex(0).getPayeeFinancialAccount().getNameValue()).isEqualTo("LINIC SRL");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPaymentMeansAtIndex(0).getPayeeFinancialAccount().getFinancialInstitutionBranch().getIDValue()).isEqualTo("BT");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPaymentMeansAtIndex(0).getPayeeFinancialAccount().getCurrencyCodeValue()).isEqualTo(ECurrencyCode21.RON.getID());

        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxCurrencyCodeValue()).isEqualTo(ECurrencyCode21.RON.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxAmountValue()).isEqualByComparingTo(new BigDecimal("19"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxAmount().getCurrencyID()).isEqualTo(ECurrencyCode21.RON.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxSubtotalCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxSubtotalAtIndex(0).getTaxableAmountValue()).isEqualByComparingTo(new BigDecimal("100"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxSubtotalAtIndex(0).getTaxableAmount().getCurrencyID()).isEqualTo(ECurrencyCode21.RON.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxSubtotalAtIndex(0).getTaxAmountValue()).isEqualByComparingTo(new BigDecimal("19"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxSubtotalAtIndex(0).getTaxAmount().getCurrencyID()).isEqualTo(ECurrencyCode21.RON.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxSubtotalAtIndex(0).getTaxCategory().getIDValue()).isEqualTo("S");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxSubtotalAtIndex(0).getTaxCategory().getPercentValue()).isEqualByComparingTo(new BigDecimal("19"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxSubtotalAtIndex(0).getTaxCategory().getTaxExemptionReasonCount()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxSubtotalAtIndex(0).getTaxCategory().getTaxExemptionReasonCode()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxSubtotalAtIndex(0).getTaxCategory().getTaxScheme().getIDValue()).isEqualTo("VAT");

        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeCount()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(0).getChargeIndicator().isValue()).isTrue();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(0).getAllowanceChargeReasonCodeValue()).isEqualTo(EAllowanceChargeReasonCode21.ZZZ.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(0).getAllowanceChargeReasonCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(0).getAllowanceChargeReasonAtIndex(0).getValue()).isEqualTo(EAllowanceChargeReasonCode21.ZZZ.getDisplayName());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(0).getMultiplierFactorNumericValue()).isEqualByComparingTo(new BigDecimal("0.1"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(0).getBaseAmountValue()).isEqualByComparingTo(new BigDecimal("100"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(0).getBaseAmount().getCurrencyID()).isEqualTo(ECurrencyCode21.RON.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(0).getAmountValue()).isEqualByComparingTo(new BigDecimal("10"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(0).getAmount().getCurrencyID()).isEqualTo(ECurrencyCode21.RON.getID());

        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(1).getChargeIndicator().isValue()).isFalse();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(1).getAllowanceChargeReasonCodeValue()).isEqualTo(EAllowanceChargeReasonCode21._19.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(1).getAllowanceChargeReasonCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(1).getAllowanceChargeReasonAtIndex(0).getValue()).isEqualTo(EAllowanceChargeReasonCode21._19.getDisplayName());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(1).getMultiplierFactorNumericValue()).isEqualByComparingTo(new BigDecimal("0.1"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(1).getBaseAmountValue()).isEqualByComparingTo(new BigDecimal("100"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(1).getBaseAmount().getCurrencyID()).isEqualTo(ECurrencyCode21.RON.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(1).getAmountValue()).isEqualByComparingTo(new BigDecimal("10"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(1).getAmount().getCurrencyID()).isEqualTo(ECurrencyCode21.RON.getID());

        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getLineExtensionAmountValue()).isEqualByComparingTo(new BigDecimal("100"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getLineExtensionAmount().getCurrencyID()).isEqualTo(ECurrencyCode21.RON.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getTaxExclusiveAmountValue()).isEqualByComparingTo(new BigDecimal("100"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getTaxExclusiveAmount().getCurrencyID()).isEqualTo(ECurrencyCode21.RON.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getTaxInclusiveAmountValue()).isEqualByComparingTo(new BigDecimal("119"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getTaxInclusiveAmount().getCurrencyID()).isEqualTo(ECurrencyCode21.RON.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getAllowanceTotalAmountValue()).isEqualByComparingTo(new BigDecimal("10"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getAllowanceTotalAmount().getCurrencyID()).isEqualTo(ECurrencyCode21.RON.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getChargeTotalAmountValue()).isEqualByComparingTo(new BigDecimal("10"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getChargeTotalAmount().getCurrencyID()).isEqualTo(ECurrencyCode21.RON.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getPrepaidAmountValue()).isEqualByComparingTo(new BigDecimal("50"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getPrepaidAmount().getCurrencyID()).isEqualTo(ECurrencyCode21.RON.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getPayableRoundingAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getPayableAmountValue()).isEqualByComparingTo(new BigDecimal("79"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getPayableAmount().getCurrencyID()).isEqualTo(ECurrencyCode21.RON.getID());

        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getPartyTaxSchemeCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getPartyTaxSchemeAtIndex(0).getCompanyIDValue()).isEqualTo("RO1485236");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getPartyTaxSchemeAtIndex(0).getTaxScheme().getIDValue()).isEqualTo("VAT");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getPartyNameCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getPartyNameAtIndex(0).getNameValue()).isEqualTo("Colibri - Linic");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getPartyLegalEntityCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getPartyLegalEntityAtIndex(0).getRegistrationNameValue()).isEqualTo("LINIC SRL");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getPartyLegalEntityAtIndex(0).getCompanyIDValue()).isEqualTo("J05/1001/2002");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getPartyLegalEntityAtIndex(0).getCompanyLegalFormValue()).isEqualTo("Capital social 200 RON");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getPostalAddress().getCountry().getIdentificationCodeValue()).isEqualTo(ECountryIdentificationCode21.RO.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getPostalAddress().getCountrySubentityValue()).isEqualTo("RO-BH");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getPostalAddress().getCityNameValue()).isEqualTo("MARGHITA");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getPostalAddress().getPostalZoneValue()).isEqualTo("415300");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getPostalAddress().getStreetNameValue()).isEqualTo("Balcescu 51");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getPostalAddress().getAdditionalStreetNameValue()).isEqualTo("Vladimirescu 59");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getContact().getNameValue()).isEqualTo("Groza Danut");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getContact().getTelephoneValue()).isEqualTo("0259xxx");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getContact().getElectronicMailValue()).isEqualTo("linic@gmail.com");

        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingCustomerParty().getParty().getPartyTaxSchemeCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingCustomerParty().getParty().getPartyTaxSchemeAtIndex(0).getCompanyIDValue()).isEqualTo("RO148");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingCustomerParty().getParty().getPartyTaxSchemeAtIndex(0).getTaxScheme().getIDValue()).isEqualTo("VAT");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingCustomerParty().getParty().getPartyNameCount()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingCustomerParty().getParty().getPartyLegalEntityCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingCustomerParty().getParty().getPartyLegalEntityAtIndex(0).getRegistrationNameValue()).isEqualTo("CLIENT SRL");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingCustomerParty().getParty().getPartyLegalEntityAtIndex(0).getCompanyIDValue()).isEqualTo("J05/10/2023");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingCustomerParty().getParty().getPostalAddress().getCountry().getIdentificationCodeValue()).isEqualTo(ECountryIdentificationCode21.RO.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingCustomerParty().getParty().getPostalAddress().getCountrySubentityValue()).isEqualTo("RO-BH");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingCustomerParty().getParty().getPostalAddress().getCityNameValue()).isEqualTo("MARGHITA");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingCustomerParty().getParty().getPostalAddress().getPostalZoneValue()).isEqualTo("415300");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingCustomerParty().getParty().getPostalAddress().getStreetNameValue()).isEqualTo("Revolutiei");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingCustomerParty().getParty().getContact().getNameValue()).isEqualTo("Delegat");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingCustomerParty().getParty().getContact().getTelephoneValue()).isEqualTo("0745154xxx");

        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getPartyTaxSchemeCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getPartyTaxSchemeAtIndex(0).getCompanyIDValue()).isEqualTo("RO1485236");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getPartyTaxSchemeAtIndex(0).getTaxScheme().getIDValue()).isEqualTo("VAT");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getPartyNameCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getPartyNameAtIndex(0).getNameValue()).isEqualTo("Colibri - Linic");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getPartyLegalEntityCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getPartyLegalEntityAtIndex(0).getRegistrationNameValue()).isEqualTo("LINIC SRL");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getPartyLegalEntityAtIndex(0).getCompanyIDValue()).isEqualTo("J05/1001/2002");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getPartyLegalEntityAtIndex(0).getCompanyLegalFormValue()).isEqualTo("Capital social 200 RON");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getPostalAddress().getCountry().getIdentificationCodeValue()).isEqualTo(ECountryIdentificationCode21.RO.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getPostalAddress().getCountrySubentityValue()).isEqualTo("RO-BH");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getPostalAddress().getCityNameValue()).isEqualTo("MARGHITA");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getPostalAddress().getPostalZoneValue()).isEqualTo("415300");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getPostalAddress().getStreetNameValue()).isEqualTo("Balcescu 51");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getPostalAddress().getAdditionalStreetNameValue()).isEqualTo("Vladimirescu 59");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getContact().getNameValue()).isEqualTo("Groza Danut");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getContact().getTelephoneValue()).isEqualTo("0259xxx");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getContact().getElectronicMailValue()).isEqualTo("linic@gmail.com");

        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getIDValue()).isEqualTo("1");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getNoteCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getNoteAtIndex(0).getValue()).isEqualTo("Test note");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getInvoicedQuantityValue()).isEqualByComparingTo(new BigDecimal("11"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getInvoicedQuantity().getUnitCode()).isEqualTo(EUnitOfMeasureCode21.C62.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getItem().getSellersItemIdentification().getIDValue()).isEqualTo("399");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getItem().getBuyersItemIdentification().getIDValue()).isEqualTo("598751225812");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getItem().getDescriptionCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getItem().getDescriptionAtIndex(0).getValue()).isEqualTo("Super fluffly paper");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getItem().getNameValue()).isEqualTo("Toilet paper 3ply");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getItem().getClassifiedTaxCategoryCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getItem().getClassifiedTaxCategoryAtIndex(0).getIDValue()).isEqualTo("S");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getItem().getClassifiedTaxCategoryAtIndex(0).getPercentValue()).isEqualByComparingTo(new BigDecimal("19"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getItem().getClassifiedTaxCategoryAtIndex(0).getTaxScheme().getIDValue()).isEqualTo("VAT");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getPrice().getPriceAmountValue()).isEqualByComparingTo(new BigDecimal("10"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getPrice().getPriceAmount().getCurrencyID()).isEqualTo(ECurrencyCode21.RON.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getPrice().getBaseQuantityValue()).isEqualByComparingTo(new BigDecimal("1"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getPrice().getBaseQuantity().getUnitCode()).isEqualTo(EUnitOfMeasureCode21.C62.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getAllowanceChargeCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getAllowanceChargeAtIndex(0).getChargeIndicator().isValue()).isFalse();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getAllowanceChargeAtIndex(0).getAllowanceChargeReasonCodeValue()).isEqualTo(EAllowanceChargeReasonCode21._19.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getAllowanceChargeAtIndex(0).getAllowanceChargeReasonCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getAllowanceChargeAtIndex(0).getAllowanceChargeReasonAtIndex(0).getValue()).isEqualTo(EAllowanceChargeReasonCode21._19.getDisplayName());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getAllowanceChargeAtIndex(0).getMultiplierFactorNumericValue()).isEqualByComparingTo(new BigDecimal("0.1"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getAllowanceChargeAtIndex(0).getBaseAmountValue()).isEqualByComparingTo(new BigDecimal("100"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getAllowanceChargeAtIndex(0).getBaseAmount().getCurrencyID()).isEqualTo(ECurrencyCode21.RON.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getAllowanceChargeAtIndex(0).getAmountValue()).isEqualByComparingTo(new BigDecimal("10"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getAllowanceChargeAtIndex(0).getAmount().getCurrencyID()).isEqualTo(ECurrencyCode21.RON.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getLineExtensionAmountValue()).isEqualByComparingTo(new BigDecimal("100"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getLineExtensionAmount().getCurrencyID()).isEqualTo(ECurrencyCode21.RON.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getTaxTotalCount()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(UBL21Marshaller.invoice().isValid(ublInvoice)).isTrue();
    }

    def whenInvoiceIsEmpty_thenMapFieldsToNull() {
        given:
        final Invoice invoice = new Invoice();
        final FinancialAccount payeeFinancialAccount = new FinancialAccount();
        invoice.setPayeeFinancialAccount(payeeFinancialAccount);

        final TaxSubtotal taxSubtotal = new TaxSubtotal();
        invoice.setTaxSubtotals(List.of(taxSubtotal));

        final AllowanceCharge charge = new AllowanceCharge();
        invoice.setAllowanceCharges(List.of(charge));

        final Party supplier = new Party();
        final Address supplierAddress = new Address();
        supplier.setPostalAddress(supplierAddress);
        invoice.setAccountingSupplier(supplier);
        invoice.setPayeeParty(supplier);

        final Party customer = new Party();
        final Address customerAddress = new Address();
        customer.setPostalAddress(customerAddress);
        invoice.setAccountingCustomer(customer);

        final InvoiceLine invoiceLine = new InvoiceLine();
        invoiceLine.setAllowanceCharges(List.of(charge));
        invoice.setLines(List.of(invoiceLine));

        when:
        final InvoiceType ublInvoice = InvoiceMapper.INSTANCE.toUblInvoice(invoice);

        then:
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getCustomizationIDValue()).isEqualTo("urn:cen.eu:en16931:2017#compliant#urn:efactura.mfinante.ro:CIUS-RO:1.0.1");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceTypeCodeValue()).isEqualTo("380");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getID()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getIssueDate()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getDueDate()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getDocumentCurrencyCode()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getNoteCount()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPaymentMeansCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPaymentMeansAtIndex(0).getPaymentMeansCode()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPaymentMeansAtIndex(0).getPaymentIDCount()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPaymentMeansAtIndex(0).getPayeeFinancialAccount().getID()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPaymentMeansAtIndex(0).getPayeeFinancialAccount().getName()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPaymentMeansAtIndex(0).getPayeeFinancialAccount().getFinancialInstitutionBranch()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPaymentMeansAtIndex(0).getPayeeFinancialAccount().getCurrencyCode()).isNull();

        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxCurrencyCode()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxSubtotalCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxSubtotalAtIndex(0).getTaxableAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxSubtotalAtIndex(0).getTaxAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxSubtotalAtIndex(0).getTaxCategory()).isNull();

        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(0).getChargeIndicator()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(0).getAllowanceChargeReasonCode()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(0).getAllowanceChargeReasonCount()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(0).getMultiplierFactorNumeric()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(0).getBaseAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(0).getAmount()).isNull();

        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getLineExtensionAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getTaxExclusiveAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getTaxInclusiveAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getAllowanceTotalAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getChargeTotalAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getPrepaidAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getPayableRoundingAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getPayableAmount()).isNull();

        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getPartyTaxSchemeCount()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getPartyNameCount()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getPartyLegalEntityCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getPartyLegalEntityAtIndex(0).getRegistrationName()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getPartyLegalEntityAtIndex(0).getCompanyID()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getPartyLegalEntityAtIndex(0).getCompanyLegalForm()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getPostalAddress().getCountry().getIdentificationCode()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getPostalAddress().getCountrySubentity()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getPostalAddress().getCityName()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getPostalAddress().getPostalZone()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getPostalAddress().getStreetName()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getPostalAddress().getAdditionalStreetName()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getContact().getName()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getContact().getTelephone()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingSupplierParty().getParty().getContact().getElectronicMail()).isNull();

        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingCustomerParty().getParty().getPartyTaxSchemeCount()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingCustomerParty().getParty().getPartyNameCount()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingCustomerParty().getParty().getPartyLegalEntityCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingCustomerParty().getParty().getPartyLegalEntityAtIndex(0).getRegistrationName()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingCustomerParty().getParty().getPartyLegalEntityAtIndex(0).getCompanyID()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingCustomerParty().getParty().getPartyLegalEntityAtIndex(0).getCompanyLegalForm()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingCustomerParty().getParty().getPostalAddress().getCountry().getIdentificationCode()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingCustomerParty().getParty().getPostalAddress().getCountrySubentity()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingCustomerParty().getParty().getPostalAddress().getCityName()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingCustomerParty().getParty().getPostalAddress().getPostalZone()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingCustomerParty().getParty().getPostalAddress().getStreetName()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingCustomerParty().getParty().getContact().getName()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAccountingCustomerParty().getParty().getContact().getTelephone()).isNull();

        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getPartyTaxSchemeCount()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getPartyNameCount()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getPartyLegalEntityCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getPartyLegalEntityAtIndex(0).getRegistrationName()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getPartyLegalEntityAtIndex(0).getCompanyID()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getPartyLegalEntityAtIndex(0).getCompanyLegalForm()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getPostalAddress().getCountry().getIdentificationCode()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getPostalAddress().getCountrySubentity()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getPostalAddress().getCityName()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getPostalAddress().getPostalZone()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getPostalAddress().getStreetName()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getPostalAddress().getAdditionalStreetName()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getContact().getName()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getContact().getTelephone()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getPayeeParty().getContact().getElectronicMail()).isNull();

        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getID()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getNoteCount()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getInvoicedQuantity()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getItem().getSellersItemIdentification()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getItem().getBuyersItemIdentification()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getItem().getDescriptionCount()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getItem().getName()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getItem().getClassifiedTaxCategoryCount()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getPrice().getPriceAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getPrice().getBaseQuantity()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getAllowanceChargeCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getAllowanceChargeAtIndex(0).getChargeIndicator()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getAllowanceChargeAtIndex(0).getAllowanceChargeReasonCode()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getAllowanceChargeAtIndex(0).getAllowanceChargeReasonCount()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getAllowanceChargeAtIndex(0).getMultiplierFactorNumeric()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getAllowanceChargeAtIndex(0).getBaseAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getAllowanceChargeAtIndex(0).getAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getLineExtensionAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getTaxTotalCount()).isEqualTo(0);
    }

    def whenAmountsAreMissing_thenAmountTypesShouldBeNull() {
        given:
        final Invoice invoice = new Invoice();
        invoice.setId(1L);
        invoice.setDocumentCurrencyCode(ECurrencyCode21.RON.getID());
        invoice.setTaxAmount(new BigDecimal("19"));
        invoice.setTaxCurrencyCode(ECurrencyCode21.RON.getID());

        final TaxSubtotal taxSubtotal = new TaxSubtotal();
        taxSubtotal.setTaxAmount(new BigDecimal("19"));
        final TaxCategory taxCategory = new TaxCategory();
        taxCategory.setCode("S");
        taxCategory.setPercent(new BigDecimal("0.19"));
        taxCategory.setTaxScheme("VAT");
        taxSubtotal.setTaxCategory(taxCategory);
        invoice.setTaxSubtotals(List.of(taxSubtotal));

        final AllowanceCharge charge = new AllowanceCharge();
        charge.setChargeIndicator(true);
        final AllowanceCharge allowance = new AllowanceCharge();
        allowance.setChargeIndicator(false);
        invoice.setAllowanceCharges(List.of(charge, allowance));

        final InvoiceLine invoiceLine = new InvoiceLine();
        invoiceLine.setId(1L);
        invoiceLine.setAllowanceCharges(List.of(allowance));
        invoice.setLines(List.of(invoiceLine));

        when:
        final InvoiceType ublInvoice = InvoiceMapper.INSTANCE.toUblInvoice(invoice);

        then:
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getCustomizationIDValue()).isEqualTo("urn:cen.eu:en16931:2017#compliant#urn:efactura.mfinante.ro:CIUS-RO:1.0.1");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceTypeCodeValue()).isEqualTo("380");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getDocumentCurrencyCodeValue()).isEqualTo(ECurrencyCode21.RON.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxCurrencyCodeValue()).isEqualTo(ECurrencyCode21.RON.getID());

        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxAmountValue()).isEqualByComparingTo(new BigDecimal("19"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxAmount().getCurrencyID()).isEqualTo(ECurrencyCode21.RON.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxSubtotalCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxSubtotalAtIndex(0).getTaxableAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxSubtotalAtIndex(0).getTaxAmountValue()).isEqualByComparingTo(new BigDecimal("19"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxSubtotalAtIndex(0).getTaxAmount().getCurrencyID()).isEqualTo(ECurrencyCode21.RON.getID());
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxSubtotalAtIndex(0).getTaxCategory().getIDValue()).isEqualTo("S");
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxSubtotalAtIndex(0).getTaxCategory().getPercentValue()).isEqualByComparingTo(new BigDecimal("19"));
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxSubtotalAtIndex(0).getTaxCategory().getTaxExemptionReasonCount()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxSubtotalAtIndex(0).getTaxCategory().getTaxExemptionReasonCode()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getTaxTotalAtIndex(0).getTaxSubtotalAtIndex(0).getTaxCategory().getTaxScheme().getIDValue()).isEqualTo("VAT");

        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeCount()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(0).getChargeIndicator().isValue()).isTrue();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(0).getBaseAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(0).getAmount()).isNull();

        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(1).getChargeIndicator().isValue()).isFalse();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(1).getBaseAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getAllowanceChargeAtIndex(1).getAmount()).isNull();

        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getLineExtensionAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getTaxExclusiveAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getTaxInclusiveAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getAllowanceTotalAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getChargeTotalAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getPrepaidAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getPayableRoundingAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getLegalMonetaryTotal().getPayableAmount()).isNull();

        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getInvoicedQuantity()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getPrice().getPriceAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getPrice().getBaseQuantity()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getAllowanceChargeCount()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getAllowanceChargeAtIndex(0).getChargeIndicator().isValue()).isFalse();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getAllowanceChargeAtIndex(0).getBaseAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getAllowanceChargeAtIndex(0).getAmount()).isNull();
        org.assertj.core.api.Assertions.assertThat(ublInvoice.getInvoiceLineAtIndex(0).getLineExtensionAmount()).isNull();
    }
}
