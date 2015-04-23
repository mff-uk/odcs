package cz.cuni.mff.xrg.odcs.frontend.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jasig.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.cas.userdetails.AbstractCasAssertionUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;

import cz.cuni.mff.xrg.odcs.commons.app.facade.UserFacade;
import cz.cuni.mff.xrg.odcs.commons.app.user.EmailAddress;
import cz.cuni.mff.xrg.odcs.commons.app.user.RoleEntity;
import cz.cuni.mff.xrg.odcs.commons.app.user.User;

public class CasAuthenticationUserDetailsService extends
        AbstractCasAssertionUserDetailsService {

    private static final Logger LOG = LoggerFactory
            .getLogger(CasAuthenticationUserDetailsService.class);

    private String subjectAttributeName = "Subject.UPVSIdentityID";

    private String roleAttributeName = "SPR.Roles";

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
        String subject = attributes.get(this.subjectAttributeName) != null ? attributes.get(this.subjectAttributeName).toString() : null;
        if (subject != null) {
            username = subject;
        }

        List<String> roles = new ArrayList<>();
        Object roleAttributes = attributes.get(roleAttributeName);
        if (roleAttributes != null) {
            if (roleAttributes instanceof String)
                roles.add((String) roleAttributes);// = attributes.get(ROLE_ATTRIBUTE).toString();
            else if (roleAttributes instanceof List)
                roles.addAll((List) roleAttributes);
        }

        User user = userFacade.getUserByExtId(username);

        if (user == null) {
            user = userFacade.createUser(username, "*****", new EmailAddress(username + "@nomail.com"));
            user.setExternalIdentifier(username);
            user.setTableRows(20);
        }

        user.getRoles().clear();

        for (String rolename : roles) {
            if (rolename != null) {
                //TODO nevieme to inak otestit
                if ("MOD-R-DATA".equals(rolename))
                    rolename = "MOD-R-PO";
                RoleEntity role = userFacade.getRoleByName(rolename);
                if (role != null) {
                    user.addRole(role);
                }
            }
        }

        userFacade.saveNoAuth(user);

        return user;
    }

    public void setSubjectAttributeName(String subjectAttributeName) {
        this.subjectAttributeName = subjectAttributeName;
    }

    public void setRoleAttributeName(String roleAttributeName) {
        this.roleAttributeName = roleAttributeName;
    }
}
