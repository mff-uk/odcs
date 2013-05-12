package cz.cuni.xrg.intlib.commons.app.dpu.execution;

import cz.cuni.xrg.intlib.commons.message.MessageType;

/**
 * Types for execution.record.
 * 
 * @author Petyr
 * 
 */
public enum RecordType {
	DEBUG
	,LOG
	,INFO
	,WARNING
	,ERROR;
	
	
	/**
	 * Convert message.Type to RecordType.
	 */
	public static RecordType fromMessageType(MessageType type) {
		switch(type) {
		case DEBUG:
			return RecordType.DEBUG;
		case INFO:
			return RecordType.INFO;
		case WARNING:
			return RecordType.WARNING;
		case ERROR:
			return RecordType.ERROR;
		default:
			return RecordType.INFO;
		}		
	}
}
