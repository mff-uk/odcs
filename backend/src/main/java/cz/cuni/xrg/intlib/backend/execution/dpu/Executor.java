package cz.cuni.xrg.intlib.backend.execution.dpu;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import cz.cuni.xrg.intlib.backend.context.Context;
import cz.cuni.xrg.intlib.backend.context.ContextException;
import cz.cuni.xrg.intlib.backend.dpu.event.DPUFailedEvent;
import cz.cuni.xrg.intlib.backend.dpu.event.DPUWrongState;
import cz.cuni.xrg.intlib.backend.execution.StructureException;
import cz.cuni.xrg.intlib.backend.pipeline.event.PipelineContextErrorEvent;
import cz.cuni.xrg.intlib.backend.pipeline.event.PipelineFailedEvent;
import cz.cuni.xrg.intlib.backend.pipeline.event.PipelineModuleErrorEvent;
import cz.cuni.xrg.intlib.backend.pipeline.event.PipelineStructureError;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.DPUExecutionState;
import cz.cuni.xrg.intlib.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.xrg.intlib.commons.app.execution.context.ProcessingUnitInfo;
import cz.cuni.xrg.intlib.commons.app.module.ModuleException;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineFacade;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.DependencyGraph;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Edge;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Node;
import cz.cuni.xrg.intlib.commons.data.DataUnitException;
import cz.cuni.xrg.intlib.commons.dpu.DPUException;
import cz.cuni.xrg.intlib.commons.extractor.Extract;
import cz.cuni.xrg.intlib.commons.extractor.ExtractException;
import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.commons.loader.LoadException;
import cz.cuni.xrg.intlib.commons.transformer.Transform;
import cz.cuni.xrg.intlib.commons.transformer.TransformException;

/**
 * Execute a single {@link DPUInstanceRecord} from {@link PipelineExecution}.
 * Take care about calling appropriate {@link PreExecutor}s and
 * {@link PostExecutor}.
 * 
 * The {@link Executor} must be bind to the given {@link PipelineExecution} and
 * {@link DPUInstanceRecord} by calling
 * {@link #bind(Node, Map, PipelineExecution, Date)} method before use.
 * 
 * @author Petyr
 * 
 */
public final class Executor implements Runnable {

	/**
	 * Pipeline facade.
	 */
	@Autowired
	private PipelineFacade pipelineFacade;

	/**
	 * Module facade.
	 */
	@Autowired
	private ModuleFacade moduleFacade;

	/**
	 * Bean factory used for context creation.
	 */
	@Autowired
	private BeanFactory beanFactory;

	/**
	 * List of all {@link PreExecutor}s to execute before running DPU. Can be
	 * null.
	 */
	@Autowired(required = false)
	private List<PreExecutor> preExecutors;

	/**
	 * List of all {@link PostExecutor}s to execute after DPU execution has
	 * finished. Can be null.
	 */
	@Autowired(required = false)
	private List<PostExecutor> postExecutors;

	/**
	 * Publisher instance for publishing pipeline execution events.
	 */
	@Autowired
	private ApplicationEventPublisher eventPublisher;

	/**
	 * Node to execute.
	 */
	private Node node;

	/**
	 * Current dependency graph that is used during this execution.
	 */
	private DependencyGraph graph;

	/**
	 * Contexts.
	 */
	private Map<Node, Context> contexts;

	/**
	 * Executor result. False in case of failure.
	 */
	private boolean executionSuccessful;

	/**
	 * Our pipeline execution.
	 */
	private PipelineExecution execution;

	/**
	 * Time of last successful execution. Can be null.
	 */
	private Date lastExecutionTime;

	/**
	 * Bind executor to the given {@link PipelineExecution} and
	 * {@link DPUInstanceRecord} by calling
	 * 
	 * @param node Node to execute.
	 * @param contexts Contexts of other DPU's.
	 * @param execution Pipeline execution.
	 * @param lastExecutionTime Time of last successful execution. Can be null.
	 */
	public void bind(Node node,
			DependencyGraph graph,
			Map<Node, Context> contexts,
			PipelineExecution execution,
			Date lastExecutionTime) {
		this.node = node;
		this.graph = graph;
		this.contexts = contexts;
		this.execution = execution;
		this.lastExecutionTime = lastExecutionTime;
	}

