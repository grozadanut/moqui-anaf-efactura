package ro.flexbiz.efactura.mapper.impl;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import ro.flexbiz.efactura.mapper.InvoiceDeleteEmptyMapper;
import ro.flexbiz.efactura.mapper.InvoiceMapper;
import ro.flexbiz.efactura.mapper.InvoiceTypePrimitiveMapper;
import ro.flexbiz.efactura.pojo.*;

import java.util.ArrayList;
import java.util.List;

//@Generated(
//        value = "org.mapstruct.ap.MappingProcessor",
//        date = "2026-05-29T14:08:27+0300",
//        comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.39.0.v20240820-0604, environment: Java 21.0.4 (Eclipse Adoptium)"
//)
public class InvoiceMapperImpl implements InvoiceMapper {
    private final InvoiceTypePrimitiveMapper invoiceTypePrimitiveMapper = InvoiceTypePrimitiveMapper.INSTANCE;
    private final InvoiceDeleteEmptyMapper invoiceDeleteEmptyMapper = InvoiceDeleteEmptyMapper.INSTANCE;

    @Override
    public InvoiceType toUblInvoice(Invoice inv) {
        if ( inv == null ) {
            return null;
        }

        InvoiceType invoiceType = new InvoiceType();

        invoiceType.setLegalMonetaryTotal( invoiceToMonetaryTotalType( inv ) );
        if ( invoiceTypePrimitiveMapper.isNotEmpty( inv.getInvoiceNumber() ) ) {
            invoiceType.setID( invoiceTypePrimitiveMapper.mapIDType( inv.getInvoiceNumber() ) );
        }
        invoiceType.setIssueDate( invoiceTypePrimitiveMapper.mapIssueDateType( inv.getIssueDate() ) );
        invoiceType.setDueDate( invoiceTypePrimitiveMapper.mapDueDateType( inv.getDueDate() ) );
        invoiceType.setAccountingSupplierParty( toSupplierParty( inv.getAccountingSupplier() ) );
        invoiceType.setAccountingCustomerParty( toCustomerParty( inv.getAccountingCustomer() ) );
        invoiceType.setPayeeParty( toPartyType( inv.getPayeeParty() ) );
        if ( invoiceTypePrimitiveMapper.isNotEmpty( inv.getDocumentCurrencyCode() ) ) {
            invoiceType.setDocumentCurrencyCode( invoiceTypePrimitiveMapper.mapDocumentCurrencyCodeType( inv.getDocumentCurrencyCode() ) );
        }
        if ( invoiceTypePrimitiveMapper.isNotEmpty( inv.getTaxCurrencyCode() ) ) {
            invoiceType.setTaxCurrencyCode( invoiceTypePrimitiveMapper.mapTaxCurrencyCodeType( inv.getTaxCurrencyCode() ) );
        }
        invoiceType.setAllowanceCharge( allowanceChargeListToAllowanceChargeTypeList( inv.getAllowanceCharges() ) );
        if ( invoiceTypePrimitiveMapper.isNotEmpty( inv.getNote() ) ) {
            invoiceType.setNote( mapNoteTypes( inv.getNote() ) );
        }
        invoiceType.setInvoiceLine( invoiceLineListToInvoiceLineTypeList( inv.getLines() ) );
        invoiceType.setCustomizationID( invoiceToCustomizationIDType( inv ) );
        invoiceType.setInvoiceTypeCode( invoiceToInvoiceTypeCodeType( inv ) );

        invoiceType.setPaymentMeans( listOf(mapPaymentMeansType(inv)) );
        invoiceType.setTaxTotal( listOf(mapTaxTotalType(inv)) );

        enrichInvoiceTypeWithCurrency( inv, invoiceType );

        return invoiceType;
    }

