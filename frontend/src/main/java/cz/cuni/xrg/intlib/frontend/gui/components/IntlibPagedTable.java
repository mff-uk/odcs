package cz.cuni.xrg.intlib.frontend.gui.components;

import com.jensjansson.pagedtable.ControlsLayout;
import com.jensjansson.pagedtable.PagedTable;

/**
 * Intlib extension of PagedTable add-on. PagedTable provides paging for
 * standard Vaadin Table.
 *
 * @author Bogo
 */
public class IntlibPagedTable extends PagedTable {

	/**
	 * Creates controls for navigating between pages of table. Hides the page size selector.
	 * @return {@link ControlsLayout} for accessing table controls.
	 */
	@Override
	public ControlsLayout createControls() {
		ControlsLayout controls = super.createControls();
		controls.getItemsPerPageLabel().setVisible(false);
		controls.getItemsPerPageSelect().setVisible(false);
		return controls;
	}
}
