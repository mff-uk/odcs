package cz.cuni.xrg.intlib.backend.data;

import java.io.File;

import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import cz.cuni.xrg.intlib.commons.app.conf.ConfigProperty;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.rdf.impl.LocalRDFRepo;
import cz.cuni.xrg.intlib.rdf.impl.VirtuosoRDFRepo;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataRepository;

/**
 * Create new DataUnits based on given id, name and type in given working
 * directory. The type may a changed by application configuration.
 * 
 * The class is suppose to be use as spring bean and it's methods can be run
 * concurrently.
 * 
 * @author Petyr
 * 
 */
public class DataUnitFactory {

	/**
	 * Related context id for given execution and DPU. It's unique.
	 */
	@Deprecated
	private String id;

	/**
	 * DPU instance record.
	 */
	@Deprecated
	private DPUInstanceRecord instance;

	/**
	 * Manage mapping context into execution's directory.
	 */
	@Deprecated
	private ExecutionContextInfo context;

	/**
	 * Application configuration.
	 */
	private AppConfig appConfig;

	/**
	 * Base constructor..
	 * 
	 * @param id Unique id (Context id.)
	 */
	@Deprecated
	public DataUnitFactory(String id,
			DPUInstanceRecord instance,
			ExecutionContextInfo context,
			AppConfig appConfig) {
		this.id = id;
		this.instance = instance;
		this.context = context;
		this.appConfig = appConfig;
	}

	/**
	 * Constructor for spring.
	 * 
	 * @param appConfig
	 */
	public DataUnitFactory(AppConfig appConfig) {
		this.appConfig = appConfig;
	}

	/**
	 * Create input DataUnit.
	 * 
	 * @param type DataUnit's type.
	 * @param name DataUnit's name.
	 * @return Created DataUnitContainer.
	 * @throws DataUnitCreateException
	 */
	@Deprecated
	public DataUnitContainer createInput(DataUnitType type, String name)
			throws DataUnitCreateException {
		return create(type, name, true);
	}

	/**
	 * Create input DataUnit with given configuration.
	 * 
	 * @param type DataUnit's type.
	 * @param name DataUnit's name.
	 * @param config Initial configuration.
	 * @return Created DataUnitContainer.
	 * @throws DataUnitCreateException
	 */
	@Deprecated
	public DataUnitContainer createInput(DataUnitType type,
			String name,
			Object config) throws DataUnitCreateException {
		return create(type, name, true, config);
	}

	/**
	 * Create output DataUnit.
	 * 
	 * @param type DataUnit's type.
	 * @param name DataUnit's name.
	 * @return Created DataUnitContainer.
	 * @throws DataUnitCreateException
	 */
	@Deprecated
	public DataUnitContainer createOutput(DataUnitType type, String name)
			throws DataUnitCreateException {
		return create(type, name, false);
	}

	/**
	 * Create output DataUnit.
	 * 
	 * @param type DataUnit's type.
	 * @param name DataUnit's name.
	 * @param config Initial configuration.
	 * @return Created DataUnitContainer.
	 * @throws DataUnitCreateException
	 */
	@Deprecated
	public DataUnitContainer createOutput(DataUnitType type,
			String name,
			Object config) throws DataUnitCreateException {
		return create(type, name, false, config);
	}

