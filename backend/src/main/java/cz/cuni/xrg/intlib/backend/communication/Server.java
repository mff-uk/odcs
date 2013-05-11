package cz.cuni.xrg.intlib.backend.communication;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import cz.cuni.xrg.intlib.commons.app.communication.CommunicationException;
import cz.cuni.xrg.intlib.commons.app.communication.Messages;
import cz.cuni.xrg.intlib.backend.AppConfiguration;

/**
 * Server part of communication between frontend and backend over
 * TCP/IP.
 * 
 * init method must be called before running the main routine (run method)
 * 
 * 
 * @author Petyr
 *
 */
public class Server implements Runnable, ApplicationEventPublisherAware {

	/**
	 * Class for handling communication with single client.
	 * @author Petyr
	 *
	 */
	private class ClientCommunicator implements Runnable {

		/**
		 * Communication socket.
		 */
		private Socket socket; 
		
		/**
		 * Event publisher used to publicise events.
		 */		
		private ApplicationEventPublisher eventPublisher;
		
		/**
		 * The creator. Used in events as a source.
		 */
		private Server server;
		
		public ClientCommunicator(Socket socket,ApplicationEventPublisher eventPublisher, Server server) {
			this.socket = socket;
			this.eventPublisher = eventPublisher;
			this.server = server;
		}
		
		@Override
		public void run() {
			// read message
			Messages msg = Messages.Uknown;
			try {
				DataInputStream stream = new DataInputStream(socket.getInputStream());
				int messageId = stream.readInt();
				// translate id to Message enum value
				msg = Messages.getEnum(messageId); 
				socket.close();
			} catch (IOException e) {
				
			}
			// decide what to do next based on message
			switch(msg) {
			case Uknown:
				// unknown command, ignore
				break;
			case CheckDatabase:
				// send event to engine to check database
				// as a source use Server class instance (the one who create us)
				eventPublisher.publishEvent(new ServerEvent(server, msg));
				break;
			}			
		}
		
	}
	
	/**
	 * Application configuration.
	 */
	protected AppConfiguration appConfiguration;	
	
	/**
	 * Server socket.
	 */
	protected ServerSocket socket;
	
	/**
	 * Provide executors for handling incoming communications.
	 */
	protected ExecutorService executorService;
	
	/**
	 * Event publisher used to publicise events.
	 */
	protected ApplicationEventPublisher eventPublisher;
	
	/**
	 * True if continue in execution. Set to false to stop 
	 * the loop in run method.
	 */
	protected boolean running;
	
	/**
	 * Create instance of Server. CachedThreadPool is used 
	 * as ExecutorService.
	 * @param appConfiguration
	 */
	public Server(AppConfiguration appConfiguration) {
		this.appConfiguration = appConfiguration;
		this.executorService = Executors.newCachedThreadPool();
		this.eventPublisher = null;
		this.running = true;
	}
	
	/**
	 * Create instance of Server. The given executorService should 
	 * provide enough resources to enable handling of incoming communication.
	 * @param appConfiguration
	 * @param executorService executorService to use for handling incoming connection
	 */
	public Server(AppConfiguration appConfiguration, ExecutorService executorService) {
		this.appConfiguration = appConfiguration;
		this.executorService = executorService;
		this.eventPublisher = null;
		this.running = true;
	}	
	
	/**
	 * Open connection and do initialization. In
	 * case of any error throw exception.
	 * @throws CommunicationException
	 */
	public void init() throws CommunicationException {
		// open socket
		try {
			this.socket = new ServerSocket(appConfiguration.getBackendPort());
		} catch (IOException e) {
			throw new CommunicationException(e);
		}
	}

	@Override
	public void run() {
		this.running = true;
		// wait for connection
		while (running) {

			Socket newSocket;
			try {
				newSocket = socket.accept();
				// prepare communicator class 
				ClientCommunicator communicator = new ClientCommunicator(newSocket, eventPublisher, this);
				// execute ..		
				synchronized (executorService) {
					executorService.execute(communicator);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		try {
			socket.close();
		} catch (IOException e) {
			// error while closing socket, application is probably being terminated
			// so we do not throw
		}
		
		executorService.shutdownNow();
		// wait for the end .. 
		try {
			while (!executorService.awaitTermination(10, TimeUnit.SECONDS));
		} catch (InterruptedException e) {
			// force stop of this thread 
		}
	}

	/**
	 * Try to stop the thread and all related threads as soon as possible. 
	 * This function is non blocking.
	 */
	public void stop() {
		running = false;
	}
	
	@Override
	public void setApplicationEventPublisher(
			ApplicationEventPublisher applicationEventPublisher) {
		eventPublisher = applicationEventPublisher;
	}
}
