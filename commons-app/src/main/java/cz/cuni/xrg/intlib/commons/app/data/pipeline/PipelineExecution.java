package cz.cuni.xrg.intlib.commons.app.data.pipeline;

/**
 * Information about executed pipeline and their states.
 *
 * @author Jiri Tomes
 */
public class PipelineExecution {

    /**
     * Actual status for executed pipeline.
     */
    private ExecutionStatus status;
    /**
     * Pipeline for executing.
     */
    private Pipeline pipeline;

    public PipelineExecution(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public ExecutionStatus getExecutionStatus() {
        return status;
    }

    public void setExecutionStatus(ExecutionStatus newStatus) {
        status = newStatus;
    }
}
