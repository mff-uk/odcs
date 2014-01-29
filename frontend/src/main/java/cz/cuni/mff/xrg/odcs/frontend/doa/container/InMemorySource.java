package cz.cuni.mff.xrg.odcs.frontend.doa.container;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataAccessRead;
import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link ContainerSource}. The data are all loaded and hold
 * in memory.
 *
 * Call {@link #loadData(cz.cuni.mff.xrg.odcs.commons.app.dao.DataAccessRead)}
 * method to load data before first use.
 *
 * @author Petyr
 * @param <T>
 */
public class InMemorySource<T extends DataObject> implements ContainerSource<T> {

	private static final Logger LOG = LoggerFactory.getLogger(InMemorySource.class);

	/**
	 * Store data.
	 */
	protected final Map<Long, T> data = new HashMap<>();

	/**
	 * Id's in order.
	 */
	protected final List<Long> ids = new ArrayList<>();

	protected final ClassAccessor<T> classAccessor;

	public InMemorySource(ClassAccessor<T> classAccessor) {
		this.classAccessor = classAccessor;
	}

	public InMemorySource(ClassAccessor<T> classAccessor, DataAccessRead<T> source) {
		this.classAccessor = classAccessor;
		loadData(source);
	}

	/**
	 * Load new data from data source. The old data are deleted.
	 *
	 * @param source
	 */
	public void loadData(DataAccessRead<T> source) {
		// load new data
		final List<T> newData
				= source.executeList((DbQuery<T>) source.createQueryBuilder().getQuery());
		loadData(newData);
	}

	/**
	 * Load new data from data source given list. The old data are deleted.
	 *
	 * @param newData
	 */
	public void loadData(List<T> newData) {
		// clear lists
		data.clear();
		ids.clear();
		// load new data
		for (T item : newData) {
			data.put(item.getId(), item);
			ids.add(item.getId());
		}
		// sort, so we get same order based on id every time
		Collections.sort(ids);
	}

	/**
	 * Add given object into the source.
	 *
	 * @param object
	 */
	public void add(T object) {
		if (data.containsKey(object.getId())) {
			// already here ..
		} else {
			data.put(object.getId(), object);
			// add to ids and sort them, so we get the same
			// id's every time
			ids.add(object.getId());
			Collections.sort(ids);
		}
	}

	/**
	 * Remove object of given id.
	 *
	 * @param id
	 */
	public void remove(Long id) {
		ids.remove(id);
		data.remove(id);
	}

	/**
	 * Hide object of given id. The object can be still acceded under the id by
	 * the {@link #getObject(java.lang.Long) } method but it's id is not
	 * returned by {@link #getItemIds(int, int) }.
	 *
	 * @param id
	 */
	public void hide(Long id) {
		ids.remove(id);
	}

	/**
	 * Show object of given id if it has been previously hidden.
	 *
	 * @param id
	 * @return False if such object is not presented.
	 */
	public boolean show(Long id) {
		if (data.containsKey(id)) {
			// ok, we can add
		} else {
			// no such data ..
			return false;
		}

		// add to ids and sort them, so we get the same
		// id's every time
		ids.add(id);
		Collections.sort(ids);
		return true;
	}

	@Override
	public int size() {
		LOG.info("size() -> {}", ids.size());
		return ids.size();
	}

	@Override
	public T getObject(Long id) {
		return data.get(id);
	}

	@Override
	public T getObjectByIndex(int index) {
		return data.get(ids.get(index));
	}

	@Override
	public int indexOfId(Long itemId) {
		return ids.indexOf(itemId);
	}

	@Override
	public boolean containsId(Long id) {
		return data.containsKey(id);
	}

	@Override
	public List<?> getItemIds(int startIndex, int numberOfItems) {
		LOG.info("getItemIds({}, {})", startIndex, numberOfItems);

		List<Long> result = new ArrayList<>(numberOfItems);
		for (int i = 0; i < numberOfItems; ++i) {
			result.add(i, ids.get(i + startIndex));
		}
		return result;
	}

	@Override
	public ClassAccessor<T> getClassAccessor() {
		return classAccessor;
	}

}
