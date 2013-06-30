package cz.cuni.xrg.intlib.commons.app.scheduling;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;

import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;

/**
 * Represent a scheduler plan. 
 * A single plan execute just one pipeline.
 * 
 * @author Petyr
 *
 */
@Entity
@Table(name = "schedule")
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
	@Column(name = "enabled")
	private boolean enabled;
	
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
// TODO Honza: List of pipelines from Pipelines 	
//	@ManyToOne
//	@JoinColumn(name = "predPipeline", nullable = true)	
	@Transient
    private List<Pipeline> predPipeline;	
	
	/**
	 * Empty ctor. Used by JPA. Do not use otherwise.
	 */
	public Schedule() { }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public ScheduleType getType() {
		return type;
	}

	public void setType(ScheduleType type) {
		this.type = type;
	}

	public Date getFirstExecution() {
		return firstExecution;
	}

	public void setFirstExecution(Date firstExecution) {
		this.firstExecution = firstExecution;
	}

	public Date getLastExecution() {
		return lastExecution;
	}

	public void setLastExecution(Date lastExecution) {
		this.lastExecution = lastExecution;
	}

	public Long getPeriod() {
		return period;
	}

	public void setPeriod(Long period) {
		this.period = period;
	}

	public PeriodeUnit getPeriodUnit() {
		return periodUnit;
	}

	public void setPeriodUnit(PeriodeUnit periodUnit) {
		this.periodUnit = periodUnit;
	}

	public List<Pipeline> getPredPipeline() {
		return predPipeline;
	}

	public Long getId() {
		return id;
	}
	
}
