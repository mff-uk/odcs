package cz.cuni.xrg.intlib.backend.dpu.event;

import java.util.Date;

import org.springframework.context.ApplicationEvent;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.dpu.execution.DPURecord;

/**
 * Base abstract class for the DPU event. 
 *
 * @author Jiri Tomes
 * @author Petyr
 */
public abstract class DPUEvent extends ApplicationEvent {

	/**
	 * Time of creation.
	 */
	protected Date time;
	
	/**
	 * Source of message.
	 */
	protected DPUInstance dpuInstance;
	
    public DPUEvent(Object source, DPUInstance dpuInstance) {
        super(source);
        this.dpuInstance = dpuInstance;
        this.time = new Date();
    }
    
    public DPUEvent(Object source, DPUInstance dpuInstance, Date time) {
        super(source);
        this.dpuInstance = dpuInstance;
        this.time = time;
    }    
    
    /**
     * Record that describe event.
     * @return record
     */
    public abstract DPURecord getRecord();
}
