package cz.cuni.mff.xrg.odcs.commons.app.execution.log;

import java.util.List;

/**
 * Represents an exception stacktrace that is logged by logback and saved into
 * RDBMS.
 *
 * @author Jan Vojt
 */
public class LogException {
	
	private List<LogExceptionLine> lines;

	public LogException(
			List<LogExceptionLine> lines) {
		this.lines = lines;
	}
	
	public List<LogExceptionLine> getLines() {
		return lines;
	}

	public void setLines(
			List<LogExceptionLine> lines) {
		this.lines = lines;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (LogExceptionLine line : lines) {
			sb.append(line.toString());
		}
		return sb.toString();
	}
	
}
