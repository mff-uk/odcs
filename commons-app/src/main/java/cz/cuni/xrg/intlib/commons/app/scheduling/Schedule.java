package cz.cuni.xrg.intlib.commons.app.scheduling;

import java.util.Date;

import javax.persistence.*;

import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import cz.cuni.xrg.intlib.commons.app.user.User;
import java.io.Serializable;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Represent a scheduler plan. A single plan execute just one pipeline.
 *
 * @author Petyr
 *
 */
@Entity
@Table(name = "exec_schedule")
public class Schedule implements Serializable {

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
	@JoinColumn(name = "pipeline_id", nullable = false)
	private Pipeline pipeline;

	/**
	 * Plan is active for just one execution.
	 */
	@Column(name = "just_once")
	private boolean justOnce;

	/**
	 * True if the schedule is enabled. Disabled (not enabled) schedules are
	 * ignored.
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
	 * Time o first planned execution. May be null if this plan is only supposed
	 * to run after pipelines in {@link #afterPipelines}, but has no specific
	 * time to be run at. Used only if {@link #type} is {@link ScheduleType#.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "first_exec", nullable = true)
	private Date firstExecution;

	/**
	 * Time of the last execution.
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_exec", nullable = true)
	private Date lastExecution;

	/**
	 * Execution period in {@link #periodUnit}. Used only if {@link #type} =
	 * RunInTime.
	 */
	@Column(name = "time_period")
	private Integer period;

	/**
	 * Time period unit type.
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "period_unit")
	private PeriodUnit periodUnit;

	/**
	 * Pipeline after which this job is supposed to run. Applicable only if
	 * {@link #type} is {@link ScheduleType#AFTER_PIPELINE}.
	 */
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "exec_schedule_after",
			joinColumns =
			@JoinColumn(name = "schedule_id", referencedColumnName = "id"),
			inverseJoinColumns =
			@JoinColumn(name = "pipeline_id", referencedColumnName = "id"))
	private Set<Pipeline> afterPipelines = new HashSet<>();
	
	/**
	 * Notification settings for this schedule.
	 */
	@OneToOne(mappedBy = "schedule")
	private NotificationRecord notification;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private User owner;

	/**
	 * Empty constructor. Used by JPA. Do not use otherwise.
	 */
	public Schedule() {
	}

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

	public Integer getPeriod() {
		return period;
	}

	public void setPeriod(Integer period) {
		this.period = period;
	}

	public PeriodUnit getPeriodUnit() {
		return periodUnit;
	}

	public void setPeriodUnit(PeriodUnit periodUnit) {
		this.periodUnit = periodUnit;
	}

	/**
	 * Schedules this job after every run of given pipeline.
	 *
	 * @param pipeline to run this job after
	 * @return <code>true</code> if this set did not already contain the
	 *         specified element
	 */
	public boolean addAfterPipeline(Pipeline pipeline) {
		return afterPipelines.add(pipeline);
	}

	public Set<Pipeline> getAfterPipelines() {
		return new HashSet<>(afterPipelines);
	}

	public void setAfterPipelines(Set<Pipeline> afterPipelines) {
		this.afterPipelines = afterPipelines;
	}

	public Long getId() {
		return id;
	}

	public NotificationRecord getNotification() {
		return notification;
	}

	public void setNotification(NotificationRecord notification) {
		this.notification = notification;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	/**
	 * Return estimate of time when the schedule fire. If the schedule is not
	 * time dependent return null.
	 *
	 * @return Estimate of time for next execution or null.
	 */
	public Date getNextExecutionTimeInfo() {
		if (type == ScheduleType.AFTER_PIPELINE) {
			return null;
		} else { // ScheduleType.PERIODICALLY
			if (lastExecution == null) {
				// no execution so far .. so we run in time of first execution
				return firstExecution;
			} else {
				Calendar calendarNextExec = Calendar.getInstance();
				calendarNextExec.setTime(lastExecution);
				// add value based on period
				switch (periodUnit) {
					case DAY:
						calendarNextExec.add(Calendar.DAY_OF_YEAR, period);
						break;
					case HOUR:
						calendarNextExec.add(Calendar.HOUR_OF_DAY, period);
						break;
					case MINUTE:
						calendarNextExec.add(Calendar.MINUTE, period);
						break;
					case MONTH:
						calendarNextExec.add(Calendar.MONTH, period);
						break;
					case SECOND:
						calendarNextExec.add(Calendar.SECOND, period);
						break;
					case WEEK:
						calendarNextExec.add(Calendar.WEEK_OF_YEAR, period);
						break;
					case YEAR:
						calendarNextExec.add(Calendar.YEAR, period);
						break;
				}
				return calendarNextExec.getTime();
			}
		}

	}
}
