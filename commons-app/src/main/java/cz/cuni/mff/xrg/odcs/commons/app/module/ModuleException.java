package cz.cuni.mff.xrg.odcs.commons.app.module;

/**
 * Base exception used by {@link cz.cuni.mff.xrg.odcs.commons.app.facade.ModuleFacade}
 * and related classes.
 * 
 * @author Petyr
 *
 */
public class ModuleException extends Exception {

	public ModuleException(Throwable cause) {
        super(cause);
    }

    public ModuleException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleException(String message) {
        super(message);
    }	
}
