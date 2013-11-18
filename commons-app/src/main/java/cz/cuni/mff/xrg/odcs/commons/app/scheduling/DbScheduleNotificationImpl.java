
package cz.cuni.mff.xrg.odcs.commons.app.scheduling;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;

/**
 * Implementation providing access to {@link ScheduleNotificationRecord} data objects.
 *
 * @author Jan Vojt
 */
public class DbScheduleNotificationImpl extends DbAccessBase<ScheduleNotificationRecord>
										implements DbScheduleNotification {

	public DbScheduleNotificationImpl() {
		super(ScheduleNotificationRecord.class);
	}

}
