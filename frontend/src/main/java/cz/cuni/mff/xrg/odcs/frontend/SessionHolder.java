package cz.cuni.mff.xrg.odcs.frontend;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Keeps track of all sessions in the {@link ServletContext}.
 * 
 * <p>
 * Session map is needed in websocket connections, where only a fake session is
 * available on the request implementation of {@link HttpServletRequest}.
 * Changes to fake session are only effective throughout given websocket
 * connection. However, when a different connection is made, all changes are
 * lost. Because of that, if we are operating in a PUSH request, we need to make
 * sure the changes are also propagated to the real HTTP servlet session. Map of
 * this sessions is maintained here, and is accessible from the
 * {@link ServletContext}.
 *
 * @author Jan Vojt
 */
public class SessionHolder implements HttpSessionListener {
	
	/**
	 * Attribute key for getting the attribute with session map from servlet
	 * context.
	 */
	public static final String ATTR_KEY = "intlibSessionAttrKey";

	/**
	 * Adds a newly created session to the session map in the servlet context.
	 * @param se 
	 */
	@Override
	public void sessionCreated(HttpSessionEvent se) {
		Map<String, HttpSession> sessionMap = getSessionMap(se);
		sessionMap.put(se.getSession().getId(), se.getSession());
	}

	/**
	 * Removes session from the session map in servlet context.
	 * 
	 * @param se 
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		Map<String, HttpSession> sessionMap = getSessionMap(se);
		sessionMap.remove(se.getSession().getId());
	}
	
	/**
	 * Gets the session map, or creates it in the servlet context if it does not
	 * exist.
	 * 
	 * @param se
	 * @return 
	 */
	private Map<String, HttpSession> getSessionMap(HttpSessionEvent se) {
		
		Map<String, HttpSession> sessionMap = (Map<String, HttpSession>) se
				.getSession()
				.getServletContext()
				.getAttribute(ATTR_KEY);
		
		if (sessionMap == null) {
			// this should only happen when the first session is created by servlet
			sessionMap = new HashMap<>();
			se.getSession().getServletContext().setAttribute(ATTR_KEY, sessionMap);
		}
		
		return sessionMap;
	}

}
