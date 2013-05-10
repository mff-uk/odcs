package cz.cuni.xrg.intlib.commons.app.dpu.execution;

import cz.cuni.xrg.intlib.commons.message.Type;

/**
 * Types for execution.record.
 * 
 * @author Petyr
 * 
 */
public enum RecordType {
	Debug
	,Log
	,Info
	,Warning
	,Error;
	
	
	/**
	 * Convert message.Type to RecordType.
	 */
	public static RecordType fromMessageType(Type type) {
		switch(type) {
		case Debug:
			return RecordType.Debug;
		case Info:
			return RecordType.Info;
		case Warning:
			return RecordType.Warning;
		case Error:
			return RecordType.Error;
		default:
			return RecordType.Info;
		}		
	}
}
