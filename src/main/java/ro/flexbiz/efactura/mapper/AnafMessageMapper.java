package ro.flexbiz.efactura.mapper;

import ro.flexbiz.efactura.entity.ReceivedMessage;
import ro.flexbiz.efactura.mapper.impl.AnafMessageMapperImpl;
import ro.flexbiz.efactura.mapper.impl.InvoiceMapperImpl;
import ro.flexbiz.efactura.pojo.anaf.AnafReceivedMessage;

public interface AnafMessageMapper {
    AnafMessageMapper INSTANCE = new AnafMessageMapperImpl();

    ReceivedMessage toEntity(AnafReceivedMessage message);
}