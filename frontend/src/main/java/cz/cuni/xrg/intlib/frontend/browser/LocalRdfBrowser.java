package cz.cuni.xrg.intlib.frontend.browser;

import com.vaadin.data.Container;
import com.vaadin.ui.VerticalLayout;
import java.io.File;
import java.util.List;

import cz.cuni.xrg.intlib.frontend.auxiliaries.ContainerFactory;
import cz.cuni.xrg.intlib.frontend.gui.components.IntlibPagedTable;
import cz.cuni.xrg.intlib.rdf.impl.LocalRDFRepo;
import cz.cuni.xrg.intlib.rdf.impl.RDFTriple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static Logger LOG = LoggerFactory.getLogger(LocalRdfBrowser.class);
	
	@Override
	public void loadDataUnit(File directory, String dataUnitId) {
		Logger logger = LoggerFactory.getLogger(LocalRdfBrowser.class);
		// create repository in default path - in tmp directory
		LocalRDFRepo repository = LocalRDFRepo.createLocalRepo("");
		try {
			// load data from stora
			repository.load(directory);
		} catch (Exception e) {
		}
		// load triple
		data = repository.getRDFTriplesInRepository();
		// close reporistory
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
		Container container = ContainerFactory.CreateRDFData(data);
		dataTable.setContainerDataSource(container);
		

		dataTable.setVisibleColumns("subject", "predicate", "object");
                dataTable.setFilterBarVisible(true);
	}

}
