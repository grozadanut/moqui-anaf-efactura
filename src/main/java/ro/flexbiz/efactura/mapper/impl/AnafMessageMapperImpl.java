package ro.flexbiz.efactura.mapper.impl;

import ro.flexbiz.efactura.entity.ReceivedMessage;
import ro.flexbiz.efactura.mapper.AnafMessageMapper;
import ro.flexbiz.efactura.pojo.anaf.AnafReceivedMessage;

//@Generated(
//        value = "org.mapstruct.ap.MappingProcessor",
//        date = "2026-05-29T14:08:39+0300",
//        comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.39.0.v20240820-0604, environment: Java 21.0.4 (Eclipse Adoptium)"
//)
public class AnafMessageMapperImpl implements AnafMessageMapper {

    @Override
    public ReceivedMessage toEntity(AnafReceivedMessage message) {
        if ( message == null ) {
            return null;
        }

        ReceivedMessage receivedMessage = new ReceivedMessage();

        receivedMessage.setCreationDate( message.getCreationDate() );
        receivedMessage.setDetails( message.getDetails() );
        if ( message.getId() != null ) {
            receivedMessage.setId( message.getId() );
        }
        receivedMessage.setMessageType( message.getMessageType() );
        receivedMessage.setTaxId( message.getTaxId() );
        receivedMessage.setUploadIndex( message.getUploadIndex() );

        return receivedMessage;
    }
}
