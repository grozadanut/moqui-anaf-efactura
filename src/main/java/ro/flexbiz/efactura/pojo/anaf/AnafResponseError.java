package ro.flexbiz.efactura.pojo.anaf;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class AnafResponseError {
	@XmlAttribute(name = "errorMessage")
	private String message;

	public AnafResponseError() {
	}

	public AnafResponseError(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		AnafResponseError that = (AnafResponseError) o;
		return Objects.equals(message, that.message);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(message);
	}

	@Override
	public String toString() {
		return "AnafResponseError{" +
				"message='" + message + '\'' +
				'}';
	}
}
