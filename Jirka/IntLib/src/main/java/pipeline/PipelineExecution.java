package pipeline;

/**
 *
 * @author Jiri Tomes
 */
public class PipelineExecution {

    private ExecutionStatus status;

    public ExecutionStatus getExecutionStatus() {
        return status;
    }

    public void setExecutionStatus(ExecutionStatus newStatus) {
        status = newStatus;
    }
}
