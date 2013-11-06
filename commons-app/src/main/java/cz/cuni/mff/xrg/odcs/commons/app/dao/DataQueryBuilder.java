package cz.cuni.mff.xrg.odcs.commons.app.dao;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbQuery;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbQueryCount;

/**
 * Builder for {@link DataQuery} and {@link DataQueryCount}.
 *
 * @author Petyr
 *
 * @param <T>
 */
public interface DataQueryBuilder<T extends DataObject> {

    /**
     * Provide methods that can be used to apply sort in {@link DataQuery}
     *
     * @param <T>
     */
    public interface Sortable<T extends DataObject> {

        /**
         * Remove previously applied sort and set new.
         * @param propertyName Set to null to remove sorting.
         * @param asc
         * @return
         */
        DataQueryBuilder<T> sort(String propertyName, boolean asc);
        
    }

    /**
     * Provide possibility to filter data in {@link DataQuery}
     *
     * @param <T>
     */
    public interface Filterable<T extends DataObject> {

        /**
         * Remove all user applied filters.
         * @return 
         */
        DataQueryBuilder<T> claerFilters();
        
        /**
         * Add given filter as AND to existing filters.
         *
         * @param filter
         * @return
         */
        DataQueryBuilder<T> addFilter(Object filter);

    }

    /**
     *
     * @return
     */
    DbQuery<T> getQuery();

    /**
     * Generate query that can be used to obtain size of result data.
     *
     * @return
     */
    DbQueryCount<T> getCountQuery();

}
