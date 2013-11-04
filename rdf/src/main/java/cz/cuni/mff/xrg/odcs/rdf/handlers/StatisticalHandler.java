package cz.cuni.mff.xrg.odcs.rdf.handlers;

import org.apache.log4j.Logger;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Jiri Tomes
 *
 * Class allow monitor better the extraction process - information about loaded
 * triples and detail log.
 *
 */
public class StatisticalHandler extends TripleCountHandler {

	private static final int TRIPLE_LOGGED_SIZE = 100;

	private long addedCount = 0;

	public StatisticalHandler(RepositoryConnection connection) {
		super(connection);
		logger = Logger.getLogger(StatisticalHandler.class);
	}

	@Override
	public void handleStatement(Statement st) throws RDFHandlerException {
		super.handleStatement(st);

		if (getTripleCount() % TRIPLE_LOGGED_SIZE == 0 && isStatementAdded()) {

			addedCount += TRIPLE_LOGGED_SIZE;

			logger.debug(String.format(
					"Have been parsed and added %s TRIPLES yet.",
					String.valueOf(addedCount)));

		}
	}

	@Override
	public void endRDF() throws RDFHandlerException {
		super.endRDF();
		printFindedProblems();
	}

	@Override
	public void reset() {
		super.reset();
		addedCount = 0;
	}
}