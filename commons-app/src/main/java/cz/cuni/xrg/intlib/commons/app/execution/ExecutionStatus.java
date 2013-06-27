package cz.cuni.xrg.intlib.commons.app.execution;

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
