package cz.cuni.mff.xrg.odcs.commons.app.dao.db;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import cz.cuni.mff.xrg.odcs.commons.app.dao.DataQueryBuilder;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DbQueryBuilderImpl<T extends DataObject> implements DbQueryBuilder<T> {

    /**
     * Holds information about sorting.
     */
    private class SortInformation {
        
        String propertyName = null;
        
        boolean asc = true;
        
    }
    
    private final static Logger LOG = LoggerFactory.getLogger(DbQueryBuilderImpl.class);

    /**
     * Entity manager used to create query.
     */
    private final EntityManager entityManager;

    /**
     * The main class we are querying on.
     */
    private final Class<T> entityClass;

    /**
     * Filters that should be used in query.
     */
    private final List<Object> filters = new LinkedList<>();

    /**
     * Store information about sorting for this query builder.
     */
    private final SortInformation sortInfo = new SortInformation();
    
    /**
     * Authorizator.
     */
    private final Authorizator authorizator;
    
    /**
     * Translator that can be used to translate given filter.
     */
    private final List<FilterTranslator> filterTranslators;
    
    DbQueryBuilderImpl(EntityManager entityManager, Class<T> entityClass, 
        Authorizator authorizator, List<FilterTranslator> filterTranslators) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
        this.authorizator = authorizator;
        this.filterTranslators = filterTranslators;
    }

    @Override
    public DbQuery<T> getQuery() {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<T> cq = cb.createQuery(entityClass);
        final Root<T> root = cq.from(entityClass);

        cq.select(root);

        setWhereCriteria(cb, cq, root);
        setOrderClause(cb, cq, root);

        TypedQuery<T> query = entityManager.createQuery(cq);
        return new DbQuery(query);
    }

    @Override
    public DbQueryCount<T> getCountQuery() {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        final Root<T> root = cq.from(entityClass);

        cq.select(cb.count(root));

        // we just set where criteria
        setWhereCriteria(cb, cq, root);

        TypedQuery<Long> query = entityManager.createQuery(cq);
        return new DbQueryCount(query);
    }

    @Override
    public DataQueryBuilder<T> claerFilters() {
        filters.clear();
        return this;
    }

    @Override
    public DataQueryBuilder<T> addFilter(Object filter) {
        filters.add(filter);
        return this;
    }

    @Override
    public DataQueryBuilder<T> sort(String propertyName, boolean asc) {
        sortInfo.propertyName = propertyName;
        sortInfo.asc = asc;
        return this;
    }

    private <E> void setWhereCriteria(final CriteriaBuilder cb, final CriteriaQuery<E> cq, Root<T> root) {
        // here we use the authentication predicate
        Predicate predicate = null;
        if (authorizator != null) {
            predicate = authorizator.getAuthorizationPredicate(cb, root, entityClass);
        }
        
        for (Object filter : filters) {
            Predicate filterPredicate = null;
            if (filter instanceof Predicate) {
                // we can use this directly
                filterPredicate = (Predicate)filter;
            } else if (filterTranslators != null) {            
                // try to translate it
                for (FilterTranslator translator : filterTranslators) {
                    filterPredicate = translator.translate(filter, cb, root);
                    if (filterPredicate != null) {
                        break;
                    }
                }
            }
            // has been the filter translated ?
            if (filterPredicate == null) {
                throw new UnsupportedOperationException("Filter: " + filter.getClass().getName() + " is not supported.");
            }
            // add to our predicate
            if (predicate == null) {
                predicate = filterPredicate;
            } else {
                predicate = cb.and(predicate, filterPredicate);
            }
        }
        
        // apply filters
        if (predicate != null) {
            cq.where(predicate);
        } else {
            // nothing to set
        }
    }
    
    private <E> void setOrderClause(final CriteriaBuilder cb, final CriteriaQuery<E> cq, final Root<T> root) {
        if (sortInfo.propertyName == null) {
            return;
        }
        final Expression expr = (Expression) getPropertyPath(root, 
            sortInfo.propertyName);

        // we sort only accroding to one column
        if (sortInfo.asc) {
            cq.orderBy(cb.asc(expr));
        } else {
            cq.orderBy(cb.desc(expr));
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
