package cz.cuni.mff.xrg.odcs.commons.app.pipeline;

import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthenticationContext;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.JPQLDbQuery;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import cz.cuni.mff.xrg.odcs.commons.app.user.User;

import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

/**
 * Facade providing actions with pipelines.
 *
 * @author Jan Vojt
 */
public class PipelineFacade {

    private static final Logger LOG = LoggerFactory.getLogger(PipelineFacade.class);
	
    @Autowired(required = false)
    private AuthenticationContext authCtx;
	
	@Autowired
	private DbPipeline pipelineDao;
	
	@Autowired
	private DbExecution executionDao;

    /* ******************* Methods for managing Pipeline ******************** */
    /**
     * Pipeline factory with preset currently logged-in {@link User} as owner.
     * Created instance is not yet managed by {@link EntityManager}, thus needs
     * to be saved with {@link #save(Pipeline)} method.
     *
     * @return newly created pipeline
     */
    public Pipeline createPipeline() {
		Pipeline newPipeline = new Pipeline();
        if (authCtx != null) {
            newPipeline.setUser(authCtx.getUser());
        }
		return newPipeline;
    }

    /**
     * Creates a clone of given pipeline and returns it as a new instance.
     * Original owner is not preserved, rather currently logged in user is set
     * as an owner of the newly created pipeline.
     *
     * @param pipeline original pipeline to copy
     * @return newly copied pipeline
     */
	@Transactional
    @PreAuthorize("hasPermission(#pipeline, 'copy')")
    public Pipeline copyPipeline(Pipeline pipeline) {
		Pipeline newPipeline = new Pipeline(pipeline);
        if (authCtx != null) {
            newPipeline.setUser(authCtx.getUser());
        }
		return newPipeline;
    }

    /**
     * Returns list of all pipelines persisted in the database.
     *
     * @return list of pipelines
	 * @deprecated performance intensive for many pipelines in DB, use lazy
	 *			   container with paging instead
     */
	@Deprecated
    @PostFilter("hasPermission(filterObject,'view')")
    public List<Pipeline> getAllPipelines() {
		return pipelineDao.getAll();
    }

    /**
     * Find pipeline in database by ID and return it.
     *
     * @param id of Pipeline
     * @return Pipeline the found pipeline or null if the pipeline with given ID
     * does not exist
     */
    @PostAuthorize("hasPermission(returnObject,'view')")
    public Pipeline getPipeline(long id) {
		return pipelineDao.getInstance(id);
    }

    /**
     * Saves any modifications made to the pipeline into the database.
     *
     * @param pipeline
     */
    @Transactional
    @PreAuthorize("hasPermission(#pipeline,'save')")
    public void save(Pipeline pipeline) {
		pipelineDao.save(pipeline);
    }

    /**
     * Deletes pipeline from database.
     *
     * @param pipeline
     */
    @Transactional
    @PreAuthorize("hasPermission(#pipeline, 'delete')")
    public void delete(Pipeline pipeline) {
		pipelineDao.delete(pipeline);
    }

    /**
     * Fetches all pipelines using give DPU template.
     *
     * @param dpu template
     * @return pipelines using DPU template
     */
    @PreAuthorize("hasPermission(#dpu, 'view')")
    public List<Pipeline> getPipelinesUsingDPU(DPUTemplateRecord dpu) {
		return pipelineDao.getPipelinesUsingDPU(dpu);
    }

	/**
	 * Checks for duplicate pipeline names. The name of pipeline in second
	 * argument is ignored, if given. It is to be used when editing already
	 * existing pipeline.
	 * 
	 * @param newName
	 * @param pipeline to be renamed, or null
	 * @return 
	 */
    public boolean hasPipelineWithName(String newName, Pipeline pipeline) {
		Pipeline duplicate = pipelineDao.getPipelineByName(newName);
        return duplicate == null || duplicate.equals(pipeline);
    }

    /**
     * Execute the given pipeline.
     *
     * @param pipeline
     * @param debug
     */
    public void run(Pipeline pipeline, boolean debug) {
        throw new UnsupportedOperationException();
    }

    /* ******************** Methods for managing PipelineExecutions ********* */
    /**
     * Creates a new {@link PipelineExecution}, which represents a pipeline run.
     * Created instance is not yet managed by {@link EntityManager}, thus needs
     * to be saved with {@link #save(PipelineExecution)} method.
     *
     * @param pipeline
     * @return pipeline execution of given pipeline
     */
    public PipelineExecution createExecution(Pipeline pipeline) {
		PipelineExecution newExec = new PipelineExecution(pipeline);
        if (authCtx != null) {
            newExec.setOwner(authCtx.getUser());
        }
		return newExec;
    }

