package org.xandercat.pmdb.dto;

import java.util.HashMap;
import java.util.Map;

import org.xandercat.pmdb.util.format.DataTransformer;

/**
 * Decorator class for Movie that adds formatting of attribute values for sort and display in the user interface.
 * 
 * @author Scott Arnold
 */
public class FormattedMovie {

	private Movie movie;
	private Map<String, DataTransformer<?>> transformers = new HashMap<String, DataTransformer<?>>();
	
	public FormattedMovie(Movie movie, Map<String, DataTransformer<?>> transformers) {
		this.movie = movie;
		this.transformers = transformers;
	}
	
	public String getId() {
		return movie.getId();
	}

	public void setId(String id) {
		movie.setId(id);
	}

	public String getTitle() {
		return movie.getTitle();
	}

	public void setTitle(String title) {
		movie.setTitle(title);
	}

	public void addAttribute(String key, String value) {
		movie.addAttribute(key, value);
	}

	public String getAttribute(String key) {
		return movie.getAttribute(key);
	}

	public Map<String, String> getAttributes() {
		return movie.getAttributes();
	}

	public void setAttributes(Map<String, String> attributes) {
		movie.setAttributes(attributes);
	}

	public String getCollectionId() {
		return movie.getCollectionId();
	}

	public void setCollectionId(String collectionId) {
		movie.setCollectionId(collectionId);
	}

	public String getAttributeSortValue(String key) {
		if (transformers == null) {
			String value = movie.getAttribute(key);
			return (value == null || value.length() == 0)? " " : value; // space forces Thymeleaf to include the attribute rather than omit it
		}
		DataTransformer<?> dataTransformer = transformers.get(key);
		if (dataTransformer == null) {
			String value = movie.getAttribute(key);
			return (value == null || value.length() == 0)? " " : value; // space forces Thymeleaf to include the attribute rather than omit it
		}
		String value = dataTransformer.getSortValue(movie.getAttribute(key));
		return (value == null || value.length() == 0)? " " : value; // space forces Thymeleaf to include the attribute rather than omit it
	}
	
	public String getAttributeDisplayValue(String key) {
		if (transformers == null) {
			return movie.getAttribute(key);
		}
		DataTransformer<?> dataTransformer = transformers.get(key);
		if (dataTransformer == null) {
			return movie.getAttribute(key);
		}
		return dataTransformer.getDisplayValue(movie.getAttribute(key));		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((movie == null) ? 0 : movie.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FormattedMovie other = (FormattedMovie) obj;
		if (movie == null) {
			if (other.movie != null)
				return false;
		} else if (!movie.equals(other.movie))
			return false;
		return true;
	}
}
