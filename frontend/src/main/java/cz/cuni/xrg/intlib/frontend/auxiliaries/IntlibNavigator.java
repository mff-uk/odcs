package cz.cuni.xrg.intlib.frontend.auxiliaries;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.SingleComponentContainer;
import com.vaadin.ui.UI;

/**
 * Simple extender of {@link Navigator} class, allowing to display PipelineEdit view correctly.
 *
 * @author Bogo
 */
public class IntlibNavigator extends Navigator {
    
    public IntlibNavigator(UI ui, SingleComponentContainer container) {
        super(ui, container);
    }

    @Override
    public void navigateTo(String navigationState) {
        if(navigationState.contains("PipelineEdit")) {
            App.getApp().getMain().setSizeUndefined();
        } else {
            App.getApp().getMain().setWidth(100, Unit.PERCENTAGE);
        }
        super.navigateTo(navigationState);
    }
    
    
    
}
