package cz.cuni.mff.xrg.odcs.frontend.container;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Level;

/**
 * Filter for testing presence in given collection.
 *
 * @author Bogo
 * @deprecated can be removed with old logs table
 */
@Deprecated
public class InFilter implements Filter {

	Set<?> collection;
	String name;

	public InFilter(Set<?> collection, String name) {
		this.collection = collection;
		this.name = name;
	}

	@Override
	public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException {
		Level level = (Level) item.getItemProperty("level").getValue();
		return collection.contains(level);
	}

	@Override
	public boolean appliesToProperty(Object propertyId) {
		if ("level".equals(propertyId)) {
			return true;
		}
		return false;
	}

	public Set<String> getStringSet() {
		HashSet<String> stringSet = new HashSet<>(collection.size());
		for (Object lvl : collection) {
			stringSet.add(lvl.toString());
		}
		return stringSet;
	}
}
