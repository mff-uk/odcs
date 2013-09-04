package cz.cuni.xrg.intlib.frontend;

import com.vaadin.server.VaadinServletService;
import com.vaadin.server.communication.PushHandler;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResource;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author Jan Vojt
 */
public class FilterablePushHandler extends PushHandler {
   
    public FilterablePushHandler(VaadinServletService service) {
        super(service);
    }

    @Override
    public void onRequest(AtmosphereResource resource) {

		// Hold the original request
		AtmosphereRequest req = resource.getRequest();
		RequestHolder.setRequest(req);
		SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
		
		// Do the business.
		super.onRequest(resource);
		
		// cleanup
		RequestHolder.clean();
		SecurityContextHolder.clearContext();
		
    }
   
}
