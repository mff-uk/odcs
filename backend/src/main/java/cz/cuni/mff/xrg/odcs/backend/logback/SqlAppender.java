package cz.cuni.mff.xrg.odcs.backend.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.db.ConnectionSource;
import ch.qos.logback.core.db.DBHelper;
import cz.cuni.mff.xrg.odcs.commons.app.execution.log.Log;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

/**
 * Implementation of log appender. The code is inspired by
 * {@link ch.qos.logback.core.db.DBAppenderBase}.
 *
 * The appender is designed to append into single table in Virtuoso.
 *
 * @author Petyr
 */
public class SqlAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

	protected ConnectionSource connectionSource;

	/**
	 * Return string that is used as insert query into logging table.
	 *
	 * @return
	 */
	protected String getInsertSQL() {
		return "INSERT INTO logging"
				+ " (level, timestmp, logger, message, dpu, execution, stack_trace)"
				+ " VALUES (?, ?, ? ,?, ?, ?, ?)";
	}

	@Override
	public void start() {

		if (connectionSource == null) {
			throw new IllegalStateException(
					"DBAppender cannot function without a connection source");
		}

		started = true;
	}

	@Override
	public void append(ILoggingEvent eventObject) {
		Connection connection = null;
		try {
			connection = connectionSource.getConnection();
			connection.setAutoCommit(false);

			// prepare statement
			PreparedStatement insertStatement
					= connection.prepareStatement(getInsertSQL());

			// inserting an event and getting the result must be exclusive
			synchronized (this) {
				subAppend(eventObject, connection, insertStatement);
			}

			// we no longer need the insertStatement
			if (insertStatement != null) {
				insertStatement.close();
			}

			connection.commit();
		} catch (Throwable sqle) {
			addError("problem appending event", sqle);
		} finally {
			DBHelper.closeConnection(connection);
		}
	}

	/**
	 * Bind the parameters to he insert statement.
	 * @param event
	 * @param connection
	 * @param stmt
	 * @throws Throwable 
	 */
	protected void subAppend(ILoggingEvent event, Connection connection,
			PreparedStatement stmt) throws Throwable {
		
		// bind to the query
		stmt.setInt(1, event.getLevel().toInteger());
		stmt.setLong(2, event.getTimeStamp());
		stmt.setString(3, event.getLoggerName());
		stmt.setString(4, event.getFormattedMessage());		
		
		// get DPU and EXECUTION from MDC
		final Map<String, String> mdc = event.getMDCPropertyMap();

		try {
			final String dpuString = mdc.get(Log.MDC_DPU_INSTANCE_KEY_NAME);
			final int dpuId = Integer.parseInt(dpuString);
			stmt.setInt(5, dpuId);
		} catch (Exception ex) {
			stmt.setNull(5, java.sql.Types.INTEGER);
		}

		try {
			final String execString = mdc.get(Log.MDPU_EXECUTION_KEY_NAME);
			final int execId = Integer.parseInt(execString);
			stmt.setInt(6, execId);
		} catch (Exception ex) {
			stmt.setNull(6, java.sql.Types.INTEGER);
		}

		IThrowableProxy proxy = event.getThrowableProxy();
		if (proxy != null) {
			StringBuilder sb = new StringBuilder();
			prepareStackTrace(proxy, sb);			
			stmt.setString(7, sb.toString());
		} else {
			stmt.setString(7, null);
		}
		
		// execute ..
		stmt.executeUpdate();
	}

	/**
	 * Convert information about stack trace into text form.
	 * @param proxy
	 * @param sb 
	 */
	private void prepareStackTrace(IThrowableProxy proxy, StringBuilder sb) {
		sb.append(proxy.getClassName());
		sb.append(' ');
		sb.append(proxy.getMessage());
		sb.append('\n');

		final StackTraceElementProxy[] stack
				= proxy.getStackTraceElementProxyArray();
		int index = proxy.getCommonFrames();
		if (index != 0) {
			sb.append("   ... ");
			sb.append(index);
			sb.append(" common frames omitted");
		}
		final int indexEnd = stack.length;
		for (; index < indexEnd; ++index) {
			sb.append("   ");
			sb.append(stack[index].getSTEAsString());
			sb.append('\n');
		}

		if (proxy.getCause() != null) {
			prepareStackTrace(proxy.getCause(), sb);
		}
	}

	/**
	 * @param connectionSource The connectionSource to set.
	 */
	public void setConnectionSource(ConnectionSource connectionSource) {
		this.connectionSource = connectionSource;
	}

}
