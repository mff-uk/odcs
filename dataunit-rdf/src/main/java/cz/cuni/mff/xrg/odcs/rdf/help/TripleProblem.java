package cz.cuni.mff.xrg.odcs.rdf.help;

import cz.cuni.mff.xrg.odcs.rdf.exceptions.ParsingConfictType;
import org.openrdf.model.Statement;

/**
 * Responsible for describing invalid RDF triples during data parsing- typo of
 * finded problem, place where invalidation found out, described message about
 * that.
 *
 * @author Jiri Tomes
 */
public class TripleProblem {

	private String message;

	private int line;

	private int column;

	private ParsingConfictType type;

	private Statement statement;

	/**
	 * Create new instace of {@link TripleProblem} from describe message ,
	 * number of line and column, where problem was found out and it´s type.
	 *
	 * @param message String desription of problem
	 * @param line    number of line where problem was found
	 * @param column  number of column where problem was found
	 * @param type    type of finded problem
	 */
	public TripleProblem(String message, int line, int column,
			ParsingConfictType type) {
		this.message = message;
		this.line = line;
		this.column = column;
		this.type = type;

	}

	/**
	 * Set RDF problem statement that describes finded problem.
	 *
	 * @param statement RDF statement that characterizes finded problem.
	 */
	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	/**
	 * Returns string description message of finded problem.
	 *
	 * @return string description message of finded problem.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Returns number of line, where problem was found out.
	 *
	 * @return number of line, where problem was found out.
	 */
	public int getLine() {
		return line;
	}

	/**
	 * Returns number of column, where problem was found out.
	 *
	 * @return number of column, where problem was found out.
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * Returns type of finded problem.
	 *
	 * @return type of finded problem.
	 */
	public ParsingConfictType getConflictType() {
		return type;
	}

	/**
	 * Returns RDF problem statement that describes finded problem.
	 *
	 * @return RDF problem statement that describes finded problem.
	 */
	public Statement getStatement() {
		return statement;
	}
}
