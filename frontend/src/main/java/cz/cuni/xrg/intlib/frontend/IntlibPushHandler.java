package cz.cuni.xrg.intlib.frontend;

import com.vaadin.server.VaadinServletService;
import com.vaadin.server.communication.PushHandler;
import javax.servlet.http.HttpServletRequest;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
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
		AtmosphereRequest req = resource.getRequest();
		RequestHolder.setRequest(req);

		// Clear the security context just in case it was not properly cleared
		// when executing previous job on this thread.
		SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
		
		// Do the business.
		super.onRequest(resource);
		
		// cleanup
		RequestHolder.clean();
		SecurityContextHolder.clearContext();
		
    }
   
}
