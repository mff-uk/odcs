package pipeline;

/**
 *
 * @author Jiri Tomes
 */
public class PipelineExecution {

    private ExecutionStatus status;
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
