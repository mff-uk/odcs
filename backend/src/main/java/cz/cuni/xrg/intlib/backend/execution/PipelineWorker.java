package cz.cuni.xrg.intlib.backend.execution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cz.cuni.xrg.intlib.backend.DatabaseAccess;
import cz.cuni.xrg.intlib.backend.pipeline.event.PipelineContextErrorEvent;
import cz.cuni.xrg.intlib.backend.pipeline.event.PipelineFailedEvent;
import cz.cuni.xrg.intlib.backend.pipeline.event.PipelineFinished;
import cz.cuni.xrg.intlib.backend.pipeline.event.PipelineModuleErrorEvent;
import cz.cuni.xrg.intlib.backend.pipeline.event.PipelineStructureError;
import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import cz.cuni.xrg.intlib.commons.app.conf.ConfigProperty;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.module.ModuleException;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineFacade;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.DependencyGraph;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Edge;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Node;
import cz.cuni.xrg.intlib.commons.configuration.DPUConfigObject;
import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.configuration.Configurable;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitException;
import cz.cuni.xrg.intlib.commons.extractor.Extract;
import cz.cuni.xrg.intlib.backend.context.Context;
import cz.cuni.xrg.intlib.backend.context.ContextException;
import cz.cuni.xrg.intlib.backend.data.DataUnitFactory;
import cz.cuni.xrg.intlib.backend.dpu.event.DPUCompleteEvent;
import cz.cuni.xrg.intlib.backend.dpu.event.DPUFailedEvent;
import cz.cuni.xrg.intlib.backend.dpu.event.DPUStartEvent;
import cz.cuni.xrg.intlib.backend.dpu.event.DPUNoOutputEvent;
import cz.cuni.xrg.intlib.commons.extractor.ExtractException;
import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.commons.loader.LoadException;
import cz.cuni.xrg.intlib.commons.transformer.Transform;
import cz.cuni.xrg.intlib.commons.transformer.TransformException;
import cz.cuni.xrg.intlib.commons.app.execution.DPUExecutionState;
import cz.cuni.xrg.intlib.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.xrg.intlib.commons.app.execution.context.ProcessingUnitInfo;
import cz.cuni.xrg.intlib.commons.app.execution.log.LogMessage;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

/**
 * Worker responsible for running single PipelineExecution.
 * 
 * @author Petyr
 * 
 */
class PipelineWorker implements Runnable {

	/**
	 * Publisher instance for publishing pipeline execution events.
	 */
	@Autowired
	private ApplicationEventPublisher eventPublisher;

	/**
	 * Provide access to DPURecord implementation.
	 */
	@Autowired
	private ModuleFacade moduleFacade;

	/**
	 * Access to the database
	 */
	@Autowired
	protected DatabaseAccess database;

	/**
	 * Application's configuration.
	 */
	@Autowired
	private AppConfig appConfig;

	/**
	 * DataUnit factory.
	 */
	@Autowired
	private DataUnitFactory dataUnitFactory;

	/**
	 * Bean factory used to create beans for single pipeline execution.
	 */
	@Autowired
	private BeanFactory beanFactory;

	/**
	 * Manage mapping execution context into {@link #workDirectory}.
	 */
	private ExecutionContextInfo contextInfo;

	/**
	 * End time of last successful pipeline execution.
	 */
	private Date lastSuccessfulExTime;

	/**
	 * Store context related to Nodes (DPUs).
	 */
	private Map<Node, Context> contexts;

	/**
	 * PipelineExecution record, determine pipeline to run.
	 */
	private PipelineExecution execution;

	/**
	 * Logger class.
	 */
	private static final Logger LOG = LoggerFactory
			.getLogger(PipelineWorker.class);

	public PipelineWorker() {
		this.contexts = new HashMap<>();
	}

