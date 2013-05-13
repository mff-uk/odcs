package cz.cuni.xrg.intlib.backend.execution;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cz.cuni.xrg.intlib.backend.DatabaseAccess;
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
import cz.cuni.xrg.intlib.commons.app.pipeline.ExecutionStatus;
import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.DependencyGraph;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Node;
import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import cz.cuni.xrg.intlib.commons.extractor.Extract;
import cz.cuni.xrg.intlib.commons.extractor.ExtractContext;
import cz.cuni.xrg.intlib.backend.context.ContextException;
import cz.cuni.xrg.intlib.backend.context.DataUnitMerger;
import cz.cuni.xrg.intlib.backend.context.ExtendedContext;
import cz.cuni.xrg.intlib.backend.context.ExtendedExtractContext;
import cz.cuni.xrg.intlib.backend.context.ExtendedLoadContext;
import cz.cuni.xrg.intlib.backend.context.ExtendedTransformContext;
import cz.cuni.xrg.intlib.backend.context.impl.ExtendedExtractContextImpl;
import cz.cuni.xrg.intlib.backend.context.impl.ExtendedLoadContextImpl;
import cz.cuni.xrg.intlib.backend.context.impl.ExtendedTransformContextImpl;
import cz.cuni.xrg.intlib.backend.context.impl.PrimitiveDataUniteMerger;
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

import org.apache.commons.io.FileUtils;
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
class PipelineWorker implements Runnable {

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
	 * Used data unit merger.
	 */
	private DataUnitMerger dataUnitMerger; 
	
	/**
	 * Access to the database
	 */
	protected DatabaseAccess database;	
	
	/**
	 * 
	 * @param execution The pipeline execution record to run.
	 * @param moduleFacade Module facade for obtaining DPUs instances.
	 * @param eventPublisher Application event publisher.
	 * @param workDirectory Working directory for this execution.
	 * @param database Access to database.
	 */
	public PipelineWorker(PipelineExecution execution, ModuleFacade moduleFacade, 
			ApplicationEventPublisher eventPublisher, File workDirectory,
			DatabaseAccess database) {
		this.execution = execution;
		this.moduleFacade = moduleFacade;
		this.eventPublisher = eventPublisher;
		this.contexts = new HashMap<>();
		this.workDirectory = workDirectory;
		this.logger = LoggerFactory.getLogger(PipelineWorker.class);
		this.dataUnitMerger = new PrimitiveDataUniteMerger();
		this.database = database;
	}

	/**
	 * Called in case that the execution failed.
	 * @param message Cause of failure. 
	 */
	private void executionFailed(String message) {
		// send event
		eventPublisher.publishEvent(new PipelineFailedEvent(message, execution, this));
		// set new state
		execution.setExecutionStatus(ExecutionStatus.FAILED);
		// save	into database
		database.getPipeline().save(execution);
		// and do clean up
		cleanUp();
	}
	
	/**
	 * Called in case of successful execution.
	 */
	private void executionSuccessful() {
		// update state
		execution.setExecutionStatus(ExecutionStatus.FINISHED_SUCCESS);
		// save into database
		database.getPipeline().save(execution);
		// and do clean up
		cleanUp();		
	}
	
