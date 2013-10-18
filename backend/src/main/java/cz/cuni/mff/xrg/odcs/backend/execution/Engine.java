package cz.cuni.mff.xrg.odcs.backend.execution;

import cz.cuni.mff.xrg.odcs.backend.data.ContextDeleter;
import cz.cuni.mff.xrg.odcs.backend.data.DataUnitFactory;
import cz.cuni.mff.xrg.odcs.backend.execution.event.EngineEvent;
import cz.cuni.mff.xrg.odcs.backend.execution.event.EngineEventType;
import cz.cuni.mff.xrg.odcs.backend.execution.pipeline.Executor;
import cz.cuni.mff.xrg.odcs.backend.pipeline.event.PipelineFailedEvent;
import cz.cuni.mff.xrg.odcs.backend.pipeline.event.PipelineRestart;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.mff.xrg.odcs.commons.app.module.ModuleFacade;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineFacade;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.persistence.EntityNotFoundException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;

/**
 * Responsible for running and supervision queue of PipelineExecution tasks.
 * 
 * @author Petyr
 * 
 */
public class Engine implements ApplicationListener<EngineEvent> {

	/**
	 * Provide access to DPURecord implementation.
	 */
	@Autowired
	protected ModuleFacade moduleFacade;

	/**
	 * Publisher instance.
	 */
	@Autowired
	protected ApplicationEventPublisher eventPublisher;

	/**
	 * Application's configuration.
	 */
	@Autowired
	protected AppConfig appConfig;

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

	@Autowired
	private DataUnitFactory dataUnitFactory;
	
	/**
	 * Thread pool.
	 */
	protected ExecutorService executorService;

	/**
	 * Working directory.
	 */
	protected File workingDirectory;

	/**
	 * True if startUp method has already been called.
	 */
	protected Boolean startUpDone;

	protected static Logger LOG = LoggerFactory.getLogger(Engine.class);

	public Engine() {
		this.executorService = Executors.newCachedThreadPool();
		this.startUpDone = false;
	}

	/**
	 * Setup engine from given configuration.
	 */
	protected void setupConfig() {
		LOG.info("Configuring engine ...");

		workingDirectory = new File(
				appConfig.getString(ConfigProperty.GENERAL_WORKINGDIR));

		LOG.info("Working dir: {}", workingDirectory.toString());

		// make sure that our working directory exist
		if (workingDirectory.isDirectory()) {
			workingDirectory.mkdirs();
		}
	}

	/**
	 * Ask executorService to run the pipeline. Call {@link #startUp} before
	 * this function.
	 * 
	 * @param execution
	 */
	protected void run(PipelineExecution execution) {
		Executor executor = beanFactory.getBean(Executor.class);
		executor.bind(execution);
		// execute
		this.executorService.execute(executor);
	}

	/**
	 * Check database for new task (PipelineExecutions to run). Can run
	 * concurrently.
	 */
	protected synchronized void checkDatabase() {
		List<PipelineExecution> toExecute = pipelineFacade.getAllExecutions();
		// run pipeline executions ..
		for (PipelineExecution item : toExecute) {
			if (item.getStatus() == PipelineExecutionStatus.SCHEDULED) {
				// run scheduled pipeline
				run(item);
			}
		}
	}

	/**
	 * Check database for hanging running pipelines. Should be run just once
	 * before any execution starts.
	 * 
	 * Also setup engine according to it's configuration.
	 */
	protected void startUp() {
		// setup
		setupConfig();

		startUpDone = true;
		// list executions
		List<PipelineExecution> running = pipelineFacade
				.getAllExecutions(PipelineExecutionStatus.RUNNING);
		for (PipelineExecution execution : running) {
			// hanging pipeline ..

			// schedule new pipeline start
			execution.setStatus(PipelineExecutionStatus.SCHEDULED);

			// TODO Petyr: Run from last position

			// remove all from the previous execution
			ExecutionContextInfo context = execution.getContextReadOnly();
			if (context == null) {
				// no context, just set update pipeline status
			} else {
				// delete old context files
				File root = new File(
						appConfig.getString(ConfigProperty.GENERAL_WORKINGDIR));
				File executionRoot = new File(root, context.getRootPath());
				try {

					FileUtils.deleteDirectory(executionRoot);
				} catch (IOException e) {
					LOG.error(
							"Failed to delete old context directory. For execution: {}",
							execution.getId(), e);
					// there should be at least one PDU in pipeline
					if (execution.getPipeline().getGraph().getNodes().isEmpty()) {
						//
						LOG.error(
								"There are no DPUs on pipeline. Execution: {} Pipeline: {}",
								execution.getId(), execution.getPipeline()
										.getId());
					} else {
						eventPublisher.publishEvent(PipelineFailedEvent.create(
								"Failed to recover.",
								"The working directory can't be deleted.",
								null, execution, this));
					}
					// set pipeline execution to failed
					execution.setStatus(PipelineExecutionStatus.FAILED);
					execution.setEnd(new Date());
				}
				// reset context
				context.reset();
				// send message .. about restart
				eventPublisher
						.publishEvent(new PipelineRestart(execution, this));
			}

			try {
				pipelineFacade.save(execution);
			} catch (EntityNotFoundException ex) {
				LOG.warn("Seems like someone deleted our pipeline run.", ex);
			}
		}

		ContextDeleter deleter = new ContextDeleter(dataUnitFactory, appConfig);
		List<PipelineExecution> cancelling = pipelineFacade
				.getAllExecutions(PipelineExecutionStatus.CANCELLING);
		for (PipelineExecution execution : cancelling) {
			// delete execution data
			deleter.deleteContext(execution);
			// switch to cancelled ..
			execution.setStatus(PipelineExecutionStatus.CANCELLED);
			execution.setEnd(new Date());
			try {
				pipelineFacade.save(execution);
			} catch (EntityNotFoundException ex) {
				LOG.warn("Seems like someone deleted our pipeline run.", ex);
			}
		}
	}

	/**
	 * Take care about engine event.
	 * 
	 * @param type
	 */
	protected synchronized void onEvent(EngineEventType type) {
		switch (type) {
		case CHECK_DATABASE:
			checkDatabase();
			break;
		case STARTUP:
			if (startUpDone) {
				// already called
			} else {
				startUp();
			}
			break;
		default:
			// do nothing
			break;
		}
	}

	@Override
	public void onApplicationEvent(EngineEvent event) {
		onEvent(event.getType());
	}

}
