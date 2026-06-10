package ro.flexbiz.efactura.mapper.impl;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.BranchType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ItemIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.*;
import ro.flexbiz.efactura.mapper.InvoiceTypePrimitiveMapper;

import java.math.BigDecimal;
import java.time.Instant;

//@Generated(
//        value = "org.mapstruct.ap.MappingProcessor",
//        date = "2026-05-29T14:08:33+0300",
//        comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.39.0.v20240820-0604, environment: Java 21.0.4 (Eclipse Adoptium)"
//)
public class InvoiceTypePrimitiveMapperImpl implements InvoiceTypePrimitiveMapper {

    @Override
    public IDType mapIDType(String value) {
        if ( value == null ) {
            return null;
        }

        IDType iDType = new IDType();

        if ( isNotEmpty( value ) ) {
            iDType.setValue( value );
        }

        return iDType;
    }

    @Override
    public IssueDateType mapIssueDateType(Instant value) {
        if ( value == null ) {
            return null;
        }

        IssueDateType issueDateType = new IssueDateType();

        issueDateType.setValue( mapXMLOffsetDate( value ) );

        return issueDateType;
    }

    @Override
    public DueDateType mapDueDateType(Instant value) {
        if ( value == null ) {
            return null;
        }

        DueDateType dueDateType = new DueDateType();

        dueDateType.setValue( mapXMLOffsetDate( value ) );

        return dueDateType;
    }

    @Override
    public DocumentCurrencyCodeType mapDocumentCurrencyCodeType(String value) {
        if ( value == null ) {
            return null;
        }

        DocumentCurrencyCodeType documentCurrencyCodeType = new DocumentCurrencyCodeType();

        if ( isNotEmpty( value ) ) {
            documentCurrencyCodeType.setValue( value );
        }

        return documentCurrencyCodeType;
    }

    @Override
    public TaxCurrencyCodeType mapTaxCurrencyCodeType(String value) {
        if ( value == null ) {
            return null;
        }

        TaxCurrencyCodeType taxCurrencyCodeType = new TaxCurrencyCodeType();

        if ( isNotEmpty( value ) ) {
            taxCurrencyCodeType.setValue( value );
        }

        return taxCurrencyCodeType;
    }

    @Override
    public PaymentMeansCodeType mapPaymentMeansCodeType(String value) {
        if ( value == null ) {
            return null;
        }

        PaymentMeansCodeType paymentMeansCodeType = new PaymentMeansCodeType();

        if ( isNotEmpty( value ) ) {
            paymentMeansCodeType.setValue( value );
        }

        return paymentMeansCodeType;
    }

    @Override
    public NameType mapNameType(String value) {
        if ( value == null ) {
            return null;
        }

        NameType nameType = new NameType();

        if ( isNotEmpty( value ) ) {
            nameType.setValue( value );
        }

        return nameType;
    }

    @Override
    public CurrencyCodeType mapCurrencyCodeType(String value) {
        if ( value == null ) {
            return null;
        }

        CurrencyCodeType currencyCodeType = new CurrencyCodeType();

        if ( isNotEmpty( value ) ) {
            currencyCodeType.setValue( value );
        }

        return currencyCodeType;
    }

    @Override
    public BranchType mapBranchType(String id) {
        if ( id == null ) {
            return null;
        }

        BranchType branchType = new BranchType();

        if ( isNotEmpty( id ) ) {
            branchType.setID( mapIDType( id ) );
        }

        return branchType;
    }

    @Override
    public PartyNameType mapPartyNameType(String value) {
        if ( value == null ) {
            return null;
        }

        PartyNameType partyNameType = new PartyNameType();

        if ( isNotEmpty( value ) ) {
            partyNameType.setName( stringToNameType( value ) );
        }

        return partyNameType;
    }

    @Override
    public PaymentIDType mapPaymentIDType(String value) {
        if ( value == null ) {
            return null;
        }

        PaymentIDType paymentIDType = new PaymentIDType();

        if ( isNotEmpty( value ) ) {
            paymentIDType.setValue( value );
        }

        return paymentIDType;
    }

