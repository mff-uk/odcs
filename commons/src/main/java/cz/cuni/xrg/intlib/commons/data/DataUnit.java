package cz.cuni.xrg.intlib.commons.data;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Basic data unit interface. The data unit should be passed in context between
 * modules and should carry the main information.
 * 
 * Each DataUnit has name, this can can't be changed by DPU directly. It's
 * assigned once when DataUnit is created. The name can be obtained using
 * {@link #getName()}
 * 
 * @author Petyr
 * 
 */
public interface DataUnit {

	/**
	 * Made this DataUnit read-only. This instance will be used as a input for
	 * some DPU.
	 */
	public void madeReadOnly();

	/**
	 * Merge (add) data from given DataUnit into this DataUnit. If the unit has
	 * wrong type then the {@link IllegalArgumentException} should be thrown.
	 * The method must not modify the content parameter (unit). The given
	 * DataUnit is not in read-only mode.
	 * 
	 * @param unit {@link DataUnit} to merge wit
	 * @throws {@link IllegalArgumentException} In case of unsupported unit
	 *         type.
	 */
	public void merge(DataUnit unit) throws IllegalArgumentException;

	/**
	 * Delete all data/file/resources related to the DataUnit. 
	 * Can be called even when the DataUnit is in read only mode.
	 * Can't be called before of after {@link #release()}
	 */
	public void delete();
	
	/**
	 * Release all locks, prepare for destroy in memory representation of 
	 * DataUnit. Can be called even when the DataUnit is in read only mode.
	 * Can't be called before of after {@link #delete()}
	 */
	public void release();

	/**
	 * Save DataUnit context into given directory. In case of any problem throws
	 * exception. The directory doesn't have to exist. The directory can be the
	 * same as the DataUnit working directory!
	 * 
	 * @throws RuntimeException
	 */
	public void save(File directory) throws RuntimeException;

	/**
	 * Load data unit context from directory. Throw
	 * {@link FileNotFoundException} if some of required file can't be found.
	 * And {@link RuntimeException} in case of any other error.
	 * 
	 * @param directory
	 * @throws FileNotFoundException
	 * @throws RuntimeException
	 */
	public void load(File directory)
			throws FileNotFoundException,
				RuntimeException;

	/**
	 * Return type of data unit interface implementation.
	 * 
	 * @return DataUnit type.
	 */
	public DataUnitType getType();

	/**
	 * Return dataUnit's name. The DataUnit name should be set in constructor.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Return true if DataUnit is in read only state.
	 * 
	 * @see {@link #madeReadOnly}
	 * @return True if data in DataUnit are read only.
	 */
	public boolean isReadOnly();
}
