package cz.cuni.xrg.intlib.backend.execution;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.xrg.intlib.backend.pipeline.events.PipelineCompletedEvent;
import cz.cuni.xrg.intlib.backend.pipeline.events.PipelineStartedEvent;
import cz.cuni.xrg.intlib.commons.ProcessingContext;
import cz.cuni.xrg.intlib.commons.Type;
import cz.cuni.xrg.intlib.commons.app.dpu.DPU;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.module.ModuleException;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.DependencyGraph;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Node;
import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import cz.cuni.xrg.intlib.commons.extractor.Extract;
import cz.cuni.xrg.intlib.backend.context.ContextException;
import cz.cuni.xrg.intlib.backend.extractor.events.ExtractCompletedEvent;
import cz.cuni.xrg.intlib.commons.extractor.ExtractException;
import cz.cuni.xrg.intlib.backend.extractor.events.ExtractFailedEvent;
import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.backend.loader.events.LoadCompletedEvent;
import cz.cuni.xrg.intlib.commons.loader.LoadException;
import cz.cuni.xrg.intlib.backend.loader.events.LoadFailedEvent;
import cz.cuni.xrg.intlib.commons.transformer.Transform;
import cz.cuni.xrg.intlib.backend.transformer.events.TransformCompletedEvent;
import cz.cuni.xrg.intlib.commons.transformer.TransformException;
import cz.cuni.xrg.intlib.backend.transformer.events.TransformFailedEvent;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Worker responsible for running single PipelineExecution.
 *
 * @author Petyr
 * @author Jiri Tomes
 * @author Jan Vojt
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

	/**
	 * Store context related to Nodes (DPUs).
	 */
	private Map<Node, ProcessingContext> contexts;
	// TODO: Add come id to the node ?
	
	/**
	 * Working directory for this execution.
	 */
	private File workDirectory;
	
	/**
	 * 
	 * @param execution The pipeline execution record to run.
	 * @param moduleFacade Module facade for obtaining DPUs instances.
	 * @param eventPublisher Application event publisher.
	 * @param workDirectory Working directory for this execution.
	 */
	public PipelineWorker(PipelineExecution execution, ModuleFacade moduleFacade, 
			ApplicationEventPublisher eventPublisher, File workDirectory) {
		this.execution = execution;
		this.moduleFacade = moduleFacade;
		this.eventPublisher = eventPublisher;
		this.contexts = new HashMap<Node, ProcessingContext>();
		this.workDirectory = workDirectory;
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
		eventPublisher.publishEvent(new PipelineStartedEvent(execution, this));

		// get dependency graph -> determine run order
		DependencyGraph dependencyGraph = new DependencyGraph(pipeline.getGraph());

		// run DPUs ...
		for (Node node : dependencyGraph) {
			try {
				runNode(node, dependencyGraph.getAncestors(node));
			} catch (ContextException e) {
				// quit execution
				System.out.println("Context exception:");
				e.printStackTrace();
				return;
			} catch (ModuleException e) {
				System.out.print("Module exception:");
				System.out.println(e.getMessage());
				System.out.println("Trace message: " + e.getTraceMessage());
				return;
			} catch (Exception e) {
				System.out.println("Exception:");
				e.printStackTrace();
				return;
			}
		}

		long duration = System.currentTimeMillis() - pipelineStart;
		eventPublisher.publishEvent(new PipelineCompletedEvent(duration,
				execution, this));
	}

	/**
	 * Return context that should be used when executing given DPU.
	 * 
	 * @param node
	 * @param ancestors
	 *            Ancestors of the given node.
	 * @return
	 * @throws ContextException 
	 */
	private ProcessingContext getContextForNode(DPUInstance dpuInstance, Type type, List<Node> ancestors) throws ContextException {
		ProcessingContext ctx = null;
		String contextId = "ex" + execution.getId() + "_dpuIns" + dpuInstance.getId();
		File contextDirectory = new File(workDirectory, "_dpuIns" + dpuInstance.getId() );
		// create directory
		contextDirectory.mkdirs();
		
		switch (type) {
			case EXTRACTOR: {
				cz.cuni.xrg.intlib.backend.context.ExtractContext extractContext;
				extractContext = new cz.cuni.xrg.intlib.backend.context.impl.ExtractContextImpl
						(contextId, execution, dpuInstance, eventPublisher, contextDirectory);
				ctx = extractContext;
				break;
			}
			case TRANSFORMER: {
				cz.cuni.xrg.intlib.backend.context.TransformContext extractContext;
				extractContext = new cz.cuni.xrg.intlib.backend.context.impl.TransformContextImpl
						(contextId, execution, dpuInstance, eventPublisher, contextDirectory);
				ctx = extractContext;
				for (Node item : ancestors) {
					if (contexts.containsKey(item)) {
						extractContext.addSource( contexts.get(item) ); 
					} else {
						// can't find context ..
					}
				}
				break;
			}
			case LOADER: {
				cz.cuni.xrg.intlib.backend.context.LoadContext extractContext;
				extractContext = new cz.cuni.xrg.intlib.backend.context.impl.LoadContextImpl
						(contextId, execution, dpuInstance, eventPublisher, contextDirectory);
				ctx = extractContext;
				for (Node item : ancestors) {
					if (contexts.containsKey(item)) {
						extractContext.addSource( contexts.get(item) ); 
					} else {
						// can't find context ..
					}
				}				
				break;
			}
		}
		return ctx;
	}

	
	/**
	 * Executes a single DPU associated with given Node.
	 * 
	 * @param node
	 * @param ancestors Ancestors of the given node.
	 * @throws ContextException 
	 */
	private void runNode(Node node, List<Node> ancestors) throws ContextException {
		// prepare what we need to start the execution
		DPUInstance dpuInstance = node.getDpuInstance();
		DPU dpu = dpuInstance.getDpu();
		Type dpuType = dpu.getType();
		String dpuJarPath = dpu.getJarPath();
		Configuration configuration = dpuInstance.getInstanceConfig();
		// get context ..
		ProcessingContext ctx = getContextForNode(dpuInstance, dpuType, ancestors);
		// now based on DPU type ..
		switch (dpuType) {
			case EXTRACTOR: {
				Extract extractor = moduleFacade.getInstanceExtract(dpuJarPath);
				extractor.saveConfiguration(configuration);
				runExtractor(extractor, (cz.cuni.xrg.intlib.commons.extractor.ExtractContext) ctx);
				break;
			}
			case TRANSFORMER: {
				Transform transformer = moduleFacade
						.getInstanceTransform(dpuJarPath);
				transformer.saveConfiguration(configuration);
				runTransformer(transformer, (cz.cuni.xrg.intlib.commons.transformer.TransformContext) ctx);
				break;
			}
			case LOADER: {
				Load loader = moduleFacade.getInstanceLoader(dpuJarPath);
				loader.saveConfiguration(configuration);
				runLoader(loader, (cz.cuni.xrg.intlib.commons.loader.LoadContext) ctx);
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
	private void runExtractor(Extract extractor, cz.cuni.xrg.intlib.commons.extractor.ExtractContext ctx) {

		try {
			// long start = System.currentTimeMillis();
			extractor.extract(ctx);
			// ctx.setDuration(System.currentTimeMillis() - start);
			eventPublisher.publishEvent(new ExtractCompletedEvent(extractor,
					ctx, this));
		} catch (ExtractException ex) {
			eventPublisher.publishEvent(new ExtractFailedEvent(ex, extractor,
					ctx, this));
			ex.fillInStackTrace();
		}
	}

	/**
	 * Runs a single Transformer DPU module.
	 * 
	 * @param transformer
	 * @param ctx
	 */
	private void runTransformer(Transform transformer, cz.cuni.xrg.intlib.commons.transformer.TransformContext ctx) {

		try {
			// long start = System.currentTimeMillis();
			transformer.transform(ctx);
			// ctx.setDuration(System.currentTimeMillis() - start);
			eventPublisher.publishEvent(new TransformCompletedEvent(
					transformer, ctx, this));
		} catch (TransformException ex) {
			eventPublisher.publishEvent(new TransformFailedEvent(ex,
					transformer, ctx, this));
			ex.fillInStackTrace();
		}
	}

	/**
	 * Runs a single Loader DPU module.
	 * 
	 * @param loader
	 * @param ctx
	 */
	private void runLoader(Load loader, cz.cuni.xrg.intlib.commons.loader.LoadContext ctx) {

		try {
			// long start = System.currentTimeMillis();
			loader.load(ctx);
			// ctx.setDuration(System.currentTimeMillis() - start);
			eventPublisher.publishEvent(new LoadCompletedEvent(loader, ctx,
					this));
		} catch (LoadException ex) {
			eventPublisher.publishEvent(new LoadFailedEvent(ex, loader, ctx,
					this));
			ex.fillInStackTrace();
		}
	}
}
