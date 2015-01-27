package cz.cuni.mff.xrg.odcs.frontend.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import cz.cuni.mff.xrg.odcs.commons.app.auth.PasswordAuthenticationProvider;
import cz.cuni.mff.xrg.odcs.commons.app.facade.UserFacade;
import cz.cuni.mff.xrg.odcs.commons.app.user.User;

public class FrontendUserDetailsServiceImpl implements UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(PasswordAuthenticationProvider.class);

    private final UserFacade userFacade;

    /**
     * Constructor sets up dependencies.
     *
     * @param userFacade
     */
    public FrontendUserDetailsServiceImpl(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOG.warn("loading user by username " + username);
        User user = userFacade.getUserByUsername(username);
        if (user == null) {
            LOG.error("user is null !");
            throw new UsernameNotFoundException(username);
        }

        return user;
    }
}
