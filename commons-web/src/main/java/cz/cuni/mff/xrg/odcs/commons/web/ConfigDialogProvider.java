package cz.cuni.mff.xrg.odcs.commons.web;

import eu.unifiedviews.dpu.config.DPUConfig;

/**
 * Interface which provides graphical configuration dialog associated with the
 * given DPU
 * 
 * @author Petyr
 * @param <C>
 */
public interface ConfigDialogProvider<C extends DPUConfig> {

    /**
     * @return Configuration dialog.
     */
    public AbstractConfigDialog<C> getConfigurationDialog();

}
