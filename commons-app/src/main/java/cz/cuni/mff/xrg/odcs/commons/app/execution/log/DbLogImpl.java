package cz.cuni.mff.xrg.odcs.commons.app.execution.log;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessReadBase;

/**
 * Implementation of {@link DbLog} interface.
 * 
 * @author Petyr
 */
public class DbLogImpl extends DbAccessReadBase<Log> implements DbLog {

	public DbLogImpl() {
		super(Log.class);
	}
	
}
