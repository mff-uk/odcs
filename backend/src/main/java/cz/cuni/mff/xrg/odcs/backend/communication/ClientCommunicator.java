package cz.cuni.mff.xrg.odcs.backend.communication;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import org.springframework.context.ApplicationEventPublisher;

import cz.cuni.mff.xrg.odcs.backend.execution.event.CheckDatabaseEvent;
import cz.cuni.mff.xrg.odcs.commons.app.communication.Messages;

/**
 * Class for handling communication with single client.
 *
 * @author Petyr
 *
 */
class ClientCommunicator implements Runnable {
	
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

    public ClientCommunicator(Socket socket, ApplicationEventPublisher eventPublisher, Server server) {
        this.socket = socket;
        this.eventPublisher = eventPublisher;
        this.server = server;
    }

    @Override
    public void run() {
        // read message
        Messages msg = Messages.UNKNOWN;
        try (DataInputStream stream = new DataInputStream(socket.getInputStream())) {
                int messageId = stream.readInt();
                // translate id to Message enum value
                msg = Messages.getEnum(messageId);
        } catch (IOException e) {
			throw new RuntimeException(e);
        } finally {
			try {
				socket.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
        // decide what to do next based on message
        switch (msg) {        	
            case UNKNOWN:
                // unknown command, ignore
                break;
            case CHECK_DATABASE:
                // send event to engine to check database
                // as a source use Server class instance (the one who create us)
                eventPublisher.publishEvent(new CheckDatabaseEvent(server));
                break;
            case HEARTBEAT:
            	// just heart beat, do not do anything
            	break;
        }
    }
}
