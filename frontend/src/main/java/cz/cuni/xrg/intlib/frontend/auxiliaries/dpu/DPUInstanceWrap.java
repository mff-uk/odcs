package cz.cuni.xrg.intlib.frontend.auxiliaries.dpu;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;

/**
 * Wrap {@link DPUInstanceRecord} to made work with configuration 
 * and configuration dialog easier.
 * 
 * @author Petyr
 *
 */
public class DPUInstanceWrap extends DPURecordWrap{

	/**
	 * Wrapped DPUTemplateRecord.
	 */
	private DPUInstanceRecord dpuInstance;
	
	/**
	 * Create wrap for DPUTemplateRecord.
	 * @param dpuInstance
	 */
	public DPUInstanceWrap(DPUInstanceRecord dpuTemplate) {
		super(dpuTemplate);
		this.dpuInstance = dpuTemplate;
	}
	
	/**
	 * Save wrapped DPUInstanceInto database. 
	 * To save configuration from dialog as well call {{@link #saveConfig()} first.
	 */
	public void save() {
		App.getDPUs().save(dpuInstance);
	}
	
	public DPUInstanceRecord getDPUTemplateRecord() {
		return dpuInstance;
	}	
	
}
