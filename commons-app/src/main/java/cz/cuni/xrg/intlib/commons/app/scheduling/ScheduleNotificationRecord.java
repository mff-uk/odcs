package cz.cuni.xrg.intlib.commons.app.scheduling;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * Notification settings for a single pipeline execution schedule.
 *
 * @author Jan Vojt
 */
@Entity
@Table(name = "sch_sch_notification")
public class ScheduleNotificationRecord extends NotificationRecord {

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
	@JoinTable(name = "sch_sch_notification_email",
			joinColumns = @JoinColumn(name = "notification_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "email_id", referencedColumnName = "id"))
	private Set<EmailAddress> emails = new HashSet<>();

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}
	
	/**
	 * @return defensive copy of a set of emails to send notification to
	 */
	@Override
	public Set<EmailAddress> getEmails() {
		return new HashSet<>(emails);
	}

	@Override
	public void setEmails(Set<EmailAddress> emails) {
		this.emails = new HashSet<>(emails);
	}
	
	@Override
	public void addEmail(EmailAddress email) {
		this.emails.add(email);
	}
	
	@Override
	public void removeEmail(EmailAddress email) {
		this.emails.remove(email);
	}

}
