package ro.flexbiz.efactura.pojo.anaf;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AnafReceivedMessages {
	public static final String NO_MESSAGES_ERROR = "No messages found";
	public static final String TOO_MANY_MESSAGES_ERROR = "Too many messages found. Use pagination!";
	
	@JsonProperty("mesaje")
	private List<AnafReceivedMessage> messages = new ArrayList<>();
	@JsonDeserialize(using = ErrorDeserializer.class)
	@JsonProperty("eroare")
	private String error;
	
	private static class ErrorDeserializer extends StringDeserializer {
		private static final long serialVersionUID = 1L;
		
		@Override
		public String deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
			final String value = super.deserialize(p, ctxt);
			
			if (value != null && value.toLowerCase().startsWith("nu exista mesaje in ultimele"))
				return NO_MESSAGES_ERROR;
			else if (value != null && value.toLowerCase().startsWith("lista de mesaje este mai mare decat numarul de"))
				return TOO_MANY_MESSAGES_ERROR;
			
			return value;
		}
	}

	public List<AnafReceivedMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<AnafReceivedMessage> messages) {
		this.messages = messages;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		AnafReceivedMessages that = (AnafReceivedMessages) o;
		return Objects.equals(messages, that.messages) && Objects.equals(error, that.error);
	}

	@Override
	public int hashCode() {
		return Objects.hash(messages, error);
	}

	@Override
	public String toString() {
		return "AnafReceivedMessages{" +
				"messages=" + messages +
				", error='" + error + '\'' +
				'}';
	}
}
