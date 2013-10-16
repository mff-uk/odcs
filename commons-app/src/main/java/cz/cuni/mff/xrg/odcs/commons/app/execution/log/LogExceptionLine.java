package cz.cuni.mff.xrg.odcs.commons.app.execution.log;

import java.io.Serializable;

import javax.persistence.*;

/**
 * Represents a single line of exception's stacktrace in RDBMS.
 *
 * @author Jan Vojt
 */
@Entity
@Table(name = "logging_event_exception")
public class LogExceptionLine implements Serializable {
	
	/**
	 * Log message which produced this stacktrace.
	 */
	@Id
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "event_id")
	private LogMessage message;
	
	/**
	 * Index of this line in the context of stacktrace.
	 */
	@Id
	@Column(name = "i")
	private int lineIndex;
	
	/**
	 * Contents of stacktrace line.
	 */
	@Column(name = "trace_line")
	private String line;
	
	public LogMessage getMessage() {
		return message;
	}

	public int getLineIndex() {
		return lineIndex;
	}

	public String getLine() {
		return line;
	}

	@Override
	public String toString() {
		return line + "\n";
	}
	
}
