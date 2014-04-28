package cz.cuni.mff.xrg.odcs.backend.monitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import cz.cuni.mff.xrg.odcs.backend.data.DataUnitFactory;
import cz.cuni.mff.xrg.odcs.commons.app.communication.EmailSender;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.conf.MissingConfigPropertyException;
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.virtuoso.VirtuosoRDFDataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;

/**
 * Component for monitoring rdf storage availability.
 *
 * @author Petyr
 */
@Component
class RdfDatabase {

	private static final Logger LOG = LoggerFactory.getLogger(RdfDatabase.class);

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private EmailSender emailSender;

	/**
	 * When does the last check starts.
	 */
	private Date queryStart = null;

	/**
	 * When does the last check ends.
	 */
	private Date queryEnd = null;

	/**
	 * True if the rdf is inaccessible. And the inaccessible action has been
	 * done.
	 */
	private boolean rdfInaccessible = false;

	/**
	 * True if perform the check.
	 */
	private boolean doCheck;

	@PostConstruct
	public void onStart() {
		LOG.trace("onStart");

		// find out if check
		doCheck = doCheck();

		if (doCheck) {
			LOG.info("The rdf database checked is up and running ...");
		} else {
			// we are using local repository
			LOG.info("The rdf database will not be checking ...");
		}
	}

	/**
	 * Try to connect to rdf repository and as a simple query. The execution of
	 * this method should least a few seconds.
	 */
	@Scheduled(fixedDelay = 4 * 1000 ) // * 60 * 1000)
	public void executeQuery() {
		if (!doCheck) {
			return;
		}

		AppConfig rdfConfig;
		try {
			rdfConfig = appConfig.getSubConfiguration(ConfigProperty.RDF);
		} catch (RuntimeException ex) {
			doCheck = false;
			LOG.error("Failed to get configuration group for rdf.inaccessible. "
					+ "The periodical check has been stoped.", ex);
			// finish with sucess
			queryEnd = new Date();
			return;
		}

		// set times
		queryStart = new Date();
		queryEnd = null;

		LOG.trace("executeQuery:start");

		// load configuration from appConfig
		String hostName;
		String port;
		String user;
		String password;

		try {
			hostName = rdfConfig.getString(ConfigProperty.DATABASE_HOSTNAME);
			port = rdfConfig.getString(ConfigProperty.DATABASE_PORT);
			user = rdfConfig.getString(ConfigProperty.DATABASE_USER);
			password = rdfConfig.getString(ConfigProperty.DATABASE_PASSWORD);
		} catch (RuntimeException ex) {
			doCheck = false;
			LOG.error("Failed to get configuration. "
					+ "The periodical check has been stoped.", ex);
			// finish with sucess
			queryEnd = new Date();
			return;
		}
		try {
			Thread.sleep(1000 * 120);
		} catch (InterruptedException ex) {
			
		}
		
		VirtuosoRDFDataUnit virtuosoRepository = (VirtuosoRDFDataUnit) (new DataUnitFactory()).create(DataUnitType.RDF, "reallyWeirdNametoAvoidNameClash", "monitoringOfVirtuoso", null);
		try {
			// ok we have the repository
			virtuosoRepository.executeSelectQuery("select * where {?s ?p ?o} limit 1");
		} catch (InvalidQueryException ex) {
			// this should not happen
			LOG.error("Failed to execute check query.", ex);
		}
		
		// close the connection
		virtuosoRepository.release();
		
		LOG.trace("executeQuery:ends");

		queryEnd = new Date();
	}

	@Scheduled(fixedDelay = 1 * 60 * 1000)
	public void check() {
		if (!doCheck) {
			return;
		}

		LOG.trace("check: start");

		if (queryStart == null) {
			// no check has been done yet ..
			LOG.trace("check: no check done yet");
		} else if (queryEnd == null) {
			Long diff = (new Date()).getTime() - queryStart.getTime();
			if (diff < 20000) {
				// we wait for a little bit longer
				LOG.trace("We have queryStart != null and queryEnd == null."
						+ "But it's less then 20 second from thet start of the check.");
				LOG.trace("check: ends");
				return;
			}

			// we are waiting for check to finish .. for more then 20 second
			// for sure, by this time it should be finished
			// is this the first time ?
			if (rdfInaccessible) {
				// the action has already been done
				LOG.info("\"rdf database is still down ..");
				return;
			} else {
				LOG.info("rdf database is down .. performing action");
			}
			rdfInaccessible = true;

			// perform action
			inaccessibleAction();

		} else {
			// last check has been ok .. 
			rdfInaccessible = false;
			LOG.trace("check: ok");
		}
		// no code here !!
	}

	/**
	 * Return true if the local repository is in use.
	 *
	 * @return
	 */
	private boolean doCheck() {
		String defRdfRepo;
		try {
			defRdfRepo = appConfig.getString(ConfigProperty.BACKEND_DEFAULTRDF);
		} catch (MissingConfigPropertyException e) {
			// no value, .. 
			LOG.warn("No configuration value for BACKEND_DEFAULTRDF, the local repository is probably in use.");
			return false;
		}

		if (defRdfRepo == null) {
			// local is used
			LOG.trace("check: local repo is probably in use as defRdfRepo == null");
			return false;
		} else {
			// choose based on value in appConfig
			if (defRdfRepo.compareToIgnoreCase("virtuoso") == 0) {
				// use virtuoso -> continue
				return true;
			} else {
				// local
				LOG.trace("check: local repo is in use");
				return false;
			}
		}
	}

