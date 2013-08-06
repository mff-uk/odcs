package cz.cuni.xrg.intlib.backend.pipeline.event;

import java.util.Date;

import org.springframework.context.ApplicationEvent;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.message.MessageRecord;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

/**
 * Base class for Pipeline events.
 * 
 * @author Petyr
 */
public abstract class PipelineEvent extends ApplicationEvent {

	/**
	 * Time of creation.
	 */
	protected Date time;

	/**
	 * The most related DPURecord to the event.
	 */
	protected DPUInstanceRecord dpuInstance;

	/**
	 * Associated pipeline execution.
	 */
	protected PipelineExecution execution;

	public PipelineEvent(PipelineExecution execution, Object source) {
		super(source);
		time = new Date();
		this.dpuInstance = null;
		this.execution = execution;
	}

	public PipelineEvent(DPUInstanceRecord dpuInstance,
			PipelineExecution execution,
			Object source) {
		super(source);
		time = new Date();
		this.dpuInstance = dpuInstance;
		this.execution = execution;
	}

	/**
	 * Record that describe event.
	 * 
	 * @return MessageRecord
	 */
	public abstract MessageRecord getRecord();

}