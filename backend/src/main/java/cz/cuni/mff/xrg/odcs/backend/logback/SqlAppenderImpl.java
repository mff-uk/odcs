package cz.cuni.mff.xrg.odcs.backend.logback;

import ch.qos.logback.classic.db.PooledConnectionSource;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.db.DBHelper;
import ch.qos.logback.core.db.DriverManagerConnectionSource;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.execution.log.Log;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Implementation of log appender. The code is inspired by
 * {@link ch.qos.logback.core.db.DBAppenderBase}.
 *
 * The appender is designed to append into single table in Virtuoso.
 *
 * @author Petyr
 */
public class SqlAppenderImpl extends UnsynchronizedAppenderBase<ILoggingEvent>
		implements SqlAppender {

	private static final Logger LOG = LoggerFactory.getLogger(SqlAppenderImpl.class);
	
	/**
	 * Source of database connection.
	 */
	protected DriverManagerConnectionSource connectionSource;

	/**
	 * List into which add new logs.
	 */
	protected List<ILoggingEvent> primaryList = new ArrayList<>(100);

	/**
	 * List of logs that should be save into database.
	 */
	protected List<ILoggingEvent> secondaryList = new ArrayList<>(100);

	/**
	 * If true then the source support batch execution.
	 */
	protected boolean supportsBatchUpdates;
	
	@Autowired
	protected AppConfig appConfig;
	
	/**
	 * Return string that is used as insert query into logging table.
	 *
	 * @return
	 */
	protected String getInsertSQL() {
		return "INSERT INTO logging"
				+ " (logLevel, timestmp, logger, message, dpu, execution, stack_trace)"
				+ " VALUES (?, ?, ? ,?, ?, ?, ?)";
	}

	/**
	 * Fetch stored logs into database. It can be executed on user demand or by
	 * spring periodically.
	 */
	@Override
	@Async
	@Scheduled(fixedDelay = 4300)
	public synchronized void fetch() {
		
		if (!supportsBatchUpdates) {
			// no batch no need to execute anything
			return;
		}
		
		synchronized (this) {
			List<ILoggingEvent> swap = primaryList;
			primaryList = secondaryList;
			secondaryList = swap;
		}

		if (secondaryList.isEmpty()) {
			return;
		}

		// TODO remove those informations
		long start = (new Date()).getTime();
		int count = secondaryList.size();
				
		// now just save all the data into database
		Connection connection = null;
		try {
			connection = connectionSource.getConnection();
			connection.setAutoCommit(false);

			PreparedStatement stmt = connection.prepareStatement(getInsertSQL());
			
			for (ILoggingEvent item : secondaryList) {
				bindStatement(item, stmt);
				stmt.addBatch();
			}
			// call insert
			stmt.executeBatch();
			stmt.close();
			connection.commit();
		} catch (Throwable sqle) {
			addError("problem appending event", sqle);
		} finally {
			DBHelper.closeConnection(connection);
			// and clear the list
			secondaryList.clear();
		}

		LOG.info("Fetch done for {} logs in {} ms", count, (new Date()).getTime() - start);
	}

	@Override
	public void start() {

		// prepare and start the connection source
		connectionSource = new PooledConnectionSource(appConfig);
		connectionSource.setContext(this.getContext());
		connectionSource.start();
		
		// get information about the source
		supportsBatchUpdates = connectionSource.supportsBatchUpdates();
		
		started = true;
	}

	@Override
	public void append(ILoggingEvent eventObject) {

		if (supportsBatchUpdates) {
			// we can use batch ..
			int primarySize;
			synchronized (this) {
				primaryList.add(eventObject);
				primarySize = primaryList.size();
			}

			if (primarySize > 50 && secondaryList.isEmpty()) {
				fetch();
			}
			return;
		}
		
		// else we have to do it one by one .. 
		
		Connection connection = null;
		try {
			connection = connectionSource.getConnection();
			connection.setAutoCommit(false);

			// prepare statement
			PreparedStatement stmt= connection.prepareStatement(getInsertSQL());
			
			// inserting an event and getting the result must be exclusive
			synchronized (this) {
				// we can do more subAppend at time .. 
				bindStatement(eventObject, stmt);
				// execute ..
				stmt.executeUpdate();
			}

			// we no longer need the insertStatement
			stmt.close();
			
			connection.commit();
		} catch (Throwable sqle) {
			addError("problem appending event", sqle);
		} finally {
			DBHelper.closeConnection(connection);
		}
	}
	
	/**
	 * Bind the parameters to he insert statement.
	 *
	 * @param event
	 * @param stmt
	 * @throws Throwable
	 */
	protected void bindStatement(ILoggingEvent event,
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
			final String execString = mdc.get(Log.MDC_EXECUTION_KEY_NAME);
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

	}

	/**
	 * Convert information about stack trace into text form.
	 *
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
//	public void setConnectionSource(ConnectionSource connectionSource) {
//		this.connectionSource = connectionSource;
//	}
}