	/**
	 * Performs sets of action when the rdf database is down.
	 */
	private void inaccessibleAction() {
		LOG.trace("inaccessibleAction: start");
		// get configuration root
		AppConfig actionConfig;
		try {
			actionConfig = appConfig.getSubConfiguration(ConfigProperty.RDF).
					getSubConfiguration(ConfigProperty.DATABASE_INACCESSIBLE);
		} catch (RuntimeException ex) {
			// no configuration, just send email
			sendEmail("No action has been perform as we failed to get inaccessible "
					+ "subconfiguration from config.properties.", ex);
			return;
		}

		if (actionConfig.getProperties().isEmpty()) {
			// no user action is configured
			sendEmail("No action has been perform as no action is set in "
					+ "config.properties.");
			return;
		}

		String cmd = null;
		String path = null;
		try {
			cmd = actionConfig.getString(ConfigProperty.INACCESSIBLE_CMD);
			path = actionConfig.getString(ConfigProperty.INACCESSIBLE_PATH);
		} catch (MissingConfigPropertyException ex) {
			// some configuration is missing
			sendEmail("No action has been perform as the action is not "
					+ "completely configured in config.properties.", ex);

			LOG.error("Missing configuration for inaccessible action.", ex);
		}

		LOG.trace("inaccessibleAction: executing srcipt");
		// execute script
		List<String> command = new ArrayList<>();
		command.add(cmd);

		//Process process = new ProcessBuilder("C:\\PathToExe\\MyExe.exe","param1","param2").start();
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.directory(new File(path));
		final Process process;
		try {
			process = builder.start();
			process.waitFor();
			// send email with summary
			sendEmail(process);
		} catch (IOException ex) {
			sendEmail("The application failed to perform user action because of"
					+ " exception.", ex);

			LOG.error("Failed to execute inaccessible action.", ex);
		} catch (InterruptedException ex) {
			sendEmail("The application try to perform the user defined action,"
					+ "but has been interupter during waiting on it's ends.");
		}

		LOG.trace("inaccessibleAction: done");
	}

	/**
	 * Send log's with report about the executed process.
	 *
	 * @param process
	 */
	private void sendEmail(Process process) {
		LOG.trace("sendEmail: preparing email with process content.");

		StringBuilder messageBuilder = new StringBuilder();

		messageBuilder.append("The user command has been executed.</br>");
		messageBuilder.append("exit value:");
		messageBuilder.append(process.exitValue());
		messageBuilder.append("</br>");

		LOG.trace("sendEmail: preparing process output stream.");

		// add output and error
		final BufferedReader stdInput
				= new BufferedReader(new InputStreamReader(process.getInputStream()));
		final String stdContent = streamToString(stdInput);
		if (stdContent.isEmpty()) {
			// nothing to add
		} else {
			messageBuilder.append("process std output: </br>");
			messageBuilder.append("<pre><code>");
			messageBuilder.append(stdContent);
			messageBuilder.append("</code></pre>");
		}

		LOG.trace("sendEmail: preparing process error stream.");

		final BufferedReader stdError
				= new BufferedReader(new InputStreamReader(process.getErrorStream()));
		final String errContent = streamToString(stdError);
		if (errContent.isEmpty()) {
			// nothing to add
		} else {
			messageBuilder.append("process error output: </br>");
			messageBuilder.append("<pre><code>");
			messageBuilder.append(errContent);
			messageBuilder.append("</code></pre>");
		}

		// send email
		sendEmail(messageBuilder.toString());
	}

	/**
	 * Format data from stream into single string. The line endings are replaced
	 * with </br>.
	 *
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	private String streamToString(BufferedReader reader) {
		StringBuilder streamBuilder = new StringBuilder();

		String line;
		try {
			while ((line = reader.readLine()) != null) {
				// we need to escape dangerous characters
				streamBuilder.append(line);
				streamBuilder.append('\n');
			}
		} catch (IOException ex) {
			LOG.error("Failed to read the stream.");
			// return emtpy string
			return "";
		}
		return streamBuilder.toString();
	}

	/**
	 * Send email with given message and the end of the message add the stack
	 * trace.
	 *
	 * @param message
	 * @param ex
	 */
	private void sendEmail(String message, Throwable ex) {
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append(message);
		messageBuilder.append("</br></br>Exception:</br>");
		// transform stack trace into string
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		// append to the message
		messageBuilder.append("<pre><code>");
		messageBuilder.append(sw.toString());
		messageBuilder.append("</code></pre>");

		sendEmail(messageBuilder.toString());
	}

	/**
	 * Send email with information that the rdf database is down and given
	 * message.
	 *
	 * @param message
	 */
	private void sendEmail(String message) {
		LOG.info("Sending email.");

		StringBuilder bodyBuilder = new StringBuilder();
		bodyBuilder.append("RDF database is not responding. </br>");
		bodyBuilder.append("time: ");
		bodyBuilder.append(new Date());
		bodyBuilder.append("</br>");
		bodyBuilder.append(message);

		// send message
		final String subject = "ODCS - rdf database inaccessible";
		String recipient;
		try {
			recipient = appConfig.getString(ConfigProperty.EMAIL_ADMIN);
		} catch (MissingConfigPropertyException e) {
			LOG.error("Missing admin's email adress. Can't send email.");
			return;
		}
		emailSender.send(subject, bodyBuilder.toString(), recipient);
	}

}
