package cz.cuni.xrg.intlib.backend.execution;

import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import cz.cuni.xrg.intlib.commons.app.conf.ConfigProperty;
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
	
	public Engine(ModuleFacade moduleFacade, DatabaseAccess database) {
		this.moduleFacade = moduleFacade;		
    	this.executorService = Executors.newCachedThreadPool();
    	this.database = database;
    }

	public Engine(ModuleFacade moduleFacade, DatabaseAccess database, ExecutorService executorService) {
		this.moduleFacade = moduleFacade;
    	this.executorService = executorService;
    	this.database = database;
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
		// ..
	}
	
    /**
     * Ask executorService to run the pipeline.
     *
     * @param pipelineExecution
     */
    private void run(PipelineExecution pipelineExecution) {
    	// mark pipeline execution as Started ..
    	pipelineExecution.setExecutionStatus(ExecutionStatus.RUNNING);
    	
    	// prepare working directory for execution
    	File directory = new File(workingDirectory, "execution-" + pipelineExecution.getId() );    	
    	// store workingDirectory    	
    	pipelineExecution.setStart(new Date());
    	
    	// update record in DB
    	database.getPipeline().save(pipelineExecution);
   	
    	this.executorService.execute(
    			new PipelineWorker(pipelineExecution, moduleFacade, eventPublisher, database, directory));
    }
    
    /**
     * Check database for new task (PipelineExecutions to run).
     * Can run concurrently.
     */
    public synchronized void checkDatabase() {
    	List<PipelineExecution> toExecute = database.getPipeline().getAllExecutions();
    	// run pipeline executions ..   
    	for (PipelineExecution item : toExecute) {
    		if (item.getExecutionStatus() == ExecutionStatus.SCHEDULED) {
    			// run scheduled pipeline
    			run(item);
    		}
    	}
    }
     
	@Override
	public void onApplicationEvent(EngineEvent event) {
		// react on message from server
		switch(event.getType()) {
		case CheckDatabase:
			checkDatabase();
			break;
		default:
			// do nothing
			break;
		}
		
	}

	@Override
	public void setApplicationEventPublisher(
			ApplicationEventPublisher applicationEventPublisher) {
		this.eventPublisher = applicationEventPublisher;		
	}

}
