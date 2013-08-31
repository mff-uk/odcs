package cz.cuni.xrg.intlib.rdf.impl;

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
public class TripleCountHandler extends RDFHandlerBase {

	private long count = 0;

	@Override
	public void handleStatement(Statement st) throws RDFHandlerException {
		super.handleStatement(st);
		count++;
	}

	public long getLoadedCount() {
		return count;
	}

	public void resetTripleCount() {
		count = 0;
	}

	public boolean isEmpty() {
		return count == 0;
	}
}
