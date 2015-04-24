package cz.cuni.mff.xrg.odcs.commons.app.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;

import cz.cuni.mff.xrg.odcs.commons.app.user.User;

public class PermissionUtils {

    @Autowired
    private AuthenticationContext authCtx;

    @Autowired
    private AuthAwarePermissionEvaluator permissions;

    public boolean hasPermission(Object target, Object perm) {
        return this.permissions.hasPermission(target, perm);
    }

    public boolean hasUserAuthority(String authority) {
        if (authority == null)
            return false;
        for (GrantedAuthority ga : getUser().getAuthorities()) {
            if (authority.equals(ga.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    private User getUser() {
        return this.authCtx.getUser();
    }

}
