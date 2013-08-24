package cz.cuni.mff.xrg.intlib.extractor.silklinker;

import cz.cuni.xrg.intlib.commons.configuration.DPUConfigObject;

/**
 *
 * Put your DPU's configuration here.
 *
 */
public class SilkLinkerConfig implements DPUConfigObject {
    
    /**
     * Path to the config file driving the execution of Silk.
     */
    private String confFile;

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
