package cz.cuni.xrg.intlib.frontend;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinServletService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.DefaultBroadcasterFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.xpoft.vaadin.SpringVaadinServlet;

/**
 * Customized servlet implementation to provide access to original
 * {@link HttpServletRequest} across application.
 *
 * @see {@link RequestHolder}
 * @author Jan Vojt
 */
public class IntlibApplicationServlet extends SpringVaadinServlet {
	
	
	/**
	 * The push handler.
	 */
    IntlibPushHandler handler;
   
    @Override
    protected VaadinServletService createServletService(
            DeploymentConfiguration deploymentConfiguration)
            throws ServiceException {
        VaadinServletService service = super.createServletService(deploymentConfiguration);
       
        final AtmosphereFramework framework = DefaultBroadcasterFactory.getDefault()
				.lookup("/*").getBroadcasterConfig().getAtmosphereConfig().framework();
       
        // replace the handler registered by vaadin with this one
        handler = new IntlibPushHandler(service);
        framework.addAtmosphereHandler("/*", handler);
       
        return service;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// Store current HTTP request in thread-local, so we can access it later.
		RequestHolder.setRequest(request);
		
		// First clear the security context, as we need to load it from session.
		SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
		
		// Load authentication context from session (if there is any).
		Authentication auth = (Authentication) request.getSession()
				.getAttribute(AuthenticationService.SESSION_KEY);
		if (auth != null) {
			SecurityContextHolder.getContext().setAuthentication(auth);
		}
		
		// Do the business.
		super.service(request, response);
		
		// We remove the request from the thread local, there's no reason
		// to keep it once the work is done. Next request might be serviced
		// by different thread, which will need to load security context from
		// the session anyway.
		RequestHolder.clean();
		SecurityContextHolder.clearContext();
    }
	
}
