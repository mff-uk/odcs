package cz.cuni.xrg.intlib.commons.app.pipeline;

import javax.persistence.*;

/**
 * Information about executed pipeline and its states.
 *
 * @author Jiri Tomes
 * @author Jan Vojt
 * @author Petyr
 */
@Entity
@Table(name = "ppl_execution")
public class PipelineExecution  {

	/**
	 * Unique id of pipeline execution.
	 */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
    /**
     * Actual status for executed pipeline.
     */
	@Enumerated(EnumType.ORDINAL)
    private ExecutionStatus status;
    
    /**
     * Pipeline being executed.
     */
	@ManyToOne(fetch = FetchType.LAZY)
    private Pipeline pipeline;
    
    /**
     * Run in debug mode?
     */
	@Column(name = "debug_mode")
    private boolean isDebugging;

	/** No-arg constructor for JPA */
	public PipelineExecution() {}
    
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
