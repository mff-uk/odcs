package cz.cuni.mff.xrg.odcs.rdf.help;

import cz.cuni.mff.xrg.odcs.rdf.enums.ParsingConfictType;
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
