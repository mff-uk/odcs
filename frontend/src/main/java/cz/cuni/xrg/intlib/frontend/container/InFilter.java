/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.xrg.intlib.frontend.container;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.Level;

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
        Level level = (Level)item.getItemProperty("level").getValue();
        return collection.contains(level);
    }

    @Override
    public boolean appliesToProperty(Object propertyId) {
        if("level".equals(propertyId)) {
            return true;
        }
        return false;
    }
    
    public Set<String> getStringSet() {
        HashSet<String> stringSet = new HashSet<>(collection.size());
        for(Object lvl : collection) {
            stringSet.add(lvl.toString());
        }
        return stringSet;
    }
    
}
