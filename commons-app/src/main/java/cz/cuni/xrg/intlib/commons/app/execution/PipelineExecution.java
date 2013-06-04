package cz.cuni.xrg.intlib.commons.app.execution;

import java.io.File;
import java.util.Date;
import javax.persistence.*;

import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;

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
	 * TODO change to Long
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
	@ManyToOne
	@JoinColumn(name="pipeline_id")
    private Pipeline pipeline;
    
    /**
     * Run in debug mode?
     */
	@Column(name = "debug_mode")
    private boolean isDebugging;
	
	/**
	 * Timestamp when this execution started, or null.
	 */
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	@Column(name = "t_start")
	private Date start;
	
	/**
	 * Timestamp when this execution started, or null.
	 */
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	@Column(name = "t_end")
	private Date end;
	
	/**
	 * Execution context, can be null.
	 */
	@OneToOne
	@JoinColumn(name="context_id", nullable = true)	
	private ExecutionContextImpl context;
	
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

    /**
     * Create execution context or this execution in given directory.
     * If context already exist return the existing one.
     * @return
     */
    public ExecutionContext createExecutionContext(File directory) {
    	if (context == null) {
    		context = new ExecutionContextImpl(directory);
    	}
    	return context;
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

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}
	        
    /**
     * Use to gain read only access to the context.
     * @return Context or null.
     */    
    public ExecutionContext getContextReadOnly() {
    	return context;
    }    
}
