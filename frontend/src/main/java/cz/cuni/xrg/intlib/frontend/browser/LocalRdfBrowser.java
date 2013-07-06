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

	private List<RDFTriple> data = null;
	private IntlibPagedTable dataTable;

	@Override
	public void loadDataUnit(File directory, String dumpDirName) {
		Logger logger = LoggerFactory.getLogger(LocalRdfBrowser.class);

		// FileName is from backend LocalRdf.dumpName = "dump_dat.ttl"; .. store somewhere else ?
		logger.debug("Create LocalRDFRepo in directory={} dumpDirname={}", directory.toString(), dumpDirName);

		LocalRDFRepo repository = LocalRDFRepo.createLocalRepo(directory.getAbsolutePath(), dumpDirName, "");

		try {
			repository.load(directory);
		} catch (Exception e) {
		}
		data = repository.getRDFTriplesInRepository();

		logger.debug("Number of triples: {}", data.size());

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
		setCompositionRoot(mainLayout);
	}

	private void loadBrowserTable(List<RDFTriple> data) {
		dataTable = new IntlibPagedTable();
		Container container = ContainerFactory.CreateRDFData(data);
		dataTable.setContainerDataSource(container);

		dataTable.setVisibleColumns(new String[]{"subject", "predicate", "object"});
	}


	@Override
	public void loadDataUnit(File directory) throws Exception {
		// FileName is from backend LocalRdf.dumpName = "dump_dat.ttl"; .. store somewhere else ?
		LoggerFactory.getLogger(LocalRdfBrowser.class).debug("Create LocalRDFRepo in directory {}", directory.toString());
		LocalRDFRepo repository = new LocalRDFRepo(directory.toString(), "dump_dat.ttl", "");
		// TODO Petyr, Jirka : load repository from folder ..
		// get triples
		data = repository.getRDFTriplesInRepository();

		repository.shutDown();
		//data = buildStubRDFData();
	}

}
