package cz.cuni.mff.xrg.odcs.backend.execution.pipeline;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import cz.cuni.mff.xrg.odcs.backend.context.Context;
import cz.cuni.mff.xrg.odcs.backend.context.ContextException;
import cz.cuni.mff.xrg.odcs.backend.logback.MdcExecutionLevelFilter;
import cz.cuni.mff.xrg.odcs.backend.pipeline.event.PipelineAbortedEvent;
import cz.cuni.mff.xrg.odcs.backend.pipeline.event.PipelineFailedEvent;
import cz.cuni.mff.xrg.odcs.backend.pipeline.event.PipelineFinished;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.execution.log.LogFacade;
import cz.cuni.mff.xrg.odcs.commons.app.execution.log.LogMessage;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineFacade;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.DependencyGraph;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.Node;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;

/**
 * Execute given pipeline. The {@link Executor} must be bind to the certain
 * {@link PipelineExecution} by calling {@link #bind(PipelineExecution)} before
 * any future use.
 * 
 * @author Petyr
 * 
 */
public class Executor implements Runnable {

	/**
	 * Logger class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(Executor.class);

	/**
	 * Publisher instance for publishing pipeline execution events.
	 */
	@Autowired
	private ApplicationEventPublisher eventPublisher;

	/**
	 * Bean factory used to create beans for single pipeline execution.
	 */
	@Autowired
	private BeanFactory beanFactory;

	/**
	 * Pipeline facade.
	 */
	@Autowired
	private PipelineFacade pipelineFacade;

	/**
	 * Log facade.
	 */
	@Autowired
	private LogFacade logFacade;

	/**
	 * Application's configuration.
	 */
	@Autowired
	private AppConfig appConfig;

	/**
	 * List of all {@link PreExecutor}s to execute before executing pipeline.
	 * Can be null.
	 */
	@Autowired(required = false)
	private List<PreExecutor> preExecutors;

	/**
	 * List of all {@link PostExecutor}s to execute after pipeline execution has
	 * finished. Can be null.
	 */
	@Autowired(required = false)
	private List<PostExecutor> postExecutors;

	/**
	 * PipelineExecution record, determine pipeline to run.
	 */
	private PipelineExecution execution;

	/**
	 * Store context related to Nodes (DPUs).
	 */
	private Map<Node, Context> contexts = new HashMap<>();

	/**
	 * End time of last successful pipeline execution.
	 */
	private Date lastSuccessfulExTime;

	/**
	 * Sort pre/post executors.
	 */
	@PostConstruct
	public void init() {
		if (preExecutors != null) {
			Collections.sort(preExecutors,
					AnnotationAwareOrderComparator.INSTANCE);
		}
		if (postExecutors != null) {
			Collections.sort(postExecutors,
					AnnotationAwareOrderComparator.INSTANCE);
		}
	}

	/**
	 * Bind {@link Executor} to the given {@link PipelineExecution}. Also update
	 * the {@link PipelineExecution}'s state.
	 * 
	 * @param execution
	 */
	public void bind(PipelineExecution execution) {
		this.execution = execution;
		contexts = new HashMap<>();

		// for newly scheduled pipelines delete the execution directory
		File coreExecutionFile = new File(
				appConfig.getString(ConfigProperty.GENERAL_WORKINGDIR),
				execution.getContext().getRootPath());
		if (execution.getStatus() == PipelineExecutionStatus.QUEUED) {
			// new run, check for directory
			if (coreExecutionFile.exists() && coreExecutionFile.isDirectory()) {
				// delete
				LOG.debug("Deleting existing execution's directory. ");

				try {
					FileUtils.deleteDirectory(coreExecutionFile);
				} catch (IOException e) {
					LOG.error("Failed to delete execution directory.");
				}
			}

			// update state and set start time
			this.execution.setStart(new Date());
			this.execution.setStatus(PipelineExecutionStatus.RUNNING);

			try {
				pipelineFacade.save(this.execution);
			} catch (EntityNotFoundException ex) {
				LOG.warn("Seems like someone deleted our pipeline run.", ex);
			}
		} else {
			// we continue in run ... so just continue
		}

		// load last execution time
		Date lastSucess = pipelineFacade.getLastExecTime(
				execution.getPipeline(),
				PipelineExecutionStatus.FINISHED_SUCCESS);
		Date lastSucessWarn = pipelineFacade.getLastExecTime(
				execution.getPipeline(),
				PipelineExecutionStatus.FINISHED_WARNING);

		if (lastSucess == null) {
			this.lastSuccessfulExTime = lastSucessWarn;
		} else if (lastSucessWarn == null) {
			this.lastSuccessfulExTime = lastSucess;
		} else {
			// get last successful execution time
			this.lastSuccessfulExTime = lastSucess.after(lastSucessWarn) ? lastSucess
					: lastSucessWarn;
		}
	}

