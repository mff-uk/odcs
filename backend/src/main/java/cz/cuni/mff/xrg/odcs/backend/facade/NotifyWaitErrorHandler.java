package cz.cuni.mff.xrg.odcs.backend.facade;

import cz.cuni.mff.xrg.odcs.commons.app.communication.EmailSender;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.conf.MissingConfigPropertyException;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Handler notifying administrator by email after first failure and waiting for
 * configured period of time before thread is released for retrial.
 * 
 * <p>
 * TODO create global counter for attempts
 *
 * @author Jan Vojt
 */
public class NotifyWaitErrorHandler implements ErrorHandler {

	private static final Logger LOG = LoggerFactory.getLogger(NotifyWaitErrorHandler.class);
	
	/**
	 * Configuration with number of retries and timeouts.
	 */
	@Autowired(required = false)
	private AppConfig config;
	
	/**
	 * Service providing notification email sending functionality.
	 */
	@Autowired(required = false)
	private EmailSender emailSender;
	
	/**
	 * Number of retries before giving up on reconnecting with db. Zero means
	 * immediate failure, -1 means never give up and keep trying.
	 */
	private int retries = -1;
	
	/**
	 * Number of milliseconds to wait before next retrial.
	 */
	private int wait = 2000;
	
	/**
	 * Number of email notifications about failures already sent.
	 */
	private static int emailsSent = 0;
	
	/**
	 * Configures behavior in case of database outages.
	 */
	@PostConstruct
	public void reconfigure() {
		if (config == null) {
			LOG.info("No configoration for database reconnects given, using defaults.");
			return;
		}
		try {
			retries = config.getInteger(ConfigProperty.VIRTUOSO_RETRIES);
		} catch (MissingConfigPropertyException ex) {
			LOG.info("Missing config property {}, using default value {}.", ex.getProperty(), retries);
		}
		try {
			wait = config.getInteger(ConfigProperty.VIRTUOSO_WAIT);
		} catch (MissingConfigPropertyException ex) {
			LOG.info("Missing config property {}, using default value {}.", ex.getProperty(), wait);
		}
	}
	
	@Override
	public <E extends RuntimeException> void handle(int attempt, E ex) throws E {

		LOG.warn("Database is down after {} attempts.", attempt);
		if (attempt == 1) {
			// send notification after first error only
			notify(ex);
		}
		
		boolean loop = true;
		if (retries >= 0) {
			loop = attempt <= retries;
		}
		if (loop && wait > 0) {
			try {
				Thread.sleep(wait);
			} catch (InterruptedException e) {
				LOG.error("Thread interrupted while sleeping.", e);
			}
		} else if (!loop) {
			LOG.error("Giving up on database after {} retries.", attempt);
			throw ex;
		}
	}
	
	/**
	 * Sends email notification to administrator about DB outage.
	 * 
	 * @param ex 
	 */
	private void notify(Exception ex) {
		if (emailSender != null) {
			synchronized (PipelineFacade.class) {
				if (emailsSent <= 0) {
					String subject = "ODCS - RDBMS error";
					String recipient = config.getString(ConfigProperty.EMAIL_ADMIN);
					emailSender.send(subject, ex.toString(), recipient);
					emailsSent++;
				}
			}
		}
	}

}
