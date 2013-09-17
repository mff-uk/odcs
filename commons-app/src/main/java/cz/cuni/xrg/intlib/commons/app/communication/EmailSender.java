package cz.cuni.xrg.intlib.commons.app.communication;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import cz.cuni.xrg.intlib.commons.app.conf.ConfigProperty;

public class EmailSender {

	private static Logger LOG = LoggerFactory.getLogger(EmailSender.class);

	/**
	 * If false outcoming email are silently dropped.
	 */
	private boolean enabled;

	/**
	 * Address of SMTP server.
	 */
	private String smtpHost;

	/**
	 * Port used by SMTP server.
	 */
	private String smtpPort;

	/**
	 * True if use TTL.
	 */
	private boolean useTTL;

	/**
	 * From email address.
	 */
	private String fromEmail;

	/**
	 * Use authentication?
	 */
	private Boolean authentication;

	/**
	 * User name for authentication.
	 */
	private String username;

	/**
	 * User password for authentication.
	 */
	private String password;

	/**
	 * Create email sender based on application configuration.
	 * 
	 * @param appConfig
	 */
	public EmailSender(AppConfig appConfig) {

		this.enabled = appConfig.getBoolean(ConfigProperty.EMAIL_ENABLED);
		
		if (this.enabled) {
			this.smtpHost = appConfig.getString(ConfigProperty.EMAIL_SMTP_HOST);
			this.smtpPort = appConfig.getString(ConfigProperty.EMAIL_SMTP_PORT);
			this.useTTL = appConfig.getBoolean(ConfigProperty.EMAIL_SMTP_TTL);
			this.fromEmail = appConfig.getString(ConfigProperty.EMAIL_FROM_EMAIL);
			this.authentication = appConfig.getBoolean(ConfigProperty.EMAIL_AUTHORIZATION);

			if (this.authentication) {
				// get data for authentication
				this.username = appConfig.getString(ConfigProperty.EMAIL_USERNAME);
				this.password = appConfig.getString(ConfigProperty.EMAIL_PASSWORD);
			}
		}
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
	public boolean send(String subject, String body, List<String> recipients) {
		
		if (!enabled) {
			return false;
		}
		
		// prepare properties
		Properties props = new Properties();
		if (useTTL) {
			props.put("mail.smtp.starttls.enable", "true");
		}
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", smtpPort);
		// create session based on authentication
		
		props.put("mail.pop3s.ssl.trust","*");
		
		Session session;
		if (authentication) {
			props.put("mail.smtp.auth", "true");
			session = Session.getInstance(props,
					new javax.mail.Authenticator() {
						@Override
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(username,
									password);
						}
					});
		} else {
			props.put("mail.smtp.auth", "false");
			session = Session.getDefaultInstance(props, null);
		}

		// create message
		Message msg = new MimeMessage(session);
		try {
			msg.setFrom(new InternetAddress(fromEmail));
			for (String email : recipients) {
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
						email));
			}
			msg.setSubject(subject);
			msg.setContent(body, "text/html");
			// send message
			Transport.send(msg);
		} catch (MessagingException e) {
			LOG.error("Failed to send email.", e);
			return false;
		}
		return true;
	}
	
	/**
	 * Sends email to a single recipient.
	 * 
	 * @param subject
	 * @param body
	 * @param recipient
	 * @return 
	 */
	public boolean send(String subject, String body, String recipient) {
		return send(subject, body, Arrays.asList(recipient));
	}

}
