package cz.cuni.mff.xrg.odcs.frontend.doa.container;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import cz.cuni.mff.xrg.odcs.commons.app.dao.DataAccessRead;
import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import cz.cuni.mff.xrg.odcs.commons.app.dao.DataQueryBuilder;
import cz.cuni.mff.xrg.odcs.frontend.container.ClassAccessor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link ContainerSource}. Has data caching abilities.
 *
 * @author Petyr
 * @param <T>
 */
public class CachedSource<T extends DataObject>
		implements ContainerSource<T>,
		ContainerSource.Filterable,
		ContainerSource.Sortable {

	private static final Logger LOG = LoggerFactory.getLogger(CachedSource.class);
	
	/**
	 * Store size of data set in database.
	 */
	protected Integer size;

	/**
	 * Store cached data.
	 */
	protected final Map<Long, T> data = new HashMap<>();

	/**
	 * Store indexes for current data.
	 */
	protected final Map<Integer, Long> dataIndexes = new HashMap<>();

	/**
	 * Data source.
	 */
	protected final DataAccessRead<T> source;

	/**
	 * The query builder.
	 */
	protected final DataQueryBuilder<T> queryBuilder;
	
	/**
	 * Filters that can be set by {@link Filterable} interface.
	 */
	protected final List<Filter> filters = new LinkedList<>();

	/**
	 * Special set of core filters.
	 */
	protected final List<Filter> coreFilters;

	protected final ClassAccessor<T> classAccessor;
	
	/**
	 * Initialize the source with given data access. No core filters are used.
	 * @param access 
	 * @param classAccessor 
	 */
	public CachedSource(DataAccessRead<T> access, ClassAccessor<T> classAccessor) {
		this.source = access;
		this.queryBuilder = source.createQueryBuilder();
		this.coreFilters = null;
		this.classAccessor = classAccessor;
	}
	
	/**
	 * Initialize the source with given data access. The core filters
	 * are apply before every query, ant the list is used as reference.
	 * That means that changes in list changed the used filters in source.
	 * @param access 
	 * @param classAccessor 
	 * @param coreFilters 
	 */
	public CachedSource(DataAccessRead<T> access, ClassAccessor<T> classAccessor, 
			List<Filter> coreFilters) {
		this.source = access;
		this.queryBuilder = source.createQueryBuilder();
		this.coreFilters = coreFilters;
		this.classAccessor = classAccessor;
	}
	
	/**
	 * Invalidate data cache.
	 */
	public void invalidate() {
		size = null;
		data.clear();
		dataIndexes.clear();
	}

	/**
	 * Load data size from {@link #source} and store the value into
	 * {@link #size}.
	 */
	protected void loadSize() {
		applyFilters();
		size = (int) source.executeSize(queryBuilder.getCountQuery());
	}

	/**
	 * Read data from {@link #source} with given index.
	 *
	 * @param index
	 */
	protected T loadByIndex(int index) {
		applyFilters();
		T item = source.execute(queryBuilder.getQuery().limit(index, 1));
		if (item == null) {
			return null;
		}
		// add to caches
		data.put(item.getId(), item);
		dataIndexes.put(index, item.getId());
		return item;
	}

	/**
	 * Load data on given indexes and return list of their IDs.
	 *
	 * @param startIndex
	 * @param numberOfItems
	 * @return
	 */
	protected List<Long> loadByIndex(int startIndex, int numberOfItems) {
		applyFilters();		
		List<T> items = source.executeList(queryBuilder.getQuery().limit(startIndex, numberOfItems));
		// add to chaces
		List<Long> newIDs = new ArrayList<>(numberOfItems); 
		for (T item : items) {
			data.put(item.getId(), item);
			dataIndexes.put(startIndex++, item.getId());
			newIDs.add(item.getId());
		}
		return newIDs;
	}

	/**
	 * Read data from {@link #source} with given ID.
	 *
	 * @param id
	 */
	protected void loadById(Long id) {
		applyFilters();
	}

	/**
	 * Re-apply all filters to the {@link #queryBuilder}. If it's filterable.
	 */
	protected void applyFilters() {
		// TODO we can optimize and do not set those filter twice .. 
		
		if (queryBuilder instanceof DataQueryBuilder.Filterable) {
			// ok continue
		} else {
			LOG.warn("Can not set filters on nonfilterable query builder. The filters are ignored.");
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
		// add core filters if eqists
		if (coreFilters != null) {
			for (Filter filter : coreFilters) {
				filtrableBuilder.addFilter(filter);
			}
		}
	}
	
	@Override
	public int size() {
		if (size == null) {
			loadSize();
		}
		return size;
	}

	@Override
	public T getObject(Long id) {
		if (data.containsKey(id)) {
			// the data are already cached
		} else {
			loadById(id);
		}
		return data.get(id);
	}

	@Override
	public T getObjectByIndex(int index) {
		if (dataIndexes.containsKey(index)) {
			// we have data
			return data.get(dataIndexes.get(index));
		} else {
			return loadByIndex(index);
		}
	}

	@Override
	public boolean containsId(Long id) {
		if (data.containsKey(id)) {
			return true;
		}
		LOG.debug("containsId called on non-cached data .. this generates the query into database");
		// try to load that object
		loadById(id);
		// ask again		
		return data.containsKey(id);
	}

	@Override
	public List<?> getItemIds(int startIndex, int numberOfItems) {
		List<Long> result = new ArrayList<>(numberOfItems);
		// first try to load data from cache
		int endIndex = startIndex + numberOfItems;
		for (int index = startIndex; index < endIndex; ++index) {
			if (dataIndexes.containsKey(index)) {
				// we havedata
				result.add(dataIndexes.get(index));
			} else {
				// some data are mising, we have to load them
				final int toLoad = numberOfItems - (index - startIndex);
				List<Long> newIDs = loadByIndex(index, toLoad);
				result.addAll(newIDs);
				break;
			}
		}
		return result;
	}

	@Override
	public int indexOfId(Long itemId) {
		for (Integer index : dataIndexes.keySet()) {
			if (dataIndexes.get(index) == itemId) {
				return index;
			}
		}

		throw new RuntimeException("Can not determine the index of non cached data.");
	}

	@Override
	public ClassAccessor<T> getClassAccessor() {
		return classAccessor;
	}
	
	@Override
	public void addFilter(Container.Filter filter) {
		filters.add(filter);
		// and invalidate data
		invalidate();
	}

	@Override
	public void removeFilter(Container.Filter filter) {
		filters.remove(filter);
		// and invalidate data
		invalidate();

	}

	@Override
	public void removeAllFilters() {
		filters.clear();
		// and invalidate data
		invalidate();
	}

	@Override
	public Collection<Container.Filter> getFilters() {
		return filters;
	}

	@Override
	public void sort(Object[] propertyId, boolean[] ascending) {
		if (queryBuilder instanceof DataQueryBuilder.Sortable) {
			// ok continue
		} else {
			LOG.warn("Call of sort(Objet[], boolean[]) on non sortable-source ignored.");
			return;
		}
		
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
				sortableBuilder.sort((String) propertyId[0], ascending[0]);
				break;
		}
		
	}

}
