package cz.cuni.xrg.intlib.backend.execution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cz.cuni.xrg.intlib.backend.DatabaseAccess;
import cz.cuni.xrg.intlib.backend.pipeline.event.PipelineContextErrorEvent;
import cz.cuni.xrg.intlib.backend.pipeline.event.PipelineFailedEvent;
import cz.cuni.xrg.intlib.backend.pipeline.event.PipelineModuleErrorEvent;
import cz.cuni.xrg.intlib.backend.pipeline.event.PipelineStructureError;
import cz.cuni.xrg.intlib.commons.DpuType;
import cz.cuni.xrg.intlib.commons.ProcessingContext;
import cz.cuni.xrg.intlib.commons.app.dpu.DPU;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionContextFactory;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionContextWriter;
import cz.cuni.xrg.intlib.commons.app.module.ModuleException;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import cz.cuni.xrg.intlib.commons.app.pipeline.ExecutionStatus;
import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.DependencyGraph;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Node;
import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import cz.cuni.xrg.intlib.commons.extractor.Extract;
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
import cz.cuni.xrg.intlib.backend.extractor.events.ExtractStartEvent;
import cz.cuni.xrg.intlib.commons.extractor.ExtractException;
import cz.cuni.xrg.intlib.backend.extractor.events.ExtractFailedEvent;
import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.backend.loader.events.LoadCompletedEvent;
import cz.cuni.xrg.intlib.backend.loader.events.LoadStartEvent;
import cz.cuni.xrg.intlib.commons.loader.LoadException;
import cz.cuni.xrg.intlib.backend.loader.events.LoadFailedEvent;
import cz.cuni.xrg.intlib.commons.transformer.Transform;
import cz.cuni.xrg.intlib.backend.transformer.events.TransformCompletedEvent;
import cz.cuni.xrg.intlib.backend.transformer.events.TransformStartEvent;
import cz.cuni.xrg.intlib.commons.transformer.TransformException;
import cz.cuni.xrg.intlib.backend.transformer.events.TransformFailedEvent;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Worker responsible for running single PipelineExecution.
 *
 * @author Petyr
 * 
 */
class PipelineWorker implements Runnable {
	// TODO Petyr: release save context before then on the end of the execution
	
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
	 * Manage mapping execution context into {@link #workDirectory}. 
	 */
	private ExecutionContextWriter contextWriter;	
	
	/**
	 * @param execution The pipeline execution record to run.
	 * @param moduleFacade Module facade for obtaining DPUs instances.
	 * @param eventPublisher Application event publisher.
	 * @param database Access to database.
	 */
	public PipelineWorker(PipelineExecution execution, ModuleFacade moduleFacade, 
			ApplicationEventPublisher eventPublisher, DatabaseAccess database) {
		this.execution = execution;
		this.moduleFacade = moduleFacade;
		this.eventPublisher = eventPublisher;
		this.contexts = new HashMap<>();
		// get working directory from pipelineExecution
		this.workDirectory = new File( execution.getWorkingDirectory() );
		this.logger = LoggerFactory.getLogger(PipelineWorker.class);
		this.dataUnitMerger = new PrimitiveDataUniteMerger();
		this.database = database;
		// try to load the context ..
		try {
			this.contextWriter = ExecutionContextFactory.restoreAsWrite(workDirectory);
			if (this.contextWriter == null) {
				// can't load use empty
				this.contextWriter = ExecutionContextFactory.createNew(workDirectory);
			} else {
				// used restored
			}
		} catch (FileNotFoundException e) {
			// exception -> use new one .. 
			this.contextWriter = ExecutionContextFactory.createNew(workDirectory);
		}
		// TODO Petyr: persist Iterator from DependecyGraph into ExecutionContext, and save into DB after every DPU (also save .. DataUnits .. )
	}

	/**
	 * Called in case that the execution failed.
	 */
	private void executionFailed() {
		// set new state
		execution.setExecutionStatus(ExecutionStatus.FAILED);
		// save	into database
		database.getPipeline().save(execution);
		// 
		logger.error("execution failed");
	}
	
	/**
	 * Called in case of successful execution.
	 */
	private void executionSuccessful() {
		// update state
		execution.setExecutionStatus(ExecutionStatus.FINISHED_SUCCESS);
		// save into database
		database.getPipeline().save(execution);
	}
	
	/**
	 * Do cleanup work after pipeline execution.
	 * Also delete the worker directory it the pipeline
	 * is not in debugMode.
	 */
	private void cleanUp() {
		logger.debug("Clean up");
		// save context if in debug mode
		if (execution.isDebugging()) {
			logger.debug("Saving pipeline execution context");
			// save DPU's contexts
			for (ProcessingContext item : contexts.values()) {
				if (item instanceof ExtendedContext) {
					ExtendedContext exCtx = (ExtendedContext)item;
					exCtx.save();
				} else {
					logger.error("Unexpected ProcessingContext instance. Can't call release().");
				}	
			}
		}
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
			// do not delete anything
		} else {
			// try to delete the working directory
			try {
				FileUtils.deleteDirectory(workDirectory);
			} catch (IOException e) {
				logger.error("Can't delete directory after execution: " + execution.getId(), e);
			}
		}
	}
	
