package ro.flexbiz.efactura.entity;

import java.time.LocalDate;
import java.util.Objects;

public class ReceivedCreditNote {
	private Long id;
	private String uploadIndex;
	private String downloadId;
	private String xmlRaw;
	private LocalDate issueDate;

	public ReceivedCreditNote(Long id, String uploadIndex, String downloadId, String xmlRaw, LocalDate issueDate) {
		this.id = id;
		this.uploadIndex = uploadIndex;
		this.downloadId = downloadId;
		this.xmlRaw = xmlRaw;
		this.issueDate = issueDate;
	}

	public ReceivedCreditNote() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUploadIndex() {
		return uploadIndex;
	}

	public void setUploadIndex(String uploadIndex) {
		this.uploadIndex = uploadIndex;
	}

	public String getDownloadId() {
		return downloadId;
	}

	public void setDownloadId(String downloadId) {
		this.downloadId = downloadId;
	}

	public String getXmlRaw() {
		return xmlRaw;
	}

	public void setXmlRaw(String xmlRaw) {
		this.xmlRaw = xmlRaw;
	}

	public LocalDate getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(LocalDate issueDate) {
		this.issueDate = issueDate;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		ReceivedCreditNote that = (ReceivedCreditNote) o;
		return Objects.equals(id, that.id) && Objects.equals(uploadIndex, that.uploadIndex) && Objects.equals(downloadId, that.downloadId) && Objects.equals(xmlRaw, that.xmlRaw) && Objects.equals(issueDate, that.issueDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, uploadIndex, downloadId, xmlRaw, issueDate);
	}

	@Override
	public String toString() {
		return "ReceivedCreditNote{" +
				"id=" + id +
				", uploadIndex='" + uploadIndex + '\'' +
				", downloadId='" + downloadId + '\'' +
				", xmlRaw='" + xmlRaw + '\'' +
				", issueDate=" + issueDate +
				'}';
	}
}
