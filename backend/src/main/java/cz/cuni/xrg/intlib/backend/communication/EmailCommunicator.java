package cz.cuni.xrg.intlib.backend.communication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import cz.cuni.xrg.intlib.backend.pipeline.event.PipelineFinished;
import cz.cuni.xrg.intlib.commons.app.communication.EmailSender;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.xrg.intlib.commons.app.scheduling.EmailAddress;
import cz.cuni.xrg.intlib.commons.app.scheduling.NotificationRecordType;
import cz.cuni.xrg.intlib.commons.app.scheduling.Schedule;
import cz.cuni.xrg.intlib.commons.app.scheduling.ScheduleNotificationRecord;

/**
 * Listen to the application event and if need send email about the event 
 * to the user.
 * 
 * @author Petyr
 *
 */
public class EmailCommunicator implements ApplicationListener<ApplicationEvent>{

	private static final Logger LOG = LoggerFactory.getLogger(EmailCommunicator.class);
	
	/**
	 * Provide functionality to send email.
	 */
	@Autowired
	private EmailSender emailSender;
	
	/**
	 * Add email based on settings from {@link Schedule}. 
	 * @param emails
	 *  @param execution
	 * @param schedule
	 */
	private void add(Set<String> emails, PipelineExecution execution, Schedule schedule) {
		ScheduleNotificationRecord notification = schedule.getNotification();
		
		if (notification == null) {
			return;
		}
		
		if (execution.getStatus() == PipelineExecutionStatus.FAILED) {
			if (notification.getTypeError() == NotificationRecordType.INSTANT) {
				// continue -> send 
			} else {
				// do not send
				return;
			}
		} else {
			if (notification.getTypeSuccess() == NotificationRecordType.INSTANT) {
				// continue -> send 
			} else {
				// do not send
				return;
			}
		}
		
		// add all 
		for (EmailAddress adress : notification.getEmails()) {
			emails.add(adress.toString());
		}
	}
	
	/**
	 * Can add author of the execution based on the author email settings.
	 * @param emails
	 * @param execution
	 */
	private void add(Set<String> emails, PipelineExecution execution) {
		if (execution.getStatus() == PipelineExecutionStatus.FAILED) {
			// we send only for failed state 
			// and if user has certain settings
			
			// TODO Petyr: Send to the author of the execution
		}
	}
	
	/**
	 * Prepare body for email that inform about single execution.
	 * @param execution
	 * @param schedule
	 * @return
	 */
	private String bodyInstant(PipelineExecution execution, Schedule schedule) {
		StringBuilder body = new StringBuilder();
		
		body.append("Report for pipeline: ");
		body.append(execution.getPipeline().getName());
		body.append("<br/>");
		body.append("Execution starts at: ");
		body.append(execution.getStart().toString());
		body.append("<br/>");
		body.append("Execution ends at: ");
		body.append(execution.getEnd().toString());
		body.append("<br/>");
		body.append("Execution result: ");
		body.append(execution.getStatus());
		body.append("<br/>");
		
		return body.toString();
	}
	
	/**
	 * Prepare subject for email that inform about single execution.
	 * @param execution
	 * @param schedule
	 * @return
	 */
	private String subjectInstant(PipelineExecution execution, Schedule schedule) {
		StringBuilder subject = new StringBuilder();
		
		subject.append("Execution report for: ");
		subject.append(execution.getPipeline().getName());
		subject.append(" ");
		subject.append(execution.getStart().toString());
		
		return subject.toString();
	}
	
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof PipelineFinished) {
			PipelineFinished pipelineFinishedEvent = (PipelineFinished) event;
			PipelineExecution execution = pipelineFinishedEvent.getExecution();
			Schedule schedule = execution.getSchedule();
			// pipeline finished has been scheduled?
			if(schedule == null) {
				// we ignore non scheduled executions
			} else {
				// create list with recipients
				Set<String> emails = new HashSet<>();
				add(emails, execution);
				add(emails, execution, schedule);
				final String subject = subjectInstant(execution, schedule);
				final String body = bodyInstant(execution, schedule);
				// create list of recipients
				ArrayList<String> recipients = new ArrayList<String>();
				recipients.addAll(emails);
				emailSender.send(subject, body, recipients);
			}
		}
	}

}
