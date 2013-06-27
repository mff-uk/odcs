package cz.cuni.xrg.intlib.frontend.auxiliaries.dpu;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;

/**
 * Wrap {@link DPUTemplateRecord} to made work with configuration 
 * and configuration dialog easier.
 * 
 * @author Petyr
 *
 */
public class DPUTemplateWrap extends DPURecordWrap{

	/**
	 * Wrapped DPUTemplateRecord.
	 */
	private DPUTemplateRecord dpuTemplate;
	
	/**
	 * Create wrap for DPUTemplateRecord.
	 * @param dpuTemplate
	 */
	public DPUTemplateWrap(DPUTemplateRecord dpuTemplate) {
		super(dpuTemplate);
		this.dpuTemplate = dpuTemplate;
	}
	
	/**
	 * Save wrapped DPUInstanceInto database. 
	 * To save configuration from dialog as well call {{@link #saveConfig()} first.
	 */
	public void save() {
		App.getDPUs().save(dpuTemplate);
	}
	
	public DPUTemplateRecord getDPUTemplateRecord() {
		return dpuTemplate;
	}
	
}
