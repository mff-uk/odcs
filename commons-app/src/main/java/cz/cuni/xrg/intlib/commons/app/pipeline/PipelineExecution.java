package cz.cuni.xrg.intlib.commons.app.pipeline;

import java.util.HashMap;
import java.util.UUID;

import cz.cuni.xrg.intlib.commons.Type;
import cz.cuni.xrg.intlib.commons.app.dpu.DPU;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.DependencyGraph;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Node;
import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import cz.cuni.xrg.intlib.commons.extractor.Extract;
import cz.cuni.xrg.intlib.commons.extractor.ExtractContext;
import cz.cuni.xrg.intlib.commons.extractor.ExtractException;
import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.commons.loader.LoadContext;
import cz.cuni.xrg.intlib.commons.loader.LoadException;
import cz.cuni.xrg.intlib.commons.transformer.Transform;
import cz.cuni.xrg.intlib.commons.transformer.TransformContext;
import cz.cuni.xrg.intlib.commons.transformer.TransformException;


/**
 * Information about executed pipeline and their states.
 *
 * @author Jiri Tomes
 * @author Jan Vojt
 */
public class PipelineExecution implements Runnable/*, ApplicationEventPublisherAware*/ {

	/**
	 * Unique run identification.
	 */
	private String runId = UUID.randomUUID().toString();
	
    /**
     * Actual status for executed pipeline.
     */
    private ExecutionStatus status;
    
    /**
     * Pipeline for executing.
     */
    private Pipeline pipeline;
    
    /**
     * Publisher instance responsible for publishing pipeline execution events.
     */
//    private ApplicationEventPublisher eventPublisher;
    
    /**
     * Module facade for fetching executable DPUs through OSGi.
     */
    private ModuleFacade moduleFacade;
    
    /**
     * Constructor
     * @param pipeline
     */
    public PipelineExecution(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public ExecutionStatus getExecutionStatus() {
        return status;
    }

    public void setExecutionStatus(ExecutionStatus newStatus) {
        status = newStatus;
    }

	/**
	 * @return the pipeline
	 */
	public Pipeline getPipeline() {
		return pipeline;
	}

	/**
	 * @param pipeline the pipeline to set
	 */
	public void setPipeline(Pipeline pipeline) {
		this.pipeline = pipeline;
	}

	/**
	 * @param moduleFacade the moduleFacade to set
	 */
	public void setModuleFacade(ModuleFacade moduleFacade) {
		this.moduleFacade = moduleFacade;
	}

//	@Override
//	public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
//		this.eventPublisher = publisher;
//	}

	/**
	 * Runs the pipeline.
	 */
	@Override
    public void run() {

//        long pipelineStart = System.currentTimeMillis();
        DependencyGraph dependencyGraph = new DependencyGraph(pipeline.getGraph());
//        eventPublisher.publishEvent(new PipelineStartedEvent(pipeline, runId, this));

        for (Node node : dependencyGraph) {
        	runNode(node);
        }
        
//        long duration = System.currentTimeMillis() - pipelineStart;
//        eventPublisher.publishEvent(
//        	new PipelineCompletedEvent(duration, pipeline, runId, this)
//        );
    }
    
	/**
	 * Executes a general node (ETL) in pipeline graph.
	 * @param node
	 */
    private void runNode(Node node) {
    	
    	DPUInstance dpuInstance = node.getDpuInstance();
        DPU dpu = dpuInstance.getDpu();

        Type dpuType = dpu.getType();
        String dpuJarPath = dpu.getJarPath();
        Configuration configuration = dpuInstance.getInstanceConfig();
        
        switch (dpuType) {
        case EXTRACTOR : {
            Extract extractor = moduleFacade.getInstanceExtract(dpuJarPath);
            extractor.setSettings(configuration);
            ExtractContext ctx = new ExtractContext(runId, new HashMap<String, Object>());
            runExtractor(extractor, ctx);
        	break;
        }
        case TRANSFORMER : {
            Transform transformer = moduleFacade.getInstanceTransform(dpuJarPath);
            transformer.setSettings(configuration);
            TransformContext ctx = new TransformContext(runId, new HashMap<String, Object>());
        	runTransformer(transformer, ctx);
        	break;
        }
        case LOADER : {
            Load loader = moduleFacade.getInstanceLoader(dpuJarPath);
            loader.setSettings(configuration);
        	LoadContext ctx = new LoadContext(runId, new HashMap<String, Object>());
        	runLoader(loader, ctx);
        	break;
        }
        default :
        	throw new RuntimeException("Unknown DPU type.");
        }
    }
    
    /**
     * Runs a single extractor DPU module.
     * @param extractor
     * @param ctx
     */
    private void runExtractor(Extract extractor, ExtractContext ctx) {

        try {
            long start = System.currentTimeMillis();

            extractor.extract(ctx);
            ctx.setDuration(System.currentTimeMillis() - start);
//            eventPublisher.publishEvent(
//            	new ExtractCompletedEvent(extractor, ctx, this)
//            );

        } catch (ExtractException ex) {
//            eventPublisher.publishEvent(
//            	new ExtractFailedEvent(ex, extractor, ctx, this)
//            );
        	ex.printStackTrace();
        }
    }
    
    /**
     * Runs a single Transformer DPU module.
     * @param transformer
     * @param ctx
     */
    private void runTransformer(Transform transformer, TransformContext ctx) {

        try {
            long start = System.currentTimeMillis();

            transformer.transform(ctx);
            ctx.setDuration(System.currentTimeMillis() - start);
//            eventPublisher.publishEvent(
//            		new TransformCompletedEvent(transformer, ctx, this)
//            );

        } catch (TransformException ex) {
//            eventPublisher.publishEvent(
//            	new TransformFailedEvent(ex, transformer, ctx, this)
//            );
        	ex.printStackTrace();
        }
    }
    
    /**
     * Runs a single Loader DPU module.
     * @param loader
     * @param ctx
     */
    private void runLoader(Load loader, LoadContext ctx) {

        try {
            long start = System.currentTimeMillis();

            loader.load(ctx);
            ctx.setDuration(System.currentTimeMillis() - start);
//            eventPublisher.publishEvent(
//            	new LoadCompletedEvent(loader, ctx, this)
//            );
        } catch (LoadException ex) {
//            eventPublisher.publishEvent(
//            	new LoadFailedEvent(ex, loader, ctx, this)
//            );
        	ex.printStackTrace();
        }
    }
    
}
