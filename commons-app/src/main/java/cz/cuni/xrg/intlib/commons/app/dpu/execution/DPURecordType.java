package cz.cuni.xrg.intlib.commons.app.dpu.execution;

import cz.cuni.xrg.intlib.commons.message.MessageType;

/**
 * Types for execution.record.
 * 
 * @author Petyr
 * 
 */
public enum DPURecordType {
	DEBUG
	,LOG
	,INFO
	,WARNING
	,ERROR;
	
	
	/**
	 * Convert message.Type to DPURecordType.
	 */
	public static DPURecordType fromMessageType(MessageType type) {
		switch(type) {
		case DEBUG:
			return DPURecordType.DEBUG;
		case INFO:
			return DPURecordType.INFO;
		case WARNING:
			return DPURecordType.WARNING;
		case ERROR:
			return DPURecordType.ERROR;
		default:
			return DPURecordType.INFO;
		}		
	}
}
