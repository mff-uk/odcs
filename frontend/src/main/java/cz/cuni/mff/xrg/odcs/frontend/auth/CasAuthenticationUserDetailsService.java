package cz.cuni.mff.xrg.odcs.frontend.auth;

import java.util.Map;

import org.jasig.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.cas.userdetails.AbstractCasAssertionUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import cz.cuni.mff.xrg.odcs.commons.app.facade.UserFacade;
import cz.cuni.mff.xrg.odcs.commons.app.user.User;

public class CasAuthenticationUserDetailsService extends
		AbstractCasAssertionUserDetailsService {
	
	private static final Logger LOG = LoggerFactory
			.getLogger(CasAuthenticationUserDetailsService.class);
	
	private UserFacade userFacade;
	
	/**
	 * Constructor
	 * @param userFacade UserFacade object used for loading of user data
	 */
	public CasAuthenticationUserDetailsService(UserFacade userFacade) {
		this.userFacade = userFacade;
	}

	@Override
	protected UserDetails loadUserDetails(Assertion assertion) {
		String username = assertion.getPrincipal().getName();
		LOG.warn("loading user by username " + username);
		
		Map<String, Object> attributes = assertion.getAttributes();
		LOG.warn("Roles: " + attributes.get("role"));
		
		LOG.warn("loading user by username " + username);
        User user = userFacade.getUserByUsername(username);
        
        if (user == null) {
        	LOG.error("user is null !");
            throw new UsernameNotFoundException(username);
        }

        return user;
	}
}
