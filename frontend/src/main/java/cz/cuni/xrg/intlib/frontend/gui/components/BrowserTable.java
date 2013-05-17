package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.Container;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Table;
import cz.cuni.xrg.intlib.auxiliaries.App;
import cz.cuni.xrg.intlib.auxiliaries.ContainerFactory;
import cz.cuni.xrg.intlib.commons.data.rdf.RDFTriple;

import java.util.List;

/**
 *
 * @author Bogo
 */
public class BrowserTable extends CustomComponent {

	private Table dataTable;

	public BrowserTable(List<RDFTriple> data) {

		loadBrowserTable(data);
		dataTable.setWidth("100%");
		dataTable.setHeight("100%");
		setCompositionRoot(dataTable);

	}

	private void loadBrowserTable(List<RDFTriple> data) {
		dataTable = new Table();
		Container container = ContainerFactory.CreateRDFData(data);
		dataTable.setContainerDataSource(container);


		dataTable.setVisibleColumns(new String[]{ "subject", "predicate", "object" });
	}


}
