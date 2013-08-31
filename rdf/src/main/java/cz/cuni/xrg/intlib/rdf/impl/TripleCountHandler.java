package cz.cuni.xrg.intlib.rdf.impl;

import cz.cuni.xrg.intlib.rdf.interfaces.TripleCounter;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.helpers.RDFHandlerBase;

/**
 * Class for counting of extracted triples from SPARQL endpoint. Need for using
 * in cause throwing exception, when no triples were extracted from SPARQL
 * endpoint.
 *
 *
 * @author Jiri Tomes
 */
public class TripleCountHandler extends RDFHandlerBase implements TripleCounter {

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
