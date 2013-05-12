package cz.cuni.xrg.intlib.backend;

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
import cz.cuni.xrg.intlib.backend.execution.Engine;
import cz.cuni.xrg.intlib.commons.app.communication.CommunicationException;
import cz.cuni.xrg.intlib.commons.app.module.ModuleFacade;
import cz.cuni.xrg.intlib.commons.app.pipeline.PipelineFacade;

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
	private final static String springConfigFile = "spring.xml";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// start with logger
		Logger logger = LoggerFactory.getLogger(AppEntry.class);		
		
		String configFileLocation = null;
		
		// define args
		Options options = new Options();
		options.addOption("", "config", true, "path to the configuration file");
		// parse args
		CommandLineParser parser = new org.apache.commons.cli.BasicParser();
		try {
			CommandLine cmd = parser.parse( options, args);
			// read args ..
			configFileLocation = cmd.getOptionValue("config");
		} catch (ParseException e) {			
			logger.error("Unexpected exception:" + e.getMessage());
		}
		
		// check if 'config' parameter has been provided
		if (configFileLocation == null) {
			logger.error("Property config must be specified. Use param -config path_to_config.xml");
			return;
		}
		
		//PipelineFacade pipelines = new PipelineFacade();
		//System.out.println("Pipelines count: " + pipelines.getAllPipelines().size() );
		
		
		
		
		// load spring
		AbstractApplicationContext context = new ClassPathXmlApplicationContext(springConfigFile);
		context.registerShutdownHook();
		
		// load configuration
		AppConfiguration appConfig = (AppConfiguration)context.getBean("configuration");
		try {
			appConfig.Load(configFileLocation);
		} catch(IOException | RuntimeException e) {
			logger.error("Can't read configuration file: " + e.getMessage());
			return;
		}

		// set engine
		logger.info("Configuring engine ...");
		Engine engine = (Engine)context.getBean("engine");
		engine.setup(appConfig);
		
		// set module facade
		logger.info("Configuring dynamic module worker ...");

		ModuleFacade modeleFacade = (ModuleFacade)context.getBean("moduleFacade");
		modeleFacade.start();
		
		engine.checkDatabase();
		
		/*
		// set TCP/IP server
		logger.info("Starting TCP/IP server ...");
		Server server = (Server)context.getBean("server");
		try {
			server.init();
		} catch (CommunicationException e1) {
			logger.info("Fatal error: Can't start server");
			context.close();
			return;
		}
		// start server in another thread
		Thread serverThread = new Thread(server);
		serverThread.start();
				
		// print some information ..
		logger.info("DPU directory:" + appConfig.getModuleFacadeConfiguration().getDpuFolder());
		logger.info("Listening on port:" + appConfig.getBackendPort());
		logger.info("Running ...");
				
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);		
		while (true) {
			// read line from input
			String line = "";
			try {
				line = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}			
			// ...
		}*/
		
	}
}