package cz.cuni.xrg.intlib.backend.logback;

import ch.qos.logback.core.db.dialect.SQLDialect;

/**
 * Virtuoso SQL dialect used by logback.
 *
 * @author Jan Vojt
 */
public class VirtuosoSQLDialect implements SQLDialect {

	public static final String SELECT_INSERT_ID = "select identity_value()";

	@Override
	public String getSelectInsertId() {
		return SELECT_INSERT_ID;
	}
}
