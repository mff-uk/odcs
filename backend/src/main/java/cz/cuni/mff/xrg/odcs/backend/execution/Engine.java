package cz.cuni.mff.xrg.odcs.backend.execution;

import cz.cuni.mff.xrg.odcs.backend.execution.event.CheckDatabaseEvent;
import cz.cuni.mff.xrg.odcs.backend.execution.pipeline.Executor;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.execution.log.LogMessage;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineFacade;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Responsible for running and supervision queue of PipelineExecution tasks.
 * 
 * @author Petyr
 * 
 */
public class Engine implements ApplicationListener<CheckDatabaseEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(Engine.class);
	
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

	@PostConstruct
	private void propertySetter() {
		this.executorService = Executors.newCachedThreadPool();
		this.startUpDone = false;
		
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
		this.executorService.submit(executor);
	}

	/**
	 * Check database for new task (PipelineExecutions to run). Can run
	 * concurrently. Check database every 20 seconds.
	 */
	@Async
	@Scheduled(fixedDelay = 20000) 
	protected synchronized void checkDatabase() {
		if (!startUpDone) {
			// we does not start any execution
			// before start up method is executed
			startUp();
			return;
		}
		LOG.trace("Checking for new executions.");
		List<PipelineExecution> toExecute = pipelineFacade.getAllExecutions(PipelineExecutionStatus.SCHEDULED);
		// run pipeline executions ..
		for (PipelineExecution item : toExecute) {
			run(item);
		}
	}

	/**
	 * Check database for hanging running pipelines. Should be run just once
	 * before any execution starts.
	 * 
	 * Also setup engine according to it's configuration.
	 */
	protected void startUp() {
		if (startUpDone) {
			LOG.warn("Ignoring second startUp call");
			return;
		}
		startUpDone = true;

		ExecutionSanitizer sanitizer = beanFactory.getBean(ExecutionSanitizer.class);
				
		// list executions
		List<PipelineExecution> running = pipelineFacade
				.getAllExecutions(PipelineExecutionStatus.RUNNING);
		for (PipelineExecution execution : running) {
			MDC.put(LogMessage.MDPU_EXECUTION_KEY_NAME, execution.getId().toString());
			// hanging pipeline ..
			sanitizer.sanitize(execution);
			
			try {
				pipelineFacade.save(execution);
			} catch (EntityNotFoundException ex) {
				LOG.warn("Seems like someone deleted our pipeline run.", ex);
			}
			
			MDC.remove(LogMessage.MDPU_EXECUTION_KEY_NAME);
		}

		List<PipelineExecution> cancelling = pipelineFacade
				.getAllExecutions(PipelineExecutionStatus.CANCELLING);
		
		for (PipelineExecution execution : cancelling) {
			MDC.put(LogMessage.MDPU_EXECUTION_KEY_NAME, execution.getId().toString());
			// hanging pipeline ..
			sanitizer.sanitize(execution);
			
			try {
				pipelineFacade.save(execution);
			} catch (EntityNotFoundException ex) {
				LOG.warn("Seems like someone deleted our pipeline run.", ex);
			}
			
			MDC.remove(LogMessage.MDPU_EXECUTION_KEY_NAME);
		}
	}

	@Override
	public void onApplicationEvent(CheckDatabaseEvent event) {
		checkDatabase();
	}

}
