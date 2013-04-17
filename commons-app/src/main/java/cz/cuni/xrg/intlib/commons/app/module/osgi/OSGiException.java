package cz.cuni.xrg.intlib.commons.app.module.osgi;

import cz.cuni.xrg.intlib.commons.app.module.ModuleException;

/**
 * OSGi framework exception.
 * @author Petyr
 *
 */
public class OSGiException extends ModuleException {

	/**
	 * @param message Exception message.
	 * @param ex Original exception.
	 */	
	public OSGiException(String message, Exception ex) {
		super(message + " reason: " + ex.getMessage() , ex);
	}

}