	/**
	 * Load and return the instance for DPU given by {@link #node}.
	 * 
	 * @param dpu DPU's instance record for which load instance.
	 * @return DPU's instance.
	 * @throws ModuleException
	 */
	private Object loadInstance() throws ModuleException {
		DPUInstanceRecord dpu = node.getDpuInstance();
		// load instance
		try {
			dpu.loadInstance(moduleFacade);
		} catch (FileNotFoundException e) {
			throw new ModuleException(
					"Failed to load instance of DPU from file.", e);
		}
		return dpu.getInstance();
	}

	/**
	 * Add data from {@link Context} of given Node's DPU to the given
	 * {@link Context}.
	 * 
	 * @param source Node that represent source DPU.
	 * @param contexts Target DPU context.
	 * @throws StructureException It the edge for given connection can be found.
	 * @throws ContextException
	 */
	private void addContextData(Node source, Context context)
			throws StructureException,
				ContextException {
		Set<Edge> edges = execution.getPipeline().getGraph().getEdges();
		for (Edge item : edges) {
			if (item.getFrom() == source && item.getTo() == node) {
				Context sourceContext = contexts.get(source);
				if (sourceContext == null) {
					throw new StructureException("Missing context for '"
							+ source.getDpuInstance().getName()
							+ "' required by '" 
							+ node.getDpuInstance().getName() + "'");
				}
				// add data
				context.addContext(sourceContext, item.getDataUnitName());
				return;
			}
		}
		throw new StructureException("Missing edge from "
				+ source.getDpuInstance().getName() + " to "
				+ node.getDpuInstance().getName());
	}

	/**
	 * Create context for given {@link Node}. Also add data from all the
	 * ancestor's {@link Context}s. In case of error publish error event message.
	 * 
	 * @param dpu DPU for which we create context.
	 * @return Null in case of error.
	 */
	private Context prepareContext(DPUInstanceRecord dpu) {
		ExecutionContextInfo pipelineContext = execution.getContext();

		Context context = beanFactory.getBean(Context.class);
		context.bind(node.getDpuInstance(), pipelineContext, lastExecutionTime);
		// add data from ancestors
		Set<Node> ancestors = graph.getAncestors(node);
		if (ancestors != null) {
			for (Node item : ancestors) {
				try {
					addContextData(item, context);
				} catch (StructureException e) {
					eventPublisher.publishEvent(new PipelineStructureError(e, dpu,
							execution, this));
					return null;
				} catch (ContextException e) {
					eventPublisher.publishEvent(new PipelineContextErrorEvent(e, dpu,
							execution, this));
					return null;
				}
			}
			// seal inputs
			context.sealInputs();
		}
		return context;
	}

