package cz.cuni.mff.xrg.odcs.frontend;

import com.vaadin.server.VaadinServletService;
import com.vaadin.server.communication.PushHandler;
import javax.servlet.http.HttpServletRequest;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Custom implementation of push handler for servicing websocket connection.
 * Custom push handler is needed to be able to access original
 * {@link HttpServletRequest} across application.
 * 
 * @see RequestHolder
 * @author Jan Vojt
 */
public class IntlibPushHandler extends PushHandler {
   
    public IntlibPushHandler(VaadinServletService service) {
        super(service);
    }

    @Override
    public void onRequest(AtmosphereResource resource) {

		// Hold the original request
		AtmosphereRequest request = resource.getRequest();
		RequestHolder.setRequest(request);

		// Clear the security context just in case it was not properly cleared
		// when executing previous job on this thread.
		SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
		
		// Load authentication context from session (if there is any).
		Authentication auth = (Authentication) request.getSession()
				.getAttribute(AuthenticationService.SESSION_KEY);
		if (auth != null) {
			SecurityContextHolder.getContext().setAuthentication(auth);
		}
		
		// Do the business.
		super.onRequest(resource);
		
		// cleanup
		RequestHolder.clean();
		SecurityContextHolder.clearContext();
		
    }
   
}
