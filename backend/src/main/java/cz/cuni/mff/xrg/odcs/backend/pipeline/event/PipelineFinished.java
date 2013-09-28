package cz.cuni.mff.xrg.odcs.backend.pipeline.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;

/**
 * Report that pipelineExecution is finished. This does not inherit from
 * PepelineEvent, because is not used to log information about pipeline run.
 * Instead is used to inform
 * {@link cz.cuni.mff.xrg.odcs.backend.scheduling.Scheduler}
 * 
 * @author Petyr
 * 
 */
public final class PipelineFinished extends ApplicationEvent {

	private static final Logger LOG = LoggerFactory
			.getLogger(PipelineFinished.class);

	/**
	 * Associated pipeline execution.
	 */
	protected PipelineExecution execution;

	public PipelineFinished(PipelineExecution pipelineExec, Object source) {
		super(source);
		this.execution = pipelineExec;

		LOG.info("Pipeline finished with status: {}", execution.getStatus()
				.toString());
	}

	public PipelineExecution getExecution() {
		return execution;
	}

	/**
	 * Return true if respective execution finished with
	 * {@link PipelineExecutionStatus#FINISHED_SUCCESS} or
	 * {@link PipelineExecutionStatus#FINISHED_WARNING}.
	 * 
	 * @return
	 */
	public boolean sucess() {
		return execution.getStatus() == PipelineExecutionStatus.FINISHED_SUCCESS
				|| execution.getStatus() == PipelineExecutionStatus.FINISHED_WARNING;
	}

}
