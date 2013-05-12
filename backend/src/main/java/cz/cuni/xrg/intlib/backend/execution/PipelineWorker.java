package cz.cuni.xrg.intlib.backend.execution;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cuni.xrg.intlib.backend.AppEntry;
import cz.cuni.xrg.intlib.backend.pipeline.events.PipelineCompletedEvent;
import cz.cuni.xrg.intlib.backend.pipeline.events.PipelineContextErrorEvent;
import cz.cuni.xrg.intlib.backend.pipeline.events.PipelineFailedEvent;
import cz.cuni.xrg.intlib.backend.pipeline.events.PipelineModuleErrorEvent;
import cz.cuni.xrg.intlib.backend.pipeline.events.PipelineStartedEvent;
import cz.cuni.xrg.intlib.backend.pipeline.events.PipelineStructureError;
import cz.cuni.xrg.intlib.commons.DpuType;
import cz.cuni.xrg.intlib.commons.ProcessingContext;
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
import cz.cuni.xrg.intlib.commons.extractor.ExtractContext;
import cz.cuni.xrg.intlib.backend.context.ContextException;
import cz.cuni.xrg.intlib.backend.context.ExtendedExtractContext;
import cz.cuni.xrg.intlib.backend.context.ExtendedLoadContext;
import cz.cuni.xrg.intlib.backend.context.ExtendedTransformContext;
import cz.cuni.xrg.intlib.backend.context.impl.ExtendedExtractContextImpl;
import cz.cuni.xrg.intlib.backend.context.impl.ExtendedLoadContextImpl;
import cz.cuni.xrg.intlib.backend.context.impl.ExtendedTransformContextImpl;
import cz.cuni.xrg.intlib.backend.extractor.events.ExtractCompletedEvent;
import cz.cuni.xrg.intlib.commons.extractor.ExtractException;
import cz.cuni.xrg.intlib.backend.extractor.events.ExtractFailedEvent;
import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.commons.loader.LoadContext;
import cz.cuni.xrg.intlib.backend.loader.events.LoadCompletedEvent;
import cz.cuni.xrg.intlib.commons.loader.LoadException;
import cz.cuni.xrg.intlib.backend.loader.events.LoadFailedEvent;
import cz.cuni.xrg.intlib.commons.transformer.Transform;
import cz.cuni.xrg.intlib.commons.transformer.TransformContext;
import cz.cuni.xrg.intlib.backend.transformer.events.TransformCompletedEvent;
import cz.cuni.xrg.intlib.commons.transformer.TransformException;
import cz.cuni.xrg.intlib.backend.transformer.events.TransformFailedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	 * Logger class.
	 */
	private Logger logger;
	
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
		this.logger = LoggerFactory.getLogger(PipelineWorker.class);
	}

	/**
	 * Called in case that the execution failed.
	 * @param message Cause of failure. 
	 */
	private void executionFailed(String message) {
		// send event
		eventPublisher.publishEvent(new PipelineFailedEvent(message, execution, this));
		// and do clean up
		cleanUp();
	}
	
	/**
	 * Called in case of successful execution.
	 */
	private void executionSuccessful() {
		// and do clean up
		cleanUp();		
	}
	
	/**
	 * Do cleanup work after pipeline execution.
	 */
	private void cleanUp() {
		
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
			boolean result;
			try {
				result = runNode(node, dependencyGraph.getAncestors(node));
			} catch (ContextException e) {
				eventPublisher.publishEvent(new PipelineContextErrorEvent(e, execution, this));				
				logger.error("Context exception: " + e.getMessage());
			e.printStackTrace();
				executionFailed(e.getMessage());
				return;
			} catch (ModuleException e) {
				eventPublisher.publishEvent(new PipelineModuleErrorEvent(e, execution, this));
				logger.error("Module exception: " + e.getMessage());
			e.printStackTrace();
				executionFailed(e.getMessage());
				return;
			} catch (StructureException e) {
				eventPublisher.publishEvent(new PipelineStructureError(e, execution, this));
				logger.error("Structure exception: " + e.getMessage());
				executionFailed(e.getMessage());
				return;				
			} catch (Exception e) {
				logger.error("Exception: " + e.getMessage());
			e.printStackTrace();
				executionFailed(e.getMessage());
				return;
			}
			
			if (result) {
				// DPU executed successfully
			} else {
				// error -> end pipeline
				executionFailed("DPU execution failed.");
				return;
			}
		}

		long duration = System.currentTimeMillis() - pipelineStart;
		eventPublisher.publishEvent(new PipelineCompletedEvent(duration,
				execution, this));
		
		executionSuccessful();
	}

	/**
	 * Return context that should be used when executing given DPU.
	 * 
	 * @param node
	 * @param ancestors Ancestors of the given node.
	 * @return
	 * @throws ContextException 
	 * @throws StructureException 
	 */
	private ProcessingContext getContextForNode(DPUInstance dpuInstance, DpuType type, List<Node> ancestors) throws ContextException, StructureException {
		ProcessingContext ctx = null;
		String contextId = "ex" + execution.getId() + "_dpuIns" + dpuInstance.getId();
		File contextDirectory = new File(workDirectory, "_dpuIns" + dpuInstance.getId() );
		// create directory
		contextDirectory.mkdirs();
		
		switch (type) {
			case EXTRACTOR: {
				ExtendedExtractContext extractContext;
				extractContext = new ExtendedExtractContextImpl
						(contextId, execution, dpuInstance, eventPublisher, contextDirectory);
				ctx = extractContext;
				break;
			}
			case TRANSFORMER: {
				ExtendedTransformContext extractContext;
				extractContext = new ExtendedTransformContextImpl
						(contextId, execution, dpuInstance, eventPublisher, contextDirectory);
				ctx = extractContext;
				if (ancestors.isEmpty()) {
					// no ancestors ? -> error
					throw new StructureException("No inputs.");
				}					
				for (Node item : ancestors) {
					if (contexts.containsKey(item)) {
						extractContext.addSource( contexts.get(item) ); 
					} else {
						// can't find context ..
						throw new StructureException("Can't find context.");
					}
				}
				break;
			}
			case LOADER: {
				ExtendedLoadContext extractContext;
				extractContext = new ExtendedLoadContextImpl
						(contextId, execution, dpuInstance, eventPublisher, contextDirectory);
				ctx = extractContext;
				if (ancestors.isEmpty()) {
					// no ancestors ? -> error
					throw new StructureException("No inputs.");
				}				
				for (Node item : ancestors) {
					if (contexts.containsKey(item)) {
						extractContext.addSource( contexts.get(item) ); 
					} else {
						// can't find context ..
						throw new StructureException("Can't find context.");
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
	 * @return false if execution failed
	 * @throws ContextException 
	 * @throws StructureException 
	 */
	private boolean runNode(Node node, List<Node> ancestors) throws ContextException, StructureException {
		// prepare what we need to start the execution
		DPUInstance dpuInstance = node.getDpuInstance();
		DPU dpu = dpuInstance.getDpu();
		DpuType dpuType = dpu.getType();
		String dpuJarPath = dpu.getJarPath();
		Configuration configuration = dpuInstance.getInstanceConfig();
		// get context ..
		ProcessingContext ctx = getContextForNode(dpuInstance, dpuType, ancestors);
		// now based on DPU type ..
		switch (dpuType) {
			case EXTRACTOR: {
				Extract extractor = moduleFacade.getInstanceExtract(dpuJarPath);
				extractor.saveConfiguration(configuration);
				return runExtractor(extractor, (ExtractContext) ctx);
			}
			case TRANSFORMER: {
				Transform transformer = moduleFacade
						.getInstanceTransform(dpuJarPath);
				transformer.saveConfiguration(configuration);
				return runTransformer(transformer, (TransformContext) ctx);
			}
			case LOADER: {
				Load loader = moduleFacade.getInstanceLoader(dpuJarPath);
				loader.saveConfiguration(configuration);
				return runLoader(loader, (LoadContext) ctx);
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
	 * @return false if execution failed
	 */
	private boolean runExtractor(Extract extractor,ExtractContext ctx) {

		try {
			extractor.extract(ctx);
			eventPublisher.publishEvent(new ExtractCompletedEvent(extractor,
					ctx, this));
		} catch (ExtractException ex) {
			eventPublisher.publishEvent(new ExtractFailedEvent(ex, extractor,
					ctx, this));
			return false;
		}
		return true;
	}

	/**
	 * Runs a single Transformer DPU module.
	 * 
	 * @param transformer
	 * @param ctx
	 * @return false if execution failed
	 */
	private boolean runTransformer(Transform transformer, TransformContext ctx) {

		try {
			transformer.transform(ctx);
			eventPublisher.publishEvent(new TransformCompletedEvent(
					transformer, ctx, this));
		} catch (TransformException ex) {
			eventPublisher.publishEvent(new TransformFailedEvent(ex,
					transformer, ctx, this));
			return false;
		}
		return true;
	}

	/**
	 * Runs a single Loader DPU module.
	 * 
	 * @param loader
	 * @param ctx
	 * @return false if execution failed
	 */
	private boolean runLoader(Load loader, LoadContext ctx) {

		try {
			loader.load(ctx);
			eventPublisher.publishEvent(new LoadCompletedEvent(loader, ctx,
					this));
		} catch (LoadException ex) {
			eventPublisher.publishEvent(new LoadFailedEvent(ex, loader, ctx,
					this));
			return false;
		}
		return true;
	}
}
