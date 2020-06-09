package org.xandercat.pmdb.util.ajax;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonResponseTest {

	@Test
	public void testResponseMarshalling() throws JsonProcessingException {
		JsonContent jsonContent = new JsonContent();
		jsonContent.getContent().put("one", "two");
		JsonResponse jsonResponse = new JsonResponse();
		jsonResponse.setContent(jsonContent);
		String mr = new ObjectMapper().writeValueAsString(jsonResponse);
		assertEquals("{\"ok\":true,\"errorMessage\":null,\"content\":{\"one\":\"two\"}}", mr);
	}

}
