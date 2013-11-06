package cz.cuni.mff.xrg.odcs.frontend.container;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

/**
 * Interface for building predicates used by {@link CriteriaBuilder}.
 *
 * @author Jan Vojt
 * @deprecated used by AuthorizationPredicateBuilder that is deprecated
 */
@Deprecated
public interface PredicateBuilder {

	/**
	 * @return predicate produced
	 */
	public Predicate build();
}
