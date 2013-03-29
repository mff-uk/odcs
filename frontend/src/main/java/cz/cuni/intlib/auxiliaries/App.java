package cz.cuni.intlib.auxiliaries;

import com.vaadin.ui.UI;

import cz.cuni.intlib.frontend.AppEntry;

/**
 * Class provide access to application data.
 * The purpose of the class is to wrap some function class
 * and made writing code easier.
 * 
 * Class can not be instantiated.
 * 
 * @author Petyr
 *
 */
public class App {

	/**
	 * Private ctor, class should not be instantiated.
	 */
	private App() {
	}

	/**
	 * Return instance of current AppEntry class.
	 * @return
	 */
	public static cz.cuni.intlib.frontend.AppEntry getApp() {
		return (AppEntry)UI.getCurrent();
	}
	
	/**
	 * Return access to data access class.
	 * @return
	 */
	public static cz.cuni.intlib.frontend.data.DataAccess getDataAccess() {
		return getApp().getDataAccess();
	}
}
