package cz.cuni.xrg.intlib.frontend.container;

import cz.cuni.xrg.intlib.commons.app.auth.AuthenticationContext;
import cz.cuni.xrg.intlib.commons.app.auth.IntlibPermissionEvaluator;
import cz.cuni.xrg.intlib.commons.app.auth.SharedEntity;
import cz.cuni.xrg.intlib.commons.app.user.OwnedEntity;
import cz.cuni.xrg.intlib.commons.app.user.Role;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Builder of predicate with authentication logic deciding about permissions on
 * entity selected in query.
 * 
 * @author Jan Vojt
 * @param <E> entity class selected in query
 */
public class AuthorizationPredicateBuilder<E> implements PredicateBuilder {
	
	private AuthenticationContext authCtx;
	
	private Root<E> root;
	
	private CriteriaQuery query;
	
	private CriteriaBuilder builder;
	
	private final Class<E> entityClass;
	
	/**
	 * A set of predicates deciding authorization logic. Each predicate is
	 * indexed by class to which it is relevant.
	 */
	private Map<Class, PredicateBuilder> predicates = new HashMap<>();

	public AuthorizationPredicateBuilder(
			AuthenticationContext authCtx,
			Root<E> root,
			CriteriaQuery query,
			CriteriaBuilder builder,
			Class<E> entityClass) {
		
		this.authCtx = authCtx;
		this.root = root;
		this.query = query;
		this.builder = builder;
		this.entityClass = entityClass;
		preparePredicates();
	}

	/**
	 * Builds the filtering predicate.
	 * 
	 * @return predicate deciding about permission
	 */
	@Override
	public Predicate build() {
		
		// Disjunction with zero disjuncts is always false,
		// which corresponds with restrictive policy of PermissionEvaluator.
		Predicate predicate = builder.or();
		
		for (Map.Entry<Class, PredicateBuilder> e : predicates.entrySet()) {
			if (e.getKey().isAssignableFrom(entityClass)) {
				// predicate is relevant for our entity
				predicate = builder.or(predicate, e.getValue().build());
			}
		}
		
		return predicate;
	}
	
	/**
	 * Builds predicates for incorporating permission logic into query.
	 * Should be consistent with <code>view</code> permission on entity in
	 * {@link IntlibPermissionEvaluator#hasPermission(org.springframework.security.core.Authentication, java.lang.Object, "view") }.
	 */
	private void preparePredicates() {
		
		// admin role predicate
		if (authCtx.getAuthentication().getAuthorities().contains(Role.ROLE_ADMIN)) {
			
			predicates.put(Object.class, new PredicateBuilder() {

				@Override
				public Predicate build() {
					return builder.and();
				}
			});
			
		} else {
		
			predicates.put(OwnedEntity.class, new PredicateBuilder() {

				@Override
				public Predicate build() {
					return builder.and(
							builder.equal(root.get("owner"),
							authCtx.getUser())
					);
				}
			});

			predicates.put(SharedEntity.class, new PredicateBuilder() {

				@Override
				public Predicate build() {
					return builder.equal(root.get("public"), true);
				}
			});

			// TODO special rules for pipelines
		}
	}

}
