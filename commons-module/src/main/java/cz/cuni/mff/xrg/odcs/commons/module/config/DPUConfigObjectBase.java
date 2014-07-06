package cz.cuni.mff.xrg.odcs.commons.module.config;

import eu.unifiedviews.dpu.config.DPUConfig;

/**
 * Base class for DPU's configuration. Provide default implementation
 * of {@link #isValid()} method.
 * 
 * @author Petyr
 */
public class DPUConfigObjectBase implements DPUConfig {

    @Override
    public boolean isValid() {
        // as default implementation return true
        return true;
    }

    @Override
    public void onSerialize() {

    }

    @Override
    public void onDeserialize() {

    }

}
