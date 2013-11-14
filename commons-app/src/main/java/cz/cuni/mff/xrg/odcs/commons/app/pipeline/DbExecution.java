package cz.cuni.mff.xrg.odcs.commons.app.pipeline;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAcess;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Interface for access to {@link PipelineExecution}s. 
 * Spring does not support autowired on generic types
 *
 * @author Petyr
 */
public interface DbExecution extends DbAcess<PipelineExecution> {
	
	/**
	 * @param pipeline or null
	 * @param status or null
	 * @return all pipeline executions with given status
	 */
	public List<PipelineExecution> getAllExecutions(Pipeline pipeline,
			PipelineExecutionStatus status);
	
    /**
     * Return latest execution of given statuses for given pipeline. Ignore null
     * values.
     *
     * @param pipeline
     * @param statuses Set of execution statuses, used to filter pipelines.
     * @return last execution or null
     */
	public PipelineExecution getLastExecution(Pipeline pipeline,
			Set<PipelineExecutionStatus> statuses);

    /**
     * Return latest execution of given statuses for given schedule. Ignore null
     * values.
     *
     * @param schedule
     * @param statuses Set of execution statuses, used to filter pipelines.
     * @return last execution or null
     */
    public PipelineExecution getLastExecution(Schedule schedule,
            Set<PipelineExecutionStatus> statuses);

    /**
     * Tells whether there were any changes to pipeline executions since the
     * last load.
     *
     * <p>
     * This method is provided purely for performance optimization of refreshing
     * execution statuses. Functionality is backed by database trigger
     * &quot;update_last_change&quot;.
     *
     * @param since
     * @return whether any execution was updated since given date
     */
    public boolean hasModifiedExecutions(Date since);
	
	
	
}