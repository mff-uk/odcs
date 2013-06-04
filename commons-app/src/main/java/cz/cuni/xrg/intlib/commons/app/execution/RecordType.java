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
	 DPU_DEBUG
	/**
	 * Log information from DPURecord.
	 */
	,DPU_LOG
	/**
	 * Closer unspecified information from DPURecord.
	 */
	,DPU_INFO
	/**
	 * Warning from DPURecord.
	 */
	,DPU_WARNING
	/**
	 * Error from DPURecord.
	 */
	,DPU_ERROR
	/**
	 * Represent error message from pipeline.
	 */
	,PIPELINE_ERROR;
		
	/**
	 * Convert message.Type to RecordType.
	 */
	public static RecordType fromMessageType(MessageType type) {
		switch(type) {
		case DEBUG:
			return RecordType.DPU_DEBUG;
		case INFO:
			return RecordType.DPU_INFO;
		case WARNING:
			return RecordType.DPU_WARNING;
		case ERROR:
			return RecordType.DPU_ERROR;
		default:
			return RecordType.DPU_INFO;
		}		
	}
}
