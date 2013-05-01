package cz.cuni.xrg.intlib.commons;

import cz.cuni.xrg.intlib.commons.configuration.*;
import cz.cuni.xrg.intlib.commons.repository.LocalRepo;

/**
 * Basic executive interface of each DPU component.
 *
 * @author Jiri Tomes
 * @author Petyr
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
     * @return Serialised module settings.
     * @throws ConfigurationException
     */
    public Configuration getSettings() throws ConfigurationException;

    /**
     * Set DPU's settings.
     * @param configuration Configuration to load.
     * @throws ConfigurationException Thrown in case of invalid setting.
     */
    public void setSettings(Configuration configuration) throws ConfigurationException;

    /**
     * Get repository where RDF data are stored.
     * @return LocalRepo
     */
    @Deprecated
    public LocalRepo getLocalRepo();

    /**
     * Set repository as goal of local storage RDF data.
     * @param localRepo
     */
    @Deprecated
    public void setLocalRepo(LocalRepo localRepo);
}
