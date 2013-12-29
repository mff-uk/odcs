package cz.cuni.mff.xrg.odcs.commons.app.dao;

import cz.cuni.mff.xrg.odcs.commons.app.constants.LenghtLimits;

/**
 * Class provides functions that can be usefull when dealing with strings.
 *
 * @author Petyr
 */
public class StringUtils {

	private StringUtils() {

	}

	/**
	 * If the string is null then return empty string, otherwise return the
	 * original string.
	 *
	 * @param str
	 * @return
	 */
	public static String nullToEmpty(String str) {
		if (str == null) {
			return "";
		} else {
			return str;
		}
	}

	/**
	 * If the given string is null, or empty then return null. otherwise return
	 * the original string.
	 *
	 * @param str
	 * @return
	 */
	public static String emptyToNull(String str) {
		if (str == null || str.isEmpty()) {
			return null;
		} else {
			return str;
		}
	}

	/**
	 * Check the string length if it's longer then truncate him .. otherwise
	 * nothing happen.
	 * @param str
	 * @param limit
	 * @return 
	 */
	public static String secureLenght(String str, LenghtLimits limit) {
		if (str != null && str.length() >= limit.limit()) {
			StringBuilder builder = new StringBuilder(str);
			builder.setLength(limit.limit() - 4);
			builder.append("...");
			return builder.toString();
		} else {
			return str;
		}
	}
	
}
