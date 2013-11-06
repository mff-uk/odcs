package cz.cuni.mff.xrg.odcs.frontend.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.UnsupportedFilterException;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataAccessRead;
import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import cz.cuni.mff.xrg.odcs.commons.app.dao.DataQueryBuilder;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbQuery;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbQueryCount;
import java.util.LinkedList;

/**
 * Implementation of read only container that use 
 * {@link DataAccessRead} as data source.
 * 
 * @author Petyr
 * @param <T> 
 */
public class ReadOnlyContainer<T extends DataObject> implements Container,
    Container.Indexed, Container.Filterable, Container.Sortable,
    Container.ItemSetChangeNotifier, ContainerDescription {

    private final static Logger LOG = LoggerFactory
        .getLogger(ReadOnlyContainer.class);

    private final DataAccessRead<T> dbAccess;

    private final ClassAccessor<T> classAccessor;

    private final Map<Object, GeneralProperty<?, T>> properties = new HashMap<>();

    private final List<Object> propertiesIds = new LinkedList<>();
    
    private final DataQueryBuilder<T> queryBuilder;

    private final Set<Filter> filters = new HashSet<>();

    private final List<ItemSetChangeListener> changeListeners = new LinkedList<>();

    // - - - - - information about data access - - - - 
    private final boolean filterable;

    private final boolean sortable;

    // - - - - - - - - - - - cache - - - - - - - - - -
    private final ValueTimeCache<Integer> sizeCache = new ValueTimeCache<>();

    private final DataTimeCache<T> dataCache = new DataTimeCache<>();

    // - - - - - - - - - - - - -
    @SuppressWarnings("unchecked")
    public ReadOnlyContainer(DataAccessRead<T> dbAccess,
        ClassAccessor<T> classAccessor) {
        this.dbAccess = dbAccess;
        this.classAccessor = classAccessor;
        this.queryBuilder = dbAccess.createQueryBuilder();
        // generate properties
        for (String id : classAccessor.all()) {
            properties.put(id, new GeneralProperty(classAccessor.getType(id),
                id, this));
            // we also add id
            propertiesIds.add(id);
        }
        // check if we can sort and filter
        this.filterable = this.queryBuilder instanceof DataQueryBuilder.Filterable<?>;
        this.sortable = this.queryBuilder instanceof DataQueryBuilder.Sortable<?>;
        // get information from ClassAccessor
        boolean accessorFilterable = classAccessor.filtrable().isEmpty();
        boolean accessorSortable = classAccessor.sortable().isEmpty();
        // check if there is some inconsistance
        if (accessorFilterable && !this.filterable) {
            LOG.warn("Class accessor {} provides columns to filter, but data accessor {} does not support filtering.",
                classAccessor.getClass().getSimpleName(), dbAccess.getClass().getSimpleName());
        }
        if (accessorSortable && !this.sortable) {
            LOG.warn("Class accessor {} provides columns to sort, but data accessor {} does not support sorting.",
                classAccessor.getClass().getSimpleName(), dbAccess.getClass().getSimpleName());
        }
    }

    /**
     * Return object for given id.
     *
     * @param id
     * @return
     */
    public T getObject(Long id) {
        T object = dataCache.get(id);
        if (object == null) {
            object = dbAccess.getInstance(id);
            // we do not cache data here
        }
        return object;
    }

    Map<Object, GeneralProperty<?, T>> getProperties() {
        return properties;
    }

    ClassAccessor<T> getClassAccessor() {
        return classAccessor;
    }

    /**
     * Invalidate caches and emit on change listeners.
     */
    public void refresh() {
        final ReadOnlyContainer<T> container = this;
        // invalidata cache
        sizeCache.invalidate();
        dataCache.invalidate();
        
        
        for (ItemSetChangeListener listener : changeListeners) {
            // emit change
            listener.containerItemSetChange(new ItemSetChangeEvent() {
                @Override
                public Container getContainer() {
                    return container;
                }
            }) ;
        }
    }
    
    // - - - - - - - - - - - - - query - - - - - - - - - - - - - - - - - - -
    private DbQuery<T> getQuery() {
        updateBuilder();
        return queryBuilder.getQuery();
    }

    private DbQueryCount<T> getQueryCount() {
        updateBuilder();
        return queryBuilder.getCountQuery();
    }

    private void updateBuilder() {
        if (!filterable) {
            return;
        }
        DataQueryBuilder.Filterable<T> filtrableBuilder
            = (DataQueryBuilder.Filterable<T>) queryBuilder;
        // clear filters and build news
        filtrableBuilder.claerFilters();
        // add filters
        for (Filter filter : filters) {
            filtrableBuilder.addFilter(filter);
        }
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    @Override
    public Item getItem(Object itemId) {
        LOG.trace("getItem({})", itemId);
        // ...
        return new ValueItem(this, (Long) itemId);
    }

    @Override
    public Collection<?> getContainerPropertyIds() {
        LOG.trace("getContainerPropertyIds()");
        // ...
        return propertiesIds;
    }

    @Override
    public Collection<?> getItemIds() {
        LOG.trace("getItemIds() -> EX");
        // ...
        throw new UnsupportedOperationException();
    }

    @Override
    public Property<?> getContainerProperty(Object itemId, Object propertyId) {
        LOG.trace("getContainerProperty({}, {})", itemId, propertyId);
        // ...
        return properties.get(propertyId).bind((Long) itemId);
    }

    @Override
    public Class<?> getType(Object propertyId) {
        LOG.trace("getType({})", propertyId);
        // ...
        return properties.get(propertyId).getType();
    }

    @Override
    public int size() {
        final Date now = new Date();
        Integer size = sizeCache.get(now);
        if (size == null) {
            // update
            size = (int) dbAccess.executeSize(getQueryCount());
            sizeCache.set(size, now);
            LOG.trace("size() -> {}", size);
        } else {
            LOG.trace("size() -> {} cached", size);
        }
        return size;
    }

    @Override
    public boolean containsId(Object itemId) {
        LOG.trace("containsId({}) -> EX", itemId);
        // ...
        throw new UnsupportedOperationException();
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    @Override
    public Item addItem(Object itemId) throws UnsupportedOperationException {
        LOG.trace("addItem({}) -> EX", itemId);
        // ...
        throw new UnsupportedOperationException();
    }

    @Override
    public Object addItem() throws UnsupportedOperationException {
        LOG.trace("addItem() -> EX");
        // ...
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeItem(Object itemId)
        throws UnsupportedOperationException {
        LOG.trace("removeItem({}) -> EX", itemId);
        // ...
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type,
        Object defaultValue) throws UnsupportedOperationException {
        LOG.trace("addContainerProperty({}, {}, {}) -> EX", propertyId, type,
            defaultValue);
        // ...
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeContainerProperty(Object propertyId)
        throws UnsupportedOperationException {
        LOG.trace("removeContainerProperty({}) -> EX", propertyId);
        // ...
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAllItems() throws UnsupportedOperationException {
        LOG.trace("removeAllItems({}) -> EX");
        // ...
        throw new UnsupportedOperationException();
    }

    // - - - - - - - - - - - - Container.Indexed - - - - - - - - - - - - - - -
    @Override
    public Object nextItemId(Object itemId) {
        LOG.trace("nextItemId({}) -> EX", itemId);
        // ...
        throw new UnsupportedOperationException();
    }

    @Override
    public Object prevItemId(Object itemId) {
        LOG.trace("prevItemId({}) -> EX", itemId);
        // ...
        throw new UnsupportedOperationException();
    }

    @Override
    public Object firstItemId() {
        LOG.trace("firstItemId() -> EX");
        // ...
        throw new UnsupportedOperationException();
    }

    @Override
    public Object lastItemId() {
        LOG.trace("lastItemId() -> EX");
        // ...
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isFirstId(Object itemId) {
        LOG.trace("isFirstId({})", itemId);
        // ...
        return firstItemId() == itemId;
    }

    @Override
    public boolean isLastId(Object itemId) {
        LOG.trace("isLastId({})", itemId);
        // ...
        return lastItemId() == itemId;
    }

    @Override
    public Object addItemAfter(Object previousItemId)
        throws UnsupportedOperationException {
        LOG.trace("addItemAfter({}) -> EX", previousItemId);
        // ...
        throw new UnsupportedOperationException();
    }

    @Override
    public Item addItemAfter(Object previousItemId, Object newItemId)
        throws UnsupportedOperationException {
        LOG.trace("addItemAfter({}, {}) -> EX", previousItemId, newItemId);
        // ...
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOfId(Object itemId) {
        LOG.trace("indexOfId({}) -> 0", itemId);
        // ...
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getIdByIndex(int index) {
        LOG.trace("getIdByIndex({})", index);
        // select given item
        T object = dbAccess.execute(getQuery().limit(index, 1));
        // object can be null
        if (object == null) {
            return null;
        } else {
            return object.getId();
        }
    }

    @Override
    public List<?> getItemIds(int startIndex, int numberOfItems) {
        final Date now = new Date();
        final String cacheKey = Integer.toString(startIndex) + Integer.toString(numberOfItems);

        if (dataCache.isValid(cacheKey, now)) {
            LOG.trace("getItemIds({}, {}) -> CACHED", startIndex, numberOfItems);
            // data are valid, we do not need to reload
            return dataCache.getKeys();
        }

        LOG.trace("getItemIds({}, {})", startIndex, numberOfItems);
        // ...
        List<T> objects = dbAccess.executeList(getQuery().limit(startIndex,
            numberOfItems));
        // invalidate cache
        dataCache.invalidate();
        // add data to cache
        dataCache.setKey(cacheKey, now);
        for (T item : objects) {
            dataCache.set(item.getId(), item);
        }
        // return id
        return dataCache.getKeys();
    }

    @Override
    public Object addItemAt(int index) throws UnsupportedOperationException {
        LOG.trace("addItemAt({}) -> EX", index);
        // ...
        throw new UnsupportedOperationException();
    }

    @Override
    public Item addItemAt(int index, Object newItemId)
        throws UnsupportedOperationException {
        LOG.trace("addItemAt({}, {}) -> EX", index, newItemId);
        // ...
        throw new UnsupportedOperationException();
    }

    // - - - - - - - - - - - - Container.Filterable - - - - - - - - - - - - -
    @Override
    public void addContainerFilter(Filter filter)
        throws UnsupportedFilterException {
        if (!filterable) {
            LOG.trace("addConteinerFilter({}) -> IGNORED", filter);
            return;
        }

        LOG.trace("addConteinerFilter({})", filter);
        // ...
        filters.add(filter);

        // and invalidate queries
    }

    @Override
    public void removeContainerFilter(Filter filter) {
        if (!filterable) {
            LOG.trace("removeContainerFilter({}) -> IGNORED", filter);
            return;
        }

        LOG.trace("removeContainerFilter({})", filter);
        // ...
        filters.remove(filter);

        // and invalidate queries
    }

    @Override
    public void removeAllContainerFilters() {
        if (!filterable) {
            LOG.trace("removeAllContainerFilters() -> IGNORED");
            return;
        }

        LOG.trace("removeAllContainerFilters()");
        // ..
        filters.clear();

        // and invalidate queries
    }

    @Override
    public Collection<Filter> getContainerFilters() {
        LOG.trace("getContainerFilters()");
        // ...
        return filters;
    }

    // - - - - - - - - - - - - Container.Sortable - - - - - - - - - - - - -
    @Override
    public void sort(Object[] propertyId, boolean[] ascending) {

        if (!sortable) {
            // sorting not supported
            LOG.trace("sort(?, ?) -> IGNORED");
            return;
        }
        LOG.trace("sort(?, ?)");

        final DataQueryBuilder.Sortable<T> sortableBuilder
            = (DataQueryBuilder.Sortable<T>) queryBuilder;

        switch (propertyId.length) {
            case 0: // remove sort
                sortableBuilder.sort(null, false);
                break;
            default:
                LOG.warn("sort(Objet[], boolean[]) called with multiple targets."
                    + " Only first used others are ignored.");
            case 1: // sort, but we need expresion for sorting first
                sortableBuilder.sort( (String) propertyId[0], ascending[0]);
                break;
        }

        // and invalidate queries
    }

    @Override
    public Collection<?> getSortableContainerPropertyIds() {
        LOG.trace("getSortableContainerPropertyIds()");
        // ...
        if (sortable) {
            // ok return list from accessor
            return classAccessor.sortable();
        } else {
            // TODO we can warn if classAccessor has some sortable columns
            final List<String> emptyList = new ArrayList<>(0);
            return emptyList;
        }
    }

    // - - - - - - - - - - - Container.ItemSetChangeNotifier - - - - - - - - -
    @Override
    public void addItemSetChangeListener(ItemSetChangeListener listener) {
        LOG.trace("addItemSetChangeListener(?)");
        // ...
        changeListeners.add(listener);
    }

    @Override
    public void addListener(ItemSetChangeListener listener) {
        // this method is deprecated, so just recall the right one
        addItemSetChangeListener(listener);
    }

    @Override
    public void removeItemSetChangeListener(ItemSetChangeListener listener) {
        LOG.trace("removeListener(?)");
        // ...
        changeListeners.remove(listener);
    }

    @Override
    public void removeListener(ItemSetChangeListener listener) {
        // this method is deprecated, so just recall the right one
        removeItemSetChangeListener(listener);
    }

    // - - - - - - - - - - - - - ContainerDescription - - - - - - - - - - - -
    
    @Override
    public List<String> getFilterables() {
        if (filterable) {
            // ok return list from accessor
            return classAccessor.filtrable();
        } else {
            // TODO we can warn if classAccessor has some sortabel columns
            final List<String> emptyList = new ArrayList<>(0);
            return emptyList;
        }
    }

    @Override
    public String getColumnName(String id) {
        return classAccessor.getColumnName(id);
    }

}
