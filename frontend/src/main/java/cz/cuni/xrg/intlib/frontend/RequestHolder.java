package cz.cuni.xrg.intlib.frontend;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Jan Vojt
 */
public class RequestHolder {

	private static final ThreadLocal<HttpServletRequest> THREAD_LOCAL = new ThreadLocal<>();

	public static HttpServletRequest getRequest() {
		return THREAD_LOCAL.get();
	}

	static void setRequest(HttpServletRequest request) {
		THREAD_LOCAL.set(request);
	}

	static void clean() {
		THREAD_LOCAL.remove();
	}
}