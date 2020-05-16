package org.xandercat.pmdb.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.xandercat.pmdb.util.CIString;
import org.xandercat.pmdb.util.format.DataTransformer;

/**
 * Decorator class for Movie that adds formatting of attribute values for sort and display in the user interface.
 * 
 * @author Scott Arnold
 */
public class FormattedMovie {

	private Movie movie;
	private Map<CIString, DataTransformer<?>> transformers = new HashMap<CIString, DataTransformer<?>>();
	
	public FormattedMovie(Movie movie, Map<CIString, DataTransformer<?>> transformers) {
		this.movie = movie;
		this.transformers = transformers;
	}
	
	public int getId() {
		return movie.getId();
	}

	public void setId(int id) {
		movie.setId(id);
	}

	public String getTitle() {
		return movie.getTitle();
	}

	public void setTitle(String title) {
		movie.setTitle(title);
	}

	public Map<CIString, String> getAttributes() {
		return movie.getAttributes();
	}

	public void setAttributes(TreeMap<CIString, String> attributes) {
		movie.setAttributes(attributes);
	}

	public int getCollectionId() {
		return movie.getCollectionId();
	}

	public void setCollectionId(int collectionId) {
		movie.setCollectionId(collectionId);
	}

	public String getAttributeValue(String key) {
		return movie.getAttributeValue(key);
	}

	public String getAttributeSortValue(String key) {
		if (transformers == null) {
			String value = movie.getAttributeValue(key);
			return (value == null || value.length() == 0)? " " : value; // space forces Thymeleaf to include the attribute rather than omit it
		}
		DataTransformer<?> dataTransformer = transformers.get(new CIString(key));
		if (dataTransformer == null) {
			String value = movie.getAttributeValue(key);
			return (value == null || value.length() == 0)? " " : value; // space forces Thymeleaf to include the attribute rather than omit it
		}
		String value = dataTransformer.getSortValue(movie.getAttributeValue(key));
		return (value == null || value.length() == 0)? " " : value; // space forces Thymeleaf to include the attribute rather than omit it
	}
	
	public String getAttributeDisplayValue(String key) {
		if (transformers == null) {
			return movie.getAttributeValue(key);
		}
		DataTransformer<?> dataTransformer = transformers.get(new CIString(key));
		if (dataTransformer == null) {
			return movie.getAttributeValue(key);
		}
		return dataTransformer.getDisplayValue(movie.getAttributeValue(key));		
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