    @Override
    public InvoiceLineType toInvoiceLine(InvoiceLine line) {
        if ( line == null ) {
            return null;
        }

        InvoiceLineType invoiceLineType = new InvoiceLineType();

        invoiceLineType.setInvoicedQuantity( invoiceLineToInvoicedQuantityType( line ) );
        invoiceLineType.setPrice( invoiceLineToPriceType( line ) );
        if ( line.getId() != null ) {
            invoiceLineType.setID( invoiceTypePrimitiveMapper.mapIDType( String.valueOf( line.getId() ) ) );
        }
        if ( invoiceTypePrimitiveMapper.isNotEmpty( line.getNote() ) ) {
            invoiceLineType.setNote( mapNoteTypes( line.getNote() ) );
        }
        invoiceLineType.setLineExtensionAmount( invoiceTypePrimitiveMapper.mapLineExtensionAmountType( line.getLineExtensionAmount() ) );
        invoiceLineType.setItem( mapItemType( line ) );
        invoiceLineType.setAllowanceCharge( allowanceChargeListToAllowanceChargeTypeList( line.getAllowanceCharges() ) );

        invoiceDeleteEmptyMapper.toInvoiceLine_deleteIfEmpty( line, invoiceLineType );

        return invoiceLineType;
    }

    @Override
    public ItemType mapItemType(InvoiceLine line) {
        if ( line == null ) {
            return null;
        }

        ItemType itemType = new ItemType();

        if ( invoiceTypePrimitiveMapper.isNotEmpty( line.getName() ) ) {
            itemType.setName( invoiceTypePrimitiveMapper.mapNameType( line.getName() ) );
        }
        if ( invoiceTypePrimitiveMapper.isNotEmpty( line.getBuyersItemIdentification() ) ) {
            itemType.setBuyersItemIdentification( invoiceTypePrimitiveMapper.mapItemIdentificationType( line.getBuyersItemIdentification() ) );
        }
        if ( invoiceTypePrimitiveMapper.isNotEmpty( line.getSellersItemIdentification() ) ) {
            itemType.setSellersItemIdentification( invoiceTypePrimitiveMapper.mapItemIdentificationType( line.getSellersItemIdentification() ) );
        }

        itemType.setDescription( listOf(invoiceTypePrimitiveMapper.mapDescriptionType(line.getDescription())) );
        itemType.setClassifiedTaxCategory( listOf(mapTaxCategoryType(line.getClassifiedTaxCategory())) );

        return itemType;
    }

    @Override
    public AllowanceChargeType toAllowanceCharge(AllowanceCharge allCh) {
        if ( allCh == null ) {
            return null;
        }

        AllowanceChargeType allowanceChargeType = new AllowanceChargeType();

        allowanceChargeType.setChargeIndicator( invoiceTypePrimitiveMapper.mapChargeIndicatorType( allCh.getChargeIndicator() ) );
        if ( invoiceTypePrimitiveMapper.isNotEmpty( allCh.getAllowanceChargeReasonCode() ) ) {
            allowanceChargeType.setAllowanceChargeReasonCode( invoiceTypePrimitiveMapper.mapAllowanceChargeReasonCodeType( allCh.getAllowanceChargeReasonCode() ) );
        }
        allowanceChargeType.setMultiplierFactorNumeric( invoiceTypePrimitiveMapper.mapMultiplierFactorNumericType( allCh.getMultiplierFactorNumeric() ) );
        allowanceChargeType.setAmount( invoiceTypePrimitiveMapper.mapAmountType( allCh.getAmount() ) );
        allowanceChargeType.setBaseAmount( invoiceTypePrimitiveMapper.mapBaseAmountType( allCh.getBaseAmount() ) );

        allowanceChargeType.setAllowanceChargeReason( listOf(invoiceTypePrimitiveMapper.mapAllowanceChargeReasonType(allCh.getAllowanceChargeReason())) );

        return allowanceChargeType;
    }

    @Override
    public SupplierPartyType toSupplierParty(Party party) {
        if ( party == null ) {
            return null;
        }

        SupplierPartyType supplierPartyType = new SupplierPartyType();

        supplierPartyType.setParty( toPartyType( party ) );

        return supplierPartyType;
    }

    @Override
    public CustomerPartyType toCustomerParty(Party party) {
        if ( party == null ) {
            return null;
        }

        CustomerPartyType customerPartyType = new CustomerPartyType();

        customerPartyType.setParty( toPartyType( party ) );

        return customerPartyType;
    }

