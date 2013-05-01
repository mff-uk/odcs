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
    
    private Engine engine;

    public PipelineWorker(Engine engine) {
    	this.engine = engine;
    }

    /**
     * Lazy kill - waits until Pipeline run is finished.
     */
    public void kill() {
    	alive = false;
    }

    /**
     * Implementation of workers activity.
     * Worker constantly keeps asking engine for jobs to run,
     * until it is killed.
     */
    @Override
    public void run() {
    	
    	while (alive) {
    		
    		PipelineExecution exec = engine.getJob();
    		
    		if (exec == null) {
    			isWorking = false;
        		try {
    				Thread.sleep(1000);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		} else {
    			isWorking = true;
    			exec.run();
    		}
    	}
    }
    
    /**
     * Tells whether worker is currently processing any pipeline.
     * @return
     */
    public boolean isWorking() {
    	return isWorking;
    }
}
