package cz.cuni.mff.xrg.odcs.backend.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.db.DBHelper;
import ch.qos.logback.core.db.DriverManagerConnectionSource;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.constants.LenghtLimits;
import cz.cuni.mff.xrg.odcs.commons.app.dao.StringUtils;
import cz.cuni.mff.xrg.odcs.commons.app.execution.log.Log;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

	@Autowired
	protected AppConfig appConfig;

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

	/**
	 * Lock used to secure privilege access to the {@link #primaryList}
	 * and {@link #secondaryList}
	 */
	private final Object lockList = new Object();
	
	/**
	 * Return string that is used as insert query into logging table.
	 *
	 * @return
	 */
	private String getInsertSQL() {
		return "INSERT INTO logging"
				+ " (logLevel, timestmp, logger, message, dpu, execution, stack_trace)"
				+ " VALUES (?, ?, ? ,?, ?, ?, ?)";
	}

	/**
	 * Return not null {@link Conenction}. If failed to get connection then
	 * continue to try until success.
	 * @return 
	 */
	private Connection getConnection() {
		Connection connection = null;
		while (connection == null) {
			try {
				connection = connectionSource.getConnection();
				connection.setAutoCommit(false);
			} catch(SQLException ex) {
				// wait for some time an try it again .. 
				LOG.error("Failed to get sql connection, next try in second.", ex);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException intExc) {
					// ok, continue
				}
			}
		}
		return connection;
	}

	/**
	 * Store given logs into database.
	 * @param connection
	 * @param logs
	 * @return True only if all logs has been saved into database.
	 */
	private boolean flushIntoDatabase(Connection connection, List<ILoggingEvent> logs) {
		try (PreparedStatement stmt = connection.prepareStatement(getInsertSQL())) {
			// append all logs
			for (ILoggingEvent item : secondaryList) {
				bindStatement(item, stmt);
				stmt.addBatch();
			}
			// call insert
			stmt.executeBatch();
			stmt.close();
			connection.commit();
		} catch (Throwable sqle) {
			// we failed, try it again .. later
			LOG.error("Can't save logs into database.", sqle);
			// wait for some time
			try {
				Thread.sleep(2500);
			} catch (InterruptedException ex) {
				// ok just try it again
			}			
			return false;
		}
		return true;
	}
	
	/**
	 * Fetch stored logs into database. It can be executed on user demand or by
	 * spring periodically.
	 */
	@Override
	@Async
	@Scheduled(fixedDelay = 4300)
	public synchronized void flush() {
				
		if (!supportsBatchUpdates) {
			// no batches, the data are stored immediately
			// we have nothing to do
			return;
		}
		
		// switch the logs buffers
		synchronized (lockList) {
			List<ILoggingEvent> swap = primaryList;
			primaryList = secondaryList;
			secondaryList = swap;
		}

		// do we have some logs to store?
		if (secondaryList.isEmpty()) {
			return;
		}

		Date start = new Date();
		
		// if true then we try to save data into database
		boolean nextTry = true;
		while(nextTry) {
			// get connection
			LOG.debug("flush() : get connection");
			Connection connection = getConnection();
			LOG.debug("flush() : flushIntoDatabase");
			// update next try based on result
			// if the save failed, we try it again .. 
			nextTry = !flushIntoDatabase(connection, secondaryList);
			
			// at the end close the connection
			DBHelper.closeConnection(connection);
		}
		// the data has been saved, we can clear the buffer
		secondaryList.clear();
		
		LOG.debug("flush() -> finished in: {} ms ", (new Date()).getTime() - start.getTime() );
	}

	/**
	 * Do immediate write of given log into database. If the database is down
	 * then the log is not saved.
	 *
	 * @param eventObject
	 */
	private void appendImmediate(ILoggingEvent eventObject) {
		Connection connection = null;

		try {
			connection = connectionSource.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException sqle) {
			// for sure close the connection
			DBHelper.closeConnection(connection);
			// log
			addError("failed to get sql connection", sqle);
			return;
		}

		try (PreparedStatement stmt = connection.prepareStatement(getInsertSQL())) {
			// inserting an event and getting the result must be exclusive
			synchronized (this) {
				// we can do more subAppend at time .. 
				bindStatement(eventObject, stmt);
				// execute ..
				stmt.executeUpdate();
			}
			// we no longer need the insertStatement
			stmt.close();
			// commit command
			connection.commit();
		} catch (Throwable sqle) {
			addError("problem appending event", sqle);
		} finally {
			DBHelper.closeConnection(connection);
		}
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
			// we can use batch .. so we just add the logs into 
			// the queue
			synchronized (lockList) {
				primaryList.add(eventObject);
			}
		} else {
			appendImmediate(eventObject);
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

		/* ! ! ! ! ! ! ! ! ! 
		 * As the message and stackTrace are BLOBS interpreted as string they
		 * must not be empty -> they have to be null or have some content
		 *
		 */
		
		// prepare the values
		Integer logLevel = event.getLevel().toInteger();
		Long timeStamp = event.getTimeStamp();
		String logger = StringUtils.secureLenght(event.getLoggerName(),
				LenghtLimits.LOGGER_NAME);
		String message = event.getFormattedMessage();
		
		// null check
		if (logLevel == null) {
			logLevel = Level.INFO_INTEGER;
		}
		if (timeStamp == null) {
			timeStamp = (new Date()).getTime();
		}
		if (logger == null) {
			logger = "unknown";
		}
		message = StringUtils.emptyToNull(message);
	
		// bind
		stmt.setInt(1, logLevel);
		stmt.setLong(2, timeStamp);
		stmt.setString(3, logger);
		stmt.setString(4, message);

		// get DPU and EXECUTION from MDC
		final Map<String, String> mdc = event.getMDCPropertyMap();

		try {
			final String dpuString = mdc.get(Log.MDC_DPU_INSTANCE_KEY_NAME);
			final int dpuId = Integer.parseInt(dpuString);
			stmt.setInt(5, dpuId);
		} catch (NumberFormatException | SQLException ex) {
			stmt.setNull(5, java.sql.Types.INTEGER);
		}

		try {
			final String execString = mdc.get(Log.MDC_EXECUTION_KEY_NAME);
			final int execId = Integer.parseInt(execString);
			stmt.setInt(6, execId);
		} catch (NumberFormatException | SQLException ex) {
			stmt.setNull(6, java.sql.Types.INTEGER);
			LOG.error("Failed to set EXECUTION_KEY for log.", ex);
		}

		String stackTrace = null;
		IThrowableProxy proxy = event.getThrowableProxy();
		if (proxy != null) {
			StringBuilder sb = new StringBuilder();
			prepareStackTrace(proxy, sb);
			stackTrace = sb.toString();
		} 
		
		stackTrace = StringUtils.emptyToNull(stackTrace);
		stmt.setString(7, stackTrace);
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

}
