package cz.cuni.xrg.intlib.commons.app.pipeline;

/**
 * Set of possible states during pipeline execution.
 *
 * @author Jiri Tomes
 */
public enum PipelineExecutionStatus {
    CANCELLED,
    FAILED,
    FINISHED_SUCCESS,
    FINISHED_WARNING,
    RUNNING,
    SCHEDULED
}
