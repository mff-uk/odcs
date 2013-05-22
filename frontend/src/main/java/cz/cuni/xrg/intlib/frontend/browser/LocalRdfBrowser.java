package cz.cuni.xrg.intlib.frontend.browser;

import com.vaadin.data.Container;
import com.vaadin.ui.Table;
import java.io.File;
import java.util.List;

import cz.cuni.xrg.intlib.commons.app.rdf.LocalRDFRepo;
import cz.cuni.xrg.intlib.commons.app.rdf.RDFTriple;
import cz.cuni.xrg.intlib.frontend.auxiliaries.ContainerFactory;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of browser for {@link cz.cuni.xrg.intlib.backend.data.rdf.LocalRDF}.
 *
 * @author Petyr
 *
 */
class LocalRdfBrowser extends DataUnitBrowser {

	private List<RDFTriple> data = null;
	private Table dataTable;

	@Override
	public void loadDataUnit(File directory) {
		// FileName is from backend LocalRdf.dumpName = "dump_dat.ttl"; .. store somewhere else ?
		LoggerFactory.getLogger(LocalRdfBrowser.class).debug("Create LocalRDFRepo in directory {}", directory.toString());		
		LocalRDFRepo repository = new LocalRDFRepo(directory.toString(), "dump_dat.ttl");		
		// TODO Petyr, Jirka : load repository from folder ..
		// get triples
		data = repository.getRDFTriplesInRepository();
		//data = buildStubRDFData();
	}

	@Override
	public void enter() {
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

	private List<RDFTriple> buildStubRDFData() {
		List<RDFTriple> rdfTripleList = new ArrayList<>();

		rdfTripleList.add(new RDFTriple(1, "rdf:Description", "rdf:about", "http://www.recshop.fake/cd/Empire Burlesque"));
		rdfTripleList.add(new RDFTriple(2, "rdf:Description", "cd:artist", "Bob Dylan"));
		rdfTripleList.add(new RDFTriple(3, "rdf:Description", "cd:country", "USA"));
		rdfTripleList.add(new RDFTriple(4, "rdf:Description", "cd:company", "Columbia"));
		rdfTripleList.add(new RDFTriple(5, "rdf:Description", "cd:price", "10.90"));
		rdfTripleList.add(new RDFTriple(6, "rdf:Description", "cd:year", "1985"));

		return rdfTripleList;
	}

}
