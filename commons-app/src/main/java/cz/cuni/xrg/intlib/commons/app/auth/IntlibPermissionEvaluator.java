package cz.cuni.xrg.intlib.commons.app.auth;

import cz.cuni.xrg.intlib.commons.app.user.OwnedEntity;
import cz.cuni.xrg.intlib.commons.app.user.Role;
import cz.cuni.xrg.intlib.commons.app.user.User;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Evaluates whether currently authorized user has a given permission on a given
 * object.
 *
 * @author Jan Vojt
 */
public class IntlibPermissionEvaluator implements PermissionEvaluator {
	
	private final static Logger LOG = LoggerFactory.getLogger(IntlibPermissionEvaluator.class);
	
	/**
	 * Authorization logic for permissions on entities.
	 * 
	 * @param auth authentication context
	 * @param target entity to try permission on
	 * @param perm permission requested on target
	 * @return true	if authenticated user has a given permission on target,
	 *		   false otherwise
	 */
	@Override
	public boolean hasPermission(Authentication auth, Object target, Object perm) {
		
		// check for missing authentication context
		if (auth == null) {
			return false;
		}
		
		// administrator is almighty
		if (auth.getAuthorities().contains(Role.ROLE_ADMIN)) {
			return true;
		}
		
		// entity owner is almighty
		if (target instanceof OwnedEntity) {
			OwnedEntity oTarget = (OwnedEntity) target;
			User owner = oTarget.getOwner();
			if (owner != null && auth.getName().equals(owner.getUsername())) {
				return true;
			}
		}
		
		// check publicly viewable entities
		if (target instanceof SharedEntity) {
			SharedEntity sTarget = (SharedEntity) target;
			switch (perm.toString()) {
				case "view" :
				case "use" :
				case "copy" :
				case "export" :
					if (VisibilityType.PUBLIC.equals(sTarget.getVisibility())) {
						return true;
					}
					break;
			}
		}
		
		// in other cases be restrictive
		LOG.debug(String.format(
				"Method hasPermission not supported for object <?> and permission <?>.",
				target,
				perm
		));
		return false;
	}

	/**
	 * Resolves permissions on given target object for currently authenticated
	 * user.
	 * 
	 * @param target entity to try permission on
	 * @param perm permission requested on target
	 * @return true	if authenticated user has a given permission on target,
	 *		   false otherwise
	 */
	public boolean hasPermission(Object target, Object perm) {
		return hasPermission(SecurityContextHolder.getContext().getAuthentication(), target, perm);
	}

	/**
	 * Unsupported.
	 * 
	 * @param auth
	 * @param targetId
	 * @param targetType
	 * @param permission
	 * @return 
	 */
	@Override
	public boolean hasPermission(Authentication auth,
			Serializable targetId, String targetType, Object permission) {
		throw new UnsupportedOperationException("Not supported.");
	}

}
