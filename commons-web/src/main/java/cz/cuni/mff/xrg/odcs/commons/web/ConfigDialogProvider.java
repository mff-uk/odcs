package cz.cuni.mff.xrg.odcs.commons.web;


/**
 * Interface which provides graphical configuration dialog associated with the
 * given DPU
 * 
 * @author Petyr
 * @param <C>
 */
public interface ConfigDialogProvider<C> {

    /**
     * @return Configuration dialog.
     */
    public AbstractConfigDialog<C> getConfigurationDialog();

}
