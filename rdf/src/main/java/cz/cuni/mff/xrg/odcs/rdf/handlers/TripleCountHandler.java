package cz.cuni.mff.xrg.odcs.rdf.handlers;

import cz.cuni.mff.xrg.odcs.rdf.interfaces.TripleCounter;
import org.openrdf.model.Resource;

import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.RDFHandlerException;

/**
 * Class for counting of extracted triples from SPARQL endpoint or given file.
 * Need very oftern in cause throwing exception, when no triples were extracted
 * from SPARQL endpoint.
 *
 *
 * @author Jiri Tomes
 */
public class TripleCountHandler extends RDFInserter implements TripleCounter {

	public TripleCountHandler(RepositoryConnection connection) {
		super(connection);
	}

	private long tripleCount = 0;

	@Override
	public void handleStatement(Statement st) throws RDFHandlerException {
		super.handleStatement(st);
		tripleCount++;
	}

	@Override
	public long getTripleCount() {
		return tripleCount;
	}

	@Override
	public void reset() {
		tripleCount = 0;
	}

	@Override
	public boolean isEmpty() {
		return tripleCount == 0;
	}

	/**
	 * Set graphs where are insert data thanks using this handler.
	 *
	 * @param graphs Collection of graph to insert data from handler.
	 */
	public void setGraphContext(Resource... graphs) {
		if (graphs != null) {
			super.enforceContext(graphs);
		}
	}
}
