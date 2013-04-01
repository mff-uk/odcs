package cz.cuni.xrg.intlib.commons.app.data.pipeline;

/**
 * Run a s
 * 
 * @author Jiri Tomes
 */
public class Engine {

    private Worker[] threads;
    private int size;

    public Engine(int size) {
        this.size = size;
        this.threads = new Worker[size];
        inicialize();
    }

    private void inicialize() {
        for (int i = 0; i < size; i++) {
            threads[i] = new Worker();

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
