package cz.cuni.xrg.intlib.commons.app.pipeline;

/**
 * Run, cancel and debug concrete pipeline.
 *
 * @author Jiri Tomes
 */
public class PipelineWorker implements Runnable {

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
