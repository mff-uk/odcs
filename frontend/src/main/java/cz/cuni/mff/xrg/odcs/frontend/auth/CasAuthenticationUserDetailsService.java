package cz.cuni.mff.xrg.odcs.frontend.auth;

import java.util.Map;

import org.jasig.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.cas.userdetails.AbstractCasAssertionUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;

import cz.cuni.mff.xrg.odcs.commons.app.facade.UserFacade;
import cz.cuni.mff.xrg.odcs.commons.app.user.EmailAddress;
import cz.cuni.mff.xrg.odcs.commons.app.user.Organization;
import cz.cuni.mff.xrg.odcs.commons.app.user.RoleEntity;
import cz.cuni.mff.xrg.odcs.commons.app.user.User;

public class CasAuthenticationUserDetailsService extends
        AbstractCasAssertionUserDetailsService {

    private static final Logger LOG = LoggerFactory
            .getLogger(CasAuthenticationUserDetailsService.class);

    private UserFacade userFacade;

    /**
     * Constructor
     * 
     * @param userFacade
     *            UserFacade object used for loading of user data
     */
    public CasAuthenticationUserDetailsService(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @Override
    protected UserDetails loadUserDetails(final Assertion assertion) {

        String username = assertion.getPrincipal().getName();
        Map<String, Object> attributes = assertion.getPrincipal().getAttributes();

        String rolename = attributes.get("role").toString();
        
        String organization = attributes.get("organization").toString();
        
        User user = userFacade.getUserByExtId(username);

        if (user == null) {
            LOG.info("user is not found, trying to create him !");
            user = userFacade.createUser(username, "*****", new EmailAddress(username + "@nomail.com"));
            user.getExternalIdentifiers().add(username);
            user.setTableRows(20);
        }

        user.getRoles().clear();

//        for (String rolename : roles) {
            RoleEntity role = userFacade.getRoleByName(rolename);
            user.addRole(role);
//        }

        userFacade.saveNoAuth(user);

        //checks etc TODO
        
        Organization o = userFacade.getOrganizationByName(organization);
        if(o == null){
            o = new Organization();
            o.setName(organization);
            userFacade.save(o);
        }
        user.setOrganization(o);
        
        return user;
    }
}
