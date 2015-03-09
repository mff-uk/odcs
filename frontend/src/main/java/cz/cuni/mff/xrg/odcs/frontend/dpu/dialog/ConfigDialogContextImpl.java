package cz.cuni.mff.xrg.odcs.frontend.dpu.dialog;

import java.util.Locale;

import eu.unifiedviews.dpu.config.vaadin.ConfigDialogContext;
import eu.unifiedviews.util.Cryptography;

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

    private final Locale locale;

    private Cryptography cryptography;

    /**
     * Constructor.
     *
     * @param isTemplate
     *            Whether the dialog is used for DPU template.
     */
    public ConfigDialogContextImpl(boolean isTemplate, Locale locale, Cryptography cryptography) {
        this.isTemplate = isTemplate;
        this.locale = locale;
        this.cryptography = cryptography;
    }

    @Override
    public boolean isTemplate() {
        return this.isTemplate;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public Cryptography getCryptography() {
        return cryptography;
    }

}
