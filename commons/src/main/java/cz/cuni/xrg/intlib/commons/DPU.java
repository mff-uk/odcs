package cz.cuni.xrg.intlib.commons;

import cz.cuni.xrg.intlib.commons.configuration.ExceptionSetting;
import java.util.Map;
import java.util.jar.JarFile;


/**
 * Basic information of each DPU component.
 *
 * @author Jiri Tomes
 */
public interface DPU {

    /**
     * Return DPU' description. The description can be shown 
     * to the user as plain text.
     *
     * @return DPU' description
     */
    public String getDescription();

    public JarFile getJarFile();

    /**
     * The name of DPU component.
     *
     * @return DPU' name
     */
    public String getName();

    /**
     * Return DPU type (ETL). The type must correspond with implemented
     * sub-type.
     *
     * @return DPU' type
     */
    public Type getType();

    /**
     * Return DPU sub-type - concrete component for extract/transform/load data.
     *
     * @return DPU' sub-type
     */
    public SubType getSubtype();

    /**
     * Default setting for each DPU component.
     *
     * @return Configuration setting for DPU component.
     */
    public TemplateConfiguration getTemplateConfiguration();

    /**
     * Return current DPU settings. If the configuration dialog for DPU is open
     * then settings from dialog must be saved as current setting first.
     * Function can throw if the setting dialog is open and the setting in the
     * dialog is invalid.
     *
     * @return Serialised module settings.
     * @throws ExceptionSetting
     */
    public Map<String, Object> getSettings() throws ExceptionSetting;

    /**
     * Set DPU's settings. DPU settings.
     *
     * @param configuration Configuration to load.
     * @throws ExceptionSetting Thrown in case of invalid setting.
     */
    public void setSettings(Map<String, Object> configuration);
}