    @Override
    public TaxExemptionReasonType mapTaxExemptionReasonType(String value) {
        if ( value == null ) {
            return null;
        }

        TaxExemptionReasonType taxExemptionReasonType = new TaxExemptionReasonType();

        if ( isNotEmpty( value ) ) {
            taxExemptionReasonType.setValue( value );
        }

        return taxExemptionReasonType;
    }

    @Override
    public DescriptionType mapDescriptionType(String value) {
        if ( value == null ) {
            return null;
        }

        DescriptionType descriptionType = new DescriptionType();

        if ( isNotEmpty( value ) ) {
            descriptionType.setValue( value );
        }

        return descriptionType;
    }

    @Override
    public AllowanceChargeReasonType mapAllowanceChargeReasonType(String value) {
        if ( value == null ) {
            return null;
        }

        AllowanceChargeReasonType allowanceChargeReasonType = new AllowanceChargeReasonType();

        if ( isNotEmpty( value ) ) {
            allowanceChargeReasonType.setValue( value );
        }

        return allowanceChargeReasonType;
    }

    @Override
    public TaxableAmountType mapTaxableAmountType(BigDecimal value) {
        if ( value == null ) {
            return null;
        }

        TaxableAmountType taxableAmountType = new TaxableAmountType();

        taxableAmountType.setValue( value );

        return taxableAmountType;
    }

    @Override
    public TaxAmountType mapTaxAmountType(BigDecimal value) {
        if ( value == null ) {
            return null;
        }

        TaxAmountType taxAmountType = new TaxAmountType();

        taxAmountType.setValue( value );

        return taxAmountType;
    }

    @Override
    public ChargeIndicatorType mapChargeIndicatorType(Boolean value) {
        if ( value == null ) {
            return null;
        }

        ChargeIndicatorType chargeIndicatorType = new ChargeIndicatorType();

        if ( value != null ) {
            chargeIndicatorType.setValue( value );
        }

        return chargeIndicatorType;
    }

    @Override
    public AllowanceChargeReasonCodeType mapAllowanceChargeReasonCodeType(String value) {
        if ( value == null ) {
            return null;
        }

        AllowanceChargeReasonCodeType allowanceChargeReasonCodeType = new AllowanceChargeReasonCodeType();

        if ( isNotEmpty( value ) ) {
            allowanceChargeReasonCodeType.setValue( value );
        }

        return allowanceChargeReasonCodeType;
    }

    @Override
    public MultiplierFactorNumericType mapMultiplierFactorNumericType(BigDecimal value) {
        if ( value == null ) {
            return null;
        }

        MultiplierFactorNumericType multiplierFactorNumericType = new MultiplierFactorNumericType();

        multiplierFactorNumericType.setValue( value );

        return multiplierFactorNumericType;
    }

    @Override
    public AmountType mapAmountType(BigDecimal value) {
        if ( value == null ) {
            return null;
        }

        AmountType amountType = new AmountType();

        amountType.setValue( value );

        return amountType;
    }

    @Override
    public BaseAmountType mapBaseAmountType(BigDecimal value) {
        if ( value == null ) {
            return null;
        }

        BaseAmountType baseAmountType = new BaseAmountType();

        baseAmountType.setValue( value );

        return baseAmountType;
    }

    @Override
    public RegistrationNameType mapRegistrationNameType(String value) {
        if ( value == null ) {
            return null;
        }

        RegistrationNameType registrationNameType = new RegistrationNameType();

        if ( isNotEmpty( value ) ) {
            registrationNameType.setValue( value );
        }

        return registrationNameType;
    }

    @Override
    public CompanyIDType mapCompanyIDType(String value) {
        if ( value == null ) {
            return null;
        }

        CompanyIDType companyIDType = new CompanyIDType();

        if ( isNotEmpty( value ) ) {
            companyIDType.setValue( value );
        }

        return companyIDType;
    }

