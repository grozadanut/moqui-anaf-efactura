package ro.flexbiz.efactura.pojo.anaf;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.LocalDateTime;
import java.util.Objects;

public class AnafReceivedMessage {
	private String id;
	@JsonProperty("data_creare")
	@JsonDeserialize(using = AnafDateDeserializer.class)
	private LocalDateTime creationDate;
	@JsonProperty("cif")
	private String taxId;
	@JsonProperty("id_solicitare")
	private String uploadIndex;
	@JsonProperty("detalii")
	private String details;
	@JsonProperty("tip")
	private AnafReceivedMessageType messageType;
	
	public enum AnafReceivedMessageType {
		@JsonProperty("FACTURA PRIMITA")
		BILL_RECEIVED,
		@JsonProperty("FACTURA TRIMISA")
		BILL_SENT,
		@JsonProperty("ERORI FACTURA")
		BILL_ERRORS,
		@JsonProperty("MESAJ CUMPARATOR TRANSMIS")
		BUYER_MESSAGE;
	}

	public AnafReceivedMessage() {
	}

	public AnafReceivedMessage(String id, LocalDateTime creationDate, String taxId, String uploadIndex,
							   String details, AnafReceivedMessageType messageType) {
		this.id = id;
		this.creationDate = creationDate;
		this.taxId = taxId;
		this.uploadIndex = uploadIndex;
		this.details = details;
		this.messageType = messageType;
	}

	public String getId() {
		return id;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public String getTaxId() {
		return taxId;
	}

	public String getUploadIndex() {
		return uploadIndex;
	}

	public String getDetails() {
		return details;
	}

	public AnafReceivedMessageType getMessageType() {
		return messageType;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}

	public void setUploadIndex(String uploadIndex) {
		this.uploadIndex = uploadIndex;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public void setMessageType(AnafReceivedMessageType messageType) {
		this.messageType = messageType;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		AnafReceivedMessage that = (AnafReceivedMessage) o;
		return Objects.equals(id, that.id) && Objects.equals(creationDate, that.creationDate) && Objects.equals(taxId, that.taxId) && Objects.equals(uploadIndex, that.uploadIndex) && Objects.equals(details, that.details) && messageType == that.messageType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, creationDate, taxId, uploadIndex, details, messageType);
	}

	@Override
	public String toString() {
		return "AnafReceivedMessage{" +
				"id='" + id + '\'' +
				", creationDate=" + creationDate +
				", taxId='" + taxId + '\'' +
				", uploadIndex='" + uploadIndex + '\'' +
				", details='" + details + '\'' +
				", messageType=" + messageType +
				'}';
	}
}
