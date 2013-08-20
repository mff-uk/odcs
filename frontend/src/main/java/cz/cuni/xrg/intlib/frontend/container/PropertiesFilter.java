/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.xrg.intlib.frontend.container;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;

/**
 *
 * @author Bogo
 */
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
