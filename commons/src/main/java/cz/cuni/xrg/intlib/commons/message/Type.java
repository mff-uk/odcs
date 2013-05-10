package cz.cuni.xrg.intlib.commons.message;

/**
 * Types of messages that can be send
 * through the context. 
 * (ExtractContext, TransformContext, LoadContext)  
 * 
 * @author Petyr
 *
 */
public enum Type {
	/**
	 * Debug messages will be stored only if the DPU is running in debug mode.
	 */
	Debug
	/**
	 * Information messages can be used to inform about DPU
	 * execution progress. 
	 */
	,Info
	/**
	 * Warning messages.
	 */
	,Warning	
	/**
	 * Error messages can be used to report
	 * non fatal error during the DPU execution. 
	 */
	,Error
}
