package cz.cuni.mff.xrg.odcs.backend.report;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cz.cuni.mff.xrg.odcs.backend.i18n.Messages;
import cz.cuni.mff.xrg.odcs.commons.app.communication.EmailSender;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbQueryBuilder;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.filter.Compare;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.DbExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.ScheduleNotificationRecord;
import cz.cuni.mff.xrg.odcs.commons.app.user.EmailAddress;
import cz.cuni.mff.xrg.odcs.commons.app.user.NotificationRecord;
import cz.cuni.mff.xrg.odcs.commons.app.user.NotificationRecordType;
import cz.cuni.mff.xrg.odcs.commons.app.user.UserNotificationRecord;

/**
 * Component responsible for generating and sending daily reports.
 * 
 * @author Petyr
 */
@Component
class DailyReport {

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private DailyReportEmailBuilder emailBuilder;

    @Autowired
    private DbExecution dbExecution;

    /**
     * Get executions for lest 24 hours and based on the notifications settings
     * send daily reports.
     * Spring will run this at every midnight.
     */
    @Async
    @Scheduled(cron = "0 0 0 * * *")
    public void execute() {
        // today    
        Calendar date = Calendar.getInstance();
        // reset hour, minutes, seconds and millis
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        Date end = date.getTime();
        // previous day
        date.add(Calendar.DAY_OF_MONTH, -1);
        Date start = date.getTime();

        // list of executions
        List<PipelineExecution> executions = getExecutions(start, end);
        // store emails to send on
        Map<EmailAddress, List<PipelineExecution>> toSend = new HashMap<>();

        final String subject = Messages.getString("DailyReport.report");

        for (PipelineExecution exec : executions) {
            Set<EmailAddress> recipients = getRecipients(exec);
            if (recipients == null || recipients.isEmpty()) {
                // skip
            } else {
                // add to toSend list
                for (EmailAddress email : recipients) {
                    if (toSend.containsKey(email)) {
                        // ok, record exist .. 
                    } else {
                        // create new list
                        toSend.put(email, new LinkedList<PipelineExecution>());
                    }
                    // add to the list
                    toSend.get(email).add(exec);
                }
            }
        }

        // in toSend we hava data to send, so .. let's ge to the work
        for (EmailAddress email : toSend.keySet()) {
            String body = emailBuilder.build(toSend.get(email));
            // send email
            emailSender.send(subject, body, email.toString());
        }
    }

    /**
     * Return execution that has been finished in (date, data + 24h).
     * 
     * @param start
     * @param end
     * @return
     */
    private List<PipelineExecution> getExecutions(Date start, Date end) {
        // we need only executions that has been scheduled
        DbQueryBuilder<PipelineExecution> builder = dbExecution.createQueryBuilder();
        builder.addFilter(Compare.greaterEqual("end", start));
        builder.addFilter(Compare.less("end", end));
        // end get queries
        return dbExecution.executeList(builder.getQuery());
    }

    /**
     * Return list of email on which send information about this execution. The
     * list can be empty or null if the email should not be sent.
     * 
     * @param execution
     * @return
     */
    private Set<EmailAddress> getRecipients(PipelineExecution execution) {
        // just for sure check, that it has been sheduled
        if (execution.getSchedule() == null) {
            return null;
        }

        // try schedule specific notificaiton settings
        ScheduleNotificationRecord scheduleNotification = execution.getSchedule().getNotification();
        if (scheduleNotification != null) {
            // use schedule notification
            if (report(scheduleNotification, execution.getStatus())) {
                return scheduleNotification.getEmails();
            }
        }

        if (execution.getOwner() == null) {
            // no owner, we can't use his settings
            return null;
        }

        UserNotificationRecord userNotification = execution.getOwner().getNotification();
        if (userNotification != null) {
            if (report(userNotification, execution.getStatus())) {
                return userNotification.getEmails();
            }
        }

        return null;
    }

    /**
     * Return true if for given execution status and given notification settings
     * the daily email should be send.
     * 
     * @param notification
     * @param status
     * @return
     */
    private boolean report(NotificationRecord notification,
            PipelineExecutionStatus status) {
        switch (status) {
            case CANCELLED:
                return false;
            case CANCELLING:
                return false;
            case FAILED:
                // return true if use daily report
                return notification.getTypeError() == NotificationRecordType.DAILY;
            case FINISHED_SUCCESS:
            case FINISHED_WARNING:
                // return trye if use daily report
                return notification.getTypeSuccess() == NotificationRecordType.DAILY;
            case QUEUED:
                return false;
            case RUNNING:
                return false;
            default:
                return false;
        }
    }

}
