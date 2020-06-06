package org.xandercat.pmdb.ws;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.QueryParam;
import javax.ws.rs.client.WebTarget;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * Jersey and the ws.rs.Client api do not appear to have any means to utilize an annotated class to populate query parameters for a
 * Client GET request.  This class provides that capability by reusing the QueryParam annotation as a means to set request parameters
 * for a request.  For a query parameter to be set, the field must be annotated with QueryParam and the value must be non-blank.
 * 
 * @author Scott Arnold
 */
public class ClientQueryParamMarshaller {
	
	/**
	 * Return a query param map from a QueryParam annotated class object.
	 * 
	 * @param object object to produce a query param map from
	 * 
	 * @return query param map for the object
	 */
	public Map<String, String> queryParamMap(Object object) {
		Field[] fields = object.getClass().getDeclaredFields();
		List<Field> paramFields = Arrays.stream(fields)
				.filter(field -> StringUtils.hasText((String) AnnotationUtils.getValue(field.getDeclaredAnnotation(QueryParam.class))))
				.collect(Collectors.toList());
		paramFields.forEach(field -> ReflectionUtils.makeAccessible(field));
		return paramFields.stream()
				.collect(Collectors.toMap(field -> field, field -> (String) AnnotationUtils.getValue(field.getDeclaredAnnotation(QueryParam.class))))
				.entrySet().stream()
				.filter(entry -> ReflectionUtils.getField(entry.getKey(), object) != null)
				.filter(entry -> StringUtils.hasText(ReflectionUtils.getField(entry.getKey(), object).toString()))
				.collect(Collectors.toMap(Map.Entry::getValue, entry -> ReflectionUtils.getField(entry.getKey(), object).toString()));		
	}
	
	/**
	 * Configure a WebTarget with query parameters from the provided QueryParam annotated class object.
	 * 
	 * @param webTarget WebTarget to configure with query parameters
	 * @param object QueryParam annotated class object to pull query parameters from
	 * 
	 * @return WebTarget configured with query parameters from provided QueryParam annotated class object
	 */
	public WebTarget queryParams(WebTarget webTarget, Object object) {
		Map<String, String> queryParams = queryParamMap(object);
		for (Map.Entry<String, String> entry : queryParams.entrySet()) {
			webTarget = webTarget.queryParam(entry.getKey(), entry.getValue());
		}
		return webTarget;
	}
}
