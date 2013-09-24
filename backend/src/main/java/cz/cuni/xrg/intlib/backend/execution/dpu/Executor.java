package cz.cuni.xrg.intlib.backend.execution.dpu;

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
	 * Load instance of node to execute. In case of error return null and
	 * publish error event message.
	 * 
	 * @return DPU's instance or null.
	 */
	private Object loadInstance() {
		final DPUInstanceRecord dpu = node.getDpuInstance();
		// load instance
		try {
			dpu.loadInstance(moduleFacade);
		} catch (ModuleException e) {
			// eventPublisher.publishEvent(PipelineFailedEvent.createMissingFile(
			// dpu, execution, this));
			eventPublisher.publishEvent(PipelineFailedEvent.create(e, dpu,
					execution, this));
			return null;
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
	 * @param unitInfo
	 * @return
	 */
	private boolean executePreExecutors(Object dpuInstance,
			ProcessingUnitInfo unitInfo) {
		if (preExecutors == null) {
			return true;
		}

		for (PreExecutor item : preExecutors) {
			if (item.preAction(node, contexts, dpuInstance, execution, unitInfo)) {
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
				LOG.error("DPU do not implement execution interface.");
			}
		} catch (DataUnitException e) {
			eventPublisher.publishEvent(DPUEvent.createDataUnitFailed(context,
					this, e));
			return false;
		} catch (DPUException e) {
			eventPublisher.publishEvent(PipelineFailedEvent.create(e,
					node.getDpuInstance(), execution, this));
			return false;
		} catch (Exception e) {
			eventPublisher.publishEvent(PipelineFailedEvent.create(e,
					node.getDpuInstance(), execution, this));
			return false;
		} catch (Error e) {
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
	 * @param unitInfo
	 * @return
	 */
	private boolean executePostExecutors(Object dpuInstance,
			ProcessingUnitInfo unitInfo) {
		if (postExecutors == null) {
			return true;
		}

		for (PostExecutor item : postExecutors) {
			if (item.postAction(node, contexts, dpuInstance, execution,
					unitInfo)) {
				// continue execution
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * Execute binded {@link Node} from
	 * {@link cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline}. Assume that the
	 * given {@link Node} has not been executed yet.
	 * 
	 * @param unitInfo
	 * @return False if execution failed.
	 */
	private boolean execute(ProcessingUnitInfo unitInfo) {

		// get dpu's instance
		Object dpuInstance = loadInstance();
		if (dpuInstance == null) {
			return false;
		}

		// call PreExecutors
		if (!executePreExecutors(dpuInstance, unitInfo)) {
			return false;
		}

		switch (unitInfo.getState()) {
		case PREPROCESSING:
			// ok continue with execution
			break;
		case ABORTED:
		case FAILED:
			// we ignore this, they should return false when they fail
			LOG.info("Some pre-processor set ABORTED/FAILED state before start of the execution. Ignoring state change.");
			break;
		case FINISHED:
			// dpu finished .. yes this can be
			// we also do not prevent DPU with such state to get here
			return true;
		case RUNNING:
			// wrong state, probably from last interrupted execution
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
			LOG.error("Seems like someone deleted our pipeline run.", ex);
			return false;
		}

		// execute the given instance - also catch all exception
		eventPublisher.publishEvent(DPUEvent.createStart(context, this));
		boolean executionResult = executeInstance(dpuInstance);

		// set state
		if (context.canceled()) {
			// dpu has been aborted
			unitInfo.setState(DPUExecutionState.ABORTED);
		} else {
			if (!executionResult && context.errorMessagePublished()) {
				// dpu publish error or ends with exception
				unitInfo.setState(DPUExecutionState.FAILED);
				executionResult = false;
			} else {
				unitInfo.setState(DPUExecutionState.FINISHED);
			}
		}

		// call PostExecutors if they fail then the execution fail
		executionResult &= executePostExecutors(dpuInstance, unitInfo);

		// we save the state into database
		try {
			pipelineFacade.save(execution);
		} catch (EntityNotFoundException ex) {
			LOG.error("Seems like someone deleted our pipeline run.", ex);
		}

		// return execution result .. this can't be changed to positive by post
		// executors but they may change the state in unitInfo
		return executionResult;
	}

	/**
	 * Execute the single DPU. If the DPU has been executed then do not execute
	 * it again. If the DPU execution has been interrupted start the execution
	 * again.
	 */
	@Override
	public void run() {
		// assume that execution failed, if the execution thread terminates
		// or something bad happen
		executionSuccessful = false;

		// get DPU instance record, the DPU to execute
		DPUInstanceRecord dpu = node.getDpuInstance();
		// get processing context info
		ProcessingUnitInfo unitInfo = execution.getContext().getDPUInfo(dpu);
		if (unitInfo == null) {
			// no previous information about execution, create it
			unitInfo = execution.getContext().createDPUInfo(dpu);
			// DPUExecutionState.PREPROCESSING
		} else {
			// check if not finished yet
			switch (unitInfo.getState()) {
			case ABORTED:
			case FAILED:
				// dpu has been already executed and it failed
				executionSuccessful = false;
				return;
			case FINISHED:
				// we will continue as we need to run pre/post
				// Processors as the create DataUnits
			case PREPROCESSING:
			case RUNNING:
				// for these state we continue in execution
				break;
			}
		}

		// run dpu, also set executionSuccessful according to
		// the execution result
		executionSuccessful = execute(unitInfo)
		// also check for DPU messages
				&& !context.errorMessagePublished();

		// publish message
		eventPublisher.publishEvent(DPUEvent.createComplete(context, this));
	}

	/**
	 * Call {@link Context#cancel()}, can be called from other thread. Use this
	 * to order DPU to stop it's execution. The pre/post processors are executed
	 * normally.
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
