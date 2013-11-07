package cz.cuni.mff.xrg.odcs.frontend.gui.views;

/**
 * Interface for presenter that can change the application data.
 * 
 * @author Petyr
 */
public interface SavablePresenter extends Presenter {
    
    /**
     * Return true if there are unsaved changes in presenter.
     * @return 
     */
    boolean isModified();
    
    /**
     * Save data in presenter.
     */
    void save();
    
}
