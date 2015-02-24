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

    private static final String ORG_ATTRIBUTE = "organization";
    private static final String ROLE_ATTRIBUTE = "role";
    
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

        String rolename = null;
        
        if(attributes.get(ROLE_ATTRIBUTE)!=null)
            rolename = attributes.get(ROLE_ATTRIBUTE).toString();

        String organization = attributes.get(ORG_ATTRIBUTE) != null ? attributes.get(ORG_ATTRIBUTE).toString() : null;

        User user = userFacade.getUserByExtId(username);

        if (user == null) {
            user = userFacade.createUser(username, "*****", new EmailAddress(username + "@nomail.com"));
            user.getExternalIdentifiers().add(username);
            user.setTableRows(20);
        }

        user.getRoles().clear();

        if (rolename != null) {
            RoleEntity role = userFacade.getRoleByName(rolename);
            if (role != null) {
                user.addRole(role);
            }
        }

        userFacade.saveNoAuth(user);

        if (organization != null) {
            Organization o = userFacade.getOrganizationByName(organization);
            if (o == null) {
                o = new Organization();
                o.setName(organization);
                userFacade.save(o);
            }
            user.setOrganization(o);
        }
        return user;
    }
}
