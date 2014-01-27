package cz.cuni.mff.xrg.odcs.dataunit.file.handlers;

import java.io.File;

/**
 * Handler for single record in {@link FileDataUnit}. In order to user
 * recast it into {@link DirectoryHandler} or {@link FileHandler}.
 * 
 * To determine if the {@link Handler} denotes file or directory you can use
 * class inheritance (see sample code below), or utilize {@link #asFile()} 
 * result of. You should use the first approach rather then the second.
 * 
 * To every {@link Handler} in {@link FileDataUnit} the user data (string) 
 * can be attached. The string should have reasonable size as it's stored in 
 * memory for whole time of existence of given {@link FileDataUnit}.
 * 
 * <pre>
 * {@code 
 * // some unknown handler
 * Handler handler;
 * if (handler instanceof DirectoryHandler) {
 *	// it's directory
 *	DirectoryHandler dir = (DirectoryHandler) handler;
 * } else if (handler instanceof FileHandler) {
 *	// it's file
 *	FileHandler file = (FileHandler)handler;
 * } else {
 *	// unknown handler
 * }
 * </pre>
 *
 * @author Petyr
 */
public interface Handler {

	/**
	 * Return path to file in {@link FileDataUnit}. This can be used to access
	 * even read only file. It's up to the programmer to not change the input
	 * data units.
	 *
	 * @return
	 */
	File asFile();

	/**
	 * Set user data for this handler.
	 *
	 * @param newUserData
	 */
	void setUserData(String newUserData);

	/**
	 * Return user data attached to this handler.
	 *
	 * @return
	 */
	String getUserData();
	
	/**
	 * @return name
	 */
	String getName();
	
}
