package cz.cuni.xrg.intlib.commons.app.dpu;

import java.util.HashMap;
import java.util.Map;

import cz.cuni.xrg.intlib.commons.configuration.Configuration;

/**
 * Base configuration setting for concrete DPU type.
 *
 * @author Jiri Tomes
 * @author Petyr
 */
public class TemplateConfiguration implements Configuration {

	private Map<String, Object> config = new HashMap<String, Object>();
	
	/**
	 * Return value for given id.
	 * @param parameter id
	 * @return null if there is not object stored under given id
	 */
	public Object getValue(String parameter) {
		return this.config.get(parameter);
	}
	
	/**
	 * Store given object under given id. If object already exist
	 * then it's rewritten.
	 * @param parameter object id
	 * @param value object to store
	 */
	public void setValue(String parameter, Object value) {
		this.config.put(parameter, value);
	}	
		
}
