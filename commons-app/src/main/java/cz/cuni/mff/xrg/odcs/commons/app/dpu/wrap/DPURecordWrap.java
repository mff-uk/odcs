package cz.cuni.mff.xrg.odcs.commons.app.dpu.wrap;

import com.vaadin.ui.UI;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPURecord;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.dialog.ConfigDialogContextImpl;
import cz.cuni.mff.xrg.odcs.commons.app.facade.ModuleFacade;
import cz.cuni.mff.xrg.odcs.commons.app.i18n.Messages;
import cz.cuni.mff.xrg.odcs.commons.app.module.ModuleException;
import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogContext;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.InitializableConfigDialog;

import java.io.FileNotFoundException;
import java.util.Locale;

/**
 * Class wrap {@line DPURecord} and provide functions that enable easy work with
 * configuration and configuration dialog.
 *
 * @author Petyr
 */
public class DPURecordWrap {
    /**
     * Wrapped DPU.
     */
    private DPURecord dpuRecord = null;

    /**
     * DPU's configuration dialog.
     */
    private AbstractConfigDialog<?> configDialog = null;

    /**
     * True if represents the template.
     */
    private final boolean isTemplate;

    private Locale locale;

    private ModuleFacade moduleFacade;

    /**
     * True if the {@link #configuredDialog()} has been called.
     */
    private boolean dialogConfigured = false;

    protected DPURecordWrap(DPURecord dpuRecord, boolean isTemplate, Locale locale, ModuleFacade moduleFacade) {
        this.dpuRecord = dpuRecord;
        this.isTemplate = isTemplate;
        this.locale = locale;
        this.moduleFacade = moduleFacade;
    }

    /**
     * Try to save configuration from {@link #configDialog} into {@link #dpuRecord}. If the {@link #configDialog} is null nothing happen.
     * This function does not save data into database.
     *
     * @throws eu.unifiedviews.dpu.config.DPUConfigException
     * @throws DPUWrapException
     */
    public void saveConfig() throws DPUConfigException, DPUWrapException {
        if (configDialog == null) {
            return;
        }
        try {
            final String config = configDialog.getConfig();
            dpuRecord.setRawConf(config);
        } catch (DPUConfigException e) {
            throw e;
        } catch (Throwable e) {
            throw new DPUWrapException(Messages.getString("DPURecordWrap.save"), e);
        }
    }

    /**
     * Return configuration dialog for wrapped DPU. The configuration is not
     * set. To set dialog configuration call {@link #configuredDialog}
     *
     * @return configuration dialog for wrapped DPU
     * @throws cz.cuni.mff.xrg.odcs.commons.app.module.ModuleException
     * @throws java.io.FileNotFoundException
     */
    public AbstractConfigDialog<?> getDialog()
            throws ModuleException, FileNotFoundException, DPUWrapException {
        // load configuration dialog
        try {
            loadConfigDialog();
        } catch (ModuleException | FileNotFoundException e) {
            throw e;
        } catch (Throwable e) {
            throw new DPUWrapException(Messages.getString("DPURecordWrap.load"), e);
        }
        return configDialog;
    }

    /**
     * If respective configuration dialog for wrapped DPU exist, then set it's
     * configuration. Otherwise do nothing.
     *
     * @throws eu.unifiedviews.dpu.config.DPUConfigException
     * @throws DPUWrapException
     */
    public void configuredDialog()
            throws DPUConfigException, DPUWrapException {
        // set dialog configuration
        try {
            loadConfigIntoDialog();
        } catch (DPUConfigException e) {
            throw e;
        } catch (Throwable e) {
            throw new DPUWrapException(Messages.getString("DPURecordWrap.configure"), e);
        }
    }

    /**
     * Check if the configuration in configuration dialog has been changed.
     * The configuration is assumed to be changed if it satisfy all the
     * following conditions:
     * <ul>
     * <li>DPU has configuration dialog.</li>
     * <li>The dialog has been obtained by calling {@link #getDialog()}</li> <li><li><li> The dialog has been configurated by calling {@link #configuredDialog()}</li>
     * </ul>
     *
     * @return True if the configuration changed.
     * @throws DPUWrapException
     */
    public boolean hasConfigChanged() throws DPUWrapException {
        if (configDialog == null || !dialogConfigured) {
            return false;
        }
        // ok we satisfy necesary conditions, we may ask the dialog
        // for changes
        try {
            final boolean isChanged = configDialog.hasConfigChanged();
            return isChanged;
        } catch (Exception ex) {
            throw new DPUWrapException(Messages.getString("DPURecordWrap.exception"), ex);
        }
    }

    /**
     * Return description from the dialog.
     *
     * @return Null in case of no dialog.
     */
    public String getDescription() {
        if (configDialog == null) {
            return null;
        }
        return configDialog.getDescription();
    }

    /**
     * Load the configuration dialog for {@link #dpuRecord} and store it into {@link #configDialog}. If the dialog is already loaded
     * ({@link #configDialog} is not null) then nothing is done. If the {@link #dpuRecord} does not provide configuration dialog set {@link #configDialog} to
     * null.
     * Can possibly emit runtime exception.
     *
     * @throws cz.cuni.mff.xrg.odcs.commons.app.module.ModuleException
     * @throws java.io.FileNotFoundException
     */
    @SuppressWarnings("unchecked")
    private void loadConfigDialog() throws ModuleException, FileNotFoundException {
        if (configDialog == null) {
            // continue and load the dialog
        } else {
            // already loaded ..
            return;
        }
        // first we need load instance of the DPU
        dpuRecord.loadInstance(moduleFacade);
        Object instance = dpuRecord.getInstance();
        // now try to load the dialog
        if (instance instanceof ConfigDialogProvider<?>) {
            ConfigDialogProvider<?> dialogProvider;
            // 'unchecked casting' .. we check type in condition above
            dialogProvider = (ConfigDialogProvider<?>) instance;
            // get configuration dialog
            configDialog = dialogProvider.getConfigurationDialog();
            if (configDialog != null) {
                // setup the dialog
                final ConfigDialogContext context = new ConfigDialogContextImpl(isTemplate, locale);
                configDialog.setContext(context);
                if (configDialog instanceof InitializableConfigDialog) {
                    ((InitializableConfigDialog) configDialog).initialize();
                }
            }
        } else {
            // no configuration dialog
            configDialog = null;
        }

    }

    /**
     * Try to load configuration from {@link #dpuRecord} into {@link #configDialog}. Can possibly emit runtime exception.
     *
     * @throws eu.unifiedviews.dpu.config.DPUConfigException
     */
    private void loadConfigIntoDialog() throws DPUConfigException {
        if (configDialog == null) {
            // no dialog .. nothing to do
            return;
        }
        // we try to configure the dialog
        dialogConfigured = true;
        configDialog.setConfig(dpuRecord.getRawConf());
    }

}
