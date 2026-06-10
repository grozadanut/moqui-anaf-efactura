package ro.flexbiz.efactura.mapper;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.unqualifieddatatypes_21.AmountType;
import oasis.names.specification.ubl.schema.xsd.unqualifieddatatypes_21.QuantityType;
import ro.flexbiz.efactura.mapper.impl.InvoiceDeleteEmptyMapperImpl;
import ro.flexbiz.efactura.mapper.impl.InvoiceMapperImpl;
import ro.flexbiz.efactura.pojo.Invoice;
import ro.flexbiz.efactura.pojo.InvoiceLine;
import ro.flexbiz.efactura.pojo.Party;
import ro.flexbiz.efactura.util.StringUtils;

import java.util.function.Consumer;

public interface InvoiceDeleteEmptyMapper {
    InvoiceDeleteEmptyMapper INSTANCE = new InvoiceDeleteEmptyMapperImpl();

    private static <T> void deleteIfEmpty(final AmountType amount, final Consumer<T> deleteCallback) {
        if (amount == null)
            return;

        if (amount.getValue() == null)
            deleteCallback.accept(null);
    }

    private static <T> void deleteIfEmpty(final QuantityType quantity, final Consumer<T> deleteCallback) {
        if (quantity == null)
            return;

        if (quantity.getValue() == null)
            deleteCallback.accept(null);
    }

    default void mapTaxTotalType_deleteIfEmpty(final Invoice inv, final TaxTotalType taxTotalType) {
        deleteIfEmpty(taxTotalType.getTaxAmount(), (Consumer<TaxAmountType>) taxTotalType::setTaxAmount);
    }

    default void invoiceToMonetaryTotalType_deleteIfEmpty(final Invoice inv, final MonetaryTotalType monetaryTotalType) {
        deleteIfEmpty(monetaryTotalType.getLineExtensionAmount(),
                (Consumer<LineExtensionAmountType>) monetaryTotalType::setLineExtensionAmount);
        deleteIfEmpty(monetaryTotalType.getTaxExclusiveAmount(),
                (Consumer<TaxExclusiveAmountType>) monetaryTotalType::setTaxExclusiveAmount);
        deleteIfEmpty(monetaryTotalType.getTaxInclusiveAmount(),
                (Consumer<TaxInclusiveAmountType>) monetaryTotalType::setTaxInclusiveAmount);
        deleteIfEmpty(monetaryTotalType.getAllowanceTotalAmount(),
                (Consumer<AllowanceTotalAmountType>) monetaryTotalType::setAllowanceTotalAmount);
        deleteIfEmpty(monetaryTotalType.getChargeTotalAmount(),
                (Consumer<ChargeTotalAmountType>) monetaryTotalType::setChargeTotalAmount);
        deleteIfEmpty(monetaryTotalType.getPrepaidAmount(),
                (Consumer<PrepaidAmountType>) monetaryTotalType::setPrepaidAmount);
        deleteIfEmpty(monetaryTotalType.getPayableRoundingAmount(),
                (Consumer<PayableRoundingAmountType>) monetaryTotalType::setPayableRoundingAmount);
        deleteIfEmpty(monetaryTotalType.getPayableAmount(),
                (Consumer<PayableAmountType>) monetaryTotalType::setPayableAmount);
    }

    default void toInvoiceLine_deleteIfEmpty(final InvoiceLine line, final InvoiceLineType invoiceLineType) {
        deleteIfEmpty(invoiceLineType.getInvoicedQuantity(),
                (Consumer<InvoicedQuantityType>) invoiceLineType::setInvoicedQuantity);
    }

    default void invoiceLineToPriceType_deleteIfEmpty(final InvoiceLine line, final PriceType priceType) {
        deleteIfEmpty(priceType.getBaseQuantity(),
                (Consumer<BaseQuantityType>) priceType::setBaseQuantity);
    }

    default void toPartyType_deleteIfEmpty(final Party party, final PartyType partyType) {
        if (partyType.getContact() != null)
        {
            if (partyType.getContact().getName() != null &&
                    StringUtils.isBlank(partyType.getContact().getNameValue()))
                partyType.getContact().setName((NameType)null);

            if (partyType.getContact().getTelephone() != null &&
                    StringUtils.isBlank(partyType.getContact().getTelephoneValue()))
                partyType.getContact().setTelephone((TelephoneType)null);

            if (partyType.getContact().getElectronicMail() != null &&
                    StringUtils.isBlank(partyType.getContact().getElectronicMailValue()))
                partyType.getContact().setElectronicMail((ElectronicMailType)null);
        }
    }
}
