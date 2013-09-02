package cz.cuni.xrg.intlib.commons.app.scheduling;

import javax.persistence.*;

import java.io.Serializable;

import java.util.Set;

/**
 * Represent settings for scheduler notification. 
 *
 * @author Maria Kukhar
 *
 */
@MappedSuperclass
public abstract class NotificationRecord implements Serializable {

	/**
	 * Unique ID for each scheduler notification.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
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

	public Long getId() {
		return id;
	}


}
