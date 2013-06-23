package cz.cuni.xrg.intlib.frontend.gui.components;

import com.jensjansson.pagedtable.PagedTable;
import com.jensjansson.pagedtable.PagedTableContainer;
import com.vaadin.data.Container;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;

/**
 *
 * @author Bogo
 */
public class IntlibPagedTable extends PagedTable {

//	private PagedTableContainer container;
//
//	@Override
//	public void setContainerDataSource(Container container)
//	{
//		this.container = new PagedTableContainer((Container.Indexed) container);
//		super.setContainerDataSource(container);
//	}
//
	@Override
public HorizontalLayout createControls() {
		HorizontalLayout controls = super.createControls();
		controls.getComponent(0).setVisible(false);
		return controls;
    }


}
