package cz.cuni.xrg.intlib.frontend.gui.views;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;

/**
 * String filter for the DPU Tree. Matching tree items that start with or contain a
 * specified string.
 * 
 * @author Maria Kukhar 
 */


public final class SimpleTreeFilter implements Filter {

	private static final long serialVersionUID = -449297335044439900L;
	final String filterString;
    final boolean ignoreCase;
    final boolean onlyMatchPrefix;

    public SimpleTreeFilter( String filterString,
            boolean ignoreCase, boolean onlyMatchPrefix) {

        this.filterString = ignoreCase ? filterString.toLowerCase()
                : filterString;
        this.ignoreCase = ignoreCase;
        this.onlyMatchPrefix = onlyMatchPrefix;
    }

    @Override
    public boolean passesFilter(Object itemId, Item item) {
        final String value = ignoreCase ? itemId.toString()
                .toLowerCase() : itemId.toString();
        if (onlyMatchPrefix) {
            if (!value.startsWith(filterString)) {
                return false;
            }
        } else {
            if (!value.contains(filterString)) {
                return false;
            }
        }
        return true;
    }


    @Override
    public boolean equals(Object obj) {

        // Only ones of the objects of the same class can be equal
        if (!(obj instanceof SimpleTreeFilter)) {
            return false;
        }
        final SimpleTreeFilter o = (SimpleTreeFilter) obj;

        
        if (filterString != o.filterString && o.filterString != null
                && !o.filterString.equals(filterString)) {
            return false;
        }
        if (ignoreCase != o.ignoreCase) {
            return false;
        }
        if (onlyMatchPrefix != o.onlyMatchPrefix) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return  (filterString != null ? filterString.hashCode() : 0);
    }

    

    public String getFilterString() {
        return filterString;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    
    public boolean isOnlyMatchPrefix() {
        return onlyMatchPrefix;
    }

	@Override
	public boolean appliesToProperty(Object propertyId) {
		// TODO Auto-generated method stub
		return true;
	}
}
