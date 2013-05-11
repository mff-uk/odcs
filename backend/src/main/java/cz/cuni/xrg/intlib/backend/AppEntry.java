package cz.cuni.xrg.intlib.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cz.cuni.xrg.intlib.backend.communication.Server;
import cz.cuni.xrg.intlib.backend.execution.Engine;
import cz.cuni.xrg.intlib.commons.app.communication.CommunicationException;
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
	private final static String springConfigFile = "spring.xml";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// start with logger
		//Logger logger = LoggerFactory.getLogger(AppEntry.class);		
		//logger.info("Init spring context from :'" + springConfigFile + "'");
		
		// get configuration file location 
		String configFileLocation = System.getProperty("config");
		if (configFileLocation == null) {
			System.out.println("Property config must be specified. Use param -Dconfig=path_to_config.xml");
			return;
		}
		
		// load spring
		AbstractApplicationContext context = new ClassPathXmlApplicationContext(springConfigFile);
		context.registerShutdownHook();
		
		// load configuration
		AppConfiguration appConfig = (AppConfiguration)context.getBean("configuration");
		try {
			appConfig.Load(configFileLocation);
		} catch(IOException | RuntimeException e) {
			System.out.println("Can't read configuration file: " + e.getMessage());
			return;
		}
		
		// set JLog
		
		// set engine
		System.out.println("Configuring engine ...");
		Engine engine = (Engine)context.getBean("engine");
		engine.setup(appConfig);
		
		// set module facade
		System.out.println("Configuring dynamic module worker ...");
		ModuleFacade modeleFacade = (ModuleFacade)context.getBean("moduleFacade");
		modeleFacade.start("");
		
		// set TCP/IP server
		System.out.println("Starting TCP/IP server ...");
		Server server = (Server)context.getBean("server");
		try {
			server.init();
		} catch (CommunicationException e1) {
			System.out.println("Fatal error: Can't start server");
			context.close();
			return;
		}
		// start server in another thread
		Thread serverThread = new Thread(server);
		serverThread.start();
				
		// print some information ..
		System.out.println("DPU durectory:" + appConfig.getModuleFacadeConfiguration().getDpuFolder());
		System.out.println("Listening on port:" + appConfig.getBackendPort());
		System.out.println("Running ...");
				
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
		}
		
	}
}