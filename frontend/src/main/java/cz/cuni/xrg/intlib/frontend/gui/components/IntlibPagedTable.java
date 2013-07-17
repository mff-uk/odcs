package cz.cuni.xrg.intlib.frontend.gui.components;

import com.jensjansson.pagedtable.ControlsLayout;
import com.vaadin.ui.HorizontalLayout;
import org.tepi.filtertable.paged.PagedFilterTable;

/**
 * Intlib extension of FilterTable add-on. PagedFilterTable provides paging for
 * custom Vaadin Table.
 *
 * @author Bogo
 */
public class IntlibPagedTable extends PagedFilterTable {
	
	public IntlibPagedTable() {
		super();
		setFilterBarVisible(true);
		setFilterDecorator(new IntlibFilterDecorator());
	}

	/**
	 * Creates controls for navigating between pages of table. Hides the page size selector.
	 * @return {@link ControlsLayout} for accessing table controls.
	 */
	@Override
	public HorizontalLayout createControls() {
//		ControlsLayout controls = super.createControls();
//		controls.getItemsPerPageLabel().setVisible(false);
//		controls.getItemsPerPageSelect().setVisible(false);
//		return controls;
		
		HorizontalLayout controls = super.createControls();
		//Disabling displaying of page size selector.
		controls.getComponent(0).setVisible(false);
		return controls;
	}
	
}
