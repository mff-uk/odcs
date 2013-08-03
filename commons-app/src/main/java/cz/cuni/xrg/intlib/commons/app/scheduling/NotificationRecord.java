package cz.cuni.xrg.intlib.commons.app.scheduling;

import java.util.Date;

import javax.persistence.*;

import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import java.io.Serializable;

import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Represent settings for scheduler notification. 
 *
 * @author Maria Kukhar
 *
 */

@Table(name = "sch_notification")
public class NotificationRecord implements Serializable {

	/**
	 * Unique ID for each scheduler notification.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Transient
	private Long id;


	/**
	 * Scheduler to notify (notification about paticular scheduled 
	 * pipeline execution). Applicable if notification rule set in 
	 * Scheduler dialog 
	 */
	
	@OneToOne
	@JoinColumn(name = "scheduler_id", nullable = false)
	@Transient
	private Schedule scheduler;
	

	/**
	 * E-mails to wich will be sent notification.
	 */
	@Column(name = "emails")
	@Transient
	private List<String> emails = new LinkedList<>();


	/**
	 * Type of notification in case of successful execution:
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "type_success")
	@Transient
	private NotificationRecordType typeSuccess;
	
	/**
	 * Type of notification in case of error execution:
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "type_error")
	@Transient
	private NotificationRecordType typeError;

	

	/**
	 * All schedulers to notify. Applicable only if notification rule set in 
	 * User settings dialog.
	 */
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "all_sch_notification",
			joinColumns =
			@JoinColumn(name = "notification_id", referencedColumnName = "id"),
			inverseJoinColumns =
			@JoinColumn(name = "schedule_id", referencedColumnName = "id"))
	@Transient
	private Set<Schedule> allShedulers = new HashSet<>();

	/**
	 * Empty constructor. Used by JPA. Do not use otherwise.
	 */
	public NotificationRecord() {
	}


	public Schedule getSchedule() {
		return scheduler;
	}

	public void setSchedule(Schedule scheduler) {
		this.scheduler = scheduler;
	}
	
	
	public List<String> getEmails() {
		return new LinkedList<>(emails);
	}

	public void setEmails(List<String> emails) {
		this.emails = emails;
	}
	

	public NotificationRecordType getTypeSuccess() {
		return typeSuccess;
	}

	public void setTypeSuccess(NotificationRecordType typeSuccess) {
		this.typeSuccess = typeSuccess;
	}
	
	public NotificationRecordType getTypeError() {
		return typeError;
	}

	public void setTypeError(NotificationRecordType typeError) {
		this.typeError= typeError;
	}


	public Set<Schedule> getAllShedulers() {
		return new HashSet<>(allShedulers);
	}

	public void setAllShedulers(Set<Schedule> allShedulers) {
		this.allShedulers = allShedulers;
	}

	public Long getId() {
		return id;
	}


}
