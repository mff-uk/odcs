/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.xrg.intlib.frontend.container;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import java.util.Collection;
import java.util.Set;

/**
 *
 * @author Bogo
 */
public class InFilter implements Filter {
    
    Set<?> collection;
    String name;
    
    public InFilter(Set<?> collection, String name) {
        this.collection = collection;
        this.name = name;
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
