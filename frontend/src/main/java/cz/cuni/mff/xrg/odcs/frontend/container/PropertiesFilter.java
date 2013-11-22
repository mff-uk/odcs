package cz.cuni.mff.xrg.odcs.frontend.container;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;

/**
 * Filter for property filtering in database. 
 *
 * @author Bogo
 * @deprecated can be removed with the old log table
 */
@Deprecated
public class PropertiesFilter implements Filter {

	Object value;
	String parameterName;

	public PropertiesFilter(String parameterName, Object parameterValue) {
		this.value = parameterValue;
		this.parameterName = parameterName;
	}

	@Override
	public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean appliesToProperty(Object propertyId) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
