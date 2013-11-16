package cz.cuni.mff.xrg.odcs.commons.dpu;

import java.io.File;
import java.util.Date;

import cz.cuni.mff.xrg.odcs.commons.message.MessageType;

/**
 * Context used by {@link DPU} during their execution process.
 * 
 * The context provide functions that enable DPU communicate
 * with outside world and cooperate with other DPUs in scope of pipeline
 * execution.
 * 
 * The {@link #sendMessage} method should be used to notify user about more
 * serious events like: changing configuration, the fatal error, important debug
 * information .. The number of the massage emitted by single execution should
 * be reasonable small to preserve readability of the message log.
 * 
 * For more intensive logging please use slf4j.
 * 
 * @author Petyr
 * @see DPU
 *
 */
public interface DPUContext {

	/**
	 * Send message about execution. If {@link MessageType#ERROR} message
	 * is published then the execution is stopped after current DPU and the 
	 * whole execution failed.
	 * 
	 * @param type Type of message.
	 * @param shortMessage Short message, should not be more than 50 chars.
	 */
	public void sendMessage(MessageType type, String shortMessage);

	/**
	 * Send message about execution. If {@link MessageType#ERROR} message
	 * is published then the execution is stopped after current DPU and the 
	 * whole execution failed.
	 * 
	 * @param type Type of message.
	 * @param shortMessage Short message, should not be more than 50 chars.
	 * @param fullMessage The full text of the message can be longer then
	 *            shortMessage.
	 */
	public void sendMessage(MessageType type,
			String shortMessage,
			String fullMessage);	
	
	/**
	 * Return true if the DPU is running in debugging mode.
	 * 
	 * @return
	 */
	public boolean isDebugging();

	/**
	 * Return true if the execution of current DPU should be stopped
	 * as soon as possible. After the {@link DPU#execute(DPUContext)} method
	 * finished the {@link DPU#cleanUp()} will be called.
	 * @return
	 */
	public boolean canceled();
	
	/**
	 * Return path to the existing DPU working directory. The working directory
	 * is unique for every DPU and execution.
	 * 
	 * @return DPU's working directory.
	 */
	public File getWorkingDir();

	/**
	 * Return path to the existing result directory. Result directory is shared
	 * by all DPU's in pipeline.
	 * 
	 * @return Execution's result directory.
	 */
	public File getResultDir();

	/**
	 * Return path to the jar-file which contains implementation of this DPU.
	 * 
	 * @return Path to the this DPU's jar.
	 */
	public File getJarPath();

	/**
	 * Return end time of last successful pipeline execution.
	 * 
	 * @return Time or Null if there in no last execution.
	 */
	public Date getLastExecutionTime();

	/**
	 * Return existing global DPU directory. The directory is accessible only
	 * for DPU of single type (jar-file). It's shared among all the instances
	 * and executions. Be aware of concurrency access when using this directory.
	 * 
	 * @return
	 */
	public File getGlobalDirectory();

	/**
	 * Return existing DPU shared directory specific for single user. It's
	 * shared among all the instances and executions for single user and certain
	 * DPU (jar-file). Be aware of concurrency access when using this directory.
	 * 
	 * @return
	 */
	public File getUserDirectory();
	
}
