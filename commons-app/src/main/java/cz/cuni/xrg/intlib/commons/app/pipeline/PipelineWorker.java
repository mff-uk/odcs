package cz.cuni.xrg.intlib.commons.app.pipeline;

/**
 * Run, cancel and debug concrete pipeline.
 *
 * @author Jiri Tomes
 */
public class PipelineWorker extends Thread {

    private boolean alive = true;


    public void cancel(PipelineExecution execution) {
    }

    public void debug(PipelineExecution execution) {
    }

    public void kill() {
    	alive = false;
    }

    public void run() {
    	
    	while (alive) {
    		
    	}
    	
    }
}
