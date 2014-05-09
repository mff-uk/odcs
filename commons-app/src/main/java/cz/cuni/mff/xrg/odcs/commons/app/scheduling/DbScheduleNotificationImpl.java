package cz.cuni.mff.xrg.odcs.commons.app.scheduling;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation providing access to {@link ScheduleNotificationRecord} data objects.
 * 
 * @author Jan Vojt
 */
@Transactional(propagation = Propagation.MANDATORY)
public class DbScheduleNotificationImpl extends DbAccessBase<ScheduleNotificationRecord>
        implements DbScheduleNotification {

    public DbScheduleNotificationImpl() {
        super(ScheduleNotificationRecord.class);
    }

}
