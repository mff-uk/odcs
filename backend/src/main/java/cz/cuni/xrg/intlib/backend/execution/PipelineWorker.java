package cz.cuni.xrg.intlib.backend.execution;

import cz.cuni.xrg.intlib.backend.pipeline.events.PipelineCompletedEvent;
import cz.cuni.xrg.intlib.backend.pipeline.events.PipelineStartedEvent;
import cz.cuni.xrg.intlib.commons.Type;
import cz.cuni.xrg.intlib.commons.app.dpu.DPU;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.DependencyGraph;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Node;
import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import cz.cuni.xrg.intlib.commons.extractor.Extract;
import cz.cuni.xrg.intlib.backend.context.impl.ExtractContextImpl;
import cz.cuni.xrg.intlib.backend.context.impl.LoadContextImpl;
import cz.cuni.xrg.intlib.backend.context.impl.TransformContextImpl;
import cz.cuni.xrg.intlib.backend.data.rdf.LocalRDFRepo;
import cz.cuni.xrg.intlib.backend.extractor.events.ExtractCompletedEvent;
import cz.cuni.xrg.intlib.commons.extractor.ExtractContext;
import cz.cuni.xrg.intlib.commons.extractor.ExtractException;
import cz.cuni.xrg.intlib.backend.extractor.events.ExtractFailedEvent;
import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.backend.loader.events.LoadCompletedEvent;
import cz.cuni.xrg.intlib.commons.loader.LoadContext;
import cz.cuni.xrg.intlib.commons.loader.LoadException;
import cz.cuni.xrg.intlib.backend.loader.events.LoadFailedEvent;
import cz.cuni.xrg.intlib.commons.transformer.Transform;
import cz.cuni.xrg.intlib.backend.transformer.events.TransformCompletedEvent;
import cz.cuni.xrg.intlib.commons.transformer.TransformContext;
import cz.cuni.xrg.intlib.commons.transformer.TransformException;
import cz.cuni.xrg.intlib.backend.transformer.events.TransformFailedEvent;
import cz.cuni.xrg.intlib.commons.data.rdf.RDFDataRepository;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Worker responsible for running single PipelineExecution.
 *
 * @author Jiri Tomes
 * @author Jan Vojt
 * @author Petyr
 */
public class PipelineWorker implements Runnable {

    /**
     * PipelineExecution record, determine pipeline to run.
     */
    private PipelineExecution execution;
    
    /**
     * Publisher instance for publishing pipeline execution events.
     */
    private ApplicationEventPublisher eventPublisher;
    
    /**
     * Provide access to DPU implementation.
     */
    private ModuleFacade moduleFacade;
    
    public PipelineWorker(PipelineExecution execution, ModuleFacade moduleFacade, ApplicationEventPublisher eventPublisher) {
        this.execution = execution;
        this.moduleFacade = moduleFacade;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Implementation of workers activity. Worker constantly keeps asking engine
     * for jobs to run, until it is killed.
     */
    @Override
    public void run() {
    	// get pipeline to run
    	Pipeline pipeline = execution.getPipeline();
    	
        long pipelineStart = System.currentTimeMillis();

        eventPublisher.publishEvent(
        		new PipelineStartedEvent(execution, this));
                
        // get repository
        // TODO: Use context .. 
        RDFDataRepository repository = LocalRDFRepo.createLocalRepo();
        repository.cleanAllRepositoryData();
        
        // get dependency graph -> determine run order
        DependencyGraph dependencyGraph = new DependencyGraph(pipeline.getGraph());
        
        // run DPUs
        for (Node node : dependencyGraph) {
            runNode(node, repository, moduleFacade);
        }

        long duration = System.currentTimeMillis() - pipelineStart;
        eventPublisher.publishEvent(
        		new PipelineCompletedEvent(duration, execution, this));    
    }


    /**
     * Executes a general node (ETL) in pipeline graph.
     *
     * @param node
     * @param repo
     */
    private void runNode(Node node, RDFDataRepository repo, ModuleFacade moduleFacade) {

        DPUInstance dpuInstance = node.getDpuInstance();
        DPU dpu = dpuInstance.getDpu();

        Type dpuType = dpu.getType();
        String dpuJarPath = dpu.getJarPath();
        Configuration configuration = dpuInstance.getInstanceConfig();

        switch (dpuType) {
            case EXTRACTOR: {
                Extract extractor = moduleFacade.getInstanceExtract(dpuJarPath);
                extractor.saveConfiguration(configuration);
                extractor.setRDFRepo(repo);
                ExtractContext ctx = new ExtractContextImpl(execution, dpuInstance);
                runExtractor(extractor, ctx);
                break;
            }
            case TRANSFORMER: {
                Transform transformer = moduleFacade.getInstanceTransform(dpuJarPath);
                transformer.saveConfiguration(configuration);
                transformer.setRDFRepo(repo);
                TransformContext ctx = new TransformContextImpl(execution, dpuInstance);
                runTransformer(transformer, ctx);
                break;
            }
            case LOADER: {
                Load loader = moduleFacade.getInstanceLoader(dpuJarPath);
                loader.saveConfiguration(configuration);
                loader.setRDFRepo(repo);
                LoadContext ctx = new LoadContextImpl(execution, dpuInstance);
                runLoader(loader, ctx);
                break;
            }
            default:
                throw new RuntimeException("Unknown DPU type.");
        }
    }

    /**
     * Runs a single extractor DPU module.
     *
     * @param extractor
     * @param ctx
     */
    private void runExtractor(Extract extractor, ExtractContext ctx) {

        try {
            //long start = System.currentTimeMillis();

            extractor.extract(ctx);
            //ctx.setDuration(System.currentTimeMillis() - start);
            eventPublisher.publishEvent(
                    new ExtractCompletedEvent(extractor, ctx, this));

        } catch (ExtractException ex) {
            eventPublisher.publishEvent(
                    new ExtractFailedEvent(ex, extractor, ctx, this));
            ex.fillInStackTrace();

        }
    }

    /**
     * Runs a single Transformer DPU module.
     *
     * @param transformer
     * @param ctx
     */
    private void runTransformer(Transform transformer, TransformContext ctx) {

        try {
            //long start = System.currentTimeMillis();

            transformer.transform(ctx);
            //ctx.setDuration(System.currentTimeMillis() - start);
            eventPublisher.publishEvent(
                    new TransformCompletedEvent(transformer, ctx, this));

        } catch (TransformException ex) {
            eventPublisher.publishEvent(
                    new TransformFailedEvent(ex, transformer, ctx, this));
            ex.fillInStackTrace();
        }
    }

    /**
     * Runs a single Loader DPU module.
     *
     * @param loader
     * @param ctx
     */
    private void runLoader(Load loader, LoadContext ctx) {

        try {
            //long start = System.currentTimeMillis();

            loader.load(ctx);
            //ctx.setDuration(System.currentTimeMillis() - start);
            eventPublisher.publishEvent(
                    new LoadCompletedEvent(loader, ctx, this));
        } catch (LoadException ex) {
            eventPublisher.publishEvent(
                    new LoadFailedEvent(ex, loader, ctx, this));
            ex.fillInStackTrace();
        }
    }
}
