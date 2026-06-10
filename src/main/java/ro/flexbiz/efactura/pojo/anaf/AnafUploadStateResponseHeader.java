package ro.flexbiz.efactura.pojo.anaf;

import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@XmlRootElement(name = "header", namespace = AnafUploadStateResponseHeader.NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
public class AnafUploadStateResponseHeader {
	public static final String NAMESPACE = "mfp:anaf:dgti:efactura:stareMesajFactura:v1";
	public static final String OK_STATE = "ok";
	public static final String NOK_STATE = "nok";
	public static final String PENDING_STATE = "in prelucrare";
	
	@XmlAttribute(name = "stare", required = false)
	private String state;
	@XmlAttribute(name = "id_descarcare", required = false)
	private String downloadId;
	@XmlElement(name = "Errors", namespace = NAMESPACE)
    private List<AnafResponseError> errors = new ArrayList<>();
	
	public boolean isStateOk() {
		return OK_STATE.equalsIgnoreCase(state);
	}
	
	public boolean isStateNok() {
		return NOK_STATE.equalsIgnoreCase(state);
	}
	
	public boolean isStatePending() {
		return PENDING_STATE.equalsIgnoreCase(state);
	}
	
	public String prettyErrorMessage() {
		if (isStateOk())
			return "";
		
		if (!errors.isEmpty())
			return errors.stream()
					.map(AnafResponseError::getMessage)
					.collect(Collectors.joining(System.lineSeparator()));
		
		return state;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDownloadId() {
		return downloadId;
	}

	public void setDownloadId(String downloadId) {
		this.downloadId = downloadId;
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
		AnafUploadStateResponseHeader that = (AnafUploadStateResponseHeader) o;
		return Objects.equals(state, that.state) && Objects.equals(downloadId, that.downloadId) && Objects.equals(errors, that.errors);
	}

	@Override
	public int hashCode() {
		return Objects.hash(state, downloadId, errors);
	}

	@Override
	public String toString() {
		return "AnafUploadStateResponseHeader{" +
				"state='" + state + '\'' +
				", downloadId='" + downloadId + '\'' +
				", errors=" + errors +
				'}';
	}
}