	/**
	 * Set pipeline execution for execution.
	 */
	public void init(PipelineExecution execution) {
		this.execution = execution;
		this.contextInfo = execution.getContext();
		// if in SCHEDULED changed to RUNNING
		if (this.execution.getExecutionStatus() == PipelineExecutionStatus.SCHEDULED) {
			this.execution.setExecutionStatus(PipelineExecutionStatus.RUNNING);
			database.getPipeline().save(this.execution);
		}
	}

	/**
	 * Load execution times {@link #lastExTime} and
	 * {@link #lastSuccessfulExTime}
	 */
	private void loadExTimes() {
		Date lastSucess = database.getPipeline().getLastExecTime(
				execution.getPipeline(),
				PipelineExecutionStatus.FINISHED_SUCCESS);

		Date lastSucessWarn = database.getPipeline().getLastExecTime(
				execution.getPipeline(),
				PipelineExecutionStatus.FINISHED_WARNING);
		// null check ..
		if (lastSucess == null) {
			lastSuccessfulExTime = lastSucessWarn;
		} else if (lastSucessWarn == null) {
			lastSuccessfulExTime = lastSucess;
		} else {
			// get last successful execution time
			lastSuccessfulExTime = lastSucess.after(lastSucessWarn)
					? lastSucess
					: lastSucessWarn;
		}
	}

	/**
	 * Called in case that the execution failed.
	 */
	private void executionFailed() {
		execution.setEnd(new Date());
		// set new state
		execution.setExecutionStatus(PipelineExecutionStatus.FAILED);
		//
		LOG.error("execution failed");
	}

	/**
	 * Called in case of successful execution.
	 */
	private void executionSuccessful() {
		execution.setEnd(new Date());
		// update state -> check logs
		Set<Level> levels = new HashSet<>(2);
		levels.add(Level.WARN);
		levels.add(Level.ERROR);
		levels.add(Level.FATAL);
		if (database.getLog().existLogs(execution, levels)) {
			execution
					.setExecutionStatus(PipelineExecutionStatus.FINISHED_WARNING);
		} else {
			execution
					.setExecutionStatus(PipelineExecutionStatus.FINISHED_SUCCESS);
		}
	}

	/**
	 * Try to delete directory in execution directory. If error occur then is
	 * logged.
	 * 
	 * @param directory Relative path from execution directory.
	 */
	private void deleteDirectory(String directoryPath) {
		File directory = new File(
				appConfig.getString(ConfigProperty.GENERAL_WORKINGDIR),
				directoryPath);
		try {
			FileUtils.deleteDirectory(directory);
		} catch (IOException e) {
			LOG.error(
					"Can't delete directory after execution: "
							+ execution.getId(), e);
		}
	}

	/**
	 * Do cleanup work after pipeline execution. Also delete the worker
	 * directory it the pipeline if not in debugMode.
	 */
	private void cleanUp() {
		LOG.debug("Clean up");
		// release all contexts
		for (Context item : contexts.values()) {
			if (execution.isDebugging()) {
				// just release leave
				item.release();
			} else {
				// delete data ..
				item.delete();
			}
		}

		if (execution.isDebugging()) {
			// do not delete anything
		} else {
			// delete working directory
			// the sub directories should be already deleted by DPU's
			deleteDirectory(contextInfo.getWorkingPath());
			// delete storage directory
			deleteDirectory(contextInfo.getStoragePath());
			// if the execution directory is empty -> no result dir exist
			// delete it
			final File resultDir = new File(
					appConfig.getString(ConfigProperty.GENERAL_WORKINGDIR),
					contextInfo.getResultPath());
			if (resultDir.exists()) {
				// check for content
				final File[] files = resultDir.listFiles();
				if (files == null || files.length == 0) {
					// is empty -> delete execution directory
					deleteDirectory(contextInfo.getRootPath());
				} else {
					// execution and result directory are preserved
				}
			} else {
				// no result directory exist -> delete execution directory
				deleteDirectory(contextInfo.getRootPath());
			}
		}
	}

