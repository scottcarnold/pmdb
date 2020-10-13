package org.xandercat.pmdb.util.format;

import org.springframework.util.StringUtils;

/**
 * Utility class for formatting values and checking value formats.
 * 
 * @author Scott Arnold
 */
public class FormatUtil {

	public static final String ALPHA_NUMERIC_PATTERN = "^[a-zA-Z0-9 ]*$";
	public static final String USERNAME_PATTERN = "^[a-zA-Z0-9@_\\.\\-]*$";
	
	/**
	 * Returns whether or not the given string value is blank, meaning null, being of 0 length, or 
	 * consisting only of white space.
	 * 
	 * @param s  string to test
	 * 
	 * @return whether or not string is blank
	 */
	public static boolean isBlank(String s) {
		return !StringUtils.hasText(s);
	}
	
	/**
	 * Returns whether or not the given string value is NOT blank, meaning it has greater than 0 length
	 * and contains at least 1 non-white-space character.
	 * 
	 * @param s  string to test
	 * 
	 * @return whether or not string is NOT blank
	 */
	public static boolean isNotBlank(String s) {
		return StringUtils.hasText(s);
	}
	/**
	 * Returns whether or not a string consists only of alphanumeric letters and spaces.
	 * 
	 * @param s           string value to test
	 * @param allowEmpty  if true, empty is considered valid
	 * 
	 * @return whether or not string contains only letters, numbers, and spaces.
	 */
	public static boolean isAlphaNumeric(String s, boolean allowEmpty) {
		if (s == null || s.trim().length() == 0) {
			return allowEmpty;
		}
		return s.matches(ALPHA_NUMERIC_PATTERN);
	}

	/**
	 * Returns whether or not a username should be considered valid.  A valid username
	 * is considered to be a username consisting only of letters, numbers, periods, dashes,
	 * underscores, and at symbols.
	 * 
	 * @param username username to test
	 * 
	 * @return whether or not username should be considered valid
	 */
	public static boolean isValidUsername(String username) {
		if (username == null || username.length() == 0) {
			return false;
		}
		return username.matches(USERNAME_PATTERN);
	}
	
	/**
	 * Returns an alphanumeric formatted string where any non alphanumeric characters have been removed.
	 * Alphanumeric for this method is considered to be letters (upper or lower case), numbers, and spaces.
	 * @param s
	 * @return
	 */
	public static String formatAlphaNumeric(String s) {
		if (isAlphaNumeric(s, true)) {
			return s;
		}
		final String acceptableCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ";
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<s.length(); i++) {
			if (acceptableCharacters.indexOf(s.charAt(i)) >= 0) {
				sb.append(s.charAt(i));
			}
		}
		return sb.toString();
	}
	
	/**
	 * Returns string formatted as a number, containing only digits and a single decimal place.
	 * This can be used to format currencies and numbers with thousands separators to a format
	 * that can be parsed.  This is a dumb format that does not expect the number to conform to 
	 * any particular pattern, so it should only be used if certain the values represent numbers.
	 * This method can handle commas for thousands separator and period for decimal place as well
	 * as periods for thousand separator and comma for decimal place.
	 * 
	 * @param s string to format
	 * 
	 * @return string formatted as a number
	 */
	public static String formatAsNumber(String s) {
		if (s == null) {
			return null;
		}
		final String acceptableCharacters = "0123456789";
		int dpIndex = s.lastIndexOf(".");
		int eudpIndex = s.lastIndexOf(",");
		if (dpIndex >= 0 && eudpIndex > dpIndex) {
			dpIndex = eudpIndex;
		}
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<s.length(); i++) {
			if (acceptableCharacters.indexOf(s.charAt(i)) >= 0) {
				sb.append(s.charAt(i));
			} else if (i == dpIndex) {
				sb.append('.');
			}
		}
		return sb.toString();
	}
	
	/**
	 * Parses a string formatted number into a double. Non-numeric characters are stripped
	 * prior to conversion.  If it cannot be converted to double, a value of 0 is returned.
	 * 
	 * @param number   string formatted number
	 * 
	 * @return number as a double (or 0 if cannot be converted)
	 */
	public static double parseDoubleLenient(String number) {
		try {
			number = formatAsNumber(number);
			if (number == null || number.isEmpty()) {
				return 0;
			}
			return Double.parseDouble(number);
		} catch (Exception e) {
			return 0;
		}
	}
	
	/**
	 * Returns a "title case" formatted string from the string provided where each word begins with an upper case
	 * letter, and all other characters are lower case.
	 * 
	 * @param s  string to format
	 * 
	 * @return title case formatted string
	 */
	public static String titleCase(String s) {
		if (s == null || s.trim().length() == 0) {
			return s;
		}
		StringBuilder sb = new StringBuilder();
		boolean newWord = true;
		for (int i=0; i<s.length(); i++) {
			if (s.charAt(i) != ' ' && newWord) {
				newWord = false;
				sb.append(String.valueOf(s.charAt(i)).toUpperCase());
			} else if (s.charAt(i) == ' ') {
				newWord = true;
				sb.append(' ');
			} else {
				sb.append(String.valueOf(s.charAt(i)).toLowerCase());
			}
		}
		return sb.toString();
	}
	
	/**
	 * Converts a PMDB key value to a DynamoDB key value.
	 * 
	 * @param key key to convert
	 * 
	 * @return key value for DynamoDB
	 */
	public static String convertToDynamoKey(String key) {
		return key.replaceAll(" ", "_");
	}
	
	/**
	 * Converts a DynamoDB key value to a PMDB key value.
	 * 
	 * @param key key to convert
	 * 
	 * @return key value for PMDB
	 */
	public static String convertFromDynamoKey(String key) {
		return key.replaceAll("_", " ");
	}
}
