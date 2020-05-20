package org.xandercat.pmdb.util.format;

public class FormatUtil {

	public static final String ALPHA_NUMERIC_PATTERN = "^[a-zA-Z0-9 ]*$";
	
	public static boolean isAlphaNumeric(String s, boolean allowEmpty) {
		if (s == null || s.trim().length() == 0) {
			return allowEmpty;
		}
		return s.matches(ALPHA_NUMERIC_PATTERN);
	}

	public static String formatAlphaNumeric(String s) {
		if (isAlphaNumeric(s, true)) {
			return s;
		}
		final String acceptableCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ";
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<s.length(); i++) {
			if (acceptableCharacters.indexOf(s.charAt(i)) > 0) {
				sb.append(s.charAt(i));
			}
		}
		return sb.toString();
	}
	
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
	
	public static String convertToDynamoKey(String key) {
		return key.replaceAll(" ", "_");
	}
	
	public static String convertFromDynamoKey(String key) {
		return key.replaceAll("_", " ");
	}
}
