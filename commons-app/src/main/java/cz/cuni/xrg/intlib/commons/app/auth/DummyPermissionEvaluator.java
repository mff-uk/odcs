package cz.cuni.xrg.intlib.commons.app.auth;

import java.io.Serializable;
import org.springframework.security.core.Authentication;

/**
 * Dummy permission evaluator, which allows everything (even for unauthenticated
 * users). Use when you want all security rules disabled and allow all
 * permissions. Use for testing purposes only!
 *
 * @author Jan Vojt
 */
public class DummyPermissionEvaluator extends IntlibPermissionEvaluator {

	@Override
	public boolean hasPermission(Authentication authentication,
			Object targetDomainObject, Object permission) {
		return true;
	}

	@Override
	public boolean hasPermission(Authentication authentication,
			Serializable targetId, String targetType, Object permission) {
		return true;
	}

	@Override
	public boolean hasPermission(Object target, Object perm) {
		return true;
	}

}
