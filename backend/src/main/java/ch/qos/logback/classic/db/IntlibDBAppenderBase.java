/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.qos.logback.classic.db;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.db.DBAppenderBase;
import cz.cuni.xrg.intlib.backend.logback.VirtuosoSQLDialect;

/**
 *
 * @author Jan Vojt
 * @copyright (c) 2013 Jan Vojt
 */
public abstract class IntlibDBAppenderBase extends DBAppenderBase<ILoggingEvent> {

	@Override
	public void start() {

		if (connectionSource == null) {
		  throw new IllegalStateException(
			  "DBAppender cannot function without a connection source");
		}

		// The following line is the whole point of overriding this method.
		// Logback does not support Virtuoso, so we need to inject our dialect.
		sqlDialect = new VirtuosoSQLDialect();
		
		if (getGeneratedKeysMethod() != null) {
		  cnxSupportsGetGeneratedKeys = connectionSource.supportsGetGeneratedKeys();
		} else {
		  cnxSupportsGetGeneratedKeys = false;
		}
		cnxSupportsBatchUpdates = connectionSource.supportsBatchUpdates();
		if (!cnxSupportsGetGeneratedKeys && (sqlDialect == null)) {
		  throw new IllegalStateException(
			  "DBAppender cannot function if the JDBC driver does not support getGeneratedKeys method *and* without a specific SQL dialect");
		}
		
		started = true;
	}
	
}
