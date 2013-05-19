package cz.cuni.xrg.intlib.frontend.gui.components;

import com.jensjansson.pagedtable.PagedTable;
import com.vaadin.data.Container;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import cz.cuni.xrg.intlib.commons.app.rdf.RDFTriple;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.auxiliaries.ContainerFactory;

import java.util.List;

/**
 *
 * @author Bogo
 */
public class BrowserTable extends CustomComponent {

	private PagedTable dataTable;
	private VerticalLayout mainLayout;

	public BrowserTable(List<RDFTriple> data) {

		mainLayout = new VerticalLayout();
		loadBrowserTable(data);
		dataTable.setWidth("100%");
		dataTable.setHeight("100%");
		mainLayout.addComponent(dataTable);
		mainLayout.addComponent(dataTable.createControls());
		setCompositionRoot(mainLayout);

	}

	private void loadBrowserTable(List<RDFTriple> data) {
		dataTable = new PagedTable();
		Container container = ContainerFactory.CreateRDFData(data);
		dataTable.setContainerDataSource(container);


		dataTable.setVisibleColumns(new String[]{ "subject", "predicate", "object" });
		dataTable.setPageLength(3);
	}


}
