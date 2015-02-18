package cz.cuni.mff.xrg.odcs.commons.app.dpu.validator;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.i18n.Messages;
import cz.cuni.mff.xrg.odcs.commons.app.module.DPUValidator;
import cz.cuni.mff.xrg.odcs.commons.app.module.DPUValidatorException;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Validate DPU's dialog.
 * 
 * @author Petyr
 */
@Component
class ConfigurationDialogValidator implements DPUValidator {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationDialogValidator.class);

    @Override
    public void validate(DPUTemplateRecord dpu, Object dpuInstance)
            throws DPUValidatorException {
        if (dpuInstance instanceof ConfigDialogProvider) {
            @SuppressWarnings("rawtypes")
            ConfigDialogProvider provider = (ConfigDialogProvider) dpuInstance;
            try {
                @SuppressWarnings({ "rawtypes", "unused" })
                AbstractConfigDialog dialog = provider.getConfigurationDialog();
            } catch (Throwable t) {
                // catch everything ..
                LOG.error("Dialog load failed.", t);
                throw new DPUValidatorException(Messages.getString("ConfigurationDialogValidator.exception") + t.getMessage());
            }
        } else {
            // no dialog
        }
    }
}
