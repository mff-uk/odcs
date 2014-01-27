package cz.cuni.mff.xrg.odcs.rdf.handlers;

import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.rdf.help.TripleProblem;
import cz.cuni.mff.xrg.odcs.rdf.enums.ParsingConfictType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFCancelException;
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

	/**
	 * Responsible for log event in class {@link TripleCountHandler} and it´s
	 * subclasses.
	 */
	protected Logger logger = Logger.getLogger(TripleCountHandler.class);

	private List<TripleProblem> warnings = new ArrayList<>();

	private List<TripleProblem> errors = new ArrayList<>();

	private TripleProblem nextProblem = null;

	private boolean hasProblem = false;

	private boolean isStatementAdded = false;

	private DPUContext context;

	/**
	 * Variable is responsible for creating message used by {@link #logger}.
	 * True value is set if handler is used by data validator for checking data.
	 * False value is set if handler is used for data parsing and adding data to
	 * repository.
	 */
	protected boolean checkData;

	/**
	 * Default hadler contructor for parsing and adding data to repository.
	 *
	 * @param connection connection to repository where we add data.
	 */
	public TripleCountHandler(RepositoryConnection connection) {
		super(connection);
		this.context = null;
		this.checkData = false;
	}

	/**
	 * Default hadler contructor for parsing and adding data from SPARQL
	 * endpoint to repository.
	 *
	 * @param connection connection to repository where we add data.
	 * @param context    DPU context for checking if parsing was cancelled or
	 *                   not.
	 */
	public TripleCountHandler(RepositoryConnection connection,
			DPUContext context) {
		super(connection);
		this.context = context;
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

	/**
	 * If given statement contains valid RDF data then call method
	 * {@link RDFInserter#handleStatement(org.openrdf.model.Statement)} on
	 * parent class, otherwise add this problem statement to specific problem
	 * collection of {@link TripleProblem}.
	 *
	 *
	 * @param st Statement that will be added to repostory.
	 * @throws RDFHandlerException if handler find out problem during execution
	 *                             this method.
	 */
	@Override
	public void handleStatement(Statement st) throws RDFHandlerException {
		try {
			if (isParsingCanceled()) {
				throw new RDFCancelException("Extraction was CANCELLED by user");

			} else if (!hasProblem) {
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
		} catch (RDFCancelException e) {
			throw new RDFCancelException(e.getMessage(), e);

		} catch (RDFHandlerException e) {
			logger.debug(
					"\n" + "Triple contains problems:"
					+ "\n Subject:" + st.getSubject().toString()
					+ "\n Predicate:" + st.getPredicate().toString()
					+ "\n Object:" + st.getObject().toString());

		}
	}

	/**
	 * Call method {@link RDFInserter#startRDF()} on parent class and write log
	 * message about it.
	 *
	 * @throws RDFHandlerException if handler find out problem during execution
	 *                             this method.
	 */
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

	/**
	 * Call method {@link RDFInserter#endRDF()} on parent class and write log
	 * message about it.
	 *
	 * @throws RDFHandlerException if handler find out problem during execution
	 *                             this method.
	 */
	@Override
	public void endRDF() throws RDFHandlerException {
		try {
			super.endRDF();
			if (checkData) {
				logger.debug("Ending data validating - SUCCESSFUL");
				logger.debug("TOTAL VALIDATED:" + getTripleCount() + " triples");
			} else {
				logger.debug("Ending parsing - SUCCESSFUL");
				logger.debug("TOTAL ADDED:" + getTripleCount() + " triples");
			}
		} catch (RDFHandlerException e) {
			logger.error(e.getMessage());
			if (checkData) {
				logger.debug("Ending data validating - FAIL");
				logger.debug("TOTAL VALIDATED:" + getTripleCount() + " triples");

			} else {
				logger.debug("Ending parsing - FAIL");
				logger.debug("TOTAL ADDED:" + getTripleCount() + " triples");

			}
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

	/**
	 * Return true if last parsed statement was added to repository, false
	 * otherwise.
	 *
	 * @return true if last parsed statement was added to repository,false
	 *         otherwise.
	 */
	protected boolean isStatementAdded() {
		return isStatementAdded;
	}

	/**
	 * Return true, if parsing proccess was canceled by user, false otherwise.
	 *
	 * @return true, if parsing proccess was canceled by user, false otherwise.
	 */
	private boolean isParsingCanceled() {
		if (context != null && context.canceled()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns string warning message contains description of all problems from
	 * given collection.
	 *
	 * @param warningsList Collection of {@link TripleProblem} to create string
	 *                     message
	 * @return String warning message contains description of all problems from
	 *         given collection.
	 */
	protected static String getWarningsAsString(List<TripleProblem> warningsList) {
		StringBuilder result = new StringBuilder();

		int warningCount = 0;

		for (TripleProblem next : warningsList) {
			warningCount++;
			result.append(getDescribedProblem(next, warningCount));
		}

		return result.toString();
	}

	/**
	 * Returns string warning message contains description of all problems from
	 * collection {@link #warnings}.
	 *
	 * @return String warning message contains description of all problems from
	 *         collection {@link #warnings}.
	 */
	protected String getWarningsAsString() {
		return getWarningsAsString(warnings);
	}

	/**
	 * Returns string error message contains description of all problems from
	 * given collection.
	 *
	 * @param errorsList Collection of {@link TripleProblem} to create string
	 *                   message
	 * @return String error message contains description of all problems from
	 *         given collection.
	 */
	protected static String getErorrsAsString(List<TripleProblem> errorsList) {

		StringBuilder result = new StringBuilder();

		int errorCount = 0;

		for (TripleProblem next : errorsList) {
			errorCount++;
			result.append(getDescribedProblem(next, errorCount));
		}

		return result.toString();

	}

	/**
	 * Returns string warning message contains description of all problems from
	 * collection {@link #errors}.
	 *
	 * @return String warning message contains description of all problems from
	 *         collection {@link #errors}.
	 */
	protected String getErorrsAsString() {
		return getErorrsAsString(errors);
	}

	/**
	 * Create string description from given {@link TripleProblem}.
	 *
	 * @param next       Instance of {@link TripleProblem } used for creating
	 *                   description from that.
	 * @param errorCount number of finded problems used in desription.
	 * @return string description from given {@link TripleProblem}.
	 */
	private static String getDescribedProblem(TripleProblem next, int errorCount) {

		Statement statement = next.getStatement();
		String problemType = next.getConflictType().name();

		String problem = "\n" + errorCount + "] " + problemType + " in triple :"
				+ "\n Subject: " + statement.getSubject().toString()
				+ "\n Predicate: " + statement.getPredicate().toString()
				+ "\n Object: " + statement.getObject().toString()
				+ "\n PROBLEM message: " + next.getMessage()
				+ "\n Find on source line: " + next.getLine();

		return problem;
	}

	/**
	 * Returns true if handler find out some errors, false otherwise.
	 *
	 * @return true if handler find out some errors, false otherwise.
	 */
	protected boolean hasErrors() {
		return !errors.isEmpty();
	}

	/**
	 * Returns true if handler find out some warnings, false otherwise.
	 *
	 * @return true if handler find out some warnings, false otherwise.
	 */
	protected boolean hasWarnings() {
		return !warnings.isEmpty();
	}

	/**
	 * Returns list as collection of {@link TripleProblem} contains all finded
	 * problems - warning and errors.
	 *
	 * @return List as collection of {@link TripleProblem} contains all finded
	 *         problems - warning and errors.
	 */
	protected List<TripleProblem> getTripleProblems() {

		List<TripleProblem> problems = new ArrayList<>();
		problems.addAll(warnings);
		problems.addAll(errors);

		return problems;
	}

	/**
	 * Return count of extracted triples.
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
	 * Return true if there is no triples, false otherwise.
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