    @Override
    public PartyType toPartyType(Party party) {
        if ( party == null ) {
            return null;
        }

        PartyType partyType = new PartyType();

        partyType.setContact( partyToContactType( party ) );
        partyType.setPostalAddress( toAddressType( party.getPostalAddress() ) );

        partyType.setPartyTaxScheme( listOf(mapPartyTaxSchemeType(party.getTaxId())) );
        partyType.setPartyName( listOf(invoiceTypePrimitiveMapper.mapPartyNameType(party.getBusinessName())) );
        partyType.setPartyLegalEntity( listOf(mapPartyLegalEntityType(party)) );

        invoiceDeleteEmptyMapper.toPartyType_deleteIfEmpty( party, partyType );

        return partyType;
    }

    @Override
    public AddressType toAddressType(Address address) {
        if ( address == null ) {
            return null;
        }

        AddressType addressType = new AddressType();

        addressType.setCountry( addressToCountryType( address ) );
        if ( invoiceTypePrimitiveMapper.isNotEmpty( address.getCountrySubentity() ) ) {
            addressType.setCountrySubentity( invoiceTypePrimitiveMapper.mapCountrySubentityType( address.getCountrySubentity() ) );
        }
        if ( invoiceTypePrimitiveMapper.isNotEmpty( address.getCity() ) ) {
            addressType.setCityName( invoiceTypePrimitiveMapper.mapCityNameType( address.getCity() ) );
        }
        if ( invoiceTypePrimitiveMapper.isNotEmpty( address.getPostalZone() ) ) {
            addressType.setPostalZone( invoiceTypePrimitiveMapper.mapPostalZoneType( address.getPostalZone() ) );
        }
        if ( invoiceTypePrimitiveMapper.isNotEmpty( address.getPrimaryLine() ) ) {
            addressType.setStreetName( invoiceTypePrimitiveMapper.mapStreetNameType( address.getPrimaryLine() ) );
        }
        if ( invoiceTypePrimitiveMapper.isNotEmpty( address.getSecondaryLine() ) ) {
            addressType.setAdditionalStreetName( invoiceTypePrimitiveMapper.mapAdditionalStreetNameType( address.getSecondaryLine() ) );
        }

        return addressType;
    }

    @Override
    public PartyLegalEntityType mapPartyLegalEntityType(Party party) {
        if ( party == null ) {
            return null;
        }

        PartyLegalEntityType partyLegalEntityType = new PartyLegalEntityType();

        if ( invoiceTypePrimitiveMapper.isNotEmpty( party.getRegistrationName() ) ) {
            partyLegalEntityType.setRegistrationName( invoiceTypePrimitiveMapper.mapRegistrationNameType( party.getRegistrationName() ) );
        }
        if ( invoiceTypePrimitiveMapper.isNotEmpty( party.getRegistrationId() ) ) {
            partyLegalEntityType.setCompanyID( invoiceTypePrimitiveMapper.mapCompanyIDType( party.getRegistrationId() ) );
        }
        if ( invoiceTypePrimitiveMapper.isNotEmpty( party.getCompanyLegalForm() ) ) {
            partyLegalEntityType.setCompanyLegalForm( invoiceTypePrimitiveMapper.mapCompanyLegalFormType( party.getCompanyLegalForm() ) );
        }

        return partyLegalEntityType;
    }

    @Override
    public PartyTaxSchemeType mapPartyTaxSchemeType(String value) {
        if ( value == null ) {
            return null;
        }

        PartyTaxSchemeType partyTaxSchemeType = new PartyTaxSchemeType();

        if ( invoiceTypePrimitiveMapper.isNotEmpty( value ) ) {
            partyTaxSchemeType.setCompanyID( stringToCompanyIDType( value ) );
        }
        if ( invoiceTypePrimitiveMapper.isNotEmpty( value ) ) {
            partyTaxSchemeType.setTaxScheme( stringToTaxSchemeType( value ) );
        }

        enrichPartyTaxIdWithCountryCode( partyTaxSchemeType );

        return partyTaxSchemeType;
    }

    @Override
    public PaymentMeansType mapPaymentMeansType(Invoice inv) {
        if ( inv == null ) {
            return null;
        }

        PaymentMeansType paymentMeansType = new PaymentMeansType();

        paymentMeansType.setPayeeFinancialAccount( financialAccountToFinancialAccountType( inv.getPayeeFinancialAccount() ) );
        if ( invoiceTypePrimitiveMapper.isNotEmpty( inv.getPaymentMeansCode() ) ) {
            paymentMeansType.setPaymentMeansCode( invoiceTypePrimitiveMapper.mapPaymentMeansCodeType( inv.getPaymentMeansCode() ) );
        }

        paymentMeansType.setPaymentID( listOf(invoiceTypePrimitiveMapper.mapPaymentIDType(inv.getPaymentId())) );

        return paymentMeansType;
    }

