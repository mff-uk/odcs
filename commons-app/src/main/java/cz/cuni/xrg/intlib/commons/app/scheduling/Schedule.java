package cz.cuni.xrg.intlib.commons.app.scheduling;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;

import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;

/**
 * Represent a scheduler plan. 
 * A single plan execute just one pipeline.
 * 
 * @author Petyr
 *
 */
//@Entity
//@Table(name = "schedule")
public class Schedule {

    /**
     * Unique ID for each plan.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)	
	private Long id;
	
    /**
     * Plan's name.
     */
    @Column
    private String name;
    
    /**
     * Plan's description.
     */
    @Column
    private String description;
    
    /**
     * Pipeline to execute.
     */
    @ManyToOne
	@JoinColumn(name = "pipeline", unique = false, nullable = false)
    private Pipeline pipeline;
		
	/**
	 * Plan is active for just one execution.
	 */
	@Column(name = "justOnce")
	private boolean justOnce;
	
	/**
	 * True if the schedule is enabled. Disabled
	 * (not enabled) schedules are ignored.
	 */
	@Column(name = "enable")
	private boolean enable;
	
	/**
	 * Schedule rule type.
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "type")
	private ScheduleType type;
	
	/**
	 * Determine time o first execution.
	 * Used only if {@link #type} = RunInTime.
	 */
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	@Column(name = "firstExec", nullable = true)
	private Date firstExecution;	
	
	/**
	 * Time of the last execution.
	 */
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	@Column(name = "lastExec", nullable = true)	
	private Date lastExecution;	
	
	/**
	 * Execution period in {@link #periodUnit}.
	 * Used only if {@link #type} = RunInTime.
	 */
	@Column(name = "period")
	private Long period;
	
	/**
	 * Period unit type.
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "periodUnit")	
	private PeriodeUnit periodUnit;
	
    /**
     * Pipeline after which activate this plan.
	 * Used only if {@link #type} = RunAfterPipeline.
     */
	@ManyToOne
	@JoinColumn(name = "pred", nullable = true)	
    private Pipeline predPipeline;	
	
	/**
	 * Empty ctor. Used by JPA. Do not use otherwise.
	 */
	public Schedule() { }

	public Long getId() {
		return id;
	}	
	
	public Pipeline getPipeline() {
		return pipeline;
	}

	public void setPipeline(Pipeline pipeline) {
		this.pipeline = pipeline;
	}

	public boolean isJustOnce() {
		return justOnce;
	}

	public void setJustOnce(boolean justOnce) {
		this.justOnce = justOnce;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

}
