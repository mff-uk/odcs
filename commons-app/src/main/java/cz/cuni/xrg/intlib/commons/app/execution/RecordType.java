package cz.cuni.xrg.intlib.commons.app.execution;

import cz.cuni.xrg.intlib.commons.message.MessageType;

/**
 * Types for execution.record.
 * 
 * @author Petyr
 * 
 */
public enum RecordType {
	/**
	 * Debug information from DPURecord.
	 */
	 DPUDEBUG
	/**
	 * Log information from DPURecord.
	 */
	,DPULOG
	/**
	 * Closer unspecified information from DPURecord.
	 */
	,DPUINFO
	/**
	 * Warning from DPURecord.
	 */
	,DPUWARNING
	/**
	 * Error from DPURecord.
	 */
	,DPUERROR
	/**
	 * Represent error message from pipeline.
	 */
	,PIPELINEERROR;
		
	/**
	 * Convert message.Type to RecordType.
	 */
	public static RecordType fromMessageType(MessageType type) {
		switch(type) {
		case DEBUG:
			return RecordType.DPUDEBUG;
		case INFO:
			return RecordType.DPUINFO;
		case WARNING:
			return RecordType.DPUWARNING;
		case ERROR:
			return RecordType.DPUERROR;
		default:
			return RecordType.DPUINFO;
		}		
	}
}
