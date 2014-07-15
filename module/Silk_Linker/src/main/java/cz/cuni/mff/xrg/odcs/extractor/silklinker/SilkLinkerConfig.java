package cz.cuni.mff.xrg.odcs.extractor.silklinker;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;

/**
 * Configuration for SilkLinker
 * 
 * @author tomasknap
 */
public class SilkLinkerConfig extends DPUConfigObjectBase {

    /**
     * Path to the config file driving the execution of Silk.
     */
    private String confFile = "";

    private String minConfirmedLinks = "0.9";

    private String minLinksToBeVerified = "0.0";

    private String confFileLabel = "";

    /**
     * Constructor
     */
    public SilkLinkerConfig() {
        confFile = null;
    }

    /**
     * Constructor
     * 
     * @param confFile
     *            Configuration file for Silk
     * @param confFileLabel
     *            Label for the configuration file from which the
     *            configuration was created
     * @param minConfirmed
     *            Minimum score for the links to be considered as
     *            confirmed
     * @param minToBeVerified
     *            Minimum score for the links to be considered as
     *            "to be verified"
     */
    public SilkLinkerConfig(String confFile, String confFileLabel,
            String minConfirmed, String minToBeVerified) {
        this.confFile = confFile;
        this.confFileLabel = confFileLabel;
        this.minConfirmedLinks = minConfirmed;
        this.minLinksToBeVerified = minToBeVerified;
    }

    /**
     * Constructor
     * 
     * @param confFile
     *            Configuration file for Silk
     */
    public SilkLinkerConfig(String confFile) {
        this.confFile = confFile;

    }

    /**
     * Gets configuration file
     * 
     * @return configuration file
     */
    String getSilkConf() {
        return confFile;
    }

    /**
     * Gets label for the configuration file from which the configuration was
     * created
     * 
     * @return Label for the configuration file from which the configuration was
     *         created
     */
    public String getConfFileLabel() {
        return confFileLabel;
    }

    public boolean isValid() {
        return confFile != null;
    }

    /**
     * Gets minimum score for the links to be considered as confirmed
     * 
     * @return Minimum score for the links to be considered as confirmed
     */
    public String getMinConfirmedLinks() {
        return minConfirmedLinks;
    }

    /**
     * Gets minimum score for the links to be considered as "to be verified"
     * 
     * @return Minimum score for the links to be considered as "to be verified"
     */
    public String getMinLinksToBeVerified() {
        return minLinksToBeVerified;
    }

    public String getConfFile() {
        return confFile;
    }

    public void setConfFile(String confFile) {
        this.confFile = confFile;
    }

}
