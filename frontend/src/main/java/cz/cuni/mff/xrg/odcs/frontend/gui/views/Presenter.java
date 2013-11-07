package cz.cuni.mff.xrg.odcs.frontend.gui.views;

/**
 * Base interface for presenter classes. The presenters represents
 * the possible views in application.
 * 
 * @author Petyr
 */
public interface Presenter {
    
    /**
     * Activate presenter. Should return the graphical user interface object
     * that will be set as main view for application.
     * @return 
     */
    Object enter();
    
}