    @Override
    public TaxTotalType mapTaxTotalType(Invoice inv) {
        if ( inv == null ) {
            return null;
        }

        TaxTotalType taxTotalType = new TaxTotalType();

        taxTotalType.setTaxAmount( invoiceToTaxAmountType( inv ) );
        taxTotalType.setTaxSubtotal( taxSubtotalListToTaxSubtotalTypeList( inv.getTaxSubtotals() ) );

        enrichTaxSubtotalTypeWithCurrency( inv, taxTotalType );
        invoiceDeleteEmptyMapper.mapTaxTotalType_deleteIfEmpty( inv, taxTotalType );

        return taxTotalType;
    }

    @Override
    public TaxSubtotalType mapTaxSubtotalType(TaxSubtotal subtotal) {
        if ( subtotal == null ) {
            return null;
        }

        TaxSubtotalType taxSubtotalType = new TaxSubtotalType();

        taxSubtotalType.setTaxableAmount( invoiceTypePrimitiveMapper.mapTaxableAmountType( subtotal.getTaxableAmount() ) );
        taxSubtotalType.setTaxAmount( invoiceTypePrimitiveMapper.mapTaxAmountType( subtotal.getTaxAmount() ) );
        taxSubtotalType.setTaxCategory( mapTaxCategoryType( subtotal.getTaxCategory() ) );

        return taxSubtotalType;
    }

    @Override
    public TaxCategoryType mapTaxCategoryType(TaxCategory taxCat) {
        if ( taxCat == null ) {
            return null;
        }

        TaxCategoryType taxCategoryType = new TaxCategoryType();

        taxCategoryType.setPercent( taxCategoryToPercentType( taxCat ) );
        taxCategoryType.setTaxScheme( taxCategoryToTaxSchemeType( taxCat ) );
        if ( invoiceTypePrimitiveMapper.isNotEmpty( taxCat.getCode() ) ) {
            taxCategoryType.setID( invoiceTypePrimitiveMapper.mapIDType( taxCat.getCode() ) );
        }

        taxCategoryType.setTaxExemptionReason( listOf(invoiceTypePrimitiveMapper.mapTaxExemptionReasonType(taxCat.getTaxExemptionReason())) );

        return taxCategoryType;
    }

    protected LineExtensionAmountType invoiceToLineExtensionAmountType(Invoice invoice) {
        if ( invoice == null ) {
            return null;
        }

        LineExtensionAmountType lineExtensionAmountType = new LineExtensionAmountType();

        lineExtensionAmountType.setValue( invoice.getLineExtensionAmount() );
        if ( invoiceTypePrimitiveMapper.isNotEmpty( invoice.getDocumentCurrencyCode() ) ) {
            lineExtensionAmountType.setCurrencyID( invoice.getDocumentCurrencyCode() );
        }

        return lineExtensionAmountType;
    }

    protected TaxExclusiveAmountType invoiceToTaxExclusiveAmountType(Invoice invoice) {
        if ( invoice == null ) {
            return null;
        }

        TaxExclusiveAmountType taxExclusiveAmountType = new TaxExclusiveAmountType();

        taxExclusiveAmountType.setValue( invoice.getTaxExclusiveAmount() );
        if ( invoiceTypePrimitiveMapper.isNotEmpty( invoice.getDocumentCurrencyCode() ) ) {
            taxExclusiveAmountType.setCurrencyID( invoice.getDocumentCurrencyCode() );
        }

        return taxExclusiveAmountType;
    }

    protected TaxInclusiveAmountType invoiceToTaxInclusiveAmountType(Invoice invoice) {
        if ( invoice == null ) {
            return null;
        }

        TaxInclusiveAmountType taxInclusiveAmountType = new TaxInclusiveAmountType();

        taxInclusiveAmountType.setValue( invoice.getTaxInclusiveAmount() );
        if ( invoiceTypePrimitiveMapper.isNotEmpty( invoice.getDocumentCurrencyCode() ) ) {
            taxInclusiveAmountType.setCurrencyID( invoice.getDocumentCurrencyCode() );
        }

        return taxInclusiveAmountType;
    }