	/**
	 * Implementation of workers activity. Worker constantly keeps asking engine
	 * for jobs to run, until it is killed.
	 */
	@Override
	public void run() {
		// load execution times from DB
		loadExTimes();

		PipelineFacade pipelineFacade = database.getPipeline();

		// add marker to logs from this thread -> both must be specified !!
		MDC.put(LogMessage.MDPU_EXECUTION_KEY_NAME,
				Long.toString(execution.getId()));

		// get pipeline to run
		Pipeline pipeline = execution.getPipeline();

		// get dependency graph -> determine run order
		DependencyGraph dependencyGraph = null;

		// if in debug mode then pass the final DPU
		if (execution.isDebugging() && execution.getDebugNode() != null) {
			dependencyGraph = new DependencyGraph(pipeline.getGraph(),
					execution.getDebugNode());
		} else {
			dependencyGraph = new DependencyGraph(pipeline.getGraph());
		}
		LOG.debug("Started");

		// set start time
		execution.setStart(new Date());
		// contextInfo is in pipeline so by saving pipeline we also save context
		pipelineFacade.save(execution);

		boolean executionFailed = false;
		// run DPUs ...
		for (Node node : dependencyGraph) {
			boolean result = true; // be optimistic :)

			// save context with the DPU that will be executed
			ProcessingUnitInfo unitInfo = null;
			unitInfo = contextInfo.getDPUInfo(node.getDpuInstance());
			if (unitInfo == null) {
				// no previous execution ..
				unitInfo = contextInfo.createDPUInfo(node.getDpuInstance());
			} else {
				// there was some previous execution ...
				if (unitInfo.getState() == DPUExecutionState.FINISHED) {
					// DPU is already finished -> just load context
				} else {
					// DPUExecutionState.RUNNING -> unfinished
					// delete all data and start
				}
			}

			// set to running state and execute
			unitInfo.setState(DPUExecutionState.RUNNING);
			pipelineFacade.save(execution);

			// put dpuInstance id to MDC, so we can identify logs related to the
			// dpuInstance
			MDC.put(LogMessage.MDC_DPU_INSTANCE_KEY_NAME,
					Long.toString(node.getDpuInstance().getId()));
			try {
				result = runNode(node, dependencyGraph.getAncestors(node));
			} catch (ContextException e) {
				eventPublisher.publishEvent(new PipelineContextErrorEvent(e,
						node.getDpuInstance(), execution, this));
				executionFailed = true;
				LOG.error("PipelineWorker: Context exception", e);
				break;
			} catch (ModuleException e) {
				eventPublisher.publishEvent(new PipelineModuleErrorEvent(e,
						node.getDpuInstance(), execution, this));
				executionFailed = true;
				LOG.error("PipelineWorker: Module exception", e);
				break;
			} catch (StructureException e) {
				eventPublisher.publishEvent(new PipelineStructureError(e, node
						.getDpuInstance(), execution, this));
				executionFailed = true;
				LOG.error("PipelineWorker: Structure exception", e);
				break;
			} catch (DataUnitCreateException e) {
				// can't create DataUnit
				eventPublisher.publishEvent(new PipelineFailedEvent(e, node
						.getDpuInstance(), execution, this));
				executionFailed = true;
				LOG.error("PipelineWorker: DataUnit create exception", e);
				break;
			} catch (DataUnitException e) {
				// some problem with DataUnits
				eventPublisher.publishEvent(new PipelineFailedEvent(e, node
						.getDpuInstance(), execution, this));
				LOG.error("PipelineWorker: DataUnit Exception", e);
				executionFailed = true;
				break;
			} catch (Exception e) {
				eventPublisher.publishEvent(new PipelineFailedEvent(e, node
						.getDpuInstance(), execution, this));
				executionFailed = true;
				LOG.error("PipelineWorker: Exception", e);
				break;
			} catch (Error e) {
				// use for errors like java.lang.NoClassDefFoundError
				eventPublisher.publishEvent(new PipelineFailedEvent(e
						.getMessage(), node.getDpuInstance(), execution, this));
				executionFailed = true;
				LOG.error("PipelineWorker: Error", e);
				break;
			}
			MDC.remove(LogMessage.MDC_DPU_INSTANCE_KEY_NAME);

			// TODO Petyr: save custom data .. !
			if (result) {
				// DPURecord executed successfully

				// set state of last executed DPU (we know it exist)
				// to finished
				contextInfo.getDPUInfo(node.getDpuInstance()).setState(
						DPUExecutionState.FINISHED);

				// save context after last execution
				pipelineFacade.save(execution);

				// also save new DataUnits
				Context lastContext = contexts.get(node);
				lastContext.save();
			} else {
				// error -> end pipeline
				executionFailed = true;
				eventPublisher.publishEvent(new PipelineFailedEvent(
						"DPU execution failed.", node.getDpuInstance(),
						execution, this));
				// save data ?
				if (execution.isDebugging()) {
					// save new DataUnits
					Context lastContext = contexts.get(node);
					lastContext.save();
					// the context will be save at the end of the execution
				}
				// break execution
				break;
			}
		}
		// ending ..
		LOG.debug("Finished");
		// do clean up
		cleanUp();
		// clear all threads markers
		MDC.clear();
		// save context into DB
		if (executionFailed) {
			executionFailed();
		} else {
			executionSuccessful();
		}
		// save execution
		pipelineFacade.save(execution);

		// publish information for the rest of the application
		eventPublisher.publishEvent(new PipelineFinished(execution, this));
	}

