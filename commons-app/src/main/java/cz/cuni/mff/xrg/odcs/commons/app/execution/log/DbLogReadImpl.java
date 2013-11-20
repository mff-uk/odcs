package cz.cuni.mff.xrg.odcs.commons.app.execution.log;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessReadBase;

/**
 *
 * @author Petyr
 */
public class DbLogReadImpl extends DbAccessReadBase<Log> implements DbLogRead {

	public DbLogReadImpl() {
		super(Log.class);
	}
	
}
