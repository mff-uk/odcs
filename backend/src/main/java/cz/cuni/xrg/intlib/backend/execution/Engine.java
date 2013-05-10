package cz.cuni.xrg.intlib.backend.execution;

import cz.cuni.xrg.intlib.backend.communication.ServerEvent;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
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
 */
public class Engine implements ApplicationListener<ServerEvent>, ApplicationEventPublisherAware  {
	
    /**
     * Provide access to DPU implementation.
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
	
	public Engine(ModuleFacade moduleFacade) {
		this.moduleFacade = moduleFacade;
    	this.executorService = Executors.newCachedThreadPool();
    }

	public Engine(ModuleFacade moduleFacade, ExecutorService executorService) {
		this.moduleFacade = moduleFacade;
    	this.executorService = executorService;
    }	
	
    /**
     * Ask executorService to run the pipeline.
     *
     * @param pipelineExecution
     */
    private void run(PipelineExecution pipelineExecution) {
    	this.executorService.execute(
    			new PipelineWorker(pipelineExecution, moduleFacade, eventPublisher));
    }

    /**
     * Check database for new task (PipelineExecutions to run).
     * Can run concurrently.
     */
    private synchronized void checkDatabase() {
    	List<PipelineExecution> toExecute = null;
    	
    	System.out.println("Engine: checking the database");
    	
    	// run pipeline executions ..   
    	//for (PipelineExecution item : toExecute) {
    	//	run(item);
    	//}
    }
    
	@Override
	public void onApplicationEvent(ServerEvent event) {
		// react on message from server
		switch(event.getMessage()) {
		case CheckDatabase:
			checkDatabase();
			break;
		case Uknown:
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
