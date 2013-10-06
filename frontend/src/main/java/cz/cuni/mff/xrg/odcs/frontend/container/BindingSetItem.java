package cz.cuni.mff.xrg.odcs.frontend.container;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import java.util.Collection;
import org.openrdf.query.BindingSet;

/**
 * Implementation of {@link Item} interface for underlying {@link BindingSet} object.
 *
 * @author Bogo
 */
public class BindingSetItem implements Item {

	private BindingSet binding;
	private int id;

	public BindingSetItem(BindingSet binding, int id) {
		this.binding = binding;
		this.id = id;
	}

	@Override
	public Property getItemProperty(Object id) {
		if ("id".equals(id)) {
			return new ObjectProperty(this.id);
		}
		if (id.getClass() != String.class) {
			return null;
		}
		String sId = (String) id;
		if (binding.hasBinding(sId)) {
			return new ObjectProperty(binding.getValue(sId));
		}
		return null;
	}

	@Override
	public Collection<?> getItemPropertyIds() {
		return binding.getBindingNames();
	}

	@Override
	public boolean addItemProperty(Object id, Property property) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Not supported for BindingSetItem.");
	}

	@Override
	public boolean removeItemProperty(Object id) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Not supported for BindingSetItem.");
	}
}