	/**
	 * Return edge that connects the given nodes.
	 * 
	 * @param source
	 * @param target
	 * @return Edge or null if there is no connection between the given nodes.
	 */
	private Edge getEdge(Node source, Node target) {
		for (Edge edge : execution.getPipeline().getGraph().getEdges()) {
			if (edge.getFrom() == source && edge.getTo() == target) {
				return edge;
			}
		}
		return null;
	}

	/**
	 * Return command associated with edge that connect given nodes.
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	private String getCommandForEdge(Node source, Node target) {
		// try to get Edge that leads from item to node
		Edge edge = getEdge(source, target);
		String command;
		if (edge == null) {
			// there is no edge for this connection ?
			command = "";
			LOG.error("Can't find edge between {} and {}", source
					.getDpuInstance().getName(), target.getDpuInstance()
					.getName());
		} else {
			command = edge.getDataUnitName();
		}
		return command;
	}

	/**
	 * Return new {@link Context} instance for given {@link Node}. The context
	 * is also stored in {@link #contexts}.
	 * 
	 * @param node Node for which DPURecord the context is.
	 * @param ancestors Ancestors of the given node.
	 * @return Context for the DPURecord execution.
	 * @throws ContextException
	 * @throws StructureException
	 */
	private Context getContextForNode(Node node, Set<Node> ancestors)
			throws ContextException,
				StructureException {
		DPUInstanceRecord dpuInstance = node.getDpuInstance();
		// ...
		Context context = beanFactory.getBean(Context.class);
		context.bind(dpuInstance, contextInfo, lastSuccessfulExTime);
		// add DataUnits from node's ancestors
		if (ancestors != null) {
			for (Node item : ancestors) {
				// try to get Edge that leads from item to node
				String command = getCommandForEdge(item, node);
				// if there is context for given data ..
				if (contexts.containsKey(item)) {
					context.addSource(contexts.get(item), command);
				} else {
					// can't find context ..
					throw new StructureException("Can't find context.");
				}
			}
			context.sealInputs();
		}		
		// store context
		contexts.put(node, context);
		return context;
	}

