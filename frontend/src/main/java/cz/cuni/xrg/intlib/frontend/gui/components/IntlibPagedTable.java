package cz.cuni.xrg.intlib.frontend.gui.components;

import com.jensjansson.pagedtable.ControlsLayout;
import com.jensjansson.pagedtable.PagedTable;

/**
 *
 * @author Bogo
 */
public class IntlibPagedTable extends PagedTable {

	@Override
public ControlsLayout createControls() {
		ControlsLayout controls = super.createControls();
		controls.getItemsPerPageLabel().setVisible(false);
		controls.getItemsPerPageSelect().setVisible(false);
		return controls; 
    }


}
