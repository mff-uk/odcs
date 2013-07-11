package cz.cuni.xrg.intlib.frontend.browser;

import java.io.File;

import com.vaadin.ui.CustomComponent;

import cz.cuni.xrg.intlib.commons.data.DataUnitType;

/**
 * Interface for DataUnit browsers
 *
 * @author Petyr
 *
 */
public abstract class DataUnitBrowser extends CustomComponent {

	/**
	 * Load DataUnit context from given directory and show it in directory.
	 * @param directory Directory with stored @{link cz.cuni.xrg.intlib.commons.data.DataUnit} context.
	 * @throws Exception
	 */
	public abstract void loadDataUnit(File directory) throws Exception;


	/**
	 * The method is called before the component is shown. Initialise
	 * the user interface here.
	 */
	public abstract void enter();
}
