package cz.cuni.mff.xrg.odcs.frontend.auxiliaries.dpu;

import com.vaadin.ui.UI;
import java.io.FileNotFoundException;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPURecord;
import cz.cuni.mff.xrg.odcs.commons.app.facade.ModuleFacade;
import cz.cuni.mff.xrg.odcs.commons.app.module.ModuleException;
import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.configuration.DPUConfigObject;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import cz.cuni.mff.xrg.odcs.frontend.AppEntry;

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
	public void saveConfig() throws ConfigException, DPUWrapException {
		if (configDialog == null) {
			return;
		}
		try {
			dpuRecord.setRawConf(configDialog.getConfig());
		} catch (ConfigException e) {
			throw e;
		} catch (Throwable e) {
			throw new DPUWrapException("Failed to save configuration.", e);
		}
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
			throws ModuleException, FileNotFoundException, DPUWrapException {
		// load configuration dialog
		try {
			loadConfigDialog();
		} catch(ModuleException e) {
			throw e;
		} catch(FileNotFoundException e) {
			throw e;
		} catch (Throwable e) {
			throw new DPUWrapException("Failed to load dialog.", e);
		}
		return configDialog;
	}

	/**
	 * If respective configuration dialog for wrapped DPU exist, then set it's
	 * configuration. Otherwise do nothing.
	 *
	 * @throws ConfigException
	 */
	public void configuredDialog()
			throws ConfigException, DPUWrapException {
		// set dialog configuration
		try {
			loadConfigIntoDialog();
		} catch (ConfigException e) {
			throw e;
		} catch (Throwable e) {
			throw new DPUWrapException("Failed to configure dpu's dialog.", e);
		}
	}

	/**
	 * Load the configuration dialog for {@link #dpuRecord} and store it into
	 * {@link #configDialog}. If the dialog is already loaded
	 * ({@link #configDialog} is not null) then nothing is done. If the
	 * {@link #dpuRecord} does not provide configuration dialog set
	 * {@link #configDialog} to null.
	 * Can possibly emit runtime exception.
	 * 
	 * @throws ModuleException
	 * @throws FileNotFoundException
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
		dpuRecord.loadInstance(((AppEntry)UI.getCurrent()).getBean(ModuleFacade.class));
		Object instance = dpuRecord.getInstance();
		// now try to load the dialog
		if (instance instanceof ConfigDialogProvider<?>) {
			ConfigDialogProvider<DPUConfigObject> dialogProvider;
			// 'unchecked casting' .. we check type in condition above
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
	 * {@link #configDialog}. Can possibly emit runtime exception.
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
