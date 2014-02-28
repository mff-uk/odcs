package cz.cuni.mff.xrg.odcs.frontend.auxiliaries;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;

/**
 * String filter for the DPURecord Tree. Matching tree items that start with or
 * contain a specified string.
 *
 * @author Maria Kukhar
 */
public final class SimpleTreeFilter implements Filter {

	private static final long serialVersionUID = -449297335044439900L;
	final String filterString;
	final boolean ignoreCase;
	final boolean onlyMatchPrefix;

	/**
	 * Constructor of simple tree filter.
	 * 
	 * @param filterString Filter value.
	 * @param ignoreCase Whether to ignore case.
	 * @param onlyMatchPrefix Whether to match only prefix.
	 */
	public SimpleTreeFilter(String filterString,
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

		// Checks the properties one by one
		if (!filterString.equals(o.filterString) && o.filterString != null
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
		return (filterString != null ? filterString.hashCode() : 0);
	}

	/**
	 * Returns the filter string.
	 *
	 * @return filter string given to the constructor
	 */
	public String getFilterString() {
		return filterString;
	}

	/**
	 * Returns whether the filter is case-insensitive or case-sensitive.
	 *
	 * @return true if performing case-insensitive filtering, false for
	 * case-sensitive
	 */
	public boolean isIgnoreCase() {
		return ignoreCase;
	}

	/**
	 * Returns true if the filter only applies to the beginning of the value
	 * string, false for any location in the value.
	 *
	 * @return true if checking for matches at the beginning of the value only,
	 * false if matching any part of value
	 */
	public boolean isOnlyMatchPrefix() {
		return onlyMatchPrefix;
	}

	@Override
	public boolean appliesToProperty(Object propertyId) {

		return true;
	}
}
