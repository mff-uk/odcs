package cz.cuni.xrg.intlib.backend.execution;

import cz.cuni.xrg.intlib.backend.AppConfiguration;
import cz.cuni.xrg.intlib.backend.communication.ServerEvent;
import cz.cuni.xrg.intlib.commons.DpuType;
import cz.cuni.xrg.intlib.commons.app.dpu.DPU;
import cz.cuni.xrg.intlib.commons.app.dpu.InstanceConfiguration;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import cz.cuni.xrg.intlib.commons.app.pipeline.ExecutionStatus;
import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.PipelineGraph;
import cz.cuni.xrg.intlib.commons.configuration.Configuration;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;
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
	
	/**
	 * Working directory.
	 */
	protected File workingDirectory;
	
	public Engine(ModuleFacade moduleFacade) {
		this.moduleFacade = moduleFacade;
    	this.executorService = Executors.newCachedThreadPool();
    }

	public Engine(ModuleFacade moduleFacade, ExecutorService executorService) {
		this.moduleFacade = moduleFacade;
    	this.executorService = executorService;
    }
	
	/**
	 * Setup engine from given configuration.
	 * @param config
	 */
	public void setup(AppConfiguration config) {
		workingDirectory = new File( config.getWorkingDirectory() );
		// make sure that the directory exist
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
    	File directory = new File(workingDirectory, "ex" + pipelineExecution.getId() );
    	
    	this.executorService.execute(
    			new PipelineWorker(pipelineExecution, moduleFacade, eventPublisher, directory));
    }

    private PipelineExecution testExec() {
    	
        Pipeline pipe = new Pipeline();
        PipelineGraph graph = new PipelineGraph();
        pipe.setGraph(graph);    	
    	
        DPU extractor = new DPU("RDF Extractor", DpuType.EXTRACTOR);
        DPU loader = new DPU("RDF Loader", DpuType.LOADER);

        extractor.setJarPath("RDF_extractor/target/RDF_extractor-0.0.1.jar");
        loader.setJarPath("RDF_loader/target/RDF_loader-0.0.1.jar");

        int eId = graph.addDpu(extractor);
        int lId = graph.addDpu(loader);

        graph.addEdge(eId, lId);

        // set configurations
        InstanceConfiguration exConfig = new InstanceConfiguration();
        exConfig.setValue("SPARQL_endpoint", "http://ld.opendata.cz:8894/sparql-auth");
        exConfig.setValue("Host_name", "SPARQL");
        exConfig.setValue("Password", "nejlepsipaper");
        exConfig.setValue("GraphsUri", new LinkedList<String>());
        exConfig.setValue("SPARQL_query", "select * where {?s ?o ?p} LIMIT 10");

        graph.getNodeById(eId).getDpuInstance().setInstanceConfig(exConfig);

        InstanceConfiguration ldConfig = new InstanceConfiguration();
        List<String> graphsURI=new LinkedList<>();
        graphsURI.add("http://ld.opendata.cz/resource/myGraph/001");
        ldConfig.setValue("SPARQL_endpoint", "http://ld.opendata.cz:8894/sparql");
        ldConfig.setValue("Host_name", "SPARQL");
        ldConfig.setValue("Password", "nejlepsipaper");
        ldConfig.setValue("GraphsUri", (Serializable) graphsURI);

        graph.getNodeById(lId).getDpuInstance().setInstanceConfig(ldConfig);    	
    	
        return new PipelineExecution(pipe);
    }
    
    /**
     * Check database for new task (PipelineExecutions to run).
     * Can run concurrently.
     */
    public synchronized void checkDatabase() {
    	List<PipelineExecution> toExecute = new LinkedList<PipelineExecution>();

    	// add test pipeline
    	toExecute.add(testExec());
    	
    	// run pipeline executions ..   
    	for (PipelineExecution item : toExecute) {
    		if (item.getExecutionStatus() == ExecutionStatus.SCHEDULED) {
    			// run scheduled pipeline
    			run(item);
    		}
    	}
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
