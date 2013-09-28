package cz.cuni.mff.xrg.odcs.frontend.browser;

import com.vaadin.data.Container;
import com.vaadin.ui.VerticalLayout;

import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.App;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.ContainerFactory;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.IntlibPagedTable;
import cz.cuni.mff.xrg.odcs.rdf.data.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.impl.LocalRDFRepo;
import cz.cuni.mff.xrg.odcs.rdf.impl.RDFTriple;
import java.io.File;
import java.util.List;


/**
 * Implementation of browser for
 * {@link cz.cuni.xrg.intlib.backend.data.rdf.LocalRDF}.
 *
 * @author Petyr
 *
 */
class LocalRdfBrowser extends DataUnitBrowser {

	/**
	 * Data from repository.
	 */
	private List<RDFTriple> data = null;
	
	/**
	 * Table for data presentation.
	 */
	private IntlibPagedTable dataTable;

	@Override
	public void loadDataUnit(File directory, String dataUnitId) {
		// create repository in default path - in tmp directory
		LocalRDFRepo repository = RDFDataUnitFactory.createLocalRDFRepo("");
		try {
			// load data from store
			repository.load(directory);
		} catch (Exception e) {
		}
		// load triple
		data = repository.getRDFTriplesInRepository();
		// close repository
		repository.shutDown();
	}

	@Override
	public void enter() {
		VerticalLayout mainLayout = new VerticalLayout();
		loadBrowserTable(data);
		dataTable.setWidth("100%");
		dataTable.setHeight("100%");
		mainLayout.addComponent(dataTable);
		mainLayout.addComponent(dataTable.createControls());
		dataTable.setPageLength(17);
		setCompositionRoot(mainLayout);
	}

	private void loadBrowserTable(List<RDFTriple> data) {
		dataTable = new IntlibPagedTable();
		Container container = App.getApp().getBean(ContainerFactory.class).createRDFData(data);
		dataTable.setContainerDataSource(container);
		

		dataTable.setVisibleColumns("subject", "predicate", "object");
                dataTable.setFilterBarVisible(true);
	}

}
