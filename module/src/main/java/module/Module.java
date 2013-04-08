package module;

import gui.ConfigDialog;

import com.vaadin.ui.CustomComponent;

import cz.cuni.xrg.intlib.commons.Type;
import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import cz.cuni.xrg.intlib.commons.configuration.ConfigurationException;
import cz.cuni.xrg.intlib.commons.extractor.ExtractContext;
import cz.cuni.xrg.intlib.commons.extractor.ExtractException;
import cz.cuni.xrg.intlib.commons.module.GraphicalExtractor;

//#!expDialog/file:///e:/Tmp/Intlib/module-0.0.1.jar

public class Module implements GraphicalExtractor {

	private gui.ConfigDialog configDialog = null;
	
	private Configuration config = new Configuration();
	
	public Module() {
		// set initial configuration
		this.config.setValue(Config.Url.name(), "");
		this.config.setValue(Config.Login.name(), "");
		this.config.setValue(Config.Password.name(), "");
		this.config.setValue(Config.Query.name(), "CONSTRUCT {?s ?p ?o} where {?s ?p ?o}");
	}
	
	public void extract(ExtractContext context) throws ExtractException {
		// TODO Auto-generated method stub		
	}

	public Type getType() {
		return Type.EXTRACTOR;
	}

	public CustomComponent getConfigurationComponent() {
		// does dialog exist?
		if (this.configDialog == null) {
			// create it
			this.configDialog = new ConfigDialog();
			this.configDialog.setConfiguration(this.config);
		}
		return this.configDialog;
	}

	public Configuration getSettings() throws ConfigurationException {
		if (this.configDialog == null) {			
		} else {
			// get configuration from dialog
			Configuration conf = this.configDialog.getConfiguration();
			if (conf == null) {
				// in dialog is invalid configuration .. 
				return null;
			}
		}
		return this.config;
	}

	public void setSettings(Configuration configuration) {
		this.config = configuration;
		if (this.configDialog == null) {			
		} else {
			// set configuration for dialog
			this.configDialog.setConfiguration(this.config); 
		}
	}

}
