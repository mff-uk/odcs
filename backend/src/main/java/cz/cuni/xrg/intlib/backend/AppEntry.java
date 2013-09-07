package cz.cuni.xrg.intlib.backend;

import cz.cuni.xrg.intlib.commons.configuration.AppConfig;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cz.cuni.xrg.intlib.backend.communication.Server;
import cz.cuni.xrg.intlib.backend.execution.event.EngineEvent;
import cz.cuni.xrg.intlib.backend.execution.event.EngineEventType;
import cz.cuni.xrg.intlib.commons.app.communication.CommunicationException;
import cz.cuni.xrg.intlib.commons.configuration.ConfigProperty;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;

/**
 * Backend entry point.
 * 
 * @author Petyr
 *
 */
public class AppEntry {

	/**
	 * Path to the spring configuration file.
	 */
	private final static String SPRING_CONFIG_FILE = "backend-context.xml";
	
	/**
	 * Logger class.
	 */
	private static Logger LOG = LoggerFactory.getLogger(AppEntry.class);

	/**
	 * Path to the configuration file.
	 */
	private String configFileLocation = null;
	
	/**
	 * Spring context.
	 */
	private AbstractApplicationContext context = null;
	
	/**
	 * Application configuration.
	 */
	private AppConfig appConfig = null;
	
	/**
	 * Module facade.
	 */
	private ModuleFacade moduleFacade = null;
			
	/**
	 * Server for network communication.
	 */
	private Server server = null;
	
	/**
	 * Separate thread for network server.
	 */
	private Thread serverThread = null;
	
	/**
	 * Heartbeat class instance.
	 */
	private Heartbeat heartbeat = null;	
	
	/**
	 * Thread for heartbeat.
	 */
	private Thread heartbeatThread = null;
	
	/**
	 * Parse program arguments.
	 * @param args
	 */
	private void parseArgs(String[] args) {
		// define args
		Options options = new Options();
		options.addOption("c", "config", true, "path to the configuration file");
		// parse args
		CommandLineParser parser = new org.apache.commons.cli.BasicParser();
		try {
			CommandLine cmd = parser.parse( options, args);
			// read args ..
			configFileLocation = cmd.getOptionValue("config");
		} catch (ParseException e) {
			LOG.error("Failed to parse program's arguments.");
			LOG.debug("Unexpected exception:" + e.getMessage());
		}
		
		// override default configuration path if it has been provided
		if (configFileLocation != null) {
			AppConfig.confPath = configFileLocation;
		}		
	}

	/**
	 * Initialise spring and load configuration.
	 */
	private void initSpring() {
		// load spring
		context = new ClassPathXmlApplicationContext(SPRING_CONFIG_FILE);
		context.registerShutdownHook();
		// load configuration
		appConfig = context.getBean(AppConfig.class);
	}
		
	/**
	 * Initialise and start network TCP/IP server.
	 * @return False it the TCP/IP server cannot be unidealised.
	 */
	private boolean initNetworkServer() {
		// set TCP/IP server
		LOG.info("Starting TCP/IP server ...");
		server = context.getBean(Server.class);
		try {
			server.init();
		} catch (CommunicationException e) {
			LOG.error("Can't start TCP/IP server");
			LOG.debug("", e);
			return false;
		}
		// start server in another thread
		serverThread = new Thread(server);
		serverThread.start();	
		return true;
	}
	
	/**
	 * Get and start Heartbeat in other thread.
	 */
	private void initHeartbeat() {
		// start heartbeat
		heartbeat = context.getBean(Heartbeat.class);
		heartbeatThread = new Thread(heartbeat);
		heartbeatThread.start();
		LOG.info("Heartbeat is running ... ");		
	}
		
	/**
	 * Main execution method.
	 * @param args
	 */
	private void run(String[] args) {
		// parse args
		parseArgs(args);
		// initialise
		initSpring();		

		// publish event for engine about start of the execution,
		// so backend can recover for unexpected shutdown 
		context.publishEvent(new EngineEvent(EngineEventType.STARTUP, AppEntry.class));
				
		// start server
		if (initNetworkServer()) {
			// continue
		} else {
			// this can be because of another instance is running
			
			// terminate the execution			
			context.close();
			LOG.info("Closing application ...");
			return;
		}
		// start heartbeat
		initHeartbeat();
		
		// print some information ..
		LOG.info("Module's directory: " + appConfig.getString(ConfigProperty.MODULE_PATH));
		LOG.info("Listening on port: " + appConfig.getInteger(ConfigProperty.BACKEND_PORT));
		LOG.info("Running ...");
		
		// infinite loop
		
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);		
		while (true) {
			String line = "";
			try {
				line = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			if (line.compareToIgnoreCase("exit") == 0) {
				break;
			}
		}
		
		LOG.info("Closing TCP/IP server ...");
		server.stop();
		// give TCP/IP server time to close
		try {
			Thread.sleep(2 * Server.TCPIP_TIMEOUT);
		} catch (InterruptedException e) {
		}
		LOG.info("Closing spring context ...");
		heartbeatThread.interrupt();
		context.close();
		LOG.info("Closing application ...");
	}
	
	public static void main(String[] args) {
		AppEntry app = new AppEntry();
		app.run(args);				
	}
	
}
