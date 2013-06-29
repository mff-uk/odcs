package cz.cuni.xrg.intlib.backend.execution;

import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import cz.cuni.xrg.intlib.commons.app.conf.ConfigProperty;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionContextInfo;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionStatus;
import cz.cuni.xrg.intlib.commons.app.execution.PipelineExecution;
import cz.cuni.xrg.intlib.backend.DatabaseAccess;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;

/**
 * Responsible for running and supervision queue of PipelineExecution tasks.
 *
 * @author Petyr
 * 
 */
public class Engine implements ApplicationListener<EngineEvent>, ApplicationEventPublisherAware  {
	
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
	
	public Engine(ModuleFacade moduleFacade, DatabaseAccess database) {
		this.moduleFacade = moduleFacade;		
    	this.executorService = Executors.newCachedThreadPool();
    	this.database = database;
    	this.startUpDone = false;
    }

	public Engine(ModuleFacade moduleFacade, DatabaseAccess database, ExecutorService executorService) {
		this.moduleFacade = moduleFacade;
    	this.executorService = executorService;
    	this.database = database;
    	this.startUpDone = false;
    }
	
	/**
	 * Setup engine from given configuration.
	 * @param config
	 */
	public void setup(AppConfig config) {
		workingDirectory = new File( config.getString(ConfigProperty.GENERAL_WORKINGDIR) );
		// make sure that our working directory exist
		if (workingDirectory.isDirectory()) {
			workingDirectory.mkdirs();
		}
	}
	
    /**
     * Ask executorService to run the pipeline.
     *
     * @param pipelineExecution
     */
	protected void run(PipelineExecution pipelineExecution) {
    	// mark pipeline execution as Started ..
    	pipelineExecution.setExecutionStatus(ExecutionStatus.RUNNING);
    	pipelineExecution.setStart(new Date());
    	// prepare working directory for execution
    	File directory = new File(workingDirectory, "execution-" + pipelineExecution.getId() );    	
    	// update record in DB
    	database.getPipeline().save(pipelineExecution);   	
    	// run pipeline
    	this.executorService.execute(
    			new PipelineWorker(pipelineExecution, moduleFacade, eventPublisher, database, directory));
    }
    
    /**
     * Check database for new task (PipelineExecutions to run).
     * Can run concurrently.
     */
	protected void checkDatabase() {
    	List<PipelineExecution> toExecute = database.getPipeline().getAllExecutions();
    	// run pipeline executions ..   
    	for (PipelineExecution item : toExecute) {
    		if (item.getExecutionStatus() == ExecutionStatus.SCHEDULED) {
    			// run scheduled pipeline
    			run(item);
    		}
    	}
    }
    
    /**
     * Check database for hanging running pipelines. Should
     * be run just once before any execution starts.
     */
	protected void startUp() {
		
    }
    
    /**
     * Take care about engine event.
     * @param type
     */
    protected synchronized void onEvent(EngineEventType type) {
    	switch(type) {
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
	public void setApplicationEventPublisher(
			ApplicationEventPublisher applicationEventPublisher) {
		this.eventPublisher = applicationEventPublisher;		
	}

}
