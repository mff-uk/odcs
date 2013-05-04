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
import cz.cuni.xrg.intlib.backend.extractor.events.ExtractCompletedEvent;
import cz.cuni.xrg.intlib.backend.extractor.events.ExtractContextImpl;
import cz.cuni.xrg.intlib.commons.extractor.ExtractContext;
import cz.cuni.xrg.intlib.commons.extractor.ExtractException;
import cz.cuni.xrg.intlib.backend.extractor.events.ExtractFailedEvent;
import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.backend.loader.events.LoadCompletedEvent;
import cz.cuni.xrg.intlib.backend.loader.events.LoadContextImpl;
import cz.cuni.xrg.intlib.commons.loader.LoadContext;
import cz.cuni.xrg.intlib.commons.loader.LoadException;
import cz.cuni.xrg.intlib.backend.loader.events.LoadFailedEvent;
import cz.cuni.xrg.intlib.commons.repository.LocalRepo;
import cz.cuni.xrg.intlib.commons.transformer.Transform;
import cz.cuni.xrg.intlib.backend.transformer.events.TransformCompletedEvent;
import cz.cuni.xrg.intlib.backend.transformer.events.TransformContextImpl;
import cz.cuni.xrg.intlib.commons.transformer.TransformContext;
import cz.cuni.xrg.intlib.commons.transformer.TransformException;
import cz.cuni.xrg.intlib.backend.transformer.events.TransformFailedEvent;
import java.util.HashMap;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.support.StaticApplicationContext;

/**
 * Run, cancel and debug concrete pipeline.
 *
 * @author Jiri Tomes
 * @author Jan Vojt
 */
public class PipelineWorker extends Thread {

    /**
     * Unique run identification.
     */
    private String runId = UUID.randomUUID().toString();
    private boolean alive = true;
    private boolean isWorking = true;
    /**
     * Publisher instance responsible for publishing pipeline execution events.
     * Show pipeline execution events to user.
     */
    private ApplicationEventPublisher eventPublisher;
    private PipelineExecution execution;
    private Engine engine;

    public PipelineWorker(Engine engine) {
        this.engine = engine;
        eventPublisher = new StaticApplicationContext();

    }

    /**
     * Lazy kill - waits until Pipeline run is finished.
     */
    public void kill() {
        alive = false;
    }

    /**
     * Implementation of workers activity. Worker constantly keeps asking engine
     * for jobs to run, until it is killed.
     */
    @Override
    public void run() {

        while (alive) {

            execution = engine.getJob();

            if (execution == null) {
                isWorking = false;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                    e.fillInStackTrace();
                }
            } else {
                isWorking = true;
                executePipeline(execution.getPipeline(), execution.getModuleFacade());
            }
        }
    }

    private void executePipeline(Pipeline pipeline, ModuleFacade moduleFacade) {

        long pipelineStart = System.currentTimeMillis();
        DependencyGraph dependencyGraph = new DependencyGraph(pipeline.getGraph());
//        eventPublisher.publishEvent(
//                new PipelineStartedEvent(pipeline, runId, this));

        LocalRepo repository = LocalRepo.createLocalRepo();
        repository.cleanAllRepositoryData();

        for (Node node : dependencyGraph) {
            runNode(node, repository, moduleFacade);
        }

        long duration = System.currentTimeMillis() - pipelineStart;
//        eventPublisher.publishEvent(
//                new PipelineCompletedEvent(duration, pipeline, runId, this));
    }

    /**
     * Executes a general node (ETL) in pipeline graph.
     *
     * @param node
     * @param repo
     */
    private void runNode(Node node, LocalRepo repo, ModuleFacade moduleFacade) {

        DPUInstance dpuInstance = node.getDpuInstance();
        DPU dpu = dpuInstance.getDpu();

        Type dpuType = dpu.getType();
        String dpuJarPath = dpu.getJarPath();
        Configuration configuration = dpuInstance.getInstanceConfig();

        switch (dpuType) {
            case EXTRACTOR: {
                Extract extractor = moduleFacade.getInstanceExtract(dpuJarPath);
                extractor.saveConfiguration(configuration);
                extractor.setLocalRepo(repo);
                ExtractContext ctx = new ExtractContextImpl(runId, new HashMap<String, Object>());
                runExtractor(extractor, ctx);
                break;
            }
            case TRANSFORMER: {
                Transform transformer = moduleFacade.getInstanceTransform(dpuJarPath);
                transformer.saveConfiguration(configuration);
                transformer.setLocalRepo(repo);
                TransformContext ctx = new TransformContextImpl(runId, new HashMap<String, Object>()) {};
                runTransformer(transformer, ctx);
                break;
            }
            case LOADER: {
                Load loader = moduleFacade.getInstanceLoader(dpuJarPath);
                loader.saveConfiguration(configuration);
                loader.setLocalRepo(repo);
                LoadContext ctx = new LoadContextImpl(runId, new HashMap<String, Object>());
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
            long start = System.currentTimeMillis();

            extractor.extract(ctx);
            ctx.setDuration(System.currentTimeMillis() - start);
//            eventPublisher.publishEvent(
//                    new ExtractCompletedEvent(extractor, ctx, this));

        } catch (ExtractException ex) {
//            eventPublisher.publishEvent(
//                    new ExtractFailedEvent(ex, extractor, ctx, this));
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
            long start = System.currentTimeMillis();

            transformer.transform(ctx);
            ctx.setDuration(System.currentTimeMillis() - start);
//            eventPublisher.publishEvent(
//                    new TransformCompletedEvent(transformer, ctx, this));

        } catch (TransformException ex) {
//            eventPublisher.publishEvent(
//                    new TransformFailedEvent(ex, transformer, ctx, this));
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
            long start = System.currentTimeMillis();

            loader.load(ctx);
            ctx.setDuration(System.currentTimeMillis() - start);
//            eventPublisher.publishEvent(
//                    new LoadCompletedEvent(loader, ctx, this));
        } catch (LoadException ex) {
//            eventPublisher.publishEvent(
//                    new LoadFailedEvent(ex, loader, ctx, this));
            ex.fillInStackTrace();
        }
    }

    /**
     * Tells whether worker is currently processing any pipeline.
     *
     * @return
     */
    public boolean isWorking() {
        return isWorking;
    }

    /**
     * Returns the event publisher instance.
     *
     * @return
     */
    public ApplicationEventPublisher getEventPublisher() {
        return eventPublisher;
    }

    /**
     * Setter for event publisher instance.
     *
     * @param eventPublisher
     */
    
    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}
