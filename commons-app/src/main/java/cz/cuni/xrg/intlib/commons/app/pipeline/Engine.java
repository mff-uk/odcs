package cz.cuni.xrg.intlib.commons.app.pipeline;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Responsible for running and supervision queue of PipelineExecution tasks.
 *
 * @author Jiri Tomes
 * @author Jan Vojt
 */
public class Engine {

    /**
     * Thread pool of workers.
     */
    private PipelineWorker[] threads;
    /**
     * Maximum number of concurrent pipeline runs = threads.
     */
    private int size;
    private Queue<PipelineExecution> queue = new LinkedList<>();

    public Engine(int size) {
        this.size = size;
        this.threads = new PipelineWorker[size];
    }

    public void complete() {
    }

    public void kill() {
        for (int i = 0; i < size; i++) {
            threads[i].kill();
        }
    }

    /**
     * Puts pipeline run into internal queue to be processed by workers.
     *
     * @param exec
     */
    public void run(PipelineExecution exec) {
        queue.add(exec);
        optimizeWorkers();
    }

    /**
     * Optimizes number of workers according to current size of queue.
     */
    public void optimizeWorkers() {
        if (queue.isEmpty()) {
            for (int i = 0; i < threads.length; i++) {
                if (threads[i] != null && !threads[i].isWorking()) {
                    threads[i].kill();
                    threads[i] = null;
                }
            }
        } else {
            addWorkers(queue.size());
        }
    }

    /**
     * Interface where worker can obtain a job.
     *
     * @return
     */
    public synchronized PipelineExecution getJob() {
        return queue.isEmpty() ? null : queue.remove();
    }

    /**
     * Try to create more Workers in the thread pool, respects pool size.
     *
     * @param n
     * @return
     */
    private boolean addWorkers(int n) {
        for (int i = 0; i < threads.length; i++) {
            if (threads[i] == null) {
                PipelineWorker pw = new PipelineWorker(this);
                threads[i] = pw;
                pw.start();
                if ((--n) <= 0) {
                    break;
                }
            }
        }
        return n == 0;
    }
}
