package cz.cuni.xrg.intlib.frontend.auxiliaries.dpu;

import java.io.FileNotFoundException;

import  cz.cuni.xrg.intlib.commons.app.dpu.DPURecord;
import cz.cuni.xrg.intlib.commons.app.module.ModuleException;
import cz.cuni.xrg.intlib.commons.configuration.Config;
import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.web.AbstractConfigDialog;
import cz.cuni.xrg.intlib.commons.web.ConfigDialogProvider;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;

/**
 * Class wrap {@line DPURecord} 
 * and provide functions that enable easy work with configuration 
 * and configuration dialog.
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
	private AbstractConfigDialog<Config> configDialog = null;
	
	protected DPURecordWrap(DPURecord dpuRecord) 
	{
		this.dpuRecord = dpuRecord;
	}
		
	/**
	 * Return configuration dialog for wrapped DPU. The configuration dialog 
	 * contains configuration of wrapped DPURecord.
	 * 
	 * If function throw ConfigException the dialog has been created 
	 * but the configuration from DPURecord is invalid. You can use
	 * the dialog with default configuration.
	 * @return
	 * @throws ModuleException
	 * @throws FileNotFoundException
	 * @throws ConfigException
	 */
	public AbstractConfigDialog<Config> getDialog() 
			throws ModuleException, FileNotFoundException, ConfigException  {
		// load configuration dialog
		loadConfigDialog();
		// set dialog configuration
		loadConfigIntoDialog();
		return configDialog;
	}
		
	/**
	 * Load the configuration dialog for {@link #dpuRecord} and store it into {@link #configDialog}.
	 * If the dialog is already loaded ({@link #configDialog} is not null) then nothing is done.
	 * If the {@link #dpuRecord} does not provide configuration dialog set {@link #configDialog} to null.
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
		dpuRecord.loadInstance( App.getApp().getModules() );
		instance = dpuRecord.getInstance();
		// now try to load the dialog
		if (instance instanceof ConfigDialogProvider<?>) {
			ConfigDialogProvider<Config> dialogProvider;
			dialogProvider = (ConfigDialogProvider<Config>)instance;
			// get configuration dialog
			configDialog = dialogProvider.getConfigurationDialog();					
		} else {
			// no configuration dialog
			configDialog = null;
		}
		
	}

	/**
	 * Try to load configuration from {@link #dpuRecord} into {@link #configDialog}.
	 * If the {@link #configDialog} does not exist nothing happen. If the 
	 * {@link #dpuRecord} does not contains any configuration the configuration for dialog
	 * is left untouched.
	 * @throws ConfigException
	 */
	private void loadConfigIntoDialog() throws ConfigException {
		if (configDialog == null) {
			// no dialog .. nothing to do 
			return;
		}
		Config conf = dpuRecord.getConf();
		if (conf == null) {
			// use default configuration
		} else {
			configDialog.setConfiguration(conf);
		}
	}
	
	/**
	 * Try to save configuration from {@link #configDialog} into {@link #dpuRecord}. If
	 * the {@link #configDialog} is null nothing happen. This function does not 
	 * save data into database.
	 * @throws ConfigException
	 */
	public void saveConfig() throws ConfigException {
		if (configDialog == null) {
			return;
		}
		dpuRecord.setConf( configDialog.getConfiguration() );
	}

}
