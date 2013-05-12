package cz.cuni.xrg.intlib.commons;

import java.util.Map;

import cz.cuni.xrg.intlib.commons.data.DataUnitFactory;

/**
 * Base context class.
 *
 * @author Petyr
 */
public interface ProcessingContext {

    /**
     * Store given data object. The object is accessible. to the end of DPU run
     * under id.
     *
     * @param object Object to store.
     * @return object id
     */
    public String storeData(Object object);

    /**
     * Load object from repository.
     *
     * @param id Object's id.
     * @return null if there is no object under given id.
     */
    public Object loadData(String id);

    /**
     * Send message about execution.
     * 
     * @param type Type of message.
     * @param shortMessage Short message, should not be more than 50 chars.
     */
    public void sendMessage(DpuType type, String shortMessage);
    
    /**
     * Send message about execution.
     * 
     * @param type Type of message.
     * @param shortMessage Short message, should not be more than 50 chars.
     * @param fullMessage
     */
    public void sendMessage(DpuType type, String shortMessage, String fullMessage);
    
    /**
     * Store data under id in pipeline summary execution storage. These data
     * will be accessible together with pipeline execution results.
     *
     * @param id Object id.
     * @param object Object data.
     */
    public void storeDataForResult(String id, Object object);

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
    
    /**
     * Return DataUnitFactory, that can be used to create new DataUnits.
     * @return
     */
    public DataUnitFactory getDataUnitFactory();
    
}