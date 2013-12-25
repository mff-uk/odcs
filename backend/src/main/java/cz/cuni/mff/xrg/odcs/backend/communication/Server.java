package cz.cuni.mff.xrg.odcs.backend.communication;

import cz.cuni.mff.xrg.odcs.commons.app.communication.CommunicationException;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;

/**
 * Server part of communication between frontend and backend over TCP/IP.
 *
 * init method must be called before running the main routine (run method)
 *
 * @author Petyr
 *
 */
public class Server implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(Server.class);
	
	/**
	 * Timeout in ms for TCP/IP operation. Also determine the 
	 * time in which server check's for end of it's execution. 
	 */
	public static final int TCPIP_TIMEOUT = 1000;
	
    /**
     * Application configuration.
     */
    @Autowired
    private AppConfig appConfiguration;
    
    /**
     * Event publisher used to publicise events.
     */
    @Autowired
    private ApplicationEventPublisher eventPublisher;
        
    /**
     * Server socket.
     */
    private ServerSocket socket;
    
    /**
     * Provide executors for handling incoming communications.
     */
    private final ExecutorService executorService;
        
    /**
     * True if continue in execution. Set to false to stop the loop in run
     * method.
     */
    private boolean running;

    /**
     * Create instance of Server. CachedThreadPool is used as ExecutorService.
     *
     */
    public Server() {
        this.executorService = Executors.newCachedThreadPool();
        this.eventPublisher = null;
        this.running = true;
    }

    /**
     * Open connection and do initialization. In case of any error throw
     * exception.
     *
     * @throws CommunicationException
     */
    public void init() throws CommunicationException {
    	int port = appConfiguration.getInteger(ConfigProperty.BACKEND_PORT);
        // open socket
        try {
            this.socket = new ServerSocket(port);
        } catch (BindException e){
        	LOG.error("TCP/IP port {} already used.", port);
        	// check if not used by JVM_Bind
        	if (e.getMessage().contains("JVM_Bind")) {
        		LOG.info("Another instance of Intlib is probably running.");
        	}
        	throw new CommunicationException(e);
        } catch (IOException e) {
            throw new CommunicationException(e);
        }
        LOG.info("Listen on TCP/IP port: {}", port);
    }

    @Override
    public void run() {
        this.running = true;
        // set timeout
        try {
			socket.setSoTimeout(TCPIP_TIMEOUT);
		} catch (SocketException e) {
			LOG.error("Failed to set timeout for TCP/IP socket.");
			LOG.debug("", e);
		}
        // wait for connection
        while (running) {
            Socket newSocket;
            try {
                newSocket = socket.accept();
                // prepare communicator class 
                ServerCommunicator communicator = new ServerCommunicator(newSocket, eventPublisher, this);
                // execute ..		
                synchronized (executorService) {
                    executorService.execute(communicator);
                }
            } catch(SocketTimeoutException e) {
            	// just timeout .. 
            } catch (IOException e) {
            	LOG.error("Failed to accept incoming connection.");
            	LOG.debug("", e);
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
            while (!executorService.awaitTermination(TCPIP_TIMEOUT / 2, TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            // force stop of this thread 
        }
        LOG.info("TCP/IP server has been terminated.");
    }

    /**
     * Try to stop the thread and all related threads as soon as possible. This
     * function is non blocking.
     */
    public void stop() {
        running = false;
    }

}
