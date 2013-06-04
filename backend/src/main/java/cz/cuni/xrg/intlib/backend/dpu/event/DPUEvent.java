package cz.cuni.xrg.intlib.backend.dpu.event;

import java.util.Date;

import org.springframework.context.ApplicationEvent;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.execution.Record;

/**
 * Base abstract class for the DPURecord event. 
 *
 * @author Petyr
 * @author Jiri Tomes 
 * 
 */
public abstract class DPUEvent extends ApplicationEvent {

	/**
	 * Time of creation.
	 */
	protected Date time;
	
	/**
	 * Source of message.
	 */
	protected DPUInstanceRecord dpuInstance;
	
	/**
	 * Pipeline execution.
	 */
	protected PipelineExecution execution;
	
    public DPUEvent(Object source, DPUInstanceRecord dpuInstance, PipelineExecution execution) {
        super(source);
        this.time = new Date();
        this.dpuInstance = dpuInstance;
        this.execution = execution;        
    }
    
    public DPUEvent(Object source, DPUInstanceRecord dpuInstance, PipelineExecution execution, Date time) {
        super(source);
        this.time = time;
        this.dpuInstance = dpuInstance;
        this.execution = execution;        
    }    
    
    /**
     * Record that describes event.
     * @return record
     */
    public abstract Record getRecord();
}
