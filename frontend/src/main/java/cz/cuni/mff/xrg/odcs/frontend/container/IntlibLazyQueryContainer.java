package cz.cuni.mff.xrg.odcs.frontend.container;

import com.vaadin.data.util.BeanItem;
import javax.persistence.EntityManager;
import org.vaadin.addons.lazyquerycontainer.CompositeItem;
import org.vaadin.addons.lazyquerycontainer.EntityQueryDefinition;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;

/**
 *
 * @author Bogo
 */
public final class IntlibLazyQueryContainer<T> extends LazyQueryContainer {

	/**
	 * Java serialization version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor which configures query definition for accessing JPA entities.
	 *
	 * @param entityManager The JPA EntityManager.
	 * @param entityClass The entity class.
	 * @param idPropertyId The ID of the ID property or null if item index is
	 * used as ID.
	 * @param batchSize The batch size.
	 * @param applicationManagedTransactions True if application manages
	 * transactions instead of container.
	 * @param detachedEntities True if entities are detached from
	 * PersistenceContext.
	 * @param compositeItems True f items are wrapped to CompositeItems.
	 */
	public IntlibLazyQueryContainer(final EntityManager entityManager,
			final Class<?> entityClass, final int batchSize, final Object idPropertyId,
			final boolean applicationManagedTransactions,
			final boolean detachedEntities, final boolean compositeItems) {
		super(new EntityQueryDefinition(applicationManagedTransactions,
				detachedEntities, compositeItems,
				entityClass, batchSize, idPropertyId),
				new IntlibQueryFactory(entityManager));
	}

	/**
	 * Constructor which configures query definition for accessing JPA entities.
	 *
	 * @param entityManager The JPA EntityManager.
	 * @param applicationManagedTransactions True if application manages
	 * transactions instead of container.
	 * @param detachedEntities True if entities are detached from
	 * PersistenceContext. items until commit.
	 * @param compositeItems True if native items should be wrapped to
	 * CompositeItems.
	 * @param entityClass The entity class.
	 * @param batchSize The batch size.
	 * @param nativeSortPropertyIds Properties participating in the native sort.
	 * @param nativeSortPropertyAscendingStates List of property sort directions
	 * for the native sort.
	 * @param idPropertyId Property containing the property ID.
	 */
	public IntlibLazyQueryContainer(final EntityManager entityManager, final boolean applicationManagedTransactions,
			final boolean detachedEntities, final boolean compositeItems,
			final Class<?> entityClass, final int batchSize,
			final Object[] nativeSortPropertyIds, final boolean[] nativeSortPropertyAscendingStates,
			final Object idPropertyId) {
		super(new EntityQueryDefinition(applicationManagedTransactions,
				detachedEntities, compositeItems,
				entityClass, batchSize, idPropertyId),
				new IntlibQueryFactory(entityManager));
		getQueryView().getQueryDefinition().setDefaultSortState(nativeSortPropertyIds,
				nativeSortPropertyAscendingStates);
	}

	/**
	 * Adds entity to the container as first item i.e. at index 0.
	 *
	 * @return the new constructed entity.
	 */
	public T addEntity() {
		final Object itemId = addItem();
		return getEntity(indexOfId(itemId));
	}

	/**
	 * Removes given entity at given index and returns it.
	 *
	 * @param index Index of the entity to be removed.
	 * @return The removed entity.
	 */
	public T removeEntity(final int index) {
		final T entityToRemove = getEntity(index);
		removeItem(getIdByIndex(index));
		return entityToRemove;
	}

	/**
	 * Gets entity by ID.
	 *
	 * @param id The ID of the entity.
	 * @return the entity.
	 */
	@SuppressWarnings("unchecked")
	public T getEntity(final Object id) {
		return getEntity(indexOfId(id));
	}

	/**
	 * Gets entity at given index.
	 *
	 * @param index The index of the entity.
	 * @return the entity.
	 */
	@SuppressWarnings("unchecked")
	public T getEntity(final int index) {
		if (getQueryView().getQueryDefinition().isCompositeItems()) {
			final CompositeItem compositeItem = (CompositeItem) getItem(getIdByIndex(index));
			final BeanItem<T> beanItem = (BeanItem<T>) compositeItem.getItem("bean");
			return beanItem.getBean();
		} else {
			return ((BeanItem<T>) getItem(getIdByIndex(index))).getBean();
		}
	}
	
	public void refreshRow(Long id) {
		
	}
}