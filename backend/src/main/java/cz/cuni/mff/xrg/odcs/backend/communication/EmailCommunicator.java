package cz.cuni.mff.xrg.odcs.backend.communication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import cz.cuni.mff.xrg.odcs.backend.pipeline.event.PipelineFinished;
import cz.cuni.mff.xrg.odcs.commons.app.communication.EmailSender;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUFacade;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.EmailAddress;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.NotificationRecord;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.ScheduleNotificationRecord;

/**
 * Listen to the application event and if need send email about the event to the
 * user.
 * 
 * @author Petyr
 * 
 */
public class EmailCommunicator implements ApplicationListener<ApplicationEvent> {

	private static final Logger LOG = LoggerFactory
			.getLogger(EmailCommunicator.class);

	/**
	 * Provide functionality to send email.
	 */
	@Autowired
	private EmailSender emailSender;
	
	@Autowired
	private DPUFacade dpuFacade;
	
	/**
	 * Add email based on {@link ScheduleNotificationRecord}.
	 * 
	 * @param emails
	 * @param execution
	 * @param notification Notification settings.
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
	 * Prepare body for email that inform about single execution.
	 * 
	 * @param execution
	 * @param schedule
	 * @return
	 */
	private String bodyInstant(PipelineExecution execution, Schedule schedule) {
		StringBuilder body = new StringBuilder();

		body.append("<b>Report for pipeline: </b>");
		body.append(execution.getPipeline().getName());
		body.append("<br/>");
		body.append("<b>Execution starts at: </b>");
		body.append(execution.getStart().toString());
		body.append("<br/>");
		body.append("<b>Execution ends at: </b>");
		body.append(execution.getEnd().toString());
		body.append("<br/>");
		body.append("<b>Execution result: </b>");
		body.append(execution.getStatus());
		body.append("<br/><br/>");
		// append messages
		final List<MessageRecord> messages = dpuFacade.getAllDPURecords(execution);
		body.append("<b>Published messages:</b> <br/>");
		body.append("<table border=2 cellpadding=2 >");
		body.append("<tr bgcolor=\"#C0C0C0\">");
		body.append("<th>dpu</th><th>time</th><th>type</th><th>short message</th>");
		body.append("</tr>");
		
		for (MessageRecord message : messages) {
			// set color based on type
			switch (message.getType()) {
			case DPU_LOG:
			case DPU_DEBUG:
				body.append("<tr>");
				break;
			case DPU_ERROR:
			case PIPELINE_ERROR:
				body.append("<tr bgcolor=\"#FFE0E0\">");
				break;
			case DPU_INFO:
			case PIPELINE_INFO:
				body.append("<tr bgcolor=\"#E0E0FF\">");
				break;			
			case DPU_WARNING:
				body.append("<tr bgcolor=\"#FFFFA0\">");
				break;
			}
			// name
			body.append("<td>");
			body.append(message.getDpuInstance().getName());
			body.append("</td>");
			// time
			body.append("<td>");
			body.append(message.getTime());
			body.append("</td>");
			// type
			body.append("<td>");
			body.append(message.getType().toString());
			body.append("</td>");
			// short message			
			body.append("<td>");
			body.append(message.getShortMessage());
			body.append("</td>");
			// ...
			body.append("</tr>");
		}
		
		body.append("</table>");
		
		return body.toString();
	}

	/**
	 * Prepare subject for email that inform about single execution.
	 * 
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
						LOG.warn("Missing owner for schedule id: {} name: {}",
								schedule.getId(),
								schedule.getName());
						return;
					}
					
					if (schedule.getOwner().getNotification() == null) {
						// there is no rule to use to send email .. 
						LOG.warn("Missing notificaiton rule for schedule id: {} name: {}",
								schedule.getId(),
								schedule.getName());
						return;
					}
					
					add(emails, execution, schedule.getOwner()
							.getNotification(), schedule.getOwner()
							.getNotification().getEmails());
				} else {
					add(emails, execution, schedule.getNotification(), schedule
							.getNotification().getEmails());
				}

				if (emails.isEmpty()) {
					// no one to send the email
					LOG.info(
							"There is no addres to which send email for schedule {}",
							schedule.getName());
					return;
				}

				LOG.debug("Sending email for schedule {}", schedule.getName());
								
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
