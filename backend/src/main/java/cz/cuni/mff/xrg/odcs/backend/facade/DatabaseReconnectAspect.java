package cz.cuni.mff.xrg.odcs.backend.facade;

import javax.annotation.PostConstruct;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import cz.cuni.mff.xrg.odcs.commons.app.communication.EmailSender;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.conf.MissingConfigPropertyException;

/**
 * Aspect for automatically reconnection in case of database problems.
 * 
 * The aspect is designed for servicing backend, where we do not want to trash
 * all progress of unfinished pipeline runs just because of a short database
 * outage.
 * 
 * @author Petyr
 * 
 */
@Aspect
class DatabaseReconnectAspect {

	private static final Logger LOG = LoggerFactory
			.getLogger(DatabaseReconnectAspect.class);

	/**
	 * Application configuration.
	 */
	@Autowired(required = false)
	private AppConfig appConfig;

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
	 * True if email for last outage has been send.
	 */
	private boolean emailSent = false;

	@PostConstruct
	public void configure() {
		if (appConfig == null) {
			LOG.info("No configuration for database reconnects given, using defaults.");
			return;
		}
		try {
			retries = appConfig.getInteger(ConfigProperty.VIRTUOSO_RETRIES);
		} catch (MissingConfigPropertyException ex) {
			LOG.info("Missing config property {}, using default value {}.",
					ex.getProperty(), retries);
		}
		try {
			wait = appConfig.getInteger(ConfigProperty.VIRTUOSO_WAIT);
		} catch (MissingConfigPropertyException ex) {
			LOG.info("Missing config property {}, using default value {}.",
					ex.getProperty(), wait);
		}
	}

	/**
	 * Aspect that wrap methods on every facade.
	 * 
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	@Transactional
	@Around("execution(* cz.cuni.mff.xrg.odcs.commons.app.user.UserFacade.*(..)) || "
			+ "execution(* cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineFacade.*(..)) || "
			+ "execution(* cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUFacade.*(..)) || "
			+ "execution(* cz.cuni.mff.xrg.odcs.commons.app.execution.log.LogFacade.*(..)) || "
			+ "execution(* cz.cuni.mff.xrg.odcs.commons.app.scheduling.ScheduleFacade.*(..)) || "
			+ "execution(* cz.cuni.mff.xrg.odcs.commons.app.rdf.namespace.NamespacePrefixFacade.*(..))")
	public Object failureTolerant(ProceedingJoinPoint pjp) throws Throwable {
		// first try
		try {
			return pjp.proceed();
		} catch (RuntimeException ex) {
			LOG.warn("Database is down after 1 attempts.");
			notify(ex);
		}
		
		// now we for sure failed at least once so lets
		// try it again and again .. 
		
		int attempts = 1;
		while (true) {
			try {
				// we count attempts only if we have
				// finite number of tries
				attempts++;
				Object result = pjp.proceed();
				// reset email
				emailSent = false;
				return result;
			} catch (RuntimeException ex) {
				LOG.info("call failed ");
				LOG.warn("Database is down after {} attempts.", attempts);
				if (retries != -1 && attempts > retries) {
					// we have wait for too long
					LOG.error("Giving up on database after {} retries.",
							attempts);
					// and re-throw
					throw ex;
				}
			}

			// wait for some time before next try
			try {
				Thread.sleep(wait);
			} catch (InterruptedException e) {
				LOG.error("Thread interrupted while sleeping.", e);
			}
		}
	}

	/**
	 * Sends email notification to administrator about DB outage.
	 * 
	 * @param ex
	 */
	private synchronized void notify(Exception ex) {
		if (emailSender != null && appConfig != null && !emailSent) {
			final String subject = "ODCS - database error";
			String body = "Database is down with exception: </br>" +
					ex.toString();
			String recipient = appConfig.getString(ConfigProperty.EMAIL_ADMIN);
			emailSender.send(subject, body, recipient);
			// 
			emailSent = true;
		}
	}

}
