package cz.cuni.xrg.intlib.commons;

import cz.cuni.xrg.intlib.commons.configuration.*;

/**
 * Basic executive interface of each DPU component.
 *
 * @author Jiri Tomes
 */
public interface DPUExecutive {

    /**
     * Return DPU type (ETL). The type must correspond with implemented
     * sub-type.
     *
     * @return DPU' type
     */
    public Type getType();

    /**
     * Return current DPU settings. If the configuration dialog for DPU is open
     * then settings from dialog must be saved as current setting first.
     * Function can throw if the setting dialog is open and the setting in the
     * dialog is invalid.
     *
     * @return Serialised module settings.
     * @throws ConfigurationException
     */
    public Configuration getSettings() throws ConfigurationException;

    /**
     * Set DPU's settings. DPU settings.
     *
     * @param configuration Configuration to load.
     * @throws ConfigurationException Thrown in case of invalid setting.
     */
    public void setSettings(Configuration configuration);
}
