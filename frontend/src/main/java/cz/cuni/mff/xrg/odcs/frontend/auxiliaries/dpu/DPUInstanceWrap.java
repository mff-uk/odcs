package cz.cuni.mff.xrg.odcs.frontend.auxiliaries.dpu;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.App;

/**
 * Wrap {@link DPUInstanceRecord} to made work with configuration and
 * configuration dialog easier.
 *
 * @author Petyr
 *
 */
public class DPUInstanceWrap extends DPURecordWrap {

	/**
	 * Wrapped DPUTemplateRecord.
	 */
	private final DPUInstanceRecord dpuInstance;

	/**
	 * Create wrap for DPUTemplateRecord.
	 *
	 * @param dpuTemplate
	 */
	public DPUInstanceWrap(DPUInstanceRecord dpuTemplate) {
		super(dpuTemplate);
		this.dpuInstance = dpuTemplate;
	}

	/**
	 * Save wrapped DPUInstanceInto database. To save configuration from dialog
	 * as well call {{@link #saveConfig()} first.
	 */
	public void save() {
		App.getDPUs().save(dpuInstance);
	}

	public DPUInstanceRecord getDPUInstanceRecord() {
		return dpuInstance;
	}
}