	/**
	 * Execute {@link PreExecutor} from {@link Executor#preExecutors}. If any
	 * {@link PreExecutor} return false then return false. If there are no
	 * {@link PreExecutor} ({@link Executor#preExecutors} == null) then
	 * instantly return true.
	 * 
	 * @param graph
	 *            Dependency graph used for execution.
	 * @return
	 */
	private boolean executePreExecutors(DependencyGraph graph) {
		if (preExecutors == null) {
			return true;
		}

		for (PreExecutor item : preExecutors) {
			if (item.preAction(execution, contexts, graph)) {
				// continue execution
			} else {
				LOG.error("PreProcessor: {} failed", item.getClass().getName());
				return false;
			}
		}
		return true;
	}

	/**
	 * Execute {@link PostExecutor} from {@link Executor#postExecutors}. If any
	 * {@link PostExecutor} return false then return false. If there are no
	 * {@link PostExecutor} ({@link Executor#postExecutors} == null) then
	 * instantly return true.
	 * 
	 * @param graph
	 *            Dependency graph that has been used for execution.
	 * @return
	 */
	private boolean executePostExecutors(DependencyGraph graph) {
		if (postExecutors == null) {
			return true;
		}

		for (PostExecutor item : postExecutors) {
			if (item.postAction(execution, contexts, graph)) {
				// continue execution
			} else {
				LOG.error("PostProcessor: {} failed", item.getClass().getName());
				return false;
			}
		}
		return true;
	}

	/**
	 * Should be called in case that the execution failed. Does not save the
	 * {@link PipelineExecution} into database.
	 */
	private void executionFailed() {
		execution.setStatus(PipelineExecutionStatus.FAILED);
	}

	/**
	 * Should be called in case that the execution was cancelled by user. Does
	 * not save the {@link PipelineExecution} into database.
	 */
	private void executionCancelled() {
		execution.setStatus(PipelineExecutionStatus.CANCELLED);
	}

	/**
	 * Should be called in case that the execution has finished without error.
	 * Does not save the {@link PipelineExecution} into database.
	 */
	private void executionSuccessful() {
		boolean warnings = false;
		// look if there is context that finished with warnings
		for (Context item : contexts.values()) {
			if (item.warningMessagePublished()) {
				warnings = true;
				break;
			}
		}

		if (warnings) {
		} else {
			// test logs
			Set<org.apache.log4j.Level> levels = new HashSet<>(3);
			levels.add(org.apache.log4j.Level.WARN);
			levels.add(org.apache.log4j.Level.ERROR);
			levels.add(org.apache.log4j.Level.FATAL);
			warnings = logFacade.existLogs(execution, levels);
		}

		if (warnings) {
			execution.setStatus(PipelineExecutionStatus.FINISHED_WARNING);
		} else {
			execution.setStatus(PipelineExecutionStatus.FINISHED_SUCCESS);
		}
	}

	/**
	 * Prepare and return instance of {@link DependencyGraph}.
	 * 
	 * @return
	 */
	private DependencyGraph prepareDependencyGraph() {
		DependencyGraph dependencyGraph = null;
		final Pipeline pipeline = execution.getPipeline();
		// if in debug mode then pass the final DPU
		if (execution.isDebugging() && execution.getDebugNode() != null) {
			dependencyGraph = new DependencyGraph(pipeline.getGraph(),
					execution.getDebugNode());
		} else {
			dependencyGraph = new DependencyGraph(pipeline.getGraph());
		}
		return dependencyGraph;
	}

