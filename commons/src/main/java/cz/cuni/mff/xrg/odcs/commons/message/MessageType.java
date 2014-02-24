package cz.cuni.mff.xrg.odcs.commons.message;

/**
 * Types of messages that can be send through the context. 
 * 
 * @author Petyr
 * 
 */
public enum MessageType {
	/**
	 * Debug messages will be stored only if the DPU is running in debug mode.
	 */
	DEBUG,
	/**
	 * Information messages can be used to inform about DPU execution progress.
	 */
	INFO,
	/**
	 * Warning messages.
	 */
	WARNING,
	/**
	 * Error messages can be used to report non fatal error during the DPU
	 * execution.
	 */
	ERROR,
	/**
	 * Message that require skipping execution of every DPU after current
	 * DPU till the end of the pipeline. Can be used to terminate execution
	 * if there are for example no input data without failing it.
	 */
	TERMINATION_REQUEST
}
