package cz.cuni.mff.xrg.odcs.frontend.dpu.dialog;

import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogContext;

/**
 * Implementation of {@link ConfigDialogContext}.
 * 
 * @author Petyr
 */
public class ConfigDialogContextImpl implements ConfigDialogContext {

	/**
	 * True in case that the dialog is used for template, false otherwise.
	 */
	private final boolean isTemplate;
	
	/**
	 *  Constructor.
	 *
	 * @param isTemplate Whether the dialog is used for DPU template.
	 */
	public ConfigDialogContextImpl(boolean isTemplate) {
		this.isTemplate = isTemplate;
	}
	
	@Override
	public boolean isTemplate() {
		return this.isTemplate;
	}
	
}
