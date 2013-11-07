package cz.cuni.mff.xrg.odcs.rdf.handlers;

import cz.cuni.mff.xrg.odcs.rdf.enums.ParsingConfictType;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.TripleCounter;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
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

	protected Logger logger = Logger.getLogger(TripleCountHandler.class);

	private List<TripleProblem> warnings = new ArrayList<>();

	private List<TripleProblem> errors = new ArrayList<>();

	private TripleProblem nextProblem = null;

	private boolean hasProblem = false;

	private boolean isStatementAdded = false;

	protected boolean checkData;

	/**
	 * Default hadler contructor for parsing and adding data to repository.
	 *
	 * @param connection connection to repository where we add data.
	 */
	public TripleCountHandler(RepositoryConnection connection) {
		super(connection);
		this.checkData = false;
	}

	/**
	 * Handler constructor used for check data validation in repository.
	 *
	 * @param connection connection to repository where we add data.
	 * @param checkData  true for logging validation, false for logging parsing
	 *                   and adding - same as using constructor {@link
	 * TripleCountHandler#TripleCountHandler(org.openrdf.repository.RepositoryConnection)
	 *                   }.
	 *
	 */
	public TripleCountHandler(RepositoryConnection connection, boolean checkData) {
		super(connection);
		this.checkData = checkData;
	}

	private long tripleCount = 0;

	@Override
	public void handleStatement(Statement st) throws RDFHandlerException {
		try {
			if (!hasProblem) {
				super.handleStatement(st);
				tripleCount++;
				isStatementAdded = true;
			} else {
				isStatementAdded = false;
				hasProblem = false;

				nextProblem.setStatement(st);
				switch (nextProblem.getConflictType()) {
					case ERROR:
						errors.add(nextProblem);
						break;
					case WARNING:
						warnings.add(nextProblem);
						break;
				}

			}
		} catch (RDFHandlerException e) {
			logger.debug(
					"\n" + "Triple contains problems:"
					+ "\n Subject:" + st.getSubject().toString()
					+ "\n Predicate:" + st.getPredicate().toString()
					+ "\n Object:" + st.getObject().toString());
		}
	}

	@Override
	public void startRDF() throws RDFHandlerException {
		try {
			super.startRDF();
			if (checkData) {
				logger.debug("Starting data validating - SUCCESSFUL");
			} else {
				logger.debug("Starting parsing - SUCCESSFUL");
			}
		} catch (RDFHandlerException e) {
			if (checkData) {
				logger.debug("Starting data validating - FAIL");
			} else {
				logger.debug("Starting parsing - FAIL");
			}
			throw new RDFHandlerException(e.getMessage(), e);
		}
	}

	@Override
	public void endRDF() throws RDFHandlerException {
		try {
			super.endRDF();
			logger.debug("Ending parsing - SUCCESSFUL");
			logger.debug("TOTAL ADDED:" + getTripleCount() + " triples");
		} catch (RDFHandlerException e) {
			logger.error(e.getMessage());
			logger.debug("TOTAL ADDED:" + getTripleCount() + " triples");
			logger.debug("Ending parsing - FAIL");
		}
	}

	/**
	 * Add next finded error during data parsing.
	 *
	 * @param message describe of finded error
	 * @param line    number of line where error was find out
	 * @param column  number of column where error was find out
	 */
	public void addError(String message, int line, int column) {
		nextProblem = new TripleProblem(message, line, column,
				ParsingConfictType.ERROR);
		hasProblem = true;
	}

	/**
	 * Add next finded warning during data parsing.
	 *
	 * @param message describe of finded warning
	 * @param line    number of line where warning was find out
	 * @param column  number of column where warning was find out
	 */
	public void addWarning(String message, int line, int column) {
		nextProblem = new TripleProblem(message, line, column,
				ParsingConfictType.WARNING);
		hasProblem = true;
	}

	protected boolean isStatementAdded() {
		return isStatementAdded;
	}

	protected String getWarningsAsString() {
		StringBuilder result = new StringBuilder();

		int warningCount = 0;

		for (TripleProblem next : warnings) {
			warningCount++;
			result.append(getDescribedProblem(next, warningCount));
		}

		return result.toString();
	}

	protected String getErorrsAsString() {
		StringBuilder result = new StringBuilder();

		int errorCount = 0;

		for (TripleProblem next : errors) {
			errorCount++;
			result.append(getDescribedProblem(next, errorCount));
		}

		return result.toString();
	}

	private String getDescribedProblem(TripleProblem next, int errorCount) {

		Statement statement = next.getStatement();
		String problemType = next.getConflictType().toString();

		String problem = "\n" + errorCount + "] No added triple contains " + problemType + ":"
				+ "\n Subject:" + statement.getSubject().toString()
				+ "\n Predicate:" + statement.getPredicate().toString()
				+ "\n Object:" + statement.getObject().toString()
				+ "\n PROBLEM message: " + next.getMessage()
				+ "\n Find on source line: " + next.getLine()
				+ "\n Find on source column: " + next.getColumn();

		return problem;
	}

	protected boolean hasErrors() {
		return !errors.isEmpty();
	}

	protected boolean hasWarnings() {
		return !warnings.isEmpty();
	}

	/**
	 *
	 * @return count of extracted triples.
	 */
	@Override
	public long getTripleCount() {
		return tripleCount;
	}

	/**
	 * Reset counting triples, finded errors and warnings.
	 */
	@Override
	public void reset() {
		tripleCount = 0;
		hasProblem = false;
		warnings.clear();
		errors.clear();

	}

	/**
	 *
	 * @return true if there is no triples, false otherwise.
	 */
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

class TripleProblem {

	private String message;

	private int line;

	private int column;

	private ParsingConfictType type;

	private Statement statement;

	public TripleProblem(String message, int line, int column,
			ParsingConfictType type) {
		this.message = message;
		this.line = line;
		this.column = column;
		this.type = type;

	}

	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	public String getMessage() {
		return message;
	}

	public int getLine() {
		return line;
	}

	public int getColumn() {
		return column;
	}

	public ParsingConfictType getConflictType() {
		return type;
	}

	public Statement getStatement() {
		return statement;
	}
}
