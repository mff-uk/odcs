package cz.cuni.mff.xrg.odcs.commons.app.execution.log;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessRead;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Level;

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
