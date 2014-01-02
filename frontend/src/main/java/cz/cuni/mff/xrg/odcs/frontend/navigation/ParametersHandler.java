package cz.cuni.mff.xrg.odcs.frontend.navigation;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import org.tepi.filtertable.numberfilter.NumberInterval;

/**
 *
 * @author Bogo
 */
public class ParametersHandler {

	public static Object getInterval(String value) {
		if (value.contains("-")) {
			String[] boundaries = value.split("-", -1);
			return new NumberInterval(boundaries[1].isEmpty() ? null : boundaries[1], boundaries[0].isEmpty() ? null : boundaries[0], null);
		} else {
			return new NumberInterval(null, null, value);
		}
	}

	public static String getStringForInterval(NumberInterval interval) {
		if (interval.getEqualsValue() != null) {
			return interval.getEqualsValue();
		} else {
			String min = interval.getGreaterThanValue() == null ? "" : interval.getGreaterThanValue();
			String max = interval.getLessThanValue() == null ? "" : interval.getLessThanValue();
			return String.format("%s-%s", min, max);
		}
	}
	private String uriFragment;

	public ParametersHandler(String uriFragment) {
		this.uriFragment = uriFragment;
	}

	public String getUriFragment() {
		return uriFragment;
	}

	public void addParameter(String name, String value) {
		try {
			value = URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			//TODO: Invalid parameter - ignore?
			return;
		}
		String parameter = String.format("/%s=", name);
		String parameter2 = String.format("&%s=", name);
		if (uriFragment.contains(parameter) || uriFragment.contains(parameter2)) {
			int start = Math.max(uriFragment.indexOf(parameter), uriFragment.indexOf(parameter2)) + 1;
			int end = uriFragment.indexOf('&', start);
			if (end < 0) {
				end = uriFragment.length();
			}
			uriFragment = uriFragment.substring(0, start) + String.format("%s=%s", name, value) + uriFragment.substring(end);
		} else {
			if (!uriFragment.contains("/")) {
				uriFragment += '/';
			} else {
				uriFragment += '&';
			}

			uriFragment += String.format("%s=%s", name, value);
		}
	}

	public Object getValue(String parameterName) {
		return null;
	}

	public static Map<String, String> getConfiguration(String parameters) {
		HashMap<String, String> configuration = new HashMap<>();
		if (parameters.isEmpty()) {
			return null;
		}
		String[] pars = parameters.split("&");
		for (String parWhole : pars) {
			String[] parParts = parWhole.split("=");
			if (parParts.length != 2) {
				//TODO: Invalid parameter - ignore?
				continue;
			}
			try {
				configuration.put(parParts[0], URLDecoder.decode(parParts[1], "UTF-8"));
			} catch (UnsupportedEncodingException ex) {
				//TODO: Invalid parameter - ignore?
			}
		}
		return configuration;
	}

	public void removeParameter(String name) {
		String parameter = String.format("/%s=", name);
		String parameter2 = String.format("&%s=", name);
		if (uriFragment.contains(parameter) || uriFragment.contains(parameter2)) {
			int start = Math.max(uriFragment.indexOf(parameter), uriFragment.indexOf(parameter2)) + 1;
			int end = uriFragment.indexOf('&', start);
			boolean isLast = false;
			if (end < 0) {
				isLast = true;
				end = uriFragment.length();
			}
			if (isLast) {
				uriFragment = uriFragment.substring(0, start - 1);
			} else {
				uriFragment = uriFragment.substring(0, start) + uriFragment.substring(end + 1);
			}
		}
	}
}
