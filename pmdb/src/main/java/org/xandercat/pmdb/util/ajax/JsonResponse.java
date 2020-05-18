package org.xandercat.pmdb.util.ajax;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Basic AJAX JSON response class that can be used by the application.
 * 
 * Return an instance of this class from a controller method annotated with ResponseBody.
 * 
 * @author Scott Arnold
 */
public class JsonResponse {

	@JsonProperty
	private boolean ok = true;
	@JsonProperty
	private String errorMessage;
	@JsonProperty
	private JsonContent content = new JsonContent();
	
	public boolean isOk() {
		return ok;
	}
	public void setOk(boolean ok) {
		this.ok = ok;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public JsonContent getContent() {
		return content;
	}
	public void setContent(JsonContent content) {
		this.content = content;
	}
	public void put(String key, String value) {
		content.getContent().put(key, value);
	}
}
