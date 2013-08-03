package cz.cuni.xrg.intlib.commons.app.communication;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EmailSender {

	private static Logger LOG = LoggerFactory.getLogger(EmailSender.class);

	/**
	 * From email address.
	 */
	private String fromEmail;

	/**
	 * From name.
	 */
	private String fromName;

	/**
	 * Use authentication?
	 */
	private Boolean authentication;

	/**
	 * User name for authentication.
	 */
	private String user;

	/**
	 * User password for authentication.
	 */
	private String password;

	/**
	 * Create email sender without authentication.
	 * 
	 * @param from
	 */
	public EmailSender(String from) {
		this.fromEmail = from;
		this.fromName = "";
		this.authentication = false;
		this.user = null;
		this.password = null;
	}

	/**
	 * Create email sender.
	 * @param fromEmail
	 * @param fromName
	 * @param authentication
	 * @param user
	 * @param password
	 */
	public EmailSender(String fromEmail,
			String fromName,
			boolean authentication,
			String user,
			String password) {
		this.fromEmail = fromEmail;
		this.fromName = fromName;
		this.authentication = authentication;
		this.user = user;
		this.password = password;
	}

	/**
	 * Send email with html content
	 * 
	 * @param fromEmail
	 * @param fromName
	 * @param subject
	 * @param body
	 * @param emails
	 * @return
	 */
	public boolean send(String subject, String body, List<String> emails) {
		Properties props = new Properties();
		// authentication ?
		if (authentication) {
			props.setProperty("mail.user", user);
			props.setProperty("mail.password", password);
		}

		Session session = Session.getDefaultInstance(props, null);
		// create message
		Message msg = new MimeMessage(session);
		try {
			msg.setFrom(new InternetAddress(fromEmail, fromName));
			for (String email : emails) {
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
						email));
			}
			msg.setSubject(subject);
			msg.setContent(body, "text/html");
			Transport.send(msg);
		} catch (UnsupportedEncodingException | MessagingException e) {
			LOG.error("Failed to send email.", e);
			return false;
		}
		return true;
	}

}