    protected AllowanceTotalAmountType invoiceToAllowanceTotalAmountType(Invoice invoice) {
        if ( invoice == null ) {
            return null;
        }

        AllowanceTotalAmountType allowanceTotalAmountType = new AllowanceTotalAmountType();

        allowanceTotalAmountType.setValue( invoice.getAllowanceTotalAmount() );
        if ( invoiceTypePrimitiveMapper.isNotEmpty( invoice.getDocumentCurrencyCode() ) ) {
            allowanceTotalAmountType.setCurrencyID( invoice.getDocumentCurrencyCode() );
        }

        return allowanceTotalAmountType;
    }

    protected ChargeTotalAmountType invoiceToChargeTotalAmountType(Invoice invoice) {
        if ( invoice == null ) {
            return null;
        }

        ChargeTotalAmountType chargeTotalAmountType = new ChargeTotalAmountType();

        chargeTotalAmountType.setValue( invoice.getChargeTotalAmount() );
        if ( invoiceTypePrimitiveMapper.isNotEmpty( invoice.getDocumentCurrencyCode() ) ) {
            chargeTotalAmountType.setCurrencyID( invoice.getDocumentCurrencyCode() );
        }

        return chargeTotalAmountType;
    }

    protected PrepaidAmountType invoiceToPrepaidAmountType(Invoice invoice) {
        if ( invoice == null ) {
            return null;
        }

        PrepaidAmountType prepaidAmountType = new PrepaidAmountType();

        prepaidAmountType.setValue( invoice.getPrepaidAmount() );
        if ( invoiceTypePrimitiveMapper.isNotEmpty( invoice.getDocumentCurrencyCode() ) ) {
            prepaidAmountType.setCurrencyID( invoice.getDocumentCurrencyCode() );
        }

        return prepaidAmountType;
    }

    protected PayableRoundingAmountType invoiceToPayableRoundingAmountType(Invoice invoice) {
        if ( invoice == null ) {
            return null;
        }

        PayableRoundingAmountType payableRoundingAmountType = new PayableRoundingAmountType();

        payableRoundingAmountType.setValue( invoice.getPayableRoundingAmount() );
        if ( invoiceTypePrimitiveMapper.isNotEmpty( invoice.getDocumentCurrencyCode() ) ) {
            payableRoundingAmountType.setCurrencyID( invoice.getDocumentCurrencyCode() );
        }

        return payableRoundingAmountType;
    }

    protected PayableAmountType invoiceToPayableAmountType(Invoice invoice) {
        if ( invoice == null ) {
            return null;
        }

        PayableAmountType payableAmountType = new PayableAmountType();

        payableAmountType.setValue( invoice.getPayableAmount() );
        if ( invoiceTypePrimitiveMapper.isNotEmpty( invoice.getDocumentCurrencyCode() ) ) {
            payableAmountType.setCurrencyID( invoice.getDocumentCurrencyCode() );
        }

        return payableAmountType;
    }

    protected MonetaryTotalType invoiceToMonetaryTotalType(Invoice invoice) {
        if ( invoice == null ) {
            return null;
        }

        MonetaryTotalType monetaryTotalType = new MonetaryTotalType();

        monetaryTotalType.setLineExtensionAmount( invoiceToLineExtensionAmountType( invoice ) );
        monetaryTotalType.setTaxExclusiveAmount( invoiceToTaxExclusiveAmountType( invoice ) );
        monetaryTotalType.setTaxInclusiveAmount( invoiceToTaxInclusiveAmountType( invoice ) );
        monetaryTotalType.setAllowanceTotalAmount( invoiceToAllowanceTotalAmountType( invoice ) );
        monetaryTotalType.setChargeTotalAmount( invoiceToChargeTotalAmountType( invoice ) );
        monetaryTotalType.setPrepaidAmount( invoiceToPrepaidAmountType( invoice ) );
        monetaryTotalType.setPayableRoundingAmount( invoiceToPayableRoundingAmountType( invoice ) );
        monetaryTotalType.setPayableAmount( invoiceToPayableAmountType( invoice ) );

        invoiceDeleteEmptyMapper.invoiceToMonetaryTotalType_deleteIfEmpty( invoice, monetaryTotalType );

        return monetaryTotalType;
    }

