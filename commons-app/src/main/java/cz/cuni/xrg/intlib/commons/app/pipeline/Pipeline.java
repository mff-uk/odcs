package cz.cuni.xrg.intlib.commons.app.pipeline;

import cz.cuni.xrg.intlib.commons.Type;
import cz.cuni.xrg.intlib.commons.app.dpu.DPU;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import javax.persistence.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
//import cz.cuni.xrg.intlib.commons.app.pipeline.event.PipelineCompletedEvent;
//import cz.cuni.xrg.intlib.commons.app.pipeline.event.PipelineStartedEvent;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.DependencyGraph;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.GraphIterator;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Node;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.PipelineGraph;
import cz.cuni.xrg.intlib.commons.app.user.Resource;
import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import cz.cuni.xrg.intlib.commons.extractor.Extract;
import cz.cuni.xrg.intlib.commons.extractor.ExtractCompletedEvent;
import cz.cuni.xrg.intlib.commons.extractor.ExtractContext;
import cz.cuni.xrg.intlib.commons.extractor.ExtractException;
import cz.cuni.xrg.intlib.commons.extractor.ExtractFailedEvent;
import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.commons.loader.LoadCompletedEvent;
import cz.cuni.xrg.intlib.commons.loader.LoadContext;
import cz.cuni.xrg.intlib.commons.loader.LoadException;
import cz.cuni.xrg.intlib.commons.loader.LoadFailedEvent;
import cz.cuni.xrg.intlib.commons.transformer.Transform;
import cz.cuni.xrg.intlib.commons.transformer.TransformCompletedEvent;
import cz.cuni.xrg.intlib.commons.transformer.TransformContext;
import cz.cuni.xrg.intlib.commons.transformer.TransformException;
import cz.cuni.xrg.intlib.commons.transformer.TransformFailedEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Represents a fixed workflow composed of one or several {@link Extract}s,
 * {@link Transform}s and {@link Load}s organized in acyclic graph. <br/>
 * Processing will always take place in the following order: <ol> <li>Execute
 * all {@link Extract}s</li> <ul> <li>If an Extractor throws an error publish an
 * {@link ExtractFailedEvent} - otherwise publish an
 * {@link ExtractCompletedEvent}</li> <li>If an Extractor requests cancellation
 * of the pipeline through {@link ProcessingContext#cancelPipeline} publish a
 * {@link PipelineAbortedEvent} and exit</li> </ul> <li>Execute all
 * {@link Transform}s in the order of their dependences given by graph</li>
 * <ul> <li>If a Transformer throws an error publish an
 * {@link TransformFailedEvent} - otherwise publish an
 * {@link TransformCompletedEvent}</li>
 * <li>If a Transformer requests cancellation of the pipeline through
 * {@link ProcessingContext#cancelPipeline} publish a
 * {@link PipelineAbortedEvent} and exit</li> </ul> <li>Execute all
 * {@link Load}s</li> <ul> <li>If a Loader throws an error publish an
 * {@link LoadFailedEvent} - otherwise publish an {@link LoadCompletedEvent}
 * </li>
 * <li>If a Loader requests cancellation of the pipeline through
 * {@link ProcessingContext#cancelPipeline} publish a
 * {@link PipelineAbortedEvent} and exit</li> </ul> <li>Publish a
 * {@link PipelineCompletedEvent}
 * </ol> <br/> A Spring {@link ApplicationEventPublisher} is required for
 * propagation of important events occurring throughout the pipeline.
 *
 * @see Extract
 * @see Transform
 * @see Load
 * @author Alex Kreiser (akreiser@gmail.com)
 * @author Jiri Tomes
 * @author Jan Vojt <jan@vojt.net>
 * @author Bogo
 */
@Entity
@Table(name = "pipeline_model")
public class Pipeline implements Resource, ApplicationEventPublisherAware {

    /**
     * Unique ID for each pipeline
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    //private State state;
    /**
     * Human-readable pipeline name
     */
    private String name;
    /**
     * Human-readable pipeline description
     */
    private String description;
    @Transient
    private PipelineGraph graph;
    /**
     * Publisher instance responsible for publishing pipeline execution events.
     */
    @Transient
    protected ApplicationEventPublisher eventPublisher;

    /**
     * Default constructor for JPA
     */
    public Pipeline() {
        this.eventPublisher = new GenericApplicationContext();
    }

	/**
	 * Constructor with given pipeline name and description.
	 * @param name
	 * @param description
	 */
    public Pipeline(String name, String description) {
        this.name = name;
        this.description = description;
        this.eventPublisher = new GenericApplicationContext();
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String newDescription) {
        description = newDescription;
    }

    /*
     public State getState() {
     return state;
     }

     public void setState(State newState) {
     state = newState;
     }
     */
    public PipelineGraph getGraph() {
        return graph;
    }

    public void setGraph(PipelineGraph graph) {
        this.graph = graph;
    }

    public int getId() {
        return id;
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
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Runs the pipeline.
     */
    public void run() {

        long pipelineStart = System.currentTimeMillis();
        String runId = UUID.randomUUID().toString();
        final Map<String, Object> customData = new HashMap<String, Object>();

        DependencyGraph dependencyGraph = new DependencyGraph(graph);
        GraphIterator iterator = new GraphIterator(dependencyGraph);

        //eventPublisher.publishEvent(new PipelineStartedEvent(this, runId, this));

        while (iterator.hasNext()) {

            Node node = iterator.next();
            DPUInstance dpuInstance = node.getDpuInstance();
            DPU dpu = dpuInstance.getDpu();

            Type dpuType = dpu.getType();
            String dpuJarPath = dpu.getJarPath();

            Configuration configuration = dpuInstance.getInstanceConfig();
            ModuleFacade moduleFacade = new ModuleFacade();

            switch (dpuType) {

                case EXTRACTOR: {

                    Extract extractor = moduleFacade.getInstanceExtract(dpuJarPath);
                    extractor.setSettings(configuration);

                    ExtractContext context = new ExtractContext(runId, customData);

                    try {
                        long start = System.currentTimeMillis();

                        extractor.extract(context);
                        context.setDuration(System.currentTimeMillis() - start);
                        eventPublisher.publishEvent(new ExtractCompletedEvent(extractor, context, this));

                    } catch (ExtractException ex) {
                        eventPublisher.publishEvent(new ExtractFailedEvent(ex, extractor, context, this));
                    }
                }

                case TRANSFORMER: {
                    Transform transformer = moduleFacade.getInstanceTransform(dpuJarPath);
                    transformer.setSettings(configuration);

                    TransformContext context = new TransformContext(runId, customData);

                    try {
                        long start = System.currentTimeMillis();

                        transformer.transform(context);
                        context.setDuration(System.currentTimeMillis() - start);
                        eventPublisher.publishEvent(new TransformCompletedEvent(transformer, context, this));

                    } catch (TransformException ex) {
                        eventPublisher.publishEvent(new TransformFailedEvent(ex, transformer, context, this));
                    }
                }

                case LOADER: {
                    Load loader = moduleFacade.getInstanceLoader(dpuJarPath);
                    loader.setSettings(configuration);

                    LoadContext context = new LoadContext(runId, customData);
                    try {
                        long start = System.currentTimeMillis();

                        loader.load(context);
                        context.setDuration(System.currentTimeMillis() - start);
                        eventPublisher.publishEvent(new LoadCompletedEvent(loader, context, this));
                    } catch (LoadException ex) {
                        eventPublisher.publishEvent(new LoadFailedEvent(ex, loader, context, this));
                    }
                }
            }
        }

        //eventPublisher.publishEvent(new PipelineCompletedEvent((System.currentTimeMillis() - pipelineStart), this, runId, this));
    }

    @Override
    public String getResourceId() {
        return Pipeline.class.toString();
    }
}
