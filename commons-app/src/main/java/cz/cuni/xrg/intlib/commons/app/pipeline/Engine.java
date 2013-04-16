package cz.cuni.xrg.intlib.commons.app.pipeline;

/**
 * Run as
 * 
 * @author Jiri Tomes
 */
public class Engine {

    private PipelineWorker[] threads;
    private int size;

    public Engine(int size) {
        this.size = size;
        this.threads = new PipelineWorker[size];
        inicialize();
    }

    private void inicialize() {
        for (int i = 0; i < size; i++) {
            threads[i] = new PipelineWorker();

        }
    }

    public void complete() {
    }

    public void kill() {
        for (int i = 0; i < size; i++) {
            threads[i].kill();
        }

    }

    public void run() {
        for (int i = 0; i < size; i++) {
            threads[i].run();
        }
    }
}
