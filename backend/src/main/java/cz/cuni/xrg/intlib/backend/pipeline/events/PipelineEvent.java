package cz.cuni.xrg.intlib.backend.pipeline.events;

import org.springframework.context.ApplicationEvent;

import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;

/**
 * Base class for Pipeline events.
 *
 * @author Petyr
 */
public abstract class PipelineEvent extends ApplicationEvent {

	/**
	 * Associated pipeline execution.
	 */
    protected final PipelineExecution execution;

    public PipelineEvent(PipelineExecution execution, Object source) {
        super(source);
        this.execution = execution;
    }

    /**
     * Returns the associated PipelineExecution.
     *
     * @return
     */
    public PipelineExecution getPipelineExecution() {
        return execution;
    }

}