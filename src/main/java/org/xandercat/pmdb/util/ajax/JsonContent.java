package org.xandercat.pmdb.util.ajax;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

public class JsonContent {

	private Map<String, String> content = new HashMap<String, String>();

	@JsonAnyGetter
	public Map<String, String> getContent() {
		return content;
	}

	public void setContent(Map<String, String> content) {
		this.content = content;
	}

}
