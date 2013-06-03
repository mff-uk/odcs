package cz.cuni.xrg.intlib.commons.module.dpu;

import com.vaadin.ui.CustomComponent;

import cz.cuni.xrg.intlib.commons.DPUExecutive;
import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import cz.cuni.xrg.intlib.commons.configuration.ConfigurationException;
import cz.cuni.xrg.intlib.commons.module.gui.AbstractConfigDialog;
import cz.cuni.xrg.intlib.commons.web.DialogProvider;

/**
 * Abstract base implementation for DPUs.
 * 
 * @author Petyr
 *
 */
public abstract class AbstractDPU implements DPUExecutive, DialogProvider {

	/**
	 * Configuration dialog for DPU.
	 */
	protected AbstractConfigDialog configurationDialog;
	
    /**
     * DPU configuration.
     */
    protected Configuration config = null;
	
	@Override
	public void saveConfiguration(Configuration configuration)
			throws ConfigurationException {
        this.config = configuration;
        if (this.configurationDialog == null) {
        } else {
            // also set configuration for dialog
            this.configurationDialog.getConfiguration(this.config);
        }		
	}

	@Override
	public void loadConfiguration(Configuration configuration)
			throws ConfigurationException {
        if (this.configurationDialog == null) {
        } else {
            // get configuration from dialog
            this.configurationDialog.setConfiguration(configuration);
        }   
	}

	@Override
	public CustomComponent getConfigurationComponent(Configuration configuration) {
        if (this.configurationDialog == null) {
            // create it
            this.configurationDialog = createConfigurationDialog();
            this.configurationDialog.setConfiguration(configuration);
        }
        return this.configurationDialog;
	}

	/**
	 * Return instance of configuration dialog for this DPU.
	 * 
	 * @return Instance of AbstractConfigDialog.
	 */
	public abstract AbstractConfigDialog createConfigurationDialog(); 
}
