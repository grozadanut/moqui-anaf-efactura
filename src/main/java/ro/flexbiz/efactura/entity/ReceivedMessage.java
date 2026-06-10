package ro.flexbiz.efactura.entity;

import ro.flexbiz.efactura.pojo.anaf.AnafReceivedMessage;

import java.time.LocalDateTime;
import java.util.Objects;

public class ReceivedMessage {
	private Long id;
	private LocalDateTime creationDate;
	private String taxId;
	private String uploadIndex;
	private String details;
	private AnafReceivedMessage.AnafReceivedMessageType messageType;

	public ReceivedMessage(Long id, LocalDateTime creationDate, String taxId, String uploadIndex, String details, AnafReceivedMessage.AnafReceivedMessageType messageType) {
		this.id = id;
		this.creationDate = creationDate;
		this.taxId = taxId;
		this.uploadIndex = uploadIndex;
		this.details = details;
		this.messageType = messageType;
	}

	public ReceivedMessage() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	public String getTaxId() {
		return taxId;
	}

	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}

	public String getUploadIndex() {
		return uploadIndex;
	}

	public void setUploadIndex(String uploadIndex) {
		this.uploadIndex = uploadIndex;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public AnafReceivedMessage.AnafReceivedMessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(AnafReceivedMessage.AnafReceivedMessageType messageType) {
		this.messageType = messageType;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		ReceivedMessage that = (ReceivedMessage) o;
		return Objects.equals(id, that.id) && Objects.equals(creationDate, that.creationDate) && Objects.equals(taxId, that.taxId) && Objects.equals(uploadIndex, that.uploadIndex) && Objects.equals(details, that.details) && messageType == that.messageType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, creationDate, taxId, uploadIndex, details, messageType);
	}

	@Override
	public String toString() {
		return "ReceivedMessage{" +
				"id=" + id +
				", creationDate=" + creationDate +
				", taxId='" + taxId + '\'' +
				", uploadIndex='" + uploadIndex + '\'' +
				", details='" + details + '\'' +
				", messageType=" + messageType +
				'}';
	}
}
