package org.xandercat.pmdb.util.format;

/**
 * Interface for data parsers intended for converting attribute values to native
 * types to be further formatted within the view.
 * 
 * @author Scott Arnold
 *
 * @param <T>
 */
public interface DataParser<T> {

	/**
	 * Return native value for the given string, or null if value cannot be parsed.
	 * 
	 * @param s value to be parsed
	 * 
	 * @return native value for the String value
	 */
	public T parse(String s);
}
