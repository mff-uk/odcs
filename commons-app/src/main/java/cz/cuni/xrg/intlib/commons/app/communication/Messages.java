package cz.cuni.xrg.intlib.commons.app.communication;

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
	Uknown(0)
	/**
	 * Request for database check, probably because of new work.
	 */
	,CheckDatabase(1);
	
	/**
	 * Contains mapping from (Integer)id to (Enum)Messages.
	 */
	private static Map<Integer, Messages> idToMessages = null;
	
	/**
	 * Message id.
	 */
	protected Integer id;

	private Messages(Integer id) {
		this.id = id;
	}
	
	/**
	 * Return message.
	 * @return
	 */
	public Integer getMessage() {
		return this.id;
	}
	
	/**
	 * Return enum with given message value.
	 * @param id
	 * @return
	 */
	public static Messages getEnum(Integer id) {
		if (idToMessages == null) {
			// construct map
			idToMessages = new HashMap<>();
			// create mapping
			for (Messages item : Messages.class.getEnumConstants()) {
				idToMessages.put(item.getMessage(), item);
	        }
		}
	
		if (idToMessages.containsKey(id)) {
			return idToMessages.get(id);
		} else {
			// unknown message
			return Uknown;
		}
	}
}
