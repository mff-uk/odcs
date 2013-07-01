package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.Container;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;
import cz.cuni.xrg.intlib.rdf.impl.RDFTriple;
import cz.cuni.xrg.intlib.frontend.auxiliaries.ContainerFactory;

import java.util.List;

/**
 * Table showing triples from graph in debug.
 *
 * @author Bogo
 */
public class BrowserTable extends CustomComponent {

	private IntlibPagedTable dataTable;
	private VerticalLayout mainLayout;

	/**
	 * Constructor with data to show.
	 * 
	 * @param data Triples to show.
	 */
	public BrowserTable(List<RDFTriple> data) {

		mainLayout = new VerticalLayout();
		loadBrowserTable(data);
		dataTable.setWidth("100%");
		dataTable.setHeight("100%");
		mainLayout.addComponent(dataTable);
		mainLayout.addComponent(dataTable.createControls());
		setCompositionRoot(mainLayout);

	}

	/*
	 * Initializes the table and loads the data.
	 * 
	 * @param data Data to load.
	 */
	private void loadBrowserTable(List<RDFTriple> data) {
		dataTable = new IntlibPagedTable();
		Container container = ContainerFactory.CreateRDFData(data);
		dataTable.setContainerDataSource(container);


		dataTable.setVisibleColumns(new String[]{ "subject", "predicate", "object" });
		dataTable.setPageLength(3);
	}


}
