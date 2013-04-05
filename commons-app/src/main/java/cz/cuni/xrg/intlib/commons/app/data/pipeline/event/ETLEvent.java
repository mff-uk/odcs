package cz.cuni.xrg.intlib.commons.app.data.pipeline.event;

import org.springframework.context.ApplicationEvent;

/**
 * Class for the DPU event. 
 *
 * @author Jiri Tomes
 */
public abstract class ETLEvent extends ApplicationEvent {

    public ETLEvent(Object source) {
        super(source);
    }
}
