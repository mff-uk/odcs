package cz.cuni.xrg.intlib.commons.app.pipeline;

/**
 * Run, cancel and debug concrete pipeline.
 *
 * @author Jiri Tomes
 * @author Jan Vojt
 */
public class PipelineWorker extends Thread {

    private boolean alive = true;
    private boolean isWorking = true;
    private PipelineExecution execution;

    public PipelineWorker(PipelineExecution execution) {
        this.execution=execution;
    }

    /**
     * Lazy kill - waits until Pipeline run is finished.
     */
    public void kill() {
        alive = false;
    }

    /**
     * Implementation of workers activity. Worker constantly keeps asking engine
     * for jobs to run, until it is killed.
     */
    @Override
    public void run() {

        while (alive) {

            if (execution == null) {
                isWorking = false;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                    e.fillInStackTrace();
                }
            } else {
                isWorking = true;
                execution.run();
            }
        }
    }
    

    /**
     * Tells whether worker is currently processing any pipeline.
     *
     * @return
     */
    public boolean isWorking() {
        return isWorking;
    }
}
