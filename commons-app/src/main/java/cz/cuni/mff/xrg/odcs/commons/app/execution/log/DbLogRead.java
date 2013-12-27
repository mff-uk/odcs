package cz.cuni.mff.xrg.odcs.commons.app.execution.log;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessRead;
import java.util.Date;

/**
 *
 * @author Petyr
 */
public interface DbLogRead extends DbAccessRead<Log> {
	
	/**
	 * Delete all logs that are older then given date.
	 * @param date 
	 */
	void prune(Date date);

}
