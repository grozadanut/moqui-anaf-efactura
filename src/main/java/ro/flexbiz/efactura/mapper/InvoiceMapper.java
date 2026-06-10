package ro.flexbiz.efactura.mapper;

import com.helger.ubl21.codelist.ECountryIdentificationCode21;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.CompanyIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.NoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import oasis.names.specification.ubl.schema.xsd.unqualifieddatatypes_21.AmountType;
import ro.flexbiz.efactura.mapper.impl.InvoiceMapperImpl;
import ro.flexbiz.efactura.pojo.*;
import ro.flexbiz.efactura.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface InvoiceMapper {
    InvoiceMapper INSTANCE = new InvoiceMapperImpl();

    static void removeCurrencyIfAmountNull(final AmountType amountType) {
        Optional.ofNullable(amountType)
                .filter(at -> at.getValue() == null)
                .ifPresent(at -> at.setCurrencyID(null));
    }

    InvoiceType toUblInvoice(Invoice inv);
    InvoiceLineType toInvoiceLine(InvoiceLine line);
    ItemType mapItemType(InvoiceLine line);
    AllowanceChargeType toAllowanceCharge(AllowanceCharge allCh);
    SupplierPartyType toSupplierParty(Party party);
    CustomerPartyType toCustomerParty(Party party);
    PartyType toPartyType(Party party);
    AddressType toAddressType(Address address);
    PartyLegalEntityType mapPartyLegalEntityType(Party party);
    PartyTaxSchemeType mapPartyTaxSchemeType(String value);
    PaymentMeansType mapPaymentMeansType(Invoice inv);
    TaxTotalType mapTaxTotalType(Invoice inv);
    TaxSubtotalType mapTaxSubtotalType(TaxSubtotal subtotal);
    TaxCategoryType mapTaxCategoryType(TaxCategory taxCat);
    default BigDecimal displayPercentage(final BigDecimal percentage) {
        return percentage == null ? null : percentage.multiply(new BigDecimal("100"));
    }
    default List<NoteType> mapNoteTypes(final String value) {
        return value == null ? null : List.of(new NoteType(value));
    }

    default <T> List<T> listOf(final T t) {
        if (t == null)
            return List.of();
        return List.of(t);
    }
    default void enrichInvoiceTypeWithCurrency(final Invoice inv, final InvoiceType invoiceType) {
        if (invoiceType == null)
            return;

        // InvoiceType toUblInvoice(Invoice inv);
        // @Mapping(target = "allowanceCharge.amount.currencyID", source = "inv.documentCurrencyCode")
        invoiceType.getAllowanceCharge().stream()
                .map(AllowanceChargeType::getAmount)
                .filter(Objects::nonNull)
                .filter(t -> t.getValue() != null)
                .forEach(amount -> amount.setCurrencyID(inv.getDocumentCurrencyCode()));
        // InvoiceType toUblInvoice(Invoice inv);
        // @Mapping(target = "allowanceCharge.baseAmount.currencyID", source = "inv.documentCurrencyCode")
        invoiceType.getAllowanceCharge().stream()
                .map(AllowanceChargeType::getBaseAmount)
                .filter(Objects::nonNull)
                .filter(t -> t.getValue() != null)
                .forEach(amount -> amount.setCurrencyID(inv.getDocumentCurrencyCode()));

        // InvoiceLineType toInvoiceLine(InvoiceLine line);
        // @Mapping(target = "lineExtensionAmount.currencyID", source = "inv.documentCurrencyCode")
        invoiceType.getInvoiceLine().stream()
                .map(InvoiceLineType::getLineExtensionAmount)
                .filter(Objects::nonNull)
                .filter(t -> t.getValue() != null)
                .forEach(amount -> amount.setCurrencyID(inv.getDocumentCurrencyCode()));
        // InvoiceLineType toInvoiceLine(InvoiceLine line);
        // @Mapping(target = "price.priceAmount.currencyID", source = "inv.documentCurrencyCode")
        invoiceType.getInvoiceLine().stream()
                .map(InvoiceLineType::getPrice)
                .filter(Objects::nonNull)
                .map(PriceType::getPriceAmount)
                .filter(Objects::nonNull)
                .filter(t -> t.getValue() != null)
                .forEach(amount -> amount.setCurrencyID(inv.getDocumentCurrencyCode()));
        // InvoiceLineType toInvoiceLine(InvoiceLine line);
        // @Mapping(target = "taxTotal.taxAmount.currencyID", source = "inv.taxCurrencyCode")
        invoiceType.getInvoiceLine().stream()
                .flatMap(ilt -> ilt.getTaxTotal().stream())
                .filter(Objects::nonNull)
                .map(TaxTotalType::getTaxAmount)
                .filter(Objects::nonNull)
                .filter(t -> t.getValue() != null)
                .forEach(amount -> amount.setCurrencyID(inv.getTaxCurrencyCode()));
        // InvoiceLineType toInvoiceLine(InvoiceLine line);
        // @Mapping(target = "allowanceCharge.amount.currencyID", source = "inv.documentCurrencyCode")
        invoiceType.getInvoiceLine().stream()
                .flatMap(ilt -> ilt.getAllowanceCharge().stream())
                .filter(Objects::nonNull)
                .map(AllowanceChargeType::getAmount)
                .filter(Objects::nonNull)
                .filter(t -> t.getValue() != null)
                .forEach(amount -> amount.setCurrencyID(inv.getDocumentCurrencyCode()));
        // InvoiceLineType toInvoiceLine(InvoiceLine line);
        // @Mapping(target = "allowanceCharge.baseAmount.currencyID", source = "inv.documentCurrencyCode")
        invoiceType.getInvoiceLine().stream()
                .flatMap(ilt -> ilt.getAllowanceCharge().stream())
                .filter(Objects::nonNull)
                .map(AllowanceChargeType::getBaseAmount)
                .filter(Objects::nonNull)
                .filter(t -> t.getValue() != null)
                .forEach(amount -> amount.setCurrencyID(inv.getDocumentCurrencyCode()));

        // fix for legalMonetaryTotal amounts
        // if the amount is null, the currency should also be null
        if (invoiceType != null && invoiceType.getLegalMonetaryTotal() != null)
        {
            removeCurrencyIfAmountNull(invoiceType.getLegalMonetaryTotal().getLineExtensionAmount());
            removeCurrencyIfAmountNull(invoiceType.getLegalMonetaryTotal().getTaxExclusiveAmount());
            removeCurrencyIfAmountNull(invoiceType.getLegalMonetaryTotal().getTaxInclusiveAmount());
            removeCurrencyIfAmountNull(invoiceType.getLegalMonetaryTotal().getAllowanceTotalAmount());
            removeCurrencyIfAmountNull(invoiceType.getLegalMonetaryTotal().getChargeTotalAmount());
            removeCurrencyIfAmountNull(invoiceType.getLegalMonetaryTotal().getPrepaidAmount());
            removeCurrencyIfAmountNull(invoiceType.getLegalMonetaryTotal().getPayableRoundingAmount());
            removeCurrencyIfAmountNull(invoiceType.getLegalMonetaryTotal().getPayableAmount());
        }
    }

    default void enrichTaxSubtotalTypeWithCurrency(final Invoice inv, final TaxTotalType taxTotalType) {
        if (taxTotalType == null)
            return;
        // TaxSubtotalType mapTaxSubtotalType(TaxSubtotal subtotal);
        // @Mapping(target = "taxableAmount.currencyID", source = "inv.taxCurrencyCode")
        taxTotalType.getTaxSubtotal().stream()
                .map(TaxSubtotalType::getTaxableAmount)
                .filter(Objects::nonNull)
                .filter(t -> t.getValue() != null)
                .forEach(taxableAmount -> taxableAmount.setCurrencyID(inv.getTaxCurrencyCode()));
        // @Mapping(target = "taxAmount.currencyID", source = "inv.taxCurrencyCode")
        taxTotalType.getTaxSubtotal().stream()
                .map(TaxSubtotalType::getTaxAmount)
                .filter(Objects::nonNull)
                .filter(t -> t.getValue() != null)
                .forEach(taxAmount -> taxAmount.setCurrencyID(inv.getTaxCurrencyCode()));
    }

    default void enrichPartyTaxIdWithCountryCode(final PartyTaxSchemeType partyTaxSchemeType) {
        if (partyTaxSchemeType == null)
            return;
        if (partyTaxSchemeType.getCompanyID() == null)
            return;

        final CompanyIDType companyIDType = partyTaxSchemeType.getCompanyID();
        final String companyIDValue = companyIDType.getValue();

        if (StringUtils.isNotBlank(companyIDValue))
            companyIDType.setValue(Character.isDigit(companyIDValue.charAt(0)) ? ECountryIdentificationCode21.RO.getID()+companyIDValue : companyIDValue);
    }
}
