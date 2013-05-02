package cz.cuni.xrg.intlib.commons;

import java.util.Map;

/**
 * Base context class.
 *
 * @author Petyr
 */
public interface ProcessingContext {
    		
	/**
	 * Store given data object. The object is accessible.
	 * to the end of DPU run under id.  
	 * 
	 * @param object Object to store.
	 * @return object id
	 */
	public String storeData(Object object);
	
	/**
	 * Load object from repository.
	 * @param id Object's id.
	 * @return null if there is no object under given id.
	 */
	public Object loadData(String id);
	
	/**
	 * Store data under id in pipeline summary execution storage.
	 * These data will be accessible together with pipeline execution 
	 * results. 
	 * @param id Object id.
	 * @param object Object data.
	 */
	public void storeDataForResult(String id, Object object);
	
	/**
	 * Return unique pipeline id.
	 * @return
	 */
	public String getId();
	
	/**
	 * Return access to custom data.
	 * @return
	 */
	public Map<String, Object> getCustomData();    
        
}