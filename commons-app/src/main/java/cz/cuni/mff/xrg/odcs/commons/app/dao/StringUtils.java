package cz.cuni.mff.xrg.odcs.commons.app.dao;

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

}
