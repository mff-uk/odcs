package cz.cuni.mff.xrg.odcs.frontend.auxiliaries.dpu;

import java.io.FileNotFoundException;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPURecord;
import cz.cuni.mff.xrg.odcs.commons.app.module.ModuleException;
import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.configuration.DPUConfigObject;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.App;

/**
 * Class wrap {
 *
 * @line DPURecord} and provide functions that enable easy work with
 * configuration and configuration dialog.
 *
 * @author Petyr
 *
 */
class DPURecordWrap {

	/**
	 * Wrapped DPU.
	 */
	private DPURecord dpuRecord = null;
	/**
	 * DPU's configuration dialog.
	 */
	private AbstractConfigDialog<DPUConfigObject> configDialog = null;

	protected DPURecordWrap(DPURecord dpuRecord) {
		this.dpuRecord = dpuRecord;
	}

	/**
	 * Try to save configuration from {@link #configDialog} into
	 * {@link #dpuRecord}. If the {@link #configDialog} is null nothing happen.
	 * This function does not save data into database.
	 *
	 * @throws ConfigException
	 */
	public void saveConfig() throws ConfigException {
		if (configDialog == null) {
			return;
		}
		dpuRecord.setRawConf(configDialog.getConfig());
	}

	/**
	 * Return configuration dialog for wrapped DPU. The configuration is not
	 * set. To set dialog configuration call {@link #configuredDialog}
	 *
	 * @return
	 * @throws ModuleException
	 * @throws FileNotFoundException
	 */
	public AbstractConfigDialog<DPUConfigObject> getDialog()
			throws ModuleException, FileNotFoundException {
		// load configuration dialog
		loadConfigDialog();
		return configDialog;
	}

	/**
	 * If respective configuration dialog for wrapped DPU exist, then set it's
	 * configuration. Otherwise do nothing.
	 *
	 * @throws ConfigException
	 */
	public void configuredDialog()
			throws ConfigException {
		// set dialog configuration
		loadConfigIntoDialog();
	}

	/**
	 * Load the configuration dialog for {@link #dpuRecord} and store it into
	 * {@link #configDialog}. If the dialog is already loaded
	 * ({@link #configDialog} is not null) then nothing is done. If the
	 * {@link #dpuRecord} does not provide configuration dialog set
	 * {@link #configDialog} to null.
	 *
	 * @throws ModuleException
	 * @throws FileNotFoundException
	 */
	private void loadConfigDialog() throws ModuleException, FileNotFoundException {
		if (configDialog == null) {
			// continue and load the dialog
		} else {
			// already loaded .. 
			return;
		}
		// first we need load instance of the DPU
		Object instance = null;
		dpuRecord.loadInstance(App.getApp().getModules());
		instance = dpuRecord.getInstance();
		// now try to load the dialog
		if (instance instanceof ConfigDialogProvider<?>) {
			ConfigDialogProvider<DPUConfigObject> dialogProvider;
			dialogProvider = (ConfigDialogProvider<DPUConfigObject>) instance;
			// get configuration dialog
			configDialog = dialogProvider.getConfigurationDialog();
		} else {
			// no configuration dialog
			configDialog = null;
		}

	}

	/**
	 * Try to load configuration from {@link #dpuRecord} into
	 * {@link #configDialog}.
	 *
	 * @throws ConfigException
	 */
	private void loadConfigIntoDialog() throws ConfigException {
		if (configDialog == null) {
			// no dialog .. nothing to do 
			return;
		}
		byte[] conf = dpuRecord.getRawConf();
		configDialog.setConfig(conf);
	}
}
