package cz.cuni.mff.xrg.odcs.extractor.silklinker;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;

/**
 *
 * Put your DPU's configuration here.
 *
 */
public class SilkLinkerConfig extends DPUConfigObjectBase {

    /**
     * Path to the config file driving the execution of Silk.
     */
    private String confFile = null;
    
    private String minConfirmedLinks = "0.9";
    private String minLinksToBeVerified = "0.0";

    public SilkLinkerConfig() {
        confFile = null;
    }

    public SilkLinkerConfig(String confFile, String minConfirmed, String minToBeVerified) {
        this.confFile = confFile;
        this.minConfirmedLinks = minConfirmed;
        this.minLinksToBeVerified = minToBeVerified;
    }
    
    public SilkLinkerConfig(String confFile) {
        this.confFile = confFile;
        
    }

    String getSilkConf() {
        return confFile;
    }

    @Override
    public boolean isValid() {
        return confFile != null;
    }

    public String getConfFile() {
        return confFile;
    }

    public String getMinConfirmedLinks() {
        return minConfirmedLinks;
    }

    public String getMinLinksToBeVerified() {
        return minLinksToBeVerified;
    }

    
    
    
}
