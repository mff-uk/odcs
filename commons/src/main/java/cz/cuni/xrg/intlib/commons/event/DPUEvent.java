package cz.cuni.xrg.intlib.commons.event;

import org.springframework.context.ApplicationEvent;

/**
 * Class for the DPU event. 
 *
 * @author Jiri Tomes
 */
public abstract class DPUEvent extends ApplicationEvent {

    public DPUEvent(Object source) {
        super(source);
    }
}
