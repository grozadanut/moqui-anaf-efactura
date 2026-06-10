package ro.flexbiz.efactura.entity;

import java.time.LocalDate;
import java.util.Objects;

public class ReceivedInvoice {
	private Long id;
	private String uploadIndex;
	private String downloadId;
	private String xmlRaw;
	private LocalDate issueDate;
	private Long invoiceId;

	public ReceivedInvoice(Long id, String uploadIndex, String downloadId, String xmlRaw, LocalDate issueDate, Long invoiceId) {
		this.id = id;
		this.uploadIndex = uploadIndex;
		this.downloadId = downloadId;
		this.xmlRaw = xmlRaw;
		this.issueDate = issueDate;
		this.invoiceId = invoiceId;
	}

	public ReceivedInvoice() {
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

	public Long getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		ReceivedInvoice that = (ReceivedInvoice) o;
		return Objects.equals(id, that.id) && Objects.equals(uploadIndex, that.uploadIndex) && Objects.equals(downloadId, that.downloadId) && Objects.equals(xmlRaw, that.xmlRaw) && Objects.equals(issueDate, that.issueDate) && Objects.equals(invoiceId, that.invoiceId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, uploadIndex, downloadId, xmlRaw, issueDate, invoiceId);
	}

	@Override
	public String toString() {
		return "ReceivedInvoice{" +
				"id=" + id +
				", uploadIndex='" + uploadIndex + '\'' +
				", downloadId='" + downloadId + '\'' +
				", xmlRaw='" + xmlRaw + '\'' +
				", issueDate=" + issueDate +
				", invoiceId=" + invoiceId +
				'}';
	}
}
