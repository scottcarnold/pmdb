package org.xandercat.pmdb.ws;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.xandercat.pmdb.dto.imdb.SearchRequest;

public class ClientQueryParamMarshallerTest {

	@Test
	public void testSearchRequestMarshalling() {
		ClientQueryParamMarshaller m = new ClientQueryParamMarshaller();
		SearchRequest request = new SearchRequest();
		request.setTitle("title");
		request.setPage(2);
		request.setYear("1995");
		Map<String, String> params = m.queryParamMap(request);
		assertEquals(3, params.size());
		assertTrue(params.containsKey("s"));
		assertTrue(params.containsKey("page"));
		assertTrue(params.containsKey("y"));
		assertEquals("title", params.get("s"));
		assertEquals("2", params.get("page"));
		assertEquals("1995", params.get("y"));
	}

}
