package cz.cuni.xrg.intlib.commons;

import java.io.File;
import java.util.Map;

import cz.cuni.xrg.intlib.commons.message.MessageType;

/**
 * Base context class. The context provide functions that enable DPU
 * communicate with outside world and cooperate with other DPUs
 * in scope of pipeline execution.
 *
 * @author Petyr
 */
public interface ProcessingContext {
	
	/**
	 * Return path to the DPU working directory. The working
	 * directory is unique for every DPU.
	 * @return
	 */
	public File getWorkingDir();
	
	/**
	 * Return path to the result directory. Result directory is 
	 * shared by all DPU's in pipeline.
	 * @return
	 */
	public File getResultDir();

    /**
     * Send message about execution. If the message type is DEBUG and the pipeline
     * is not running in debug mode the message is ignored.
     * 
     * @param type Type of message.
     * @param shortMessage Short message, should not be more than 50 chars.
     */
    public void sendMessage(MessageType type, String shortMessage);
    
    /**
     * Send message about execution.If the message type is DEBUG and the pipeline
     * is not running in debug mode the message is ignored.
     * 
     * @param type Type of message.
     * @param shortMessage Short message, should not be more than 50 chars.
     * @param fullMessage
     */
    public void sendMessage(MessageType type, String shortMessage, String fullMessage);

    /**
     * Return true if the DPU is running in debugging mode.
     * @return
     */
    public boolean isDebugging();
        
    /**
     * Return access to custom data, this object
     * lives for the whole pipeline execution. In case of same 
     * keys the original value may be overwrite.
     * 
     * @return
     */
    public Map<String, Object> getCustomData();    
        
    /**
     * Return path to the jar-file which contains implementation
     * of this DPU. 
     * @return Path to the this DPU's jar.
     */
    public File getJarPath();
}