package cz.cuni.xrg.intlib.commons.app.pipeline;

/**
 * Information about executed pipeline and their states.
 *
 * @author Jiri Tomes
 * @author Jan Vojt
 * @author Petyr
 */
public class PipelineExecution  {

	/**
	 * Unique id of pipeline execution.
	 */
	private int id;
	
    /**
     * Actual status for executed pipeline.
     */
    private ExecutionStatus status;
    
    /**
     * Pipeline for executing.
     */
    private Pipeline pipeline;
    
    /**
     * Run in debug mode?
     */
    private boolean isDebugging;
    
    /**
     * Constructor. Create pipeline which will be run 
     * as soon as possible in non-debug mode.
     *
     * @param pipeline
     */
    public PipelineExecution(Pipeline pipeline) {        
        this.status = ExecutionStatus.SCHEDULED;
        this.pipeline = pipeline;
        this.isDebugging = false;
    }

    public int getId() {
    	return id;
    }
    
    public ExecutionStatus getExecutionStatus() {
        return status;
    }

    public void setExecutionStatus(ExecutionStatus newStatus) {
        status = newStatus;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

	public boolean isDebugging() {
		return isDebugging;
	}

	public void setDebugging(boolean isDebugging) {
		this.isDebugging = isDebugging;
	}

}