    /**
     * Fetches all {@link PipelineExecution}s from database.
     *
     * @return list of executions
	 * @deprecated performance intensive for many pipeline executions, use
	 *			   container with paging support instead
     */
	@Deprecated
    public List<PipelineExecution> getAllExecutions() {
		return executionDao.getAllExecutions(null, null);
    }

    /**
     * Fetches all {@link PipelineExecution}s with given state from database.
     *
     * @param status
     * @return list of executions
     */
    public List<PipelineExecution> getAllExecutions(PipelineExecutionStatus status) {
		return executionDao.getAllExecutions(null, status);
    }

    /**
     * Find pipeline execution in database by ID and return it.
     *
     * @param id of PipelineExecution
     * @return PipelineExecution
     */
    public PipelineExecution getExecution(long id) {
		return executionDao.getInstance(id);
    }

    /**
     * Fetch all executions for given pipeline.
     *
     * @param pipeline
     * @return pipeline executions
     */
    public List<PipelineExecution> getExecutions(Pipeline pipeline) {
		return executionDao.getAllExecutions(pipeline, null);
    }

    /**
     * Fetch executions for given pipeline in given status.
     *
     * @param pipeline Pipeline which executions should be fetched.
     * @param status Execution status, in which execution should be.
     * @return PipelineExecutions
     *
     */
    public List<PipelineExecution> getExecutions(Pipeline pipeline, PipelineExecutionStatus status) {
        return executionDao.getAllExecutions(pipeline, status);
    }

    /**
     * Return end time of latest execution of given status for given pipeline.
     *
     * Ignore null values.
     *
     * @param pipeline
     * @param status Execution status, used to filter pipelines.
     * @return
     */
    public Date getLastExecTime(Pipeline pipeline, PipelineExecutionStatus status) {

        HashSet statuses = new HashSet(1);
        statuses.add(status);
        PipelineExecution exec = getLastExec(pipeline, statuses);

        return (exec == null) ? null : exec.getEnd();
    }

    /**
     * Return latest execution of given statuses for given pipeline. Ignore null
     * values.
     *
     * @param pipeline
     * @param statuses Set of execution statuses, used to filter pipelines.
     * @return last execution or null
     */
    public PipelineExecution getLastExec(Pipeline pipeline,
            Set<PipelineExecutionStatus> statuses) {
		return executionDao.getLastExecution(pipeline, statuses);
    }

    /**
     * Return latest execution of given pipeline. Ignore null values.
     *
     * @param pipeline
     * @return last execution or null
     */
    public PipelineExecution getLastExec(Pipeline pipeline) {
		return executionDao.getLastExecution(pipeline, EnumSet.allOf(PipelineExecutionStatus.class));
    }

    /**
     * Return latest execution of given statuses for given schedule. Ignore null
     * values.
     *
     * @param schedule
     * @param statuses Set of execution statuses, used to filter pipelines.
     * @return last execution or null
     */
    public PipelineExecution getLastExec(Schedule schedule,
            Set<PipelineExecutionStatus> statuses) {
        return executionDao.getLastExecution(schedule, statuses);
    }

    /**
     * Tells whether there were any changes to pipeline executions since the
     * last load.
     *
     * <p>
     * This method is provided purely for performance optimization of refreshing
     * execution statuses. Functionality is backed by database trigger
     * &quot;update_last_change&quot;.
     *
     * @param lastLoad
     * @return
     */
    public boolean hasModifiedExecutions(Date lastLoad) {
		return executionDao.hasModifiedExecutions(lastLoad);
    }

    /**
     * Persists new {@link PipelineExecution} or updates it if it was already
     * persisted before.
     *
     * @param exec
     */
    @Transactional
    public void save(PipelineExecution exec) {
		executionDao.save(exec);
    }

    /**
     * Deletes pipeline from database.
     *
     * @param exec
     */
    @Transactional
    public void delete(PipelineExecution exec) {
		executionDao.delete(exec);
    }

    /**
     * Stop the execution.
     *
     * @param execution
     */
    public void stopExecution(PipelineExecution execution) {
        execution.stop();
        save(execution);
    }
}