	/**
	 * Implementation of workers activity. Worker constantly keeps asking engine
	 * for jobs to run, until it is killed.
	 */
	@Override
	public void run() {		
		final String pipielineExecutionId = Integer.toString( execution.getId() );

		// add marker to logs from this thread -> both must be specified !!
		MDC.put("execution", pipielineExecutionId );
		// set "file" to full path to the log file
		MDC.put("file", contextWriter.getLogFile().toString() );
		
		// get pipeline to run
		Pipeline pipeline = execution.getPipeline();

		// get dependency graph -> determine run order
		DependencyGraph dependencyGraph = new DependencyGraph(pipeline.getGraph());
		
		
		// save contextWriter before first DPU
		try {
			contextWriter.save();
		} catch (Exception e) {
			logger.error("Can't save context: " + execution.getId(), e);
		}	
		
		logger.debug("Started");
		
		boolean executionFailed = false;
		// run DPUs ...
		for (Node node : dependencyGraph) {
			boolean result;
			try {
				result = runNode(node, dependencyGraph.getAncestors(node));
			} catch (ContextException e) {
				eventPublisher.publishEvent(new PipelineContextErrorEvent(e, node.getDpuInstance(), execution, this));				
				executionFailed = true;
				break;
			} catch (ModuleException e) {
				eventPublisher.publishEvent(new PipelineModuleErrorEvent(e, node.getDpuInstance(), execution, this));
				executionFailed = true;
				break;
			} catch (StructureException e) {
				eventPublisher.publishEvent(new PipelineStructureError(e, node.getDpuInstance(), execution, this));
				executionFailed = true;
				break;			
			} catch (Exception e) {
				eventPublisher.publishEvent(new PipelineFailedEvent(e, node.getDpuInstance(),  execution, this));				
				executionFailed = true;
				break;
			}					
						
			if (result) {
				// DPU executed successfully
			} else {
				// error -> end pipeline
				executionFailed = true;
				eventPublisher.publishEvent(new PipelineFailedEvent("Error in DPU.", node.getDpuInstance(), execution, this));
				break;
			}
			
			// save contextWriter after every DPU to enable recovery
			try {
				contextWriter.save();
			} catch (Exception e) {
				logger.error("Can't save context: " + execution.getId(), e);
			}				
		}
		// ending ..
		
		logger.debug("Finished");
		// clear threads markers		
		MDC.clear();
	
		if (execution.isDebugging()) {
			// save contextWriter for the last time ..
			try {
				contextWriter.save();
			} catch (Exception e) {
				logger.error("Can't save context: " + execution.getId(), e);
			}	
		}
		
		// and do clean up
		cleanUp();
                
        if (executionFailed) {
			executionFailed();
		} else {
			executionSuccessful();
		}
		
		
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
		// ...
		ExtendedExtractContext extractContext;
		extractContext = new ExtendedExtractContextImpl
				(contextId, execution, dpuInstance, eventPublisher, contextWriter);
		
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
		// ...
		ExtendedTransformContext transformContext;
		transformContext = new ExtendedTransformContextImpl
				(contextId, execution, dpuInstance, eventPublisher, contextWriter);
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
		// store context
		contexts.put(node, transformContext);		
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
		// ...
		ExtendedLoadContext loadContext;
		loadContext = new ExtendedLoadContextImpl
				(contextId, execution, dpuInstance, eventPublisher, contextWriter);
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
		// store context
		contexts.put(node, loadContext);		
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

		eventPublisher.publishEvent(new ExtractStartEvent(extractor, ctx, this));
		
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

		eventPublisher.publishEvent(new TransformStartEvent(transformer, ctx, this));
		
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

		eventPublisher.publishEvent(new LoadStartEvent(loader, ctx, this));
		
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