	/**
	 * Create DataUnit.
	 * 
	 * @param type DataUnit's type.
	 * @param name DataUnit's name.
	 * @param input Should be DataUnit created as input?
	 * @return Created DataUnitContainer.
	 * @throws DataUnitCreateException
	 */
	@Deprecated
	private DataUnitContainer create(DataUnitType type,
			String name,
			boolean input) throws DataUnitCreateException {

		if (type == DataUnitType.RDF) {
			// select other DataUnit based on configuration
			String defRdfRepo = appConfig
					.getString(ConfigProperty.BACKEND_DEFAULTRDF);
			if (defRdfRepo == null) {
				// use local
				type = DataUnitType.RDF_Local;
			} else {
				// chose based on option
				if (defRdfRepo.compareToIgnoreCase("virtuoso") == 0) {
					// use virtuoso
					type = DataUnitType.RDF_Virtuoso;
				} else if (defRdfRepo.compareToIgnoreCase("localRDF") == 0) {
					// use local
					type = DataUnitType.RDF_Local;
				} else {
					throw new DataUnitCreateException(
							"The data unit type is unknown. Check the value of the parameter backend.defaultRDF in config.properties");
				}
			}
		}

		// get index, also register in ExecutionContextInfo
		Integer index = null;
		if (input) {
			index = context.createInput(instance, name, type);
		} else {
			index = context.createOutput(instance, name, type);
		}
		File tmpDir = context.getDataUnitTmp(instance, index);

		String dataUnitId = context.generateDataUnitId(id, index);
		switch (type) {
		case RDF_Local:
			// create DataUnit
			RDFDataRepository localRepository = LocalRDFRepo.createLocalRepo(
					tmpDir.getAbsolutePath(), dataUnitId, name);

			localRepository.setDataGraph("http://" + dataUnitId);
			// create container with DataUnit and index
			return new DataUnitContainer(localRepository, index);
		case RDF_Virtuoso:
			// load configuration from appConfig
			final String hostName = appConfig
					.getString(ConfigProperty.VIRTUOSO_HOSTNAME);
			final String port = appConfig
					.getString(ConfigProperty.VIRTUOSO_PORT);
			final String user = appConfig
					.getString(ConfigProperty.VIRTUOSO_USER);
			final String password = appConfig
					.getString(ConfigProperty.VIRTUOSO_PASSWORD);
			final String defautGraph = appConfig
					.getString(ConfigProperty.VIRTUOSO_DEFAULT_GRAPH);
			// create repository
			VirtuosoRDFRepo virtosoRepository = VirtuosoRDFRepo
					.createVirtuosoRDFRepo(hostName, port, user, password,
							defautGraph, name);
			// set default graph .. for this we need unique identifier
			// in "id" is unique id for context (execution and DPU) .. we add
			// number to make it
			// unique between data units

			virtosoRepository.setDataGraph("http://" + dataUnitId);
			return new DataUnitContainer(virtosoRepository, index);

		}
		throw new DataUnitCreateException("Unknown DataUnit type.");
	}

	/**
	 * Create DataUnit.
	 * 
	 * @param type DataUnit's type.
	 * @param name DataUnit's name.
	 * @param input Should be DataUnit created as input?
	 * @param config COnfiguration for DataUnit.
	 * @return Created DataUnitContainer.
	 * @throws DataUnitCreateException
	 */
	@Deprecated
	private DataUnitContainer create(DataUnitType type,
			String name,
			boolean input,
			Object config) throws DataUnitCreateException {
		switch (type) {
		case RDF:
		case RDF_Local: // RDF_Local does't support non-default configuration
		case RDF_Virtuoso:
			throw new DataUnitCreateException(
					"Can't create RDF with configuration.");
		}
		throw new DataUnitCreateException("Unknown DataUnit type.");
	}

	/**
	 * Create input {@link DataUnit} and store information about it into the
	 * context. The type of result {@link DataUnit} may differ from the required
	 * due the application configuration.
	 * 
	 * @param type Requested type of data unit.
	 * @param id DataUnit's id assigned by application, must be unique!
	 * @param name DataUnit's name, can't be changed in future.
	 * @param directory DataUnit's working directory.
	 * @return DataUnit
	 * @throws DataUnitCreateException
	 */
	public DataUnit createInput(DataUnitType type,
			String id,
			String name,
			File directory) throws DataUnitCreateException {
		return create(type, id, name, directory, true);
	}

	/**
	 * Create input {@link DataUnit} and store information about it into the
	 * context.The type of result {@link DataUnit} may differ from the required
	 * due the application configuration.
	 * 
	 * @param type Requested type of data unit.
	 * @param id DataUnit's id assigned by application, must be unique!
	 * @param name DataUnit's name, can't be changed in future.
	 * @param directory DataUnit's working directory.
	 * @return DataUnit
	 * @throws DataUnitCreateException
	 */
	public DataUnit createInput(DataUnitType type,
			String id,
			String name,
			File directory,
			Object configObject) throws DataUnitCreateException {
		return create(type, id, name, directory, true, configObject);
	}

	/**
	 * Create output {@link DataUnit} and store information about it into the
	 * context. The type of result {@link DataUnit} may differ from the required
	 * due the application configuration.
	 * 
	 * @param type Requested type of data unit.
	 * @param id DataUnit's id assigned by application, must be unique!
	 * @param name DataUnit's name, can't be changed in future.
	 * @param directory DataUnit's working directory.
	 * @return DataUnit
	 * @throws DataUnitCreateException
	 */
	public DataUnit createOutput(DataUnitType type,
			String id,
			String name,
			File directory) throws DataUnitCreateException {
		return create(type, id, name, directory, false);
	}