	/**
	 * Run the execution.
	 */
	private void execute() {
		// get dependency graph
		DependencyGraph dependencyGraph = prepareDependencyGraph();

		// execute pre-executors
		if (!executePreExecutors(dependencyGraph)) {
			// cancel the execution
			executionFailed();
			return;
		}

		boolean executionFailed = false;
		boolean executionCancelled = false;

		// execute each node
		for (Node node : dependencyGraph) {

			// put dpuInstance id to MDC, so we can identify logs related to the
			// dpuInstance
			MDC.put(LogMessage.MDC_DPU_INSTANCE_KEY_NAME,
					Long.toString(node.getDpuInstance().getId()));

			cz.cuni.mff.xrg.odcs.backend.execution.dpu.Executor dpuExecutor = beanFactory
					.getBean(cz.cuni.mff.xrg.odcs.backend.execution.dpu.Executor.class);
			
			try {
				dpuExecutor.bind(node, contexts, execution, lastSuccessfulExTime);
			} catch (ContextException e) {
				// failed to create context .. fail the execution
				eventPublisher.publishEvent(PipelineFailedEvent.create(e, node.getDpuInstance(), execution, this));
				executionFailed = true;
				break;
			}

			LOG.info("Starting execution of dpu {} = {}", node.getDpuInstance()
					.getId(), node.getDpuInstance().getName());
			
			final String threadName = "dpu: " + node.getDpuInstance().getName();
			Thread executorThread = new Thread(dpuExecutor, threadName);
			executorThread.start();

			// repeat until the executorThread is running
			boolean stopExecution = false;
			while (executorThread.isAlive()) {
				try {
					// sleep for two seconds
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// request stop
					stopExecution = true;
				}

				// check for user request to stop execution -> we need new
				// instance
				PipelineExecution uptodateExecution = pipelineFacade
						.getExecution(execution.getId());
				if (uptodateExecution == null) {
					LOG.warn("Seems like someone deleted our execution.");
					stopExecution = true;
				} else if (uptodateExecution.getStop()) {
					stopExecution = true;
					executionCancelled = true;
					eventPublisher.publishEvent(new PipelineAbortedEvent(
							execution, this));
				}

				if (stopExecution) {
					// update flag, so we do not override the value in database
					execution.setStatus(PipelineExecutionStatus.CANCELLING);
					// try to stop the DPU's execution thread
					stopExecution(executorThread, dpuExecutor);
					// jump out of waiting cycle
					break;
				}
			} // end of single DPU thread execution

			if (stopExecution) {
				// we should stop the execution
				executionFailed = true;
				// jump out of pipeline
				break;
			} else {
				// pipeline continue, check for DPU result
				if (dpuExecutor.executionFailed()) {
					// continue
					executionFailed = true;
					break;
				}
			}
			MDC.remove(LogMessage.MDC_DPU_INSTANCE_KEY_NAME);
		}

		// ending ..
		// set time then the pipeline's execution finished

		if (executionFailed) {
			if (executionCancelled) {
				executionCancelled();
			} else {
				executionFailed();
			}
		} else {
			executionSuccessful();
		}

		// all set
		if (!executePostExecutors(dependencyGraph)) {
			// failed ..
			executionFailed();
		}
	}

	@Override
	public void run() {
		// the execution start time has been already set in bind function
		// add marker to logs from this thread -> both must be specified !!
		final String executionId = Long.toString(execution.getId());
		if (!execution.isDebugging()) {
			// add minimal level to MDCExecutionLevelFilter
			MdcExecutionLevelFilter.add(executionId,
					ch.qos.logback.classic.Level.INFO);
		}
		MDC.put(LogMessage.MDPU_EXECUTION_KEY_NAME, executionId);

		LOG.info("Starting execution of pipeline {} = {}", executionId,
				execution.getPipeline().getName());

		// execute the pipeline it self
		execute();

		// set end time
		execution.setEnd(new Date());
		// save the execution
		try {
			pipelineFacade.save(execution);
		} catch (EntityNotFoundException ex) {
			LOG.warn("Seems like someone deleted our pipeline run.", ex);
		}

		// publish information for the rest of the application
		// that the execution finished ..
		eventPublisher.publishEvent(new PipelineFinished(execution, this));

		// unregister MDC execution filter
		MdcExecutionLevelFilter.remove(executionId);
		// clear all threads markers
		MDC.clear();
	}

	/**
	 * Stops pipeline execution. Usually invoke by user action.
	 * 
	 * @param executorThread
	 *            thread servicing execution which needs to be stopped
	 * @param dpuExecutor
	 *            Executor for given DPUs.
	 */
	private void stopExecution(Thread executorThread,
			cz.cuni.mff.xrg.odcs.backend.execution.dpu.Executor dpuExecutor) {
		LOG.debug("Cancelling the DPU execution ...");
		// set cancel flag
		dpuExecutor.cancel();
		// interrupt executorThread, and wait for it ...
		// we do not interrupt !!! as there may
		// be running pre-post executors
		try {
			executorThread.join();
		} catch (InterruptedException e) {
			// if we are interrupt stop waiting
		}
		LOG.debug("DPU thread calncelled");
	}

}
