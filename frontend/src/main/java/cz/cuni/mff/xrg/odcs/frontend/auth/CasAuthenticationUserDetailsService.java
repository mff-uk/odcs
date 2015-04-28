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
import cz.cuni.mff.xrg.odcs.commons.app.user.UserActor;

public class CasAuthenticationUserDetailsService extends
        AbstractCasAssertionUserDetailsService {

    private static final Logger LOG = LoggerFactory
            .getLogger(CasAuthenticationUserDetailsService.class);

    private String roleAttributeName = "SPR.Roles";

    private String actorIdAttributeName = "Actor.UPVSIdentityID";

    private String actorNameAttributeName = "Actor.FormattedName";

    private String fullNameAttributeName = "Subject.FormattedName";

    private String subjectIdAttributeName = "Subject.UPVSIdentityID";

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
        // FIXME: this is temporal solution; In the future, subject Id should be sent by CAS in username
        // Currently Actor ID is sent in username CAS parameter
        String subjectId = attributes.get(this.subjectIdAttributeName) != null ? attributes.get(this.subjectIdAttributeName).toString() : null;
        if (subjectId != null) {
            username = subjectId;
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
            String userFullName = attributes.get(this.fullNameAttributeName) != null ? attributes.get(this.fullNameAttributeName).toString() : null;
            if (userFullName != null) {
                user.setFullName(userFullName);
            }
            user.setExternalIdentifier(username);
            user.setTableRows(20);
        }

        user.getRoles().clear();

        for (String rolename : roles) {
            if (rolename != null) {
                RoleEntity role = this.userFacade.getRoleByName(rolename);
                if (role != null) {
                    user.addRole(role);
                }
            }
        }

        userFacade.saveNoAuth(user);

        String actorId = attributes.get(this.actorIdAttributeName) != null ? attributes.get(this.actorIdAttributeName).toString() : null;
        if (actorId != null) {
            UserActor actor = this.userFacade.getUserActorByExternalId(actorId);
            if (actor == null) {
                actor = new UserActor();
                String actorName = attributes.get(this.actorNameAttributeName).toString();
                actor.setName(actorName);
                actor.setExternalId(actorId);
                this.userFacade.save(actor);
            }
            user.setUserActor(actor);
        }

        return user;
    }

    public void setFullNameAttributeName(String fullNameAttributeName) {
        this.fullNameAttributeName = fullNameAttributeName;
    }

    public void setRoleAttributeName(String roleAttributeName) {
        this.roleAttributeName = roleAttributeName;
    }

    public void setActorNameAttributeName(String actorNameAttributeName) {
        this.actorNameAttributeName = actorNameAttributeName;
    }

    public void setSubjectIdAttributeName(String subjectIdAttributeName) {
        this.subjectIdAttributeName = subjectIdAttributeName;
    }

}
