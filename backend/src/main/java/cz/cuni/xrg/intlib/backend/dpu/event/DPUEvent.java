package cz.cuni.xrg.intlib.backend.dpu.event;

import org.springframework.context.ApplicationEvent;

/**
 * Base abstract class for the DPU event. 
 *
 * @author Jiri Tomes
 */
public abstract class DPUEvent extends ApplicationEvent {

    public DPUEvent(Object source) {
        super(source);
    }
}
