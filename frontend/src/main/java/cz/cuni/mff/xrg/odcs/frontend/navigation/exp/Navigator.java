package cz.cuni.mff.xrg.odcs.frontend.navigation.exp;

import com.vaadin.ui.CustomComponent;

/**
 * Navigator that manage navigation in the application.
 *
 * @author Petyr
 */
public interface Navigator {

    /**
     * Navigate to given object.
     *
     * @param clazz
     */
    void navigateTo(Class<?> clazz);

    /**
     * Navigate to given object with given configuration.
     *
     * @param clazz
     * @param parameter
     */
    void navigateTo(Class<?> clazz, Object parameter);

    /**
     * Add new class to the navigator.
     * @param <T>
     * @param clazz Type of view.
     * @param view View instance.
     */
    <T> void addView(Class<T> clazz, T view);
    
    /**
     * Add change listener.
     *
     * @param listener
     */
    void addChangeListener(ChangeListener listener);

    /**
     * Remove change listener.
     *
     * @param listener
     */
    void removeChangeListener(ChangeListener listener);

    /**
     * Add url parser.
     *
     * @param parser
     */
    void addUrlParser(UrlParser parser);

    /**
     * Remove url parser.
     *
     * @param parser
     */
    void removeUrlParser(UrlParser parser);

    /**
     * Add {@link Interpreter} that can be used to interpret views from 
     * {@link #addView(java.lang.Class, java.lang.Object) }.
     * @param interpreter 
     */
    void addInterpreter(Interpreter interpreter);
    
    /**
     * Remove {@link Interpreter} from listener.
     * @param interpreter 
     */
    void removeInterpreter(Interpreter interpreter);
    
    /**
     * Interface for listening to view changes before and after they occur.
     */
    public interface ChangeListener {

        /**
         * Invoked before the view is changed.
         *
         * @param oldView
         * @param newView
         * @return False it the view should not be changed.
         */
        public boolean beforeViewChange(Object oldView, Object newView);

        /**
         * Invoked after the view is successfully changed.
         *
         * @param newView
         */
        public void afterViewChange(Object newView);

    }

    /**
     * Parser of url fragment. The parsers are used to parse existing
     * url fragments and their translation into navigation. 
     */
    public interface UrlParser {

        public class UrlNavigation {

            /**
             * Class to navigate to.
             */
            public Class<?> clazz;

            /**
             * Parameter.
             */
            public Object parameter;

        }

        /**
         * Return {@link UrlNavigation} that corresponds to given url fragment.
         * Can return null if the url fragment is not recognized by the parser.
         *
         * @param urlFragment
         * @return
         */
        UrlNavigation parse(String urlFragment);

    }

    /**
     * Used to show certain object as a Vaadin's view.
     */
    public interface Interpreter {
        
        /**
         * Translate (interpret) given object as {@link CustomComponent}.
         * @param view
         * @return Null it the interpreter does not support given class.
         */
        CustomComponent interpret(Object view);
        
    }
    
}
