package cz.cuni.xrg.intlib.commons.message;

/**
 * Types of messages that can be send
 * through the context. 
 * (ExtractContext, TransformContext, LoadContext)  
 * 
 * @author Petyr
 *
 */
public enum MessageType {
	/**
	 * Debug messages will be stored only if the DPU is running in debug mode.
	 */
	DEBUG
	/**
	 * Information messages can be used to inform about DPU
	 * execution progress. 
	 */
	,INFO
	/**
	 * Warning messages.
	 */
	,WARNING	
	/**
	 * Error messages can be used to report
	 * non fatal error during the DPU execution. 
	 */
	,ERROR
}
