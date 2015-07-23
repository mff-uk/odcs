/*******************************************************************************
 * This file is part of UnifiedViews.
 *
 * UnifiedViews is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UnifiedViews is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UnifiedViews.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package cz.cuni.mff.xrg.odcs.commons.app.dao.db;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * used to translate user defined filters into {@link javax.persistence.criteria.Predicate} that can be used in queries.
 * 
 * @author Petyr
 */
public interface FilterTranslator {

    /**
     * Translate given filter. Return null it the filter is unknown to the
     * translator.
     * 
     * @param filter
     *            Filter to translate.
     * @param cb
     *            Criteria builder used to create the query.
     * @param root
     *            Root of the query.
     * @return Translated predicate, or null.
     */
    Predicate translate(Object filter, CriteriaBuilder cb, Root<?> root);

    /**
     * Explain given filter. Return null it the filter is unknown to the
     * translator.
     * 
     * @param filter
     *            Filter to explain.
     * @return Explanation or null.
     */
    FilterExplanation explain(Object filter);

}
