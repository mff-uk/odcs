package cz.cuni.mff.xrg.odcs.commons.app.user;

import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;

/**
 * Represent settings for scheduler notification.
 *
 * @author Maria Kukhar
 */
@MappedSuperclass
public abstract class NotificationRecord implements DataObject {

    /**
     * Unique ID for each scheduler notification.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
        this.typeError = typeError;
    }

    /**
     * Returns true if two objects represent the same pipeline. This holds if
     * and only if <code>this.id == null ? this == obj : this.id == o.id</code>.
     *
     * @param obj
     * @return true if both objects represent the same pipeline
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final DataObject other = (DataObject) obj;
        if (this.getId() == null) {
            return super.equals(other);
        }

        return Objects.equals(this.getId(), other.getId());
    }

    /**
     * Hashcode is compatible with {@link #equals(java.lang.Object)}.
     *
     * @return The value of hashcode.
     */
    @Override
    public int hashCode() {
        if (this.getId() == null) {
            return super.hashCode();
        }
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.getId());
        return hash;
    }

}
