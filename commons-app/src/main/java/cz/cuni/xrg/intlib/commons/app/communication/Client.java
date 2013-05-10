package cz.cuni.xrg.intlib.commons.app.communication;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import cz.cuni.xrg.intlib.commons.app.AppConfiguration;

/**
 * Client part of communication between frontend and backend over
 * TCP/IP.
 * 
 * @author Petyr
 *
 */
public class Client {

	/**
	 * Application configuration.
	 */
	AppConfiguration appConfiguration;
	
	/**
	 * Address of backend.
	 */
	InetAddress address;
	
	public Client(AppConfiguration appConfiguration) {
		this.appConfiguration = appConfiguration;
		this.address = null;
	}
	
	/**
	 * Request backend to check database for new tasks.
	 * If the communication can be established then exception is thrown.
	 * @throws CommunicationException
	 */
	public void checkDatabase() throws CommunicationException {
		// do we know backend address ?
		if (address == null) {
			// no -> translate backend address
			try {
				address = InetAddress.getByName(appConfiguration.getBackendAddress());
			} catch (UnknownHostException e) {
				throw new CommunicationException("Can't resolve host name.", e);
			}		
		}
		// connect to backend
		Socket socket;		
		try {
			socket = new Socket(address, appConfiguration.getBackendPort());
		} catch (IOException e) {
			throw new CommunicationException("Can't connect to backend.", e);
		}
			
		// communication
		try {
			// send message: Messages.CheckDatabase
			DataOutputStream  stream = new DataOutputStream (socket.getOutputStream());
			stream.writeInt(Messages.CheckDatabase.getMessage());
			// flush and close .. 
			stream.flush();	
			stream.close();
			socket.close();
		} catch (IOException e) {			
			throw new CommunicationException("Error in communication with backend.", e);
		}
	}
	
}
