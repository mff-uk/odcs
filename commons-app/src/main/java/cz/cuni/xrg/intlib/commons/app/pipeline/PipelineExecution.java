package cz.cuni.xrg.intlib.commons.app.pipeline;

import java.util.Date;
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
	 * Path to the pipeline execution working directory.
	 */
	@Column(name = "execution_directory")
	private String workingDirectory;
	
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

    public String getWorkingDirectory() {
        return workingDirectory;
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
	
    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    } 	
}
