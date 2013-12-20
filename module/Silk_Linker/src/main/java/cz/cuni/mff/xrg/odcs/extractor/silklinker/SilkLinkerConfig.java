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

    public SilkLinkerConfig() {
        confFile = null;
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
}
