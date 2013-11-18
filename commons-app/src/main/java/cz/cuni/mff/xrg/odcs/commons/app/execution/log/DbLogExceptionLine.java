package cz.cuni.mff.xrg.odcs.commons.app.execution.log;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccess;

/**
 * Interface providing access to {@link LogExceptionLine} data objects.
 *
 * @author Jan Vojt
 */
public interface DbLogExceptionLine extends DbAccess<LogExceptionLine> {

    /**
     * Fetches exception stacktraces for given log message logged by logback
     * into RDBMS.
     *
     * @param message
     * @return
     */
    public LogException getLogException(LogMessage message);

}
