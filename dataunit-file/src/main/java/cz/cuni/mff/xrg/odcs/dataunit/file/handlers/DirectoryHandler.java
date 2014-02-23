package cz.cuni.mff.xrg.odcs.dataunit.file.handlers;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnit;
import cz.cuni.mff.xrg.odcs.dataunit.file.options.OptionsAdd;
import java.io.File;
import java.util.Collection;

/**
 * Represent a directory from {@link FileDataUnit}. The sample how to 
 * iterate over directory content is in documentation for {@link FileDataUnit}.
 *
 * @author Petyr
 */
public interface DirectoryHandler extends Collection<Handler>, Handler {

	/**
	 * Create new file of given name and add it to this directory. If the file
	 * with given name already exists then the handler to the existing file is
	 * returned.
	 *
	 * @param name File name please respect the limitation of file system for
	 * file naming.
	 * @return Null if given name is used by already existing directory.
	 * @throws cz.cuni.mff.xrg.odcs.commons.data.DataUnitException
	 */
	FileHandler addNewFile(String name) throws DataUnitException;

	/**
	 * Add existing file to the directory. In every case the file of same name
	 * Based on {@link OptionsAdd#isLink} the following action is taken:
	 * <ul>
	 * <li><b>isLink = true</b> Empty file is created in directory. Every to
	 * that file will be redirected to the given file.</li>
	 * <li><b>isLink = false</b> Copy of given file will be created in
	 * directory.</li>
	 * </ul>
	 * In case of the name of file is already used the same policy as in case of
	 * {@link #addNewFile(java.lang.String)} is used.
	 *
	 * @param file Path to existing file.
	 * @param options Options used do add file.
	 * @return Null if given name is used by already existing directory.
	 * @throws cz.cuni.mff.xrg.odcs.commons.data.DataUnitException
	 */
	FileHandler addExistingFile(File file, OptionsAdd options)
			throws DataUnitException;

	/**
	 * Create a new directory of given name and add it to this directory.If
	 * directory with given name already exists then the handler to the existing
	 * directory is returned.
	 *
	 * @param name Directory name please respect the limitation of file system
	 * for directory naming.
	 * @return Null if given name is used by already existing file.
	 * @throws cz.cuni.mff.xrg.odcs.commons.data.DataUnitException
	 */
	DirectoryHandler addNewDirectory(String name) throws DataUnitException;

	/**
	 * Add existing directory recursively. The semantic is similar to the
	 * {@link #addExistingFile(java.io.File, cz.cuni.mff.xrg.odcs.dataunit.file.options.OptionsAdd)}
	 * method.
	 *
	 * @param directory Path to existing directory.
	 * @param options Options used do add file.
	 * @return Null if given name is used by already existing file.
	 * @throws cz.cuni.mff.xrg.odcs.commons.data.DataUnitException
	 */
	DirectoryHandler addExistingDirectory(File directory, OptionsAdd options)
			throws DataUnitException;

	/**
	 * Same as calling the
	 * {@link #add(cz.cuni.mff.xrg.odcs.dataunit.file.handlers.Handler, cz.cuni.mff.xrg.odcs.dataunit.file.options.OptionsAdd)}
	 * with the default {@link OptionsAdd} parameter.
	 *
	 * @param e Handler to add.
	 * @return True if the handler has been added.
	 */
	@Override
	boolean add(Handler e);

	/**
	 * Based on given handler type just recall 
	 * {@link #addExistingDirectory(java.io.File, cz.cuni.mff.xrg.odcs.dataunit.file.options.OptionsAdd) }
	 * or 
	 * {@link #addExistingFile(java.io.File, cz.cuni.mff.xrg.odcs.dataunit.file.options.OptionsAdd) }.
	 * Also copy the user data.
	 *
	 * @param e Handler to add.
	 * @param options Options used do add handler.
	 * @return True if the handler has been added.
	 */
	boolean add(Handler e, OptionsAdd options);

	/**
	 * Same as calling the
	 * {@link #addAll(java.util.Collection, cz.cuni.mff.xrg.odcs.dataunit.file.options.OptionsAdd)}
	 * with the default {@link OptionsAdd} parameter.
	 *
	 * @param c Collection of handlers to add.
	 * @return True at least one element has been added.
	 */
	@Override
	boolean addAll(Collection<? extends Handler> c);

	/**
	 * Call
	 * {@link #add(cz.cuni.mff.xrg.odcs.dataunit.file.handlers.Handler, cz.cuni.mff.xrg.odcs.dataunit.file.options.OptionsAdd)}
	 * on every element of given collection.
	 *
	 * @param c Collection of handlers to add.
	 * @param options Options used do add file.
	 * @return True at least one element has been added.
	 */
	boolean addAll(Collection<? extends Handler> c, OptionsAdd options);

	/**
	 * If given object is {@link FileHandler} or {@link DirectoryHandler} and is
	 * in directory then remove. Also if it's not link then delete it's content.
	 *
	 * @param o Handler to remove.
	 * @return True if an object has been removed as a result of this call.
	 */
	@Override
	boolean remove(Object o);

	/**
	 * This method is not supported. If call just return false.
	 *
	 * @param c Handler to retain.
	 * @return False.
	 */
	@Override
	boolean retainAll(Collection<?> c);

	/**
	 * Return handler to object with the given name.
	 *
	 * @param queryName Name of required handler.
	 * @return Null is no handler for this name is in this directory.
	 */
	Handler getByName(String queryName);

	/**
	 * Return handler to object with given rooted name.
	 * 
	 * @param queryName Rooted name of required handler.
	 * @return Null if the object for given rooted path does not exists.
	 */
	Handler getByRootedName(String queryName);
	
}
