package cz.cuni.mff.xrg.odcs.commons.app.dao.db;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * used to translate user defined filters into javax.persistence predicates,
 * that can be used in queries.
 * 
 * @author Petyr
 */
public interface FilterTranslator {
    	
    /**
     * Translate given filter. Return null it the filter is unknown to the 
	 * translator.
	 * 
     * @param filter
     * @param cb
     * @param root
     * @return Translated predicate, or null.
     */
    Predicate translate(Object filter, CriteriaBuilder cb, Root<?> root);
 
	/**
	 * Explain given filter. Return null it the filter is unknown to the 
	 * translator.
	 * 
	 * @param filter
	 * @return Explanation or null.
	 */
	FilterExplanation explain(Object filter);
	
}
