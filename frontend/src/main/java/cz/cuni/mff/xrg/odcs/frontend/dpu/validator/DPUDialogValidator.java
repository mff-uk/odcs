package cz.cuni.mff.xrg.odcs.frontend.dpu.validator;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.module.DPUValidator;
import cz.cuni.mff.xrg.odcs.commons.app.module.DPUValidatorException;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;

/**
 * Validate DPU's dialog.
 *
 * @author Petyr
 *
 */
public class DPUDialogValidator implements DPUValidator {

	@Override
	public void validate(DPUTemplateRecord dpu, Object dpuInstance)
			throws DPUValidatorException {
		if (dpuInstance instanceof ConfigDialogProvider) {
			@SuppressWarnings("rawtypes")
			ConfigDialogProvider provider = (ConfigDialogProvider) dpuInstance;
			try {
				@SuppressWarnings({"rawtypes", "unused"})
				AbstractConfigDialog dialog = provider.getConfigurationDialog();
			} catch (Throwable t) {
				// catch everything ..
				throw new DPUValidatorException("Failed to load dialog for exception: " + t.getMessage());
			}
		} else {
			// no dialog
		}
	}
}