	/**
	 * Create output {@link DataUnit} and store information about it into the
	 * context. The type of result {@link DataUnit} may differ from the required
	 * due the application configuration.
	 * 
	 * @param type Requested type of data unit.
	 * @param id DataUnit's id assigned by application, must be unique!
	 * @param name DataUnit's name, can't be changed in future.
	 * @param directory DataUnit's working directory.
	 * @param configObject Configuration object for DataUnit.
	 * @return DataUnit
	 * @throws DataUnitCreateException
	 */
	public DataUnit createOutput(DataUnitType type,
			String id,
			String name,
			File directory,
			Object configObject) throws DataUnitCreateException {
		return create(type, id, name, directory, false, configObject);
	}

	/**
	 * Check required type based on application configuration and return
	 * {@link DataUnitType} that should be created. Can thrown
	 * {@link DataUnitCreateException} in case of unknown {@link DataUnitType}.
	 * 
	 * @param type Required type.
	 * @return Type to create.
	 * @throws DataUnitCreateException
	 */
	private DataUnitType checkType(DataUnitType type)
			throws DataUnitCreateException {
		if (type == DataUnitType.RDF) {
			// select other DataUnit based on configuration
			String defRdfRepo = appConfig
					.getString(ConfigProperty.BACKEND_DEFAULTRDF);
			if (defRdfRepo == null) {
				// use local
				type = DataUnitType.RDF_Local;
			} else {
				// choose based on value in appConfig
				if (defRdfRepo.compareToIgnoreCase("virtuoso") == 0) {
					// use virtuoso
					type = DataUnitType.RDF_Virtuoso;
				} else if (defRdfRepo.compareToIgnoreCase("localRDF") == 0) {
					// use local
					type = DataUnitType.RDF_Local;
				} else {
					throw new DataUnitCreateException(
							"The data unit type is unknown."
									+ "Check the value of the parameter "
									+ "backend.defaultRDF in config.properties");
				}
			}
		}
		return type;
	}

	/**
	 * Create {@link DataUnit} and store information about it into the context.
	 * The type of result {@link DataUnit} may differ from the required due the
	 * application configuration.
	 * 
	 * @param type Requested type of data unit.
	 * @param id DataUnit's id assigned by application, must be unique!
	 * @param name DataUnit's name, can't be changed in future.
	 * @param directory DataUnit's working directory.
	 * @param isInput True if created DataUnit will be used as input.
	 * @return DataUnit
	 * @throws DataUnitCreateException
	 */
	private DataUnit create(DataUnitType type,
			String id,
			String name,
			File directory,
			boolean isInput) throws DataUnitCreateException {
		// check type
		type = checkType(type);

		switch (type) {
		case RDF_Local:
			// create DataUnit
			RDFDataRepository localRepository = LocalRDFRepo.createLocalRepo(
					directory.getAbsolutePath(), id, name);

			localRepository.setDataGraph("http://" + id);
			// create container with DataUnit and index
			return localRepository;
		case RDF_Virtuoso:
			// load configuration from appConfig
			final String hostName = appConfig
					.getString(ConfigProperty.VIRTUOSO_HOSTNAME);
			final String port = appConfig
					.getString(ConfigProperty.VIRTUOSO_PORT);
			final String user = appConfig
					.getString(ConfigProperty.VIRTUOSO_USER);
			final String password = appConfig
					.getString(ConfigProperty.VIRTUOSO_PASSWORD);
			final String defautGraph = appConfig
					.getString(ConfigProperty.VIRTUOSO_DEFAULT_GRAPH);
			// create repository
			VirtuosoRDFRepo virtosoRepository = VirtuosoRDFRepo
					.createVirtuosoRDFRepo(hostName, port, user, password,
							defautGraph, name);
			// use unique DataUnit id as a graph name
			virtosoRepository.setDataGraph("http://" + id);
			return virtosoRepository;
		default:
			throw new DataUnitCreateException("Unknown DataUnit type.");
		}
	}

	/**
	 * Create {@link DataUnit} and store information about it into the context.
	 * The type of result {@link DataUnit} may differ from the required due the
	 * application configuration.
	 * 
	 * @param type Requested type of data unit.
	 * @param id DataUnit's id assigned by application, must be unique!
	 * @param name DataUnit's name, can't be changed in future.
	 * @param directory DataUnit's working directory.
	 * @param isInput True if created DataUnit will be used as input.
	 * @param configObject Configuration object for DataUnit.
	 * @return Container with created DataUnit.
	 * @throws DataUnitCreateException
	 */
	private DataUnit create(DataUnitType type,
			String id,
			String name,
			File directory,
			boolean isInput,
			Object configObject) throws DataUnitCreateException {
		// check type
		type = checkType(type);
		//
		throw new DataUnitCreateException(
				"Required DataUnit does not support configuration.");
	}
}
