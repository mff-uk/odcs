package cz.cuni.mff.xrg.odcs.backend.report;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import cz.cuni.mff.xrg.odcs.backend.i18n.Messages;
import cz.cuni.mff.xrg.odcs.backend.pipeline.event.PipelineFinished;
import cz.cuni.mff.xrg.odcs.commons.app.communication.EmailSender;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.ScheduleNotificationRecord;
import cz.cuni.mff.xrg.odcs.commons.app.user.EmailAddress;
import cz.cuni.mff.xrg.odcs.commons.app.user.NotificationRecord;

/**
 * Listen to the application event and if need send email about the event to the
 * user.
 * 
 * @author Petyr
 */
@Component
class InstantReport implements ApplicationListener<ApplicationEvent> {

    private static final Logger LOG = LoggerFactory
            .getLogger(InstantReport.class);

    /**
     * Provide functionality to send email.
     */
    @Autowired
    private EmailSender emailSender;

    @Autowired
    private InstantReportEmailBuilder emailBuilder;

    /**
     * Add email based on {@link ScheduleNotificationRecord}.
     * 
     * @param emails
     * @param execution
     * @param notification
     *            Notification settings.
     * @param emailsToAdd
     */
    private void add(Set<String> emails,
            PipelineExecution execution,
            NotificationRecord notification,
            Set<EmailAddress> emailsToAdd) {
        switch (execution.getStatus()) {
            case FINISHED_SUCCESS:
            case FINISHED_WARNING:
                switch (notification.getTypeSuccess()) {
                    case INSTANT: // add email
                        for (EmailAddress email : emailsToAdd) {
                            emails.add(email.toString());
                        }
                        return;
                    case DAILY:
                        // we do not send now ... will be send in daily report
                    case NO_REPORT:
                        return;
                }
                break;
            case FAILED:
            case CANCELLED:
                switch (notification.getTypeError()) {
                    case INSTANT: // add email
                        for (EmailAddress email : emailsToAdd) {
                            emails.add(email.toString());
                        }
                        return;
                    case DAILY:
                        // we do not send now ... will be send in daily report
                    case NO_REPORT:
                        return;
                }
                break;
            default:
                LOG.warn("Unexpected execution status '{}' ", execution.getStatus());
                return;
        }
    }

    /**
     * Prepare subject for email that inform about single execution.
     * 
     * @param execution
     * @param schedule
     * @return
     */
    private String subjectInstant(PipelineExecution execution, Schedule schedule) {
        return Messages.getString("InstantReport.execution.report", execution.getPipeline().getName(), execution.getStart().toString());
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof PipelineFinished) {
            PipelineFinished pipelineFinishedEvent = (PipelineFinished) event;
            PipelineExecution execution = pipelineFinishedEvent.getExecution();
            Schedule schedule = execution.getSchedule();
            // pipeline finished has been scheduled?
            if (schedule == null) {
                // we ignore non scheduled executions
            } else {
                // create list with recipients
                Set<String> emails = new HashSet<>();

                // use Schedule notification if available
                // otherwise use setting from user
                if (schedule.getNotification() == null) {
                    if (schedule.getOwner() == null) {
                        // there is no owner to use to send email .. 
                        LOG.warn("Missing owner for schedule id: {}",
                                schedule.getId()
                                );
                        return;
                    }

                    if (schedule.getOwner().getNotification() == null) {
                        // there is no rule to use to send email .. 
                        LOG.warn("Missing notificaiton rule for schedule id: {}",
                                schedule.getId());
                        return;
                    }

                    add(emails, execution, schedule.getOwner().getNotification(),
                            schedule.getOwner().getNotification().getEmails());
                } else {
                    LOG.debug("Using schedule's settings for email");
                    add(emails, execution, schedule.getNotification(),
                            schedule.getNotification().getEmails());
                }

                if (emails.isEmpty()) {
                    // no one to send the email
                    LOG.debug("There is no addres to which send email for schedule id: {}",
                            schedule.getId());
                    return;
                }

                final String subject = subjectInstant(execution, schedule);
                final String body = emailBuilder.build(execution, schedule);
                // create list of recipients
                ArrayList<String> recipients = new ArrayList<>();
                recipients.addAll(emails);

                for (String email : emails) {
                    LOG.debug("Sending email for schedule {} "
                            , email);
                }

                emailSender.send(subject, body, recipients);
            }
        }
    }

}
