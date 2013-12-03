package cz.cuni.mff.xrg.odcs.frontend.container;

import java.util.Collection;

import com.vaadin.data.Item;
import com.vaadin.data.Property;

public class ValueItem implements Item {

	private final ReadOnlyContainer<?> container;
	
	private final Long objectId;
	
	ValueItem(ReadOnlyContainer<?> container, Long objectId) {
		this.container = container;
		this.objectId = objectId;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Property getItemProperty(Object id) {
		return container.getProperties().get(id).bind(objectId);
	}

	@Override
	public Collection<?> getItemPropertyIds() {
		// every item has all values
		return container.getProperties().keySet();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean addItemProperty(Object id, Property property)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeItemProperty(Object id)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Return embedded object id.
	 * @return 
	 */
	public Long getId() {
		return objectId;
	}
	
}
