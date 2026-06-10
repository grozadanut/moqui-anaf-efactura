package ro.flexbiz.efactura.mapper;

import com.helger.commons.datetime.XMLOffsetDate;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.BranchType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ItemIdentificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyNameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.*;
import ro.flexbiz.efactura.mapper.impl.InvoiceTypePrimitiveMapperImpl;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public interface InvoiceTypePrimitiveMapper {
    InvoiceTypePrimitiveMapper INSTANCE = new InvoiceTypePrimitiveMapperImpl();

    IDType mapIDType(String value);
    IssueDateType mapIssueDateType(Instant value);
    DueDateType mapDueDateType(Instant value);
    DocumentCurrencyCodeType mapDocumentCurrencyCodeType(String value);
    TaxCurrencyCodeType mapTaxCurrencyCodeType(String value);
    PaymentMeansCodeType mapPaymentMeansCodeType(String value);
    NameType mapNameType(String value);
    CurrencyCodeType mapCurrencyCodeType(String value);
    BranchType mapBranchType(String id);
    PartyNameType mapPartyNameType(String value);
    PaymentIDType mapPaymentIDType(String value);
    TaxExemptionReasonType mapTaxExemptionReasonType(String value);
    DescriptionType mapDescriptionType(String value);
    AllowanceChargeReasonType mapAllowanceChargeReasonType(String value);
    TaxableAmountType mapTaxableAmountType(BigDecimal value);
    TaxAmountType mapTaxAmountType(BigDecimal value);
    ChargeIndicatorType mapChargeIndicatorType(Boolean value);
    AllowanceChargeReasonCodeType mapAllowanceChargeReasonCodeType(String value);
    MultiplierFactorNumericType mapMultiplierFactorNumericType(BigDecimal value);
    AmountType mapAmountType(BigDecimal value);
    BaseAmountType mapBaseAmountType(BigDecimal value);
    RegistrationNameType mapRegistrationNameType(String value);
    CompanyIDType mapCompanyIDType(String value);
    CompanyLegalFormType mapCompanyLegalFormType(String value);
    IdentificationCodeType mapIdentificationCodeType(String value);
    CountrySubentityType mapCountrySubentityType(String value);
    CityNameType mapCityNameType(String value);
    PostalZoneType mapPostalZoneType(String value);
    StreetNameType mapStreetNameType(String value);
    AdditionalStreetNameType mapAdditionalStreetNameType(String value);
    TelephoneType mapTelephoneType(String value);
    ElectronicMailType mapElectronicMailType(String value);
    ItemIdentificationType mapItemIdentificationType(String id);
    PriceAmountType mapPriceAmountType(BigDecimal value);
    LineExtensionAmountType mapLineExtensionAmountType(BigDecimal value);

    default XMLOffsetDate mapXMLOffsetDate(final Instant value) {
        return value == null ? null : XMLOffsetDate.of(LocalDate.ofInstant(value, ZoneId.of("Europe/Bucharest")));
    }

    default boolean isNotEmpty(final String value) {
        return value != null && !value.isBlank();
    }
}