	/**
	 * @throws DataUnitException
	 * @throws DataUnitCreateException Executes a single DPURecord associated
	 *             with given Node.
	 * 
	 * @param node
	 * @param ancestors Ancestors of the given node.
	 * @return false if execution failed
	 * @throws ContextException
	 * @throws StructureException
	 * @throws
	 */
	private boolean runNode(Node node, Set<Node> ancestors)
			throws ContextException,
				StructureException,
				DataUnitCreateException,
				DataUnitException {
		// prepare what we need to start the execution
		DPUInstanceRecord dpuInstanceRecord = node.getDpuInstance();

		// load instance
		try {
			dpuInstanceRecord.loadInstance(moduleFacade);
		} catch (FileNotFoundException e) {
			throw new StructureException(
					"Failed to load instance of DPU from file.", e);
		} catch (ModuleException e) {
			throw new StructureException("Failed to create instance of DPU.", e);
		}

		// get instance
		Object dpuInstance = dpuInstanceRecord.getInstance();
		// set configuration
		if (dpuInstance instanceof Configurable<?>) {
			Configurable<DPUConfigObject> configurable = (Configurable<DPUConfigObject>) dpuInstance;
			try {
				configurable.configure(dpuInstanceRecord.getRawConf());
			} catch (ConfigException e) {
				throw new StructureException("Failed to configure DPU.", e);
			}
		}
		Context context = getContextForNode(node, ancestors);
		// run dpu instance
		if (dpuInstance instanceof Extract) {
			Extract extractor = (Extract) dpuInstance;
			boolean result = runExtractor(extractor, context);
			// check for outputs
			if (context.getOutputs().isEmpty()) {
				// no outputs
				eventPublisher.publishEvent(new DPUNoOutputEvent(context, this));
			}
			return result;
		} else if (dpuInstance instanceof Transform) {
			Transform transformer = (Transform) dpuInstance;
			boolean result = runTransformer(transformer, context);
			// check for outputs
			if (context.getOutputs().isEmpty()) {
				// no outputs
				eventPublisher.publishEvent(new DPUNoOutputEvent(context, this));
			}
			return result;
		} else if (dpuInstance instanceof Load) {
			Load loader = (Load) dpuInstance;
			return runLoader(loader, context);
		} else {
			throw new RuntimeException("Unknown DPURecord type.");
		}
	}

	/**
	 * Runs a single extractor DPURecord module.
	 * 
	 * @param extractor
	 * @param ctx
	 * @return false if execution failed
	 * @throws DataUnitCreateException
	 * @throws DataUnitException
	 */
	private boolean runExtractor(Extract extractor, Context ctx)
			throws DataUnitCreateException,
				DataUnitException {
		eventPublisher.publishEvent(new DPUStartEvent(ctx, this));
		try {
			extractor.extract(ctx);
			eventPublisher.publishEvent(new DPUCompleteEvent(ctx, this));
		} catch (ExtractException ex) {
			eventPublisher.publishEvent(new DPUFailedEvent(ex, ctx, this));
			return false;
		}
		return true;
	}

	/**
	 * Runs a single Transformer DPURecord module.
	 * 
	 * @param transformer
	 * @param ctx
	 * @return false if execution failed
	 * @throws DataUnitCreateException
	 * @throws DataUnitException
	 */
	private boolean runTransformer(Transform transformer, Context ctx)
			throws DataUnitCreateException,
				DataUnitException {
		eventPublisher.publishEvent(new DPUStartEvent(ctx, this));
		try {
			transformer.transform(ctx);
			eventPublisher.publishEvent(new DPUCompleteEvent(ctx, this));
		} catch (TransformException ex) {
			eventPublisher.publishEvent(new DPUFailedEvent(ex, ctx, this));
			return false;
		}
		return true;
	}

	/**
	 * Runs a single Loader DPURecord module.
	 * 
	 * @param loader
	 * @param ctx
	 * @return false if execution failed
	 * @throws DataUnitCreateException
	 * @throws DataUnitException
	 */
	private boolean runLoader(Load loader, Context ctx)
			throws DataUnitCreateException,
				DataUnitException {
		eventPublisher.publishEvent(new DPUStartEvent(ctx, this));
		try {
			loader.load(ctx);
			eventPublisher.publishEvent(new DPUCompleteEvent(ctx, this));
		} catch (LoadException ex) {
			eventPublisher.publishEvent(new DPUFailedEvent(ex, ctx, this));
			return false;
		}
		return true;
	}
	
}
