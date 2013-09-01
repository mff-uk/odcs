package cz.cuni.xrg.intlib.commons.app.communication;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Client part of communication between frontend and backend over TCP/IP.
 *
 * @author Petyr
 *
 */
public class Client {

	/**
	 * Backend address.
	 */
	private String backendAddress;
	/**
	 * Port for communication.
	 */
	private int port;
	/**
	 * Address of backend in InetAdress format. Used as a cache.
	 */
	private InetAddress chacedAddress;
	/**
	 * Socket for loading data from database.
	 */
	private Socket socket;

	public Client(String backendAddress, int port) {
		this.backendAddress = backendAddress;
		this.port = port;
		this.chacedAddress = null;
	}

	/**
	 * Tries to establish communication with backend.
	 *
	 * @return If connection was successful.
	 */
	public boolean connect() throws CommunicationException {
		// do we know backend address ?
		if (chacedAddress == null) {
			// no -> translate backend address
			try {
				chacedAddress = InetAddress.getByName(backendAddress);
			} catch (UnknownHostException e) {
				throw new CommunicationException("Can't resolve host name.", e);
			}
		}
		// connect to backend
		try {
			socket = new Socket(chacedAddress, port);
		} catch (IOException e) {
			throw new CommunicationException("Can't connect to backend.", e);
		}
		return true;
	}

	/**
	 * Checks if connection to backend is alive.
	 * 
	 * @return If backend is running.
	 */
	public boolean checkStatus() {
		if (socket == null) {
			try {
				connect();
			} catch (CommunicationException ex) {
				return false;
			}
		}
		// communication
		try {
			try (DataOutputStream stream = new DataOutputStream(socket.getOutputStream())) {
				stream.writeInt(Messages.HEARTBEAT.getMessage());
				stream.flush();
			}
			close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Request backend to check database for new tasks. If the communication can
	 * be established then exception is thrown.
	 *
	 * @throws CommunicationException
	 */
	public void checkDatabase() throws CommunicationException {
		if (socket == null) {
			connect();
		}

		// communication
		try {
			try (DataOutputStream stream = new DataOutputStream(socket.getOutputStream())) {
				stream.writeInt(Messages.CHECK_DATABASE.getMessage());
				// flush and close .. 
				stream.flush();
			}
			close();
		} catch (IOException e) {
			throw new CommunicationException("Error in communication with backend.", e);
		}
		
	}
	
	/**
	 * Closes connection to backend.
	 */
	public void close() {
		if(socket != null) {
			try {
				socket.close();
				socket = null;
			} catch (IOException ex) {
				Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
			}
			
		}
	}
}
