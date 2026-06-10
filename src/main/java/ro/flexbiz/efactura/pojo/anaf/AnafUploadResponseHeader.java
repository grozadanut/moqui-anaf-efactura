package ro.flexbiz.efactura.pojo.anaf;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@XmlRootElement(name = "header", namespace = AnafUploadResponseHeader.NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
public class AnafUploadResponseHeader {
    public static final String NAMESPACE = "mfp:anaf:dgti:spv:respUploadFisier:v1";
    private static final String STATUS_OK = "0";

    @XmlAttribute(name = "ExecutionStatus")
    private String executionStatus;
    @XmlAttribute(name = "index_incarcare", required = false)
    private String uploadIndex;
    @XmlElement(name = "Errors", namespace = NAMESPACE)
    private List<AnafResponseError> errors = new ArrayList<>();

    public boolean isExecutionStatusOk() {
        return STATUS_OK.equalsIgnoreCase(executionStatus);
    }

	public String getExecutionStatus() {
		return executionStatus;
	}

	public void setExecutionStatus(String executionStatus) {
		this.executionStatus = executionStatus;
	}

	public String getUploadIndex() {
		return uploadIndex;
	}

	public void setUploadIndex(String uploadIndex) {
		this.uploadIndex = uploadIndex;
	}

	public List<AnafResponseError> getErrors() {
		return errors;
	}

	public void setErrors(List<AnafResponseError> errors) {
		this.errors = errors;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		AnafUploadResponseHeader that = (AnafUploadResponseHeader) o;
		return Objects.equals(executionStatus, that.executionStatus) && Objects.equals(uploadIndex, that.uploadIndex) && Objects.equals(errors, that.errors);
	}

	@Override
	public int hashCode() {
		return Objects.hash(executionStatus, uploadIndex, errors);
	}

	@Override
	public String toString() {
		return "AnafUploadResponseHeader{" +
				"executionStatus='" + executionStatus + '\'' +
				", uploadIndex='" + uploadIndex + '\'' +
				", errors=" + errors +
				'}';
	}
}