	/**
	 * Do cleanup work after pipeline execution.
	 * Also delete the worker directory it the pipeline
	 * is not in debugMode.
	 */
	private void cleanUp() {
		// release all contexts 
		for (ProcessingContext item : contexts.values()) {
			if (item instanceof ExtendedContext) {
				ExtendedContext exCtx = (ExtendedContext)item;
				exCtx.release();
			} else {
				logger.error("Unexpected ProcessingContext instance. Can't call release().");
			}	
		}
		// delete working folder
		if (execution.isDebugging()) {
			// keep the working directory
		} else {
			// try to delete the working directory
			try {
				FileUtils.deleteDirectory(workDirectory);
			} catch (IOException e) {
				logger.error("Can't delete directory after execution: " + execution.getId() + " exception: " + e.getMessage());
			}
		}
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
			e.fillInStackTrace();
				executionFailed(e.getMessage());
				return;
			} catch (ModuleException e) {
				eventPublisher.publishEvent(new PipelineModuleErrorEvent(e, execution, this));
				logger.error("Module exception: " + e.getMessage());
			e.fillInStackTrace();
				executionFailed(e.getMessage());
				return;
			} catch (StructureException e) {
				eventPublisher.publishEvent(new PipelineStructureError(e, execution, this));
				logger.error("Structure exception: " + e.getMessage());
				executionFailed(e.getMessage());
				return;				
			} catch (Exception e) {
				logger.error("Exception: " + e.getMessage());
			e.fillInStackTrace();
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
	 * Return context that should be used when executing given Extractor.
	 * The context is also stored in {@link #contexts }
	 * 
	 * @param node Node for which DPU the context is.
	 * @param ancestors Ancestors of the given node.
	 * @return Context for the DPU execution.
	 * @throws ContextException 
	 * @throws StructureException 
	 */		
	private ExtendedExtractContext getContextForNodeExtractor(Node node, 
			Set<Node> ancestors) throws ContextException, StructureException {
		DPUInstance dpuInstance = node.getDpuInstance();
		String contextId = "ex" + execution.getId() + "_dpu-" + dpuInstance.getId();
		File contextDirectory = new File(workDirectory, "dpu-" + dpuInstance.getId() );
		// ...
		ExtendedExtractContext extractContext;
		extractContext = new ExtendedExtractContextImpl
				(contextId, execution, dpuInstance, eventPublisher, contextDirectory);
		
		// store context
		contexts.put(node, extractContext);		
		return extractContext;		
	}
	
	/**
	 * Return context that should be used when executing given Transform.
	 * The context is also stored in {@link #contexts }
	 * 
	 * @param node Node for which DPU the context is.
	 * @param ancestors Ancestors of the given node.
	 * @return Context for the DPU execution.
	 * @throws ContextException 
	 * @throws StructureException 
	 */		
	private ExtendedTransformContext getContextForNodeTransform(Node node,
			Set<Node> ancestors) throws ContextException, StructureException {
		DPUInstance dpuInstance = node.getDpuInstance();
		String contextId = "ex" + execution.getId() + "_dpu-" + dpuInstance.getId();
		File contextDirectory = new File(workDirectory, "dpu-" + dpuInstance.getId() );
		// ...
		ExtendedTransformContext transformContext;
		transformContext = new ExtendedTransformContextImpl
				(contextId, execution, dpuInstance, eventPublisher, contextDirectory);
		if (ancestors.isEmpty()) {
			// no ancestors ? -> error
			throw new StructureException("No inputs.");
		}					
		for (Node item : ancestors) {
			if (contexts.containsKey(item)) {
				transformContext.addSource(contexts.get(item), dataUnitMerger); 
			} else {
				// can't find context ..
				throw new StructureException("Can't find context.");
			}
		}
		transformContext.sealInputs();
		return transformContext;		
	}
	
	/**
	 * Return context that should be used when executing given Loader.
	 * The context is also stored in {@link #contexts }
	 * 
	 * @param node Node for which DPU the context is.
	 * @param ancestors Ancestors of the given node.
	 * @return Context for the DPU execution.
	 * @throws ContextException 
	 * @throws StructureException 
	 */	
	private ExtendedLoadContext getContextForNodeLoader(Node node, 
			Set<Node> ancestors) throws ContextException, StructureException {
		DPUInstance dpuInstance = node.getDpuInstance();
		String contextId = "ex" + execution.getId() + "_dpu-" + dpuInstance.getId();
		File contextDirectory = new File(workDirectory, "dpu-" + dpuInstance.getId() );
		// ...
		ExtendedLoadContext loadContext;
		loadContext = new ExtendedLoadContextImpl
				(contextId, execution, dpuInstance, eventPublisher, contextDirectory);
		if (ancestors.isEmpty()) {
			// no ancestors ? -> error
			throw new StructureException("No inputs.");
		}				
		for (Node item : ancestors) {
			if (contexts.containsKey(item)) {
				loadContext.addSource(contexts.get(item), dataUnitMerger); 
			} else {
				// can't find context ..
				throw new StructureException("Can't find context.");
			}
		}	
		loadContext.sealInputs();
		return loadContext;
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
	private boolean runNode(Node node, Set<Node> ancestors) throws ContextException, StructureException {
		// prepare what we need to start the execution
		DPUInstance dpuInstance = node.getDpuInstance();
		DPU dpu = dpuInstance.getDpu();
		DpuType dpuType = dpu.getType();
		String dpuJarPath = dpu.getJarPath();
		Configuration configuration = dpuInstance.getInstanceConfig();
		// now based on DPU type ..
		switch (dpuType) {
			case EXTRACTOR: {
				Extract extractor = moduleFacade.getInstanceExtract(dpuJarPath);
				extractor.saveConfiguration(configuration);
				return runExtractor(extractor, getContextForNodeExtractor(node, ancestors) );
			}
			case TRANSFORMER: {
				Transform transformer = moduleFacade
						.getInstanceTransform(dpuJarPath);
				transformer.saveConfiguration(configuration);
				return runTransformer(transformer, getContextForNodeTransform(node, ancestors) );
			}
			case LOADER: {
				Load loader = moduleFacade.getInstanceLoader(dpuJarPath);
				loader.saveConfiguration(configuration);
				return runLoader(loader, getContextForNodeLoader(node, ancestors) );
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
	private boolean runExtractor(Extract extractor, ExtendedExtractContext ctx) {

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
	private boolean runTransformer(Transform transformer, ExtendedTransformContext ctx) {

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
	private boolean runLoader(Load loader, ExtendedLoadContext ctx) {

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
