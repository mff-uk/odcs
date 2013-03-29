package pipeline;

/**
 *
 * @author Jiri Tomes
 */
public class Worker implements Runnable {

    private boolean justWorking = false;

    public boolean isWorking() {
        return justWorking;
    }

    /*
     * TODO - Implement me
     */
    public void cancel(PipelineExecution execution) {
    }

    public void debug(PipelineExecution execution) {
    }

    public void kill() {
    }

    public void run() {
    }
}
