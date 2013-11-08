package cz.cuni.mff.xrg.odcs.frontend.navigation.exp;

import com.vaadin.server.Page;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Petyr
 */
public class NavigatorImpl implements Navigator, UriFragmentChangedListener {

    private final Page page;

    private final Panel navigationPanel;

    private final List<ChangeListener> changeListeners = new LinkedList<>();

    private final List<UrlParser> urlParsers = new LinkedList<>();

    private final List<Interpreter> interpreters = new LinkedList<>();

    protected final Map<Class<?>, Object> views = new HashMap<>();

    public NavigatorImpl(UI ui, Panel panel) {
        page = ui.getPage();
        navigationPanel = panel;
        // register as as a listener
        page.addUriFragmentChangedListener(this);
        // do initial navigation
        navigate(page.getUriFragment());
    }

    @Override
    public void navigateTo(Class<?> clazz) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void navigateTo(Class<?> clazz, Object parameter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> void addView(Class<T> clazz, T view) {
        views.put(clazz, view);
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        changeListeners.add(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        changeListeners.remove(listener);
    }

    @Override
    public void addUrlParser(UrlParser parser) {
        urlParsers.add(parser);
    }

    @Override
    public void removeUrlParser(UrlParser parser) {
        urlParsers.remove(parser);
    }

    @Override
    public void addInterpreter(Interpreter interpreter) {
        interpreters.add(interpreter);
    }

    @Override
    public void removeInterpreter(Interpreter interpreter) {
        interpreters.remove(interpreter);
    }

    @Override
    public void uriFragmentChanged(Page.UriFragmentChangedEvent event) {
        navigate(event.getUriFragment());
    }

    /**
     * Do navigation based on current url.
     * @param url 
     */
    protected void navigate(String url) {
        
    }
    
    protected boolean fireBeforeChange(Object oldView, Object newView) {
        for (ChangeListener l : changeListeners) {
            if (!l.beforeViewChange(oldView, newView)) {
                return false;
            }
        }
        return true;
    }

    protected void fireAfterChange(Object newView) {
        for (ChangeListener l : changeListeners) {
            l.afterViewChange(newView);
        }
    }

}
