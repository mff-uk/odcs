package cz.cuni.mff.xrg.odcs.commons.app.scheduling;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import javax.persistence.*;

import java.util.Set;

/**
 * Represent settings for scheduler notification. 
 *
 * @author Maria Kukhar
 *
 */
@MappedSuperclass
public abstract class NotificationRecord implements DataObject {

	/**
	 * Unique ID for each scheduler notification.
	 */
	@Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_sch_notification")
	@SequenceGenerator(name = "seq_sch_notification", allocationSize = 1)
	private Long id;
	
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

	@Override
	public Long getId() {
		return id;
	}
	
	/**
	 * @return defensive copy of a set of emails to send notification to
	 */
	public abstract Set<EmailAddress> getEmails();

	public abstract void setEmails(Set<EmailAddress> emails);
	
	public abstract void addEmail(EmailAddress email);
	
	public abstract void removeEmail(EmailAddress email);

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

}
