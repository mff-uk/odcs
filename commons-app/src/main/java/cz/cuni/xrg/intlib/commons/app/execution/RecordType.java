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
	 * Debug information from DPU.
	 */
	 DPUDEBUG
	/**
	 * Log information from DPU.
	 */
	,DPULOG
	/**
	 * Closer unspecified information from DPU.
	 */
	,DPUINFO
	/**
	 * Warning from DPU.
	 */
	,DPUWARNING
	/**
	 * Error from DPU.
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
