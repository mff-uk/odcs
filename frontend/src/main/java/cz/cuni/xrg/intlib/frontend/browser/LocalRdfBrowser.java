package cz.cuni.xrg.intlib.frontend.browser;

import com.vaadin.data.Container;
import com.vaadin.ui.VerticalLayout;
import java.io.File;
import java.util.List;

import cz.cuni.xrg.intlib.frontend.auxiliaries.ContainerFactory;
import cz.cuni.xrg.intlib.frontend.gui.components.IntlibPagedTable;
import cz.cuni.xrg.intlib.rdf.impl.LocalRDFRepo;
import cz.cuni.xrg.intlib.rdf.impl.RDFTriple;
import java.util.ArrayList;

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

		LocalRDFRepo repository = LocalRDFRepo.createLocalRepo(directory.getAbsolutePath(), dumpDirName);

		try {
			repository.load();
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

	private List<RDFTriple> buildStubRDFData() {

		List<RDFTriple> rdfTripleList = new ArrayList<>();
		/*
		 rdfTripleList.add(new RDFTriple(1, "rdf:Description", "rdf:about", "http://www.recshop.fake/cd/Empire Burlesque"));
		 rdfTripleList.add(new RDFTriple(2, "rdf:Description", "cd:artist", "Bob Dylan"));
		 rdfTripleList.add(new RDFTriple(3, "rdf:Description", "cd:country", "USA"));
		 rdfTripleList.add(new RDFTriple(4, "rdf:Description", "cd:company", "Columbia"));
		 rdfTripleList.add(new RDFTriple(5, "rdf:Description", "cd:price", "10.90"));
		 rdfTripleList.add(new RDFTriple(6, "rdf:Description", "cd:year", "1985"));
		 */
		return rdfTripleList;

	}

	@Override
	public void loadDataUnit(File directory) throws Exception {
		// FileName is from backend LocalRdf.dumpName = "dump_dat.ttl"; .. store somewhere else ?
		LoggerFactory.getLogger(LocalRdfBrowser.class).debug("Create LocalRDFRepo in directory {}", directory.toString());
		LocalRDFRepo repository = new LocalRDFRepo(directory.toString(), "dump_dat.ttl");
		// TODO Petyr, Jirka : load repository from folder ..
		// get triples
		data = repository.getRDFTriplesInRepository();

		repository.shutDown();
		//data = buildStubRDFData();
	}
}
