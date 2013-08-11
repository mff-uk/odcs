package cz.cuni.xrg.intlib.commons.context;

import java.io.File;
import java.util.Date;
import java.util.Map;

import cz.cuni.xrg.intlib.commons.message.MessageType;

/**
 * Base context class. The context provide functions that enable DPU communicate
 * with outside world and cooperate with other DPUs in scope of pipeline
 * execution.
 * 
 * The {@link #sendMessage} method should be used to notify user about more
 * serious events like: changing configuration, the fatal error, important 
 * debug information .. 
 * The number of the massage emitted by single execution should be reasonable
 * small to preserve readability of the message log. 
 * 
 * For more intensive logging please use slf4j.
 * 
 * @author Petyr
 */
public interface ProcessingContext {

	/**
	 * Return path to the existing DPU working directory. The working directory
	 * is unique for every DPU.
	 * @return DPU's working directory.
	 */
	public File getWorkingDir();

	/**
	 * Return path to the existing result directory. Result directory is shared
	 * by all DPU's in pipeline.
	 * @return Execution's result directory.
	 */
	public File getResultDir();

	/**
	 * Send message about execution. If the message type is DEBUG and the
	 * pipeline is not running in debug mode the message is ignored.
	 * @param type Type of message.
	 * @param shortMessage Short message, should not be more than 50 chars.
	 */
	public void sendMessage(MessageType type, String shortMessage);

	/**
	 * Send message about execution.If the message type is DEBUG and the
	 * pipeline is not running in debug mode the message is ignored.
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
	 * @return
	 */
	public boolean isDebugging();

	/**
	 * Return access to custom data, this object lives for the whole pipeline
	 * execution. In case of same keys the original value may be overwrite.
	 * 
	 * Only the following data types can be stored:
	 * <ul>
	 * <li>String</li>
	 * <li>Integer</li> 
	 * </ul>
	 * Storing of another data types may result in execution failure.
	 * @return
	 */
	public Map<String, Object> getCustomData();

	/**
	 * Return path to the jar-file which contains implementation of this DPU.
	 * @return Path to the this DPU's jar.
	 */
	public File getJarPath();
	
	/**
	 * Return end time of last successful pipeline execution.
	 * @return Time or Null if there in no last execution.
	 */
	public Date getLastExecutionTime();
	
	/**
	 * Return existing global DPU directory. The directory is 
	 * accessible only for DPU of single type (jar-file). It's shared 
	 * among all the instances and executions.
	 * Be aware of concurrency access when using this directory. 
	 * @return
	 */
	public File getGlobalDirectory();
	
	/**
	 * Return existing DPU shared directory specific for single user. It's shared 
	 * among all the instances and executions for single user and certain 
	 * DPU (jar-file).
	 * Be aware of concurrency access when using this directory. 
	 * @return
	 */
	public File getUserDirectory();
}