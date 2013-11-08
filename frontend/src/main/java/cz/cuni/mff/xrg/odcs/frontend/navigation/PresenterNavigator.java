package cz.cuni.mff.xrg.odcs.frontend.navigation;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.ui.SingleComponentContainer;
import com.vaadin.ui.UI;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Presenter;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Extends Vaadin's {@link Navigator} for possibility to navigate over
 * {@link Presenter}s.
 *
 * The navigator also cooperate with spring and do autodiscovery for beans that
 * implements {@link View} or {@link Presenter} and add them as views. The
 * views, that are obtained from spring, must have {@link Adress} annotation.
 *
 * @author Petyr
 */
public class PresenterNavigator extends Navigator {

    private static final Logger LOG
        = LoggerFactory.getLogger(PresenterNavigator.class);

    public PresenterNavigator(UI ui) {
        super(ui, new UriFragmentManager(ui.getPage()),
            new PresenterViewDisplay());
    }

    public void bind(SingleComponentContainer container, ApplicationContext context) {
        // set container 
        ((PresenterViewDisplay) getDisplay()).setContainer(container);
        // discover views and presenters from spring
        Collection<View> views = context.getBeansOfType(View.class).values();
        if (views == null) {
            LOG.warn("No views has been found.");
        } else {
            for (View view : views) {
                Address address = view.getClass().getAnnotation(Address.class);
                if (address == null) {
                    LOG.error("Autowired '{}' does not have Address annotation, it will be ignored!", view);
                } else {
                    // add to navigator
                    addView(address.url(), view);
                }
            }
        }
        // presenters
        Collection<Presenter> presenters = 
            context.getBeansOfType(Presenter.class).values();
        if (presenters == null) {
             LOG.warn("No presenters has been found.");
        } else {
            for (Presenter presenter : presenters) {
                Address address = presenter.getClass().getAnnotation(Address.class);
                if (address == null) {
                    LOG.error("Autowired '{}' does not have Address annotation, it will be ignored!", presenter);
                } else {
                    // create wrap class
                    PresenterWrap wrap = new PresenterWrap(presenter);
                    // add to navigator
                    addView(address.url(), wrap);
                }
            }
        }
    }
    
    /**
     * Navigate to given {@link Presenter} the presenter must have
     * {@link Address} annotation.
     *
     * @param presenter
     */
    public void navigateTo(Class<Presenter> presenter) {
        Address address = presenter.getAnnotation(Address.class);
        if (address == null) {
            throw new RuntimeException("There is no address for presenter"
                + presenter);
        }
        // we have address, so we let others do the work instead of us 
        navigateTo(address.url());
    }

    /**
     * Navigate to given {@link Presenter} the presenter must have
     * {@link Address} annotation.
     *
     * @param presenter
     * @param parameters
     */
    public void navigateTo(Class<Presenter> presenter, String parameters) {
        Address address = presenter.getAnnotation(Address.class);
        if (address == null) {
            throw new RuntimeException("There is no address for presenter"
                + presenter);
        }
        // we have address, so we let others do the work instead of us 
        navigateTo(address.url() + '/' + parameters);
    }

}