	/**
	 * Execute {@link PreExecutor} from {@link Executor#preExecutors}. If any
	 * {@link PreExecutor} return false then return false. If there are no
	 * {@link PreExecutor} ({@link Executor#preExecutors} == null) then
	 * instantly return true.
	 * 
	 * @param dpuInstance Instance of DPU to execute.
	 * @param context
	 * @return
	 */
	private boolean executePreExecutors(Object dpuInstance, Context context) {
		if (preExecutors == null) {
			return true;
		}

		DPUInstanceRecord dpu = node.getDpuInstance();
		for (PreExecutor item : preExecutors) {
			if (item.preAction(dpu, dpuInstance, execution, context)) {
				// continue execution
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * Execute a single DPU instance. If the DPU execution failed ie. throw
	 * exception then publish event and return false. In case of exception
	 * publish appropriate error event and return false.
	 * 
	 * @param dpuInstance DPU instance.
	 * @param context Context that should be used for DPU's execution.
	 * @return True if the pipeline execution should continue.
	 */
	private boolean executeInstance(Object dpuInstance, Context context) {
		// execute
		try {
			if (dpuInstance instanceof Extract) {
				((Extract) dpuInstance).extract(context);
			} else if (dpuInstance instanceof Transform) {
				((Transform) dpuInstance).transform(context);
			} else if (dpuInstance instanceof Load) {
				((Load) dpuInstance).load(context);
			}
		} catch (ExtractException | TransformException | LoadException ex) {
			eventPublisher.publishEvent(new DPUFailedEvent(ex, context, this));
			return false;
		} catch (DataUnitException ex) {
			eventPublisher.publishEvent(new DPUFailedEvent(ex, context, this));
			return false;
		} catch (DPUException e) {
			eventPublisher.publishEvent(new PipelineFailedEvent(e, node
					.getDpuInstance(), execution, this));
			return false;
		} catch (Exception e) {
			eventPublisher.publishEvent(new PipelineFailedEvent(e, node
					.getDpuInstance(), execution, this));
			return false;
		} catch (Error e) {
			// use for errors like java.lang.NoClassDefFoundError
			eventPublisher.publishEvent(new PipelineFailedEvent(e.getMessage(),
					node.getDpuInstance(), execution, this));
			return false;
		}
		return true;
	}

	/**
	 * Execute {@link PostExecutor} from {@link Executor#postExecutors}. If any
	 * {@link PostExecutor} return false then return false. If there are no
	 * {@link PostExecutor} ({@link Executor#postExecutors} == null) then
	 * instantly return true.
	 * 
	 * @param dpuInstance Instance of DPU to execute.
	 * @param context
	 * @return
	 */
	private boolean executePostExecutors(Context context) {
		if (postExecutors == null) {
			return true;
		}
		DPUInstanceRecord dpu = node.getDpuInstance();
		for (PostExecutor item : postExecutors) {
			if (item.postAction(dpu, execution, context)) {
				// continue execution
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * Load instance of node to execute. In case of error return null and 
	 * publish error event message.
	 * @param dpu DPU for which create instance.
	 * @return Null in case of error.
	 */
	private Object loadInstance(DPUInstanceRecord dpu) {
		Object dpuInstance = null;
		try {
			dpuInstance = loadInstance();
		} catch (ModuleException e) {
			eventPublisher.publishEvent(new PipelineModuleErrorEvent(e, dpu,
					execution, this));
			// cancel the execution
			return null;
		}
		return dpuInstance;
	}

	/**
	 * Execute given {@link Node} from
	 * {@link cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline}. Set 
	 * {@link #executionSuccessful}. Does not set DPUExecutionState.FINISHED
	 * state for {@link ProcessingUnitInfo}.
	 * 
	 * @param dpu
	 * @param unitInfo
	 * @return False if execution failed.
	 */
	private boolean execute(DPUInstanceRecord dpu, ProcessingUnitInfo unitInfo) {
		// get instance
		Object dpuInstance = loadInstance(dpu);
		if (dpuInstance == null) {
			return false;
		}
		// prepare context
		Context context = prepareContext(dpu);
		if (context == null) {
			return false;
		} else {
			// save to contexts
			contexts.put(node, context);
		}
		if (unitInfo.getState() == DPUExecutionState.FINISHED) {
			// no further execution needed
			return true;
		}
		// call PreExecutors
		if (!executePreExecutors(dpuInstance, context)) {
			return false;
		}		
		if (unitInfo.getState() == DPUExecutionState.RUNNING) {
			// we can continue with this state
			eventPublisher.publishEvent(new DPUWrongState(context, this,
					"DPU is in wrong state.", ""));
			return false;
		}
		// set state to RUNNING and save this, by this we announce
		// that we have started the execution of this DPU
		unitInfo.setState(DPUExecutionState.RUNNING);
		pipelineFacade.save(execution);
		// execute the given instance - also catch all exception
		if (executeInstance(dpuInstance, context)) {
			// execute finished successfully
		} else {
			return false;
		}
		// call PostExecutors
		if (!executePostExecutors(context)) {
			return false;
		}
		return true;
	}

	/**
	 * Execute the single DPU. If the DPU has been executed then do not execute
	 * it again. If the DPU execution has been interrupted start the execution
	 * again.
	 */
	@Override
	public void run() {
		// get DPU instance record, the DPU to execute
		DPUInstanceRecord dpu = node.getDpuInstance();
		// get processing context info
		ProcessingUnitInfo unitInfo = execution.getContext().getDPUInfo(dpu);
		if (unitInfo == null) {
			// no previous information about execution, create it
			unitInfo = execution.getContext().createDPUInfo(dpu);
			// DPUExecutionState.PREPROCESSING
		}
		// run DPU
		executionSuccessful = execute(dpu, unitInfo);
		// set finished state
		unitInfo.setState(DPUExecutionState.FINISHED);
		pipelineFacade.save(execution);
	}

	/**
	 * Return true if execution should be cancelled because of error during DPU
	 * execution.
	 * 
	 * @return
	 */
	public boolean executionFailed() {
		return !executionSuccessful;
	}


}
