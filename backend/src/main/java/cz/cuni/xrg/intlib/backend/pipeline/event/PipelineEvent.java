package cz.cuni.xrg.intlib.backend.pipeline.event;

import java.util.Date;

import org.springframework.context.ApplicationEvent;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.execution.Record;
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
	 * The most related DPU to the event. 
	 */
	protected DPUInstance dpuInstance;
	
	/**
	 * Associated pipeline execution.
	 */
    protected PipelineExecution execution;

    public PipelineEvent(DPUInstance dpuInstance, PipelineExecution execution, Object source) {
        super(source);
        time = new Date();
        this.dpuInstance = dpuInstance;
        this.execution = execution;
    }

    /**
     * Record that describe event.
     * @return record
     */
    public abstract Record getRecord();

}