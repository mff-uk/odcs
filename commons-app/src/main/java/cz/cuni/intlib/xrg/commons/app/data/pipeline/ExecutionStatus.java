package cz.cuni.intlib.xrg.commons.app.data.pipeline;

/**
 * Set of possible states during pipeline execution.
 *
 * @author Jiri Tomes
 */
public enum ExecutionStatus {

    CANCELLED,
    FAILED,
    FINISHED_SUCCESS,
    FINISHED_WARNING,
    RUNNING,
    SCHEDULED
}
