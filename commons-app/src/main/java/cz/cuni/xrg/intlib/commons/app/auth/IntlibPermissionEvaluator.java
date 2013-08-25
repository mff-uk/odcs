package cz.cuni.xrg.intlib.commons.app.auth;

import cz.cuni.xrg.intlib.commons.app.user.OwnedEntity;
import java.io.Serializable;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

/**
 * Evaluates whether currently authorized user has a given permission on a given
 * object.
 *
 * @author Jan Vojt
 */
public class IntlibPermissionEvaluator implements PermissionEvaluator {

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
		
		if (target instanceof OwnedEntity) {
			OwnedEntity oTarget = (OwnedEntity) target;
			switch (perm.toString()) {
				case "edit" :
				case "delete" :
					return auth.getName().equals(oTarget.getOwner().getUsername());
			}
		}
		
		// catch all undefined instance-operation pair
		throw new UnsupportedOperationException(String.format(
				"Method hasPermission not supported for object <?> and permission <?>.",
				target,
				perm
		));
		
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
