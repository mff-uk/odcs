package cz.cuni.mff.xrg.odcs.commons.app.dao.db.filter;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.FilterTranslator;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Translator for filters from this package.
 *
 * @author Petyr
 */
class ODCSFilterTranslator implements FilterTranslator {

	@Override
	public Predicate translate(Object filter, CriteriaBuilder cb, Root<?> root) {

		if (filter instanceof BaseFilter) {
			// ok, continue
		} else {
			return null;
		}

		BaseFilter baseFilter = (BaseFilter) filter;
		// prepare filter expresion
		final Expression<Comparable> property
				= (Expression) getPropertyPath(root, baseFilter.propertyName);

		if (filter instanceof Compare) {
			Compare compare = (Compare) filter;

			switch (compare.type) {
				case EQUAL:
					return cb.equal(property, compare.propertyValue);
				case GREATER:
					return cb.greaterThan(property, (Comparable) compare.propertyValue);
				case GREATER_OR_EQUAL:
					return cb.greaterThanOrEqualTo(property, (Comparable) compare.propertyValue);
				case LESS:
					return cb.lessThan(property, (Comparable) compare.propertyValue);
				case LESS_OR_EQUAL:
					return cb.lessThanOrEqualTo(property, (Comparable) compare.propertyValue);
				default:
					return null;
			}

		} else {
			// unknown filter
			return null;
		}

	}

	/**
	 * Gets property path.
	 *
	 * @param root the root where path starts form
	 * @param propertyId the property ID
	 * @return the path to property
	 */
	private Path<Object> getPropertyPath(final Root<?> root, final Object propertyId) {
		final String[] propertyIdParts = ((String) propertyId).split("\\.");

		Path<Object> path = null;
		for (final String part : propertyIdParts) {
			if (path == null) {
				path = root.get(part);
			} else {
				path = path.get(part);
			}
		}
		return path;
	}

}
