package cz.cuni.xrg.intlib.backend.execution;

import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import cz.cuni.xrg.intlib.commons.app.conf.ConfigProperty;
import cz.cuni.xrg.intlib.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.xrg.intlib.backend.DatabaseAccess;
import cz.cuni.xrg.intlib.backend.pipeline.event.PipelineFailedEvent;
import cz.cuni.xrg.intlib.backend.pipeline.event.PipelineRestart;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Node;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;

/**
 * Responsible for running and supervision queue of PipelineExecution tasks.
 * 
 * @author Petyr
 * 
 */
public class Engine
		implements ApplicationListener<EngineEvent>,
		ApplicationEventPublisherAware {

	/**
	 * Provide access to DPURecord implementation.
	 */
	protected ModuleFacade moduleFacade;

	/**
	 * Thread pool.
	 */
	protected ExecutorService executorService;

	/**
	 * Publisher instance.
	 */
	protected ApplicationEventPublisher eventPublisher;

	/**
	 * Working directory.
	 */
	protected File workingDirectory;

	/**
	 * Access to the database
	 */
	protected DatabaseAccess database;

	/**
	 * True if startUp method has already been called.
	 */
	protected Boolean startUpDone;

	/**
	 * Application's configuration.
	 */
	protected AppConfig appConfig;
	
	protected static Logger LOG = LoggerFactory.getLogger(Engine.class);

	public Engine(ModuleFacade moduleFacade,
			DatabaseAccess database,
			AppConfig appConfig) {
		this.moduleFacade = moduleFacade;
		this.executorService = Executors.newCachedThreadPool();
		this.database = database;
		this.startUpDone = false;
		this.appConfig = appConfig;
	}

	public Engine(ModuleFacade moduleFacade,
			DatabaseAccess database,
			AppConfig appConfig,
			ExecutorService executorService) {
		this.moduleFacade = moduleFacade;
		this.executorService = executorService;
		this.database = database;
		this.startUpDone = false;
		this.appConfig = appConfig;
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
	 * @param pipelineExecution
	 */
	protected void run(PipelineExecution pipelineExecution) {
		// mark pipeline execution as Started ..
		pipelineExecution.setExecutionStatus(PipelineExecutionStatus.RUNNING);
		pipelineExecution.setStart(new Date());
		// prepare working directory for execution
		File directory = new File(workingDirectory, "execution-"
				+ pipelineExecution.getId());
		// update record in DB
		database.getPipeline().save(pipelineExecution);
		// run pipeline
		this.executorService.execute(new PipelineWorker(pipelineExecution,
				moduleFacade, eventPublisher, database, directory, appConfig));
	}

	/**
	 * Check database for new task (PipelineExecutions to run). Can run
	 * concurrently.
	 */
	protected void checkDatabase() {
		List<PipelineExecution> toExecute = database.getPipeline()
				.getAllExecutions();
		// run pipeline executions ..
		for (PipelineExecution item : toExecute) {
			if (item.getExecutionStatus() == PipelineExecutionStatus.SCHEDULED) {
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
		List<PipelineExecution> toExecute = database.getPipeline()
				.getAllExecutions();
		for (PipelineExecution execution : toExecute) {
			if (execution.getExecutionStatus() == PipelineExecutionStatus.RUNNING) {
				// hanging pipeline ..

				// schedule new pipeline start
				execution.setExecutionStatus(PipelineExecutionStatus.SCHEDULED);

				// TODO Petyr: Run from last position

				// remove all from the previous execution
				ExecutionContextInfo context = execution.getContextReadOnly();
				if (context == null) {
					// no context, just set update pipeline status
				} else {
					// delete old context files
					File root = new File(appConfig.getString(ConfigProperty.GENERAL_WORKINGDIR));
					File executionRoot = new File(root, context.getRootPath());					
					try {
						
						FileUtils.deleteDirectory(executionRoot);
					} catch (IOException e) {
						LOG.error(
								"Failed to delete old context directory. For execution: {}",
								execution.getId(), e);
						// there should be at least one PDU in pipeline
						if (execution.getPipeline().getGraph().getNodes()
								.isEmpty()) {
							//
							LOG.error(
									"There are no DPUs on pipeline. Execution: {} Pipeline: {}",
									execution.getId(), execution.getPipeline()
											.getId());
						} else {
							// use first node ..
							Node node = execution.getPipeline().getGraph()
									.getNodes().iterator().next();
							eventPublisher
									.publishEvent(new PipelineFailedEvent(
											"Failed to recover. The working directory can't be deleted.",
											node.getDpuInstance(), execution,
											this));
						}
						// set pipeline execution to failed
						execution
								.setExecutionStatus(PipelineExecutionStatus.FAILED);
						execution.setEnd(new Date());
					}
					// reset context
					context.reset();
					// send message .. about restart
					eventPublisher.publishEvent(new PipelineRestart(execution, this));
				}
				database.getPipeline().save(execution);
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
		case CheckDatabase:
			checkDatabase();
			break;
		case StartUp:
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

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.eventPublisher = applicationEventPublisher;
	}

}
