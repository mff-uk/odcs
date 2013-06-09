package cz.cuni.xrg.intlib.commons;

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
     * Store given data object. The object is accessible. to the end of DPU run
     * under id. If object can't be stored throw {@link RuntimeException}.
     *
     * @param object Object to store.
     * @return object id
     * throws RuntimeException
     */
    public String storeData(Object object) throws RuntimeException;

    /**
     * Load object from repository.
     *
     * @param id Object's id.
     * @return Loaded object or null in case of problems.
     */
    public Object loadData(String id);

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
     * Store data under id in pipeline summary execution storage. These data
     * will be accessible together with pipeline execution results.
     *
     * @param id Object id.
     * @param object Object data.
     * @throws RuntimeException
     */
    public void storeDataForResult(String id, Object object) throws RuntimeException;

    /**
     * Return true if the DPU is running in debugging mode.
     * @return
     */
    public boolean isDebugging();
        
    /**
     * Return access to custom data, this object
     * live for the whole pipeline execution. In case of same 
     * keys the original value may be overwrite.
     * 
     * @return
     */
    public Map<String, Object> getCustomData();    
        
}