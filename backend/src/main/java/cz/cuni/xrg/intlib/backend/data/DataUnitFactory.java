package cz.cuni.xrg.intlib.backend.data;

import java.io.File;

import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.rdf.impl.LocalRDFRepo;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataRepository;

/**
 * Create new DataUnits.
 *
 * @author Petyr
 *
 */
public class DataUnitFactory {

	/**
	 * Related context id, is unique.
	 */
	private String id;

	/**
	 * Counter for creating a working directories.
	 */
	private Integer counter;
	
	/**
	 * Root for working directories.
	 */
	private File rootWorkingDirectory;
	
	/**
	 * Base constructor..
	 *
	 * @param id Unique id (Context id.)
	 * @param storageDirectory The folder does not have to exist.
	 * @param dpuInstance
	 * @param rootWorkingDirectory Directory where DataUnits working directory can be created.
	 */
	public DataUnitFactory(String id, File rootWorkingDirectory) {
		this.id = id;
		this.counter = 0;
		this.rootWorkingDirectory = rootWorkingDirectory; 
	}

	/**
	 * Create input DataUnit.
	 *
	 * @param type DataUnit's type.
	 * @return Created data unit or null in case of failure.
	 * @throws DataUnitCreateException
	 */
	public DataUnit createInput(DataUnitType type) throws DataUnitCreateException {
		return create(type, true);
	}

	/**
	 * Create input DataUnit with given configuration.
	 *
	 * @param type   DataUnit's type.
	 * @param config Initial configuration.
	 * @return Created data unit or null in case of failure.
	 * @throws DataUnitCreateException
	 */
	public DataUnit createInput(DataUnitType type, Object config) throws DataUnitCreateException {
		return create(type, true, config);
	}

	/**
	 * Create output DataUnit.
	 *
	 * @param type DataUnit's type.
	 * @return Created data unit or null in case of failure.
	 * @throws DataUnitCreateException
	 */
	public DataUnit createOutput(DataUnitType type) throws DataUnitCreateException {
		return create(type, false);
	}

	/**
	 * Create output DataUnit.
	 *
	 * @param type   DataUnit's type.
	 * @param config Initial configuration.
	 * @return Created data unit or null in case of failure.
	 * @throws DataUnitCreateException
	 */
	public DataUnit createOutput(DataUnitType type, Object config) throws DataUnitCreateException {
		return create(type, false, config);
	}

	/**
	 * Create DataUnit.
	 *
	 * @param type  DataUnit's type.
	 * @param input Should be DataUnit created as input?
	 * @return Created DataUnit or null in case of failure.
	 * @throws DataUnitCreateException
	 */
	private DataUnit create(DataUnitType type, boolean input) throws DataUnitCreateException {
		++counter;
		switch (type) {
			case RDF:
			case RDF_Local:
				// create new working directory
				File workingDirectory = new File(rootWorkingDirectory, counter.toString());
				// create DataUnit
				RDFDataRepository localRepository = LocalRDFRepo
						.createLocalRepo(workingDirectory.getAbsolutePath(), id);
				return localRepository;
			case RDF_Virtuoso:
				break;
		}
		return null;
	}

	/**
	 * Create DataUnit.
	 *
	 * @param type  DataUnit's type.
	 * @param input Should be DataUnit created as input?
	 * @return Created DataUnit or null in case of failure.
	 * @throws DataUnitCreateException
	 */
	private DataUnit create(DataUnitType type, boolean input, Object config)
			throws DataUnitCreateException {
		++counter;
		switch (type) {
			case RDF:
			case RDF_Local: // RDF_Local does't support non-default configuration
				return null;
			case RDF_Virtuoso:
				break;
		}
		return null;
	}
}
