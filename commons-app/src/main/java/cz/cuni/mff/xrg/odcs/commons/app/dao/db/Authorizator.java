package cz.cuni.mff.xrg.odcs.commons.app.dao.db;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Adds filters to container based on authorization logic.
 *
 * @author Jan Vojt
 * @author Petyr
 */
public interface Authorizator {
        
    /**
     * Return authorization {@link Predicat}.
     * @param cb
     * @param root
     * @param entityClass
     * @return 
     */
    Predicate getAuthorizationPredicate(CriteriaBuilder cb, Root<?> root, Class<?> entityClass);
    
}
