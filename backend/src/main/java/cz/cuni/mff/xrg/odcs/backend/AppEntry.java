package cz.cuni.mff.xrg.odcs.backend;

import cz.cuni.mff.xrg.odcs.backend.auxiliaries.AppLock;
import cz.cuni.mff.xrg.odcs.backend.communication.Server;
import cz.cuni.mff.xrg.odcs.backend.execution.event.EngineEvent;
import cz.cuni.mff.xrg.odcs.backend.execution.event.EngineEventType;
import cz.cuni.mff.xrg.odcs.commons.app.communication.CommunicationException;
import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;



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
	 * Server for network communication.
	 */
	private Server server = null;
	
	/**
	 * Separate thread for network server.
	 */
	private Thread serverThread = null;
		
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
			LOG.error("Failed to parse program's arguments.", e);
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
			LOG.error("Can't start TCP/IP server", e);
			return false;
		}
		// start server in another thread
		serverThread = new Thread(server);
		serverThread.start();	
		return true;
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

		// try to get application-lock 
		// we construct lock key based on port		
		final StringBuilder lockKey = new StringBuilder();
		lockKey.append("INTLIB_");
		lockKey.append(context.getBean(AppConfig.class).getInteger(ConfigProperty.BACKEND_PORT));
		if (!AppLock.setLock(lockKey.toString())) {
			// another application is already running
			LOG.info("Another instance of Intlib is probably running.");
			context.close();			
			return;
		}
				
		// publish event for engine about start of the execution,
		// so backend can recover for unexpected shutdown 
		context.publishEvent(new EngineEvent(EngineEventType.STARTUP, AppEntry.class));
				
		// start server
		if (initNetworkServer()) {
			// continue
		} else {
			// terminate the execution			
			context.close();
			LOG.info("Closing application ...");
			// release application log
			AppLock.releaseLock();
			return;
		}
		
		// print some information ..
		LOG.info("Module directory: " + appConfig.getString(ConfigProperty.MODULE_PATH));		
		LOG.info("Running ...");
		
		// infinite loop
		while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
               continue;
            }
		}
	}
	
	public static void main(String[] args) {
		AppEntry app = new AppEntry();
		app.run(args);
		
		LOG.info("Closing application ...");
	}
	
}
