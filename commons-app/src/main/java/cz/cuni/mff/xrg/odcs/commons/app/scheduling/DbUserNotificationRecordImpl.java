package cz.cuni.mff.xrg.odcs.commons.app.scheduling;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;

/**
 * Implementation providing access to {@link UserNotificationRecord} data objects.
 *
 * @author Jan Vojt
 */
public class DbUserNotificationRecordImpl extends DbAccessBase<UserNotificationRecord>
											implements DbUserNotification {

	public DbUserNotificationRecordImpl() {
		super(UserNotificationRecord.class);
	}

}