    protected List<AllowanceChargeType> allowanceChargeListToAllowanceChargeTypeList(List<AllowanceCharge> list) {
        if ( list == null ) {
            return null;
        }

        List<AllowanceChargeType> list1 = new ArrayList<AllowanceChargeType>( list.size() );
        for ( AllowanceCharge allowanceCharge : list ) {
            list1.add( toAllowanceCharge( allowanceCharge ) );
        }

        return list1;
    }

    protected List<InvoiceLineType> invoiceLineListToInvoiceLineTypeList(List<InvoiceLine> list) {
        if ( list == null ) {
            return null;
        }

        List<InvoiceLineType> list1 = new ArrayList<InvoiceLineType>( list.size() );
        for ( InvoiceLine invoiceLine : list ) {
            list1.add( toInvoiceLine( invoiceLine ) );
        }

        return list1;
    }

    protected CustomizationIDType invoiceToCustomizationIDType(Invoice invoice) {
        if ( invoice == null ) {
            return null;
        }

        CustomizationIDType customizationIDType = new CustomizationIDType();

        customizationIDType.setValue( "urn:cen.eu:en16931:2017#compliant#urn:efactura.mfinante.ro:CIUS-RO:1.0.1" );

        return customizationIDType;
    }

    protected InvoiceTypeCodeType invoiceToInvoiceTypeCodeType(Invoice invoice) {
        if ( invoice == null ) {
            return null;
        }

        InvoiceTypeCodeType invoiceTypeCodeType = new InvoiceTypeCodeType();

        invoiceTypeCodeType.setValue( "380" );

        return invoiceTypeCodeType;
    }

    protected InvoicedQuantityType invoiceLineToInvoicedQuantityType(InvoiceLine invoiceLine) {
        if ( invoiceLine == null ) {
            return null;
        }

        InvoicedQuantityType invoicedQuantityType = new InvoicedQuantityType();

        invoicedQuantityType.setValue( invoiceLine.getQuantity() );
        if ( invoiceTypePrimitiveMapper.isNotEmpty( invoiceLine.getUom() ) ) {
            invoicedQuantityType.setUnitCode( invoiceLine.getUom() );
        }

        return invoicedQuantityType;
    }

    protected BaseQuantityType invoiceLineToBaseQuantityType(InvoiceLine invoiceLine) {
        if ( invoiceLine == null ) {
            return null;
        }

        BaseQuantityType baseQuantityType = new BaseQuantityType();

        baseQuantityType.setValue( invoiceLine.getBaseQuantity() );
        if ( invoiceTypePrimitiveMapper.isNotEmpty( invoiceLine.getUom() ) ) {
            baseQuantityType.setUnitCode( invoiceLine.getUom() );
        }

        return baseQuantityType;
    }

    protected PriceType invoiceLineToPriceType(InvoiceLine invoiceLine) {
        if ( invoiceLine == null ) {
            return null;
        }

        PriceType priceType = new PriceType();

        priceType.setBaseQuantity( invoiceLineToBaseQuantityType( invoiceLine ) );
        priceType.setPriceAmount( invoiceTypePrimitiveMapper.mapPriceAmountType( invoiceLine.getPrice() ) );

        invoiceDeleteEmptyMapper.invoiceLineToPriceType_deleteIfEmpty( invoiceLine, priceType );

        return priceType;
    }

    protected ContactType partyToContactType(Party party) {
        if ( party == null ) {
            return null;
        }

        ContactType contactType = new ContactType();

        if ( invoiceTypePrimitiveMapper.isNotEmpty( party.getContactName() ) ) {
            contactType.setName( invoiceTypePrimitiveMapper.mapNameType( party.getContactName() ) );
        }
        if ( invoiceTypePrimitiveMapper.isNotEmpty( party.getTelephone() ) ) {
            contactType.setTelephone( invoiceTypePrimitiveMapper.mapTelephoneType( party.getTelephone() ) );
        }
        if ( invoiceTypePrimitiveMapper.isNotEmpty( party.getElectronicMail() ) ) {
            contactType.setElectronicMail( invoiceTypePrimitiveMapper.mapElectronicMailType( party.getElectronicMail() ) );
        }

        return contactType;
    }

