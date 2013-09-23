package cz.cuni.xrg.intlib.commons.app.pipeline;

import java.util.Date;
import javax.persistence.*;

import cz.cuni.xrg.intlib.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.xrg.intlib.commons.app.pipeline.graph.Node;
import cz.cuni.xrg.intlib.commons.app.scheduling.Schedule;
import java.io.Serializable;
import java.util.Objects;

/**
 * Information about executed pipeline and its states.
 *
 * @author Jiri Tomes
 * @author Jan Vojt
 * @author Petyr
 */
@Entity
@Table(name = "exec_pipeline")
public class PipelineExecution implements Serializable {

	/**
	 * Unique id of pipeline execution.
	 */
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Actual status for executed pipeline.
	 */
	@Enumerated(EnumType.ORDINAL)
	private PipelineExecutionStatus status;

	/**
	 * Pipeline being executed.
	 */
	@ManyToOne
	@JoinColumn(name = "pipeline_id")
	private Pipeline pipeline;

	/**
	 * Node where execution should end. Only for debug mode.
	 */
	@ManyToOne(optional = true)
	@JoinColumn(name = "debugnode_id", nullable = true)
	private Node debugNode;

	/**
	 * Run in debug mode?
	 */
	@Column(name = "debug_mode")
	private boolean isDebugging;

	/**
	 * Timestamp when this execution started, or null.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "t_start")
	private Date start;

	/**
	 * Timestamp when this execution started, or null.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "t_end")
	private Date end;

	/**
	 * Execution context, can be null.
	 */
	@OneToOne(cascade = CascadeType.ALL, optional = false)
	@JoinColumn(name = "context_id", nullable = true)
	private ExecutionContextInfo context;

	/**
	 * Schedule that planned this execution. Null for execution created by user.
	 */
	@ManyToOne(optional = true)
	@JoinColumn(name = "schedule_id", nullable = true)
	private Schedule schedule;

	/**
	 * It true pipeline run in silent mode and the end of the execution can't be
	 * used to fire schedule.
	 */
	@Column(name = "silent_mode")
	private boolean silentMode;

	/**
	 * True if pipeline should or has been stoped on user request.
	 */
	@Column(name = "stop")
	private boolean stop;
        
        /**
	 * Timestamp when this execution was last changed.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "t_last_change")
	private Date lastChange;
	
	/**
	 * No-arg constructor for JPA
	 */
	public PipelineExecution() {
	}

	/**
	 * Constructor. Create pipeline which will be run as soon as possible in
	 * non-debug mode. The pipeline execution will not run other pipelines based
	 * on scheduling rules.
	 *
	 * @param pipeline
	 */
	public PipelineExecution(Pipeline pipeline) {
		this.status = PipelineExecutionStatus.SCHEDULED;
		this.pipeline = pipeline;
		this.isDebugging = false;
		this.schedule = null;
		this.silentMode = true;
		this.stop = false;
		
		// Execution context is obligatory, so that we do not need to check for
		// nulls everywhere. A new execution has an empty context.
		this.context = new ExecutionContextInfo(this);
	}

	public Long getId() {
		return id;
	}
      
    public PipelineExecutionStatus getStatus() {
		return status;
	}

	public void setStatus(PipelineExecutionStatus newStatus) {
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
        
        public boolean getIsDebugging() {
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
	 *
	 * @return Context or null.
	 */
	public ExecutionContextInfo getContextReadOnly() {
		return context;
	}

	public ExecutionContextInfo getContext() {
		return context;
	}
	
	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public boolean getSilentMode() {
		return silentMode;
	}

	public void setSilentMode(boolean silentMode) {
		this.silentMode = silentMode;
	}

	public Node getDebugNode() {
		return debugNode;
	}

	public void setDebugNode(Node debugNode) {
		this.debugNode = debugNode;
	}

	public boolean getStop() {
		return stop;
	}
	
	public void stop() {
		stop = true;
		status = PipelineExecutionStatus.CANCELLED;
	}
        
        public Date getLastChange() {
            return lastChange;
        }
        
        /**
         * Returns duration of execution. Returns -1 for unfinished/not started executions.
         * 
         **/
        public long getDuration() {
            if(start == null || end == null) {
                return -1;
            }
            return end.getTime() - start.getTime();
        }
	
	/**
	 * Hashcode is compatible with {@link #equals(java.lang.Object)}.
	 * 
	 * @return hashcode
	 */
	@Override
	public int hashCode() {
		int hash = 3;
		hash = 23 * hash + (int) (this.id ^ (this.id >>> 32));
		return hash;
	}

	/**
	 * Returns true if two objects represent the same pipeline execution. This
	 * holds if and only if <code>this.id == null ? this == obj : this.id == o.id</code>.
	 * 
	 * @param o
	 * @return true if both objects represent the same pipeline execution
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (getClass() != o.getClass()) {
			return false;
		}
		
		final PipelineExecution other = (PipelineExecution) o;
		if (this.id == null) {
			return super.equals(other);
		}
		
		return Objects.equals(this.id, other.id);
	}

    
	
}
