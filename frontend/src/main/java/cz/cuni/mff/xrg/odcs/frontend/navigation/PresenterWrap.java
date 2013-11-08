package cz.cuni.mff.xrg.odcs.frontend.navigation;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Presenter;

/**
 * Wrap presenter class so it can be used in navigator as a view.
 * 
 * @author Petyr
 */
class PresenterWrap implements View {

    /**
     * Wrapped presenter.
     */
    private final Presenter presenter;
    
    private String parameters;
    
    public PresenterWrap(Presenter presenter) {
        this.presenter = presenter;
    }
    
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        parameters = event.getParameters();
    }
    
    public Presenter getPresenter() {
        return presenter;
    }
    
    /**
     * Enter presenter with last parameters from 
     * {@link #enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)}
     * and return result of {@link Presenter#enter(java.lang.Object)}.
     * @return 
     */
    public Object enterPresenter() {
        return presenter.enter(parameters);
    }
    
}
