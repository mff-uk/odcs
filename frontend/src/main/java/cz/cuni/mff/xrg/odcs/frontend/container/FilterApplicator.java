package cz.cuni.mff.xrg.odcs.frontend.container;

import com.vaadin.data.Container;
import com.vaadin.data.util.filter.Between;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Not;
import com.vaadin.data.util.filter.SimpleStringFilter;
import cz.cuni.mff.xrg.odcs.commons.app.dao.DataQueryBuilder;
import cz.cuni.mff.xrg.odcs.commons.app.dao.FilterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Apply vaadin filters to {@link DbQueryBuilder}.
 *
 * @author Petyr
 */
class FilterApplicator {

    private static final Logger LOG = LoggerFactory.getLogger(FilterApplicator.class);

    /**
     * Apply given filter to given builder using given class.
     *
     * @param builder
     * @param entityClass
     * @param filter
     */
    public void apply(DataQueryBuilder.Filterable<?> builder, Class<?> entityClass, Container.Filter filter) {
        if (filter instanceof SimpleStringFilter) {
            apply(builder, entityClass, (SimpleStringFilter) filter);
        } else if (filter instanceof Compare) {
            apply(builder, entityClass, (Compare) filter);
        } else if (filter instanceof Not) {
            LOG.warn("Detectec usage of unsupported filter: {}", filter.getClass().getSimpleName());
        } else if (filter instanceof Between) {
            apply(builder, entityClass, (Between) filter);
        } else {
            // unsupported filter
            LOG.warn("Detectec usage of unsupported filter: {}", filter.getClass().getSimpleName());
        }
        
        // TODO add support for another filters
    }

    private void apply(DataQueryBuilder.Filterable<?> builder, Class<?> entityClass, SimpleStringFilter filter) {
        final String qString = "**" + filter.getFilterString();
        // update query
        builder.filter(entityClass, (String) filter.getPropertyId(),
            FilterType.LIKE, qString);
    }

    private void apply(DataQueryBuilder.Filterable<?> builder, Class<?> entityClass, Compare filter) {
        FilterType filterType = null;
        switch (filter.getOperation()) {
            case EQUAL:
                filterType = FilterType.EQUAL;
                break;
            case GREATER:
                filterType = FilterType.GREATER;
                break;
            case LESS:
                filterType = FilterType.LESS;
                break;                
            case GREATER_OR_EQUAL:
                filterType = FilterType.GREATER_OR_EQUAL;
                break;
            case LESS_OR_EQUAL:
                filterType = FilterType.LESS_OR_EQUAL;
                break;
        }
        builder.filter(entityClass, (String) filter.getPropertyId(), filterType,
            filter.getValue());
    }

    private void apply(DataQueryBuilder.Filterable<?> builder, Class<?> entityClass, Between filter) {
        // we use: property >= start && AND property <= end
        builder.filter(entityClass, (String) filter.getPropertyId(), FilterType.GREATER_OR_EQUAL,
            filter.getStartValue());
        builder.filter(entityClass, (String) filter.getPropertyId(), FilterType.LESS_OR_EQUAL,
            filter.getEndValue());
    }
}
