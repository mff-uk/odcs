package cz.cuni.xrg.intlib.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cz.cuni.xrg.intlib.backend.communication.Server;
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
		
		// load spring
		AbstractApplicationContext context = new ClassPathXmlApplicationContext(springConfigFile);
		context.registerShutdownHook();
		
		// load configuration ? x do in spring
		
		// set JLog
		
		ModuleFacade modeleFacade = (ModuleFacade)context.getBean("moduleFacade");
		modeleFacade.start("");
		
		Server server = (Server)context.getBean("server");
		// init server
		try {
			server.init();
		} catch (CommunicationException e1) {
			System.out.println("Fatal error: Can't start server");
			return;
		}
		// start server in another thread
		Thread serverThread = new Thread(server);
		serverThread.start();
		
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(converter);
		
		System.out.println("Running ...");
		
		while (true) {
			// read line from input
			String line = "";
			try {
				line = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}			
			// decide what to do based on input (line) ..  
			
		}

	}
}