package cz.cuni.xrg.intlib.backend.communication;

import org.springframework.context.ApplicationEvent;

import cz.cuni.xrg.intlib.commons.app.communication.Messages;

/**
 * Class for server event.
 * @author Petyr
 *
 */
public class ServerEvent extends ApplicationEvent {

	/**
	 * Message.
	 */
	private Messages message;
	
	public ServerEvent(Object source, Messages message) {
		super(source);
		this.message = message;
	}

	public Messages getMessage() {
		return message;
	}
	
}
