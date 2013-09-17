package cz.cuni.xrg.intlib.backend.execution.dpu;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import cz.cuni.xrg.intlib.backend.context.Context;
import cz.cuni.xrg.intlib.backend.dpu.event.DPUEvent;
import cz.cuni.xrg.intlib.backend.pipeline.event.PipelineFailedEvent;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.DPUExecutionState;
import cz.cuni.xrg.intlib.commons.app.execution.context.ProcessingUnitInfo;
import cz.cuni.xrg.intlib.commons.app.execution.log.LogFacade;
import cz.cuni.xrg.intlib.commons.app.module.ModuleException;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineFacade;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Node;
import cz.cuni.xrg.intlib.commons.data.DataUnitException;
import cz.cuni.xrg.intlib.commons.dpu.DPU;
import cz.cuni.xrg.intlib.commons.dpu.DPUException;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger LOG = LoggerFactory.getLogger(Executor.class);
	
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
	 * Log facade used to access logs.
	 */
	@Autowired
	private LogFacade logFacade;

	/**
	 * Node to execute.
	 */
	private Node node;

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
	 * Context for current execution.
	 */
	private Context context;

	/**
	 * Sort pre/post executors.
	 */
	@PostConstruct
	public void init() {
		if (preExecutors != null) {
			Collections.sort(preExecutors,
					AnnotationAwareOrderComparator.INSTANCE);
		}
		if (postExecutors != null) {
			Collections.sort(postExecutors,
					AnnotationAwareOrderComparator.INSTANCE);
		}

	}

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
			Map<Node, Context> contexts,
			PipelineExecution execution,
			Date lastExecutionTime) {
		this.node = node;
		this.contexts = contexts;
		this.execution = execution;
		// create context, bind it to this execution and add it to the contexts
		this.context = beanFactory.getBean(Context.class);
		this.context.bind(node.getDpuInstance(), execution.getContext(),
				lastExecutionTime);
		this.contexts.put(node, context);
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
			LOG.error("Missing DPU jar file: '{}' for DPU: {}",
					dpu.getJarPath(), dpu.getName());
			// publish event DPU not found
			eventPublisher.publishEvent(PipelineFailedEvent.createMissingFile(
					dpu, execution, this));
			throw new ModuleException(
					"Failed to load instance of DPU from file.", e);
		}
		return dpu.getInstance();
	}

	/**
	 * Execute {@link PreExecutor} from {@link Executor#preExecutors}. If any
	 * {@link PreExecutor} return false then return false. If there are no
	 * {@link PreExecutor} ({@link Executor#preExecutors} == null) then
	 * instantly return true.
	 * 
	 * @param dpuInstance Instance of DPU to execute.
	 * @return
	 */
	private boolean executePreExecutors(Object dpuInstance) {
		if (preExecutors == null) {
			return true;
		}

		for (PreExecutor item : preExecutors) {
			if (item.preAction(node, contexts, dpuInstance, execution)) {
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
	 * @return True if the pipeline execution should continue.
	 */
	private boolean executeInstance(Object dpuInstance) {
		// execute
		try {
			if (dpuInstance instanceof DPU) {
				((DPU) dpuInstance).execute(context);
			} else {
				// can not be executed
			}
		} catch (DataUnitException e) {
			LOG.error("Execution:DataUnitException", e);
			eventPublisher.publishEvent(DPUEvent.createDataUnitFailed(context,
					this, e));
			return false;
		} catch (DPUException e) {
			LOG.error("Execution:DPUException", e);
			eventPublisher.publishEvent(PipelineFailedEvent.create(e,
					node.getDpuInstance(), execution, this));
			return false;
		} catch (Exception e) {
			LOG.error("Execution:Exception", e);
			eventPublisher.publishEvent(PipelineFailedEvent.create(e,
					node.getDpuInstance(), execution, this));
			return false;
		} catch (Error e) {
			LOG.error("Execution:Error", e);
			eventPublisher.publishEvent(PipelineFailedEvent.create(e,
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
	 * @param dpuInstance Instance of DPU that has been executed.
	 * @return
	 */
	private boolean executePostExecutors(Object dpuInstance) {
		if (postExecutors == null) {
			return true;
		}

		for (PostExecutor item : postExecutors) {
			if (item.postAction(node, contexts, dpuInstance, execution)) {
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
	 * 
	 * @param dpu DPU for which create instance.
	 * @return Null in case of error.
	 */
	private Object loadInstance(DPUInstanceRecord dpu) {
		Object dpuInstance = null;
		try {
			dpuInstance = loadInstance();
		} catch (ModuleException e) {
			LOG.error("Failed to load required bundle", e);
			eventPublisher.publishEvent(PipelineFailedEvent.create(e, dpu,
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
		if (unitInfo.getState() == DPUExecutionState.FINISHED) {
			// DPU has been executed already ..
			return true;
		}
		// get instance
		Object dpuInstance = loadInstance(dpu);
		if (dpuInstance == null) {
			return false;
		}

		// call PreExecutors
		if (!executePreExecutors(dpuInstance)) {
			return false;
		}
		if (unitInfo.getState() == DPUExecutionState.RUNNING) {
			// we can not continue with this state
			eventPublisher.publishEvent(DPUEvent
					.createWrongState(context, this));
			return false;
		}
		// set state to RUNNING and save this, by this we announce
		// that we have started the execution of this DPU
		unitInfo.setState(DPUExecutionState.RUNNING);

		try {
			pipelineFacade.save(execution);
		} catch (EntityNotFoundException ex) {
			LOG.warn("Seems like someone deleted our pipeline run.", ex);
			return false;
		}

		// execute the given instance - also catch all exception
		eventPublisher.publishEvent(DPUEvent.createStart(context, this));
		if (executeInstance(dpuInstance)) {
			// execute finished successfully
			eventPublisher.publishEvent(DPUEvent.createComplete(context, this));
		} else {
			return false;
		}

		// check for the error message in context or in logs
		if (context.errorMessagePublished()) {
			// cancel because of logged/published error record
			return false;
		}

		// call PostExecutors
		if (!executePostExecutors(dpuInstance)) {
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

		try {
			pipelineFacade.save(execution);
		} catch (EntityNotFoundException ex) {
			// Pipeline execution was probably deleted by user
			LOG.warn("Seems like someone deleted our pipeline run.", ex);
		}
	}

	/**
	 * Call {@link Context#cancel()}, can be called from other thread.
	 */
	public void cancel() {
		context.cancel();
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
