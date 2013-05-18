package cz.cuni.xrg.intlib.frontend.browser;

import java.io.File;
import java.util.List;

import cz.cuni.xrg.intlib.commons.app.rdf.LocalRDFRepo;
import cz.cuni.xrg.intlib.commons.app.rdf.RDFTriple;

/**
 * Implementation of browser for {@link cz.cuni.xrg.intlib.backend.data.rdf.LocalRDF}.
 * 
 * @author Petyr
 *
 */
class LocalRdfBrowser extends DataUnitBrowser {

	private List<RDFTriple> data = null;

	@Override
	public void loadDataUnit(File directory) {
		LocalRDFRepo repository = null;
		// TODO Petyr, Jirka : load repository from folder .. 
		// get triples
		data = repository.getRDFTriplesInRepository();
	}

	@Override
	public void enter() {
		// TODO Bohuslav : initialise user interface here .. ie. copy your browser's code here
	}
	
}
