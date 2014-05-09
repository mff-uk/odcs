package cz.cuni.mff.xrg.odcs.dataunit.file.handlers;

/**
 * Provide additional functionality to manage the {@link cz.cuni.mff.xrg.odcs.dataunit.file.handlers.Handler}.
 * 
 * @author Petyr
 */
public interface ManageableHandler extends Handler {

    /**
     * @return True if this handler represents link to the existing file/directory.
     */
    boolean isLink();

}
