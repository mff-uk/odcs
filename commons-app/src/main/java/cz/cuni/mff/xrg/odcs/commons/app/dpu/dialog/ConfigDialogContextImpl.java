package cz.cuni.mff.xrg.odcs.commons.app.dpu.dialog;

import eu.unifiedviews.helpers.dpu.config.ConfigDialogContext;

import java.util.Locale;

/**
 * Implementation of {@link eu.unifiedviews.helpers.dpu.config.ConfigDialogContext}.
 *
 * @author Petyr
 */
public class ConfigDialogContextImpl implements ConfigDialogContext {

    /**
     * True in case that the dialog is used for template, false otherwise.
     */
    private final boolean isTemplate;

    private final Locale locale;

    /**
     * Constructor.
     *
     * @param isTemplate
     *            Whether the dialog is used for DPU template.
     */
    public ConfigDialogContextImpl(boolean isTemplate, Locale locale) {
        this.isTemplate = isTemplate;
        this.locale = locale;
    }

    @Override
    public boolean isTemplate() {
        return this.isTemplate;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }
}
