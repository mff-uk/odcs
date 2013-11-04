package cz.cuni.mff.xrg.odcs.commons.app.execution.message;

import cz.cuni.mff.xrg.odcs.commons.message.MessageType;

/**
 * Types for execution.record.
 * 
 * @author Petyr
 * 
 */
public enum MessageRecordType {
	/**
	 * Debug information from DPURecord.
	 */
	 DPU_DEBUG
	/**
	 * Log information from DPURecord.
	 */
	,@Deprecated
         DPU_LOG
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
	,PIPELINE_ERROR
	/**
	 * Represent information about pipeline execution.
	 */
	,PIPELINE_INFO;
		
	/**
	 * Convert message.Type to RecordType.
	 */
	public static MessageRecordType fromMessageType(MessageType type) {
		switch(type) {
		case DEBUG:
			return MessageRecordType.DPU_DEBUG;
		case INFO:
			return MessageRecordType.DPU_INFO;
		case WARNING:
			return MessageRecordType.DPU_WARNING;
		case ERROR:
			return MessageRecordType.DPU_ERROR;
		default:
			return MessageRecordType.DPU_INFO;
		}		
	}
}
