package cz.cuni.xrg.intlib.commons;

import cz.cuni.xrg.intlib.commons.configuration.*;

/**
 * Basic executive interface of each DPU component.
 *
 * @author Jiri Tomes
 * @author Petyr
 */
@Deprecated
public interface DPUExecutive {

    /**
     * Return DPU type (ETL). The type must correspond with implemented
     * sub-type.
     *
     * @return DPU' type
     */
    public DpuType getType();

    /**
     * Load default (empty) configuration into given Configuration instance class. 
     * @param configuration Configuration instance.
     */
    public void saveConfigurationDefault(Configuration configuration);
    
    /**
     * Load current DPU setting into given Configuration instance. If the 
     * configuration dialog is open, the configuration in dialog is consider
     * to be the current DPU setting. If the dialog is open and the configuration 
     * is invalid then ConfigurationException should be throw.
     * @param configuration Configuration instance.
     * @throws ConfigurationException Thrown in case of invalid setting.
     */
    public void saveConfiguration(Configuration configuration) throws ConfigurationException;    
    
    /**
     * DPU should load configuration from given Configuration instance. DPU can save 
     * the reference on given instance but should not modify it. If the configuration
     * dialog for given DPU is opened then the configuration is also loaded into dialog. 
     * @param configuration Configuration instance.
     * @throws ConfigurationException
     */
    public void loadConfiguration(Configuration configuration) throws ConfigurationException;
}
