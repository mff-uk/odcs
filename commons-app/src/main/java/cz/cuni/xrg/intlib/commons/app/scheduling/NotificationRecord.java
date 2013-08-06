package cz.cuni.xrg.intlib.commons.app.scheduling;

import javax.persistence.*;

import java.io.Serializable;

import java.util.HashSet;
import java.util.Set;

/**
 * Represent settings for scheduler notification. 
 *
 * @author Maria Kukhar
 *
 */
@Entity
@Table(name = "sch_notification")
public class NotificationRecord implements Serializable {

	/**
	 * Unique ID for each scheduler notification.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;


	/**
	 * Scheduler to notify (notification about paticular scheduled 
	 * pipeline execution). Applicable if notification rule set in 
	 * Scheduler dialog 
	 */
	@OneToOne
	@JoinColumn(name = "schedule_id", nullable = false)
	private Schedule schedule;
	

	/**
	 * E-mails the notification will be sent to.
	 */
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "sch_notification_email",
			joinColumns = @JoinColumn(name = "notification_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "email_id", referencedColumnName = "id"))
	private Set<EmailAddress> emails = new HashSet<>();

	
	/**
	 * Type of notification in case of successful execution:
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "type_success")
	private NotificationRecordType typeSuccess;
	
	/**
	 * Type of notification in case of error execution:
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "type_error")
	private NotificationRecordType typeError;

	
	/**
	 * Empty constructor. Used by JPA. Do not use otherwise.
	 */
	public NotificationRecord() {
	}


	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}
	
	/**
	 * @return defensive copy of a set of emails to send notification to
	 */
	public Set<EmailAddress> getEmails() {
		return new HashSet<>(emails);
	}

	public void setEmails(Set<EmailAddress> emails) {
		this.emails = new HashSet<>(emails);
	}
	
	public void addEmail(EmailAddress email) {
		this.emails.add(email);
	}
	
	public void removeEmail(EmailAddress email) {
		this.emails.remove(email);
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

	public Long getId() {
		return id;
	}


}
