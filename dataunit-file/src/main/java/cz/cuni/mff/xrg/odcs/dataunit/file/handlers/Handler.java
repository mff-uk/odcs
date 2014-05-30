package cz.cuni.mff.xrg.odcs.dataunit.file.handlers;

import java.io.File;

/**
 * Handler for single record in {@link cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnit}. In order to user
 * recast it into {@link DirectoryHandler} or {@link FileHandler}.
 * To determine if the {@link Handler} denotes file or directory you can use
 * class inheritance (see sample code below), or utilize {@link #asFile()} result of. You should use the first approach rather then the second.
 * Every {@link Handler} in {@link cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnit} can holds user data.
 * The string should have reasonable size as it's stored in memory for whole
 * time of existence of given {@link cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnit}.
 * 
 * <pre>
 * {
 *     &#064;code
 *     // some unknown handler
 *     Handler handler;
 *     if (handler instanceof DirectoryHandler) {
 *         // it's directory
 *         DirectoryHandler dir = (DirectoryHandler) handler;
 *     } else if (handler instanceof FileHandler) {
 *         // it's file
 *         FileHandler file = (FileHandler) handler;
 *     } else {
 *         // unknown handler
 *     }
 * }
 * </pre>
 * 
 * Every handler can hold user data, that can be used to add additional info to
 * the handler. The user data should be reasonable small as they are hold in
 * memory.
 * 
 * @author Petyr
 */
public interface Handler {

    /**
     * Return path to file in {@link cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnit}. This can be used
     * to access even read only file. It's up to the programmer to not change
     * the input data units.
     * 
     * @return File representation of this handler.
     */
    public File asFile();

    /**
     * Set user data for this handler, the user data should be reasonable small.
     * 
     * @param newUserData
     *            New user data, that will be attached to this handler.
     */
    public void setUserData(String newUserData);

    /**
     * @return User data attached to this handler.
     */
    public String getUserData();

    /**
     * @return Name of the handler.
     */
    public String getName();

    /**
     * Returned path is relative to the root of {@link cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnit}. To get access
     * use {@link #asFile()} instead.
     * 
     * @return String representation of path from the root of respective {@link cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnit}, can be
     *         used as {@link Handler}'s id.
     */
    String getRootedPath();

    /**
     * Normalizes file name of the create file, so that DPU developer will not get
     * warning when creating file containing special characters that may not be supported on certain platforms
     * 
     * @param origString
     *            Original file name suggested by DPU developer
     * @return Normalized file name which will be created
     */
    public String normalizeFileName(String origString);
}
