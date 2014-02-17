package cz.cuni.mff.xrg.odcs.commons.app.communication;

import java.util.HashMap;
import java.util.Map;

/**
 * Define message that are used for communication 
 * between backend and frontend.
 * 
 * @author Petyr
  */
public enum Messages {
	/**
	 * Unknown message, or error during communication.
	 */	
	UNKNOWN(0)
	/**
	 * Request for database check, probably because of new work.
	 */
	,CHECK_DATABASE(1)
	/**
	 * Heartbeat message
	 */
	,HEARTBEAT(2);
	
	/**
	 * Contains mapping from integers into the messages.
	 */
	private static final Map<Integer, Messages> TRANSLATOR = new HashMap<>();
	
	/**
	 * Message id.
	 */
	protected final Integer id;

	private Messages(Integer id) {
		this.id = id;
	}
	
	/**
	 * @return message
	 */
	public Integer getMessage() {
		return this.id;
	}
	
	/**
	 * Translate Integer into the {@link Messages}.
	 * 
	 * @param id
	 * @return message corresponding to given id
	 */
	public static Messages getEnum(Integer id) {
		if (TRANSLATOR.isEmpty()) {
			// create mapping
			for (Messages item : Messages.class.getEnumConstants()) {
				TRANSLATOR.put(item.getMessage(), item);
	        }
		}
	
		if (TRANSLATOR.containsKey(id)) {
			return TRANSLATOR.get(id);
		} else {
			// unknown message
			return UNKNOWN;
		}
	}
}
