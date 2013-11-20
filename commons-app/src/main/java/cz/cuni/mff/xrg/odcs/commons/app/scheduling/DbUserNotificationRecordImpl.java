package cz.cuni.mff.xrg.odcs.commons.app.scheduling;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;
import cz.cuni.mff.xrg.odcs.commons.app.user.UserNotificationRecord;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation providing access to {@link UserNotificationRecord} data objects.
 *
 * @author Jan Vojt
 */
@Transactional(propagation = Propagation.MANDATORY)
public class DbUserNotificationRecordImpl extends DbAccessBase<UserNotificationRecord>
											implements DbUserNotification {

	public DbUserNotificationRecordImpl() {
		super(UserNotificationRecord.class);
	}

}
