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
public interface DataQueryBuilder<T> {

    /**
     * Provide methods that can be used to apply sort in {@link DataQuery}
     *
     * @param <T>
     */
    public interface Sortable<T> {

        /**
         *
         * @param clazz If null sort clause is removed.
         * @param propertyName
         * @param asc
         * @return
         */
        DataQueryBuilder<T> sort(Class<?> clazz, String propertyName, boolean asc);
    }

    /**
     * Provide possibility to filter data in {@link DataQuery}
     *
     * @param <T>
     */
    public interface Filterable<T> {

        /**
         * Remove all filters from.
         *
         * @return
         */
        DataQueryBuilder<T> filterClear();

        /**
         * Add filter.
         *
         * @param clazz
         * @param type
         * @param value
         * @return
         */
        DataQueryBuilder<T> filter(Class<?> clazz, FilterType type, Object value);

        /**
         * Add filter.
         *
         * @param clazz
         * @param propertyName
         * @param type
         * @param value
         * @return
         */
        DataQueryBuilder<T> filter(Class<?> clazz, String propertyName, FilterType type, Object value);
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
