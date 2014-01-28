package cz.cuni.mff.xrg.odcs.frontend.auxiliaries.dpu;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;

/**
 * Wrap {@link DPUTemplateRecord} to made work with configuration and
 * configuration dialog easier.
 *
 * @author Petyr
 *
 */
public class DPUTemplateWrap extends DPURecordWrap {

	/**
	 * Wrapped DPUTemplateRecord.
	 */
	private final DPUTemplateRecord dpuTemplate;

	/**
	 * Create wrap for DPUTemplateRecord.
	 *
	 * @param dpuTemplate
	 */
	public DPUTemplateWrap(DPUTemplateRecord dpuTemplate) {
		super(dpuTemplate, true);
		this.dpuTemplate = dpuTemplate;
	}

	public DPUTemplateRecord getDPUTemplateRecord() {
		return dpuTemplate;
	}
	
}