    protected CountryType addressToCountryType(Address address) {
        if ( address == null ) {
            return null;
        }

        CountryType countryType = new CountryType();

        if ( invoiceTypePrimitiveMapper.isNotEmpty( address.getCountry() ) ) {
            countryType.setIdentificationCode( invoiceTypePrimitiveMapper.mapIdentificationCodeType( address.getCountry() ) );
        }

        return countryType;
    }

    protected CompanyIDType stringToCompanyIDType(String string) {
        if ( string == null ) {
            return null;
        }

        CompanyIDType companyIDType = new CompanyIDType();

        if ( invoiceTypePrimitiveMapper.isNotEmpty( string ) ) {
            companyIDType.setValue( string );
        }

        return companyIDType;
    }

    protected TaxSchemeType stringToTaxSchemeType(String string) {
        if ( string == null ) {
            return null;
        }

        TaxSchemeType taxSchemeType = new TaxSchemeType();

        taxSchemeType.setID( invoiceTypePrimitiveMapper.mapIDType( "VAT" ) );

        return taxSchemeType;
    }

    protected FinancialAccountType financialAccountToFinancialAccountType(FinancialAccount financialAccount) {
        if ( financialAccount == null ) {
            return null;
        }

        FinancialAccountType financialAccountType = new FinancialAccountType();

        if ( invoiceTypePrimitiveMapper.isNotEmpty( financialAccount.getId() ) ) {
            financialAccountType.setID( invoiceTypePrimitiveMapper.mapIDType( financialAccount.getId() ) );
        }
        if ( invoiceTypePrimitiveMapper.isNotEmpty( financialAccount.getName() ) ) {
            financialAccountType.setName( invoiceTypePrimitiveMapper.mapNameType( financialAccount.getName() ) );
        }
        if ( invoiceTypePrimitiveMapper.isNotEmpty( financialAccount.getFinancialInstitutionBranch() ) ) {
            financialAccountType.setFinancialInstitutionBranch( invoiceTypePrimitiveMapper.mapBranchType( financialAccount.getFinancialInstitutionBranch() ) );
        }
        if ( invoiceTypePrimitiveMapper.isNotEmpty( financialAccount.getCurrency() ) ) {
            financialAccountType.setCurrencyCode( invoiceTypePrimitiveMapper.mapCurrencyCodeType( financialAccount.getCurrency() ) );
        }

        return financialAccountType;
    }

    protected TaxAmountType invoiceToTaxAmountType(Invoice invoice) {
        if ( invoice == null ) {
            return null;
        }

        TaxAmountType taxAmountType = new TaxAmountType();

        taxAmountType.setValue( invoice.getTaxAmount() );
        if ( invoiceTypePrimitiveMapper.isNotEmpty( invoice.getTaxCurrencyCode() ) ) {
            taxAmountType.setCurrencyID( invoice.getTaxCurrencyCode() );
        }

        return taxAmountType;
    }

    protected List<TaxSubtotalType> taxSubtotalListToTaxSubtotalTypeList(List<TaxSubtotal> list) {
        if ( list == null ) {
            return null;
        }

        List<TaxSubtotalType> list1 = new ArrayList<TaxSubtotalType>( list.size() );
        for ( TaxSubtotal taxSubtotal : list ) {
            list1.add( mapTaxSubtotalType( taxSubtotal ) );
        }

        return list1;
    }

    protected PercentType taxCategoryToPercentType(TaxCategory taxCategory) {
        if ( taxCategory == null ) {
            return null;
        }

        PercentType percentType = new PercentType();

        percentType.setValue( displayPercentage( taxCategory.getPercent() ) );

        return percentType;
    }

    protected TaxSchemeType taxCategoryToTaxSchemeType(TaxCategory taxCategory) {
        if ( taxCategory == null ) {
            return null;
        }

        TaxSchemeType taxSchemeType = new TaxSchemeType();

        if ( invoiceTypePrimitiveMapper.isNotEmpty( taxCategory.getTaxScheme() ) ) {
            taxSchemeType.setID( invoiceTypePrimitiveMapper.mapIDType( taxCategory.getTaxScheme() ) );
        }

        return taxSchemeType;
    }
}
