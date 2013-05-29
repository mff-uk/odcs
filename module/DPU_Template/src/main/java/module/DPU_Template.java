package module;

import gui.ConfigDialog;

import com.vaadin.ui.CustomComponent;

import cz.cuni.xrg.intlib.commons.DpuType;
import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import cz.cuni.xrg.intlib.commons.configuration.ConfigurationException;
import cz.cuni.xrg.intlib.commons.extractor.ExtractContext;
import cz.cuni.xrg.intlib.commons.extractor.ExtractException;
import cz.cuni.xrg.intlib.commons.web.*;

public class DPU_Template implements GraphicalExtractor { // TODO 1: Select super interface here

    /**
     * Configuration component.
     */
    private gui.ConfigDialog configDialog = null;
	
    /**
     * DPU configuration.
     */
    private Configuration config = null;
        
	@Override
	public DpuType getType() {
		// TODO 2:
		return DpuType.EXTRACTOR;
	}

	@Override
	public void saveConfigurationDefault(Configuration configuration) {
		// TODO 3: Store default configuration into configuration	
		configuration.setValue("myNumer", 3);
		configuration.setValue("myString", "Configuration ...");
	}

	@Override
	public void saveConfiguration(Configuration configuration)
			throws ConfigurationException {
		// TODO 4:
        this.config = configuration;
        if (this.configDialog == null) {
        } else {
            // also set configuration for dialog
            this.configDialog.getConfiguration(this.config);
        }
	}

	@Override
	public void loadConfiguration(Configuration configuration)
			throws ConfigurationException {
		// TODO 5:
        if (this.configDialog == null) {
        } else {
            // get configuration from dialog
            this.configDialog.setConfiguration(configuration);
        }   		
	}

	@Override
	public CustomComponent getConfigurationComponent(Configuration configuration) {
		// TODO 6:
        // does dialog exist?
        if (this.configDialog == null) {
            // create it
            this.configDialog = new ConfigDialog();
            this.configDialog.setConfiguration(configuration);
        }
        return this.configDialog;
	} 

	// TODO 7. implement missing method based on implemented interface
	
}
