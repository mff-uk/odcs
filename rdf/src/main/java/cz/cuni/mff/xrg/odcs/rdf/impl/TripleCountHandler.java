package cz.cuni.mff.xrg.odcs.rdf.impl;

import cz.cuni.mff.xrg.odcs.rdf.interfaces.TripleCounter;

import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.RDFHandlerException;


/**
 * Class for counting of extracted triples from SPARQL endpoint. Need for using
 * in cause throwing exception, when no triples were extracted from SPARQL
 * endpoint.
 *
 *
 * @author Jiri Tomes
 */
public class TripleCountHandler extends RDFInserter implements TripleCounter {

	public TripleCountHandler(RepositoryConnection connection) {
		super(connection);
	}	
	private long count = 0;

	@Override
	public void handleStatement(Statement st) throws RDFHandlerException {
		super.handleStatement(st);
		count++;
	}

	@Override
	public long getTripleCount() {
		return count;
	}

	@Override
	public void reset() {
		count = 0;
	}

	@Override
	public boolean isEmpty() {
		return count == 0;
	}
	
	
}