    @Override
    public CompanyLegalFormType mapCompanyLegalFormType(String value) {
        if ( value == null ) {
            return null;
        }

        CompanyLegalFormType companyLegalFormType = new CompanyLegalFormType();

        if ( isNotEmpty( value ) ) {
            companyLegalFormType.setValue( value );
        }

        return companyLegalFormType;
    }

    @Override
    public IdentificationCodeType mapIdentificationCodeType(String value) {
        if ( value == null ) {
            return null;
        }

        IdentificationCodeType identificationCodeType = new IdentificationCodeType();

        if ( isNotEmpty( value ) ) {
            identificationCodeType.setValue( value );
        }

        return identificationCodeType;
    }

    @Override
    public CountrySubentityType mapCountrySubentityType(String value) {
        if ( value == null ) {
            return null;
        }

        CountrySubentityType countrySubentityType = new CountrySubentityType();

        if ( isNotEmpty( value ) ) {
            countrySubentityType.setValue( value );
        }

        return countrySubentityType;
    }

    @Override
    public CityNameType mapCityNameType(String value) {
        if ( value == null ) {
            return null;
        }

        CityNameType cityNameType = new CityNameType();

        if ( isNotEmpty( value ) ) {
            cityNameType.setValue( value );
        }

        return cityNameType;
    }

    @Override
    public PostalZoneType mapPostalZoneType(String value) {
        if ( value == null ) {
            return null;
        }

        PostalZoneType postalZoneType = new PostalZoneType();

        if ( isNotEmpty( value ) ) {
            postalZoneType.setValue( value );
        }

        return postalZoneType;
    }

    @Override
    public StreetNameType mapStreetNameType(String value) {
        if ( value == null ) {
            return null;
        }

        StreetNameType streetNameType = new StreetNameType();

        if ( isNotEmpty( value ) ) {
            streetNameType.setValue( value );
        }

        return streetNameType;
    }

    @Override
    public AdditionalStreetNameType mapAdditionalStreetNameType(String value) {
        if ( value == null ) {
            return null;
        }

        AdditionalStreetNameType additionalStreetNameType = new AdditionalStreetNameType();

        if ( isNotEmpty( value ) ) {
            additionalStreetNameType.setValue( value );
        }

        return additionalStreetNameType;
    }

    @Override
    public TelephoneType mapTelephoneType(String value) {
        if ( value == null ) {
            return null;
        }

        TelephoneType telephoneType = new TelephoneType();

        if ( isNotEmpty( value ) ) {
            telephoneType.setValue( value );
        }

        return telephoneType;
    }

    @Override
    public ElectronicMailType mapElectronicMailType(String value) {
        if ( value == null ) {
            return null;
        }

        ElectronicMailType electronicMailType = new ElectronicMailType();

        if ( isNotEmpty( value ) ) {
            electronicMailType.setValue( value );
        }

        return electronicMailType;
    }

    @Override
    public ItemIdentificationType mapItemIdentificationType(String id) {
        if ( id == null ) {
            return null;
        }

        ItemIdentificationType itemIdentificationType = new ItemIdentificationType();

        if ( isNotEmpty( id ) ) {
            itemIdentificationType.setID( mapIDType( id ) );
        }

        return itemIdentificationType;
    }

    @Override
    public PriceAmountType mapPriceAmountType(BigDecimal value) {
        if ( value == null ) {
            return null;
        }

        PriceAmountType priceAmountType = new PriceAmountType();

        priceAmountType.setValue( value );

        return priceAmountType;
    }

    @Override
    public LineExtensionAmountType mapLineExtensionAmountType(BigDecimal value) {
        if ( value == null ) {
            return null;
        }

        LineExtensionAmountType lineExtensionAmountType = new LineExtensionAmountType();

        lineExtensionAmountType.setValue( value );

        return lineExtensionAmountType;
    }

    protected NameType stringToNameType(String string) {
        if ( string == null ) {
            return null;
        }

        NameType nameType = new NameType();

        if ( isNotEmpty( string ) ) {
            nameType.setValue( string );
        }

        return nameType;
    }
}
