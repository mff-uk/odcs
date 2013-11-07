package cz.cuni.mff.xrg.odcs.rdf.handlers;

import org.apache.log4j.Logger;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Jiri Tomes
 *
 * Class allow monitor better the extraction data process - information about
 * parsed triples and detail error log.
 *
 */
public class StatisticalHandler extends TripleCountHandler {

	private static final int TRIPLE_LOGGED_SIZE = 100;

	private long addedCount = 0;

	/**
	 * Default hadler contructor for parsing and adding data to repository.
	 *
	 * @param connection connection to repository where we add data.
	 */
	public StatisticalHandler(RepositoryConnection connection) {
		super(connection);
		logger = Logger.getLogger(StatisticalHandler.class);
	}

	/**
	 * Handler constructor used for check data validation in repository.
	 *
	 * @param connection connection to repository where we add data.
	 * @param checkData  true for logging validation, false for logging parsing
	 *                   and adding - same as using constructor {@link
	 * StatisticalHandler#StatisticalHandler(org.openrdf.repository.RepositoryConnection)
	 *                   }
	 *
	 */
	public StatisticalHandler(RepositoryConnection connection, boolean checkData) {
		super(connection, checkData);
		logger = Logger.getLogger(StatisticalHandler.class);
	}

	@Override
	public void handleStatement(Statement st) throws RDFHandlerException {
		super.handleStatement(st);

		if (getTripleCount() % TRIPLE_LOGGED_SIZE == 0 && isStatementAdded()) {

			addedCount += TRIPLE_LOGGED_SIZE;
			if (checkData) {
				logger.debug(String.format(
						"Have been valided %s TRIPLES yet.",
						String.valueOf(addedCount)));
			} else {
				logger.debug(String.format(
						"Have been parsed and added %s TRIPLES yet.",
						String.valueOf(addedCount)));
			}

		}
	}

	@Override
	public void endRDF() throws RDFHandlerException {
		super.endRDF();
		printFindedProblems();
	}

	/**
	 * Reset counting triples, finded errors and warnings.
	 */
	@Override
	public void reset() {
		super.reset();
		addedCount = 0;
	}

	/**
	 *
	 * @return if during parsing data using handler were find some problems
	 *         (invalid data) or not.
	 */
	public boolean hasFindedProblems() {
		if (hasWarnings() || hasErrors()) {
			return true;
		} else {
			return false;
		}
	}

	private void printFindedProblems() {
		if (hasWarnings()) {
			logger.debug("\nWARNINGS list:");

			logger.warn(getWarningsAsString());

		}
		if (hasErrors()) {
			logger.debug("\nERRORS list:");
			logger.error(getErorrsAsString());
		}
	}

	/**
	 *
	 * @return String representation of all finded problems with data
	 *         validation. If all data are valid, return empty string.
	 */
	public String getFindedProblemsAsString() {
		StringBuilder result = new StringBuilder();

		if (hasWarnings()) {
			result.append("\nWARNINGS list:");
			result.append(getWarningsAsString());

		}
		if (hasErrors()) {
			result.append("\nERRORS list:");
			result.append(getErorrsAsString());
		}

		return result.toString();
	}
}
