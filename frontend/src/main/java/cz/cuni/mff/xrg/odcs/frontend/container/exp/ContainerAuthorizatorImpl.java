package cz.cuni.mff.xrg.odcs.frontend.container.exp;

import com.vaadin.data.Container;
import com.vaadin.data.util.filter.Compare;
import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthenticationContext;
import cz.cuni.mff.xrg.odcs.commons.app.auth.SharedEntity;
import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.ValuePostEvaluator;
import cz.cuni.mff.xrg.odcs.commons.app.user.OwnedEntity;
import cz.cuni.mff.xrg.odcs.commons.app.user.Role;
import cz.cuni.mff.xrg.odcs.commons.app.user.User;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Adds filters to container based on authorization logic.
 *
 * @author Jan Vojt
 * @deprecated unused class, will be removed
 */
@Deprecated
public class ContainerAuthorizatorImpl implements ContainerAuthorizator {

	@Autowired
	private AuthenticationContext authCtx;
	
	private Map<Class,Container.Filter> filters;
	
	@PostConstruct
	public void init() {
		
		ValuePostEvaluator<User> currentUser = new ValuePostEvaluator<User>() {
			@Override
			public User evaluate() {
				return authCtx.getUser();
			}
		};
		
		filters = new HashMap<>();
		filters.put(SharedEntity.class, new Compare.Equal("public", true));
		filters.put(OwnedEntity.class, new Compare.Equal("owner", currentUser));
	}
	
	@Override
	public void authorize(Container.Filterable container, Class<? extends DataObject> entityClass) {
		
		// admin is almighty
		if (authCtx.getAuthentication().getAuthorities().contains(Role.ROLE_ADMIN)) {
			return;
		}
		
		// otherwise apply filters for normal user
		for (Map.Entry<Class, Container.Filter> e : filters.entrySet()) {
			if (e.getKey().isAssignableFrom(entityClass)) {
				// predicate is relevant for our entity
				container.addContainerFilter(e.getValue());
			}
		}
	}

}
