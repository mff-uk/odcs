package cz.cuni.xrg.intlib.backend.data;

import java.io.File;

import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import cz.cuni.xrg.intlib.commons.app.conf.ConfigProperty;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.ExecutionContextInfo;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.rdf.impl.LocalRDFRepo;
import cz.cuni.xrg.intlib.rdf.impl.VirtuosoRDFRepo;
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
	 * DPU instance record.
	 */
	private DPUInstanceRecord instance;

	/**
	 * Manage mapping context into execution's directory.
	 */
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
	public DataUnitFactory(String id, DPUInstanceRecord instance,
			ExecutionContextInfo context, AppConfig appConfig) {
		this.id = id;
		this.instance = instance;
		this.context = context;
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
	public DataUnitContainer createInput(DataUnitType type, String name) throws DataUnitCreateException {
		return create(type, name, true);
	}

	/**
	 * Create input DataUnit with given configuration.
	 *
	 * @param type   DataUnit's type.
	 * @param name DataUnit's name.
	 * @param config Initial configuration.
	 * @return Created DataUnitContainer.
	 * @throws DataUnitCreateException
	 */
	public DataUnitContainer createInput(DataUnitType type, String name, Object config)
			throws DataUnitCreateException {
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
	public DataUnitContainer createOutput(DataUnitType type, String name) throws DataUnitCreateException {
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
	public DataUnitContainer createOutput(DataUnitType type, String name, Object config)
			throws DataUnitCreateException {
		return create(type, name, false, config);
	}

	/**
	 * Create DataUnit.
	 *
	 * @param type  DataUnit's type.
	 * @param name DataUnit's name.
	 * @param input Should be DataUnit created as input?
	 * @return Created DataUnitContainer.
	 * @throws DataUnitCreateException
	 */
	private DataUnitContainer create(DataUnitType type, String name, boolean input) throws DataUnitCreateException {
		
		if (type == DataUnitType.RDF) {
			// select other DataUnit based on configuration
			String defRdfRepo = appConfig.getString(ConfigProperty.BACKEND_DEFAULTRDF);
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
                    throw new DataUnitCreateException("The data unit type is unknown. Check the value of the parameter backend.defaultRDF in config.properties");
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
		
		switch (type) {
			case RDF_Local:
				// create DataUnit
				RDFDataRepository localRepository = LocalRDFRepo
						.createLocalRepo(tmpDir.getAbsolutePath(), id, name);
				// create container with DataUnit and index
				return new DataUnitContainer(localRepository, index);
			case RDF_Virtuoso:				
				// load configuration from appConfig
				final String hostName = 
						appConfig.getString(ConfigProperty.VIRTUOSO_HOSTNAME);
				final String port = 
						appConfig.getString(ConfigProperty.VIRTUOSO_PORT);
				final String user = 
						appConfig.getString(ConfigProperty.VIRTUOSO_USER);
				final String password = 
						appConfig.getString(ConfigProperty.VIRTUOSO_PASSWORD);
				final String defautGraph = 
						appConfig.getString(ConfigProperty.VIRTUOSO_DEFAULT_GRAPH);				
				// create repository
				RDFDataRepository virtosoRepository = VirtuosoRDFRepo
						.createVirtuosoRDFRepo(hostName, port, user, password, defautGraph, name);
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
	private DataUnitContainer create(DataUnitType type, String name,
			boolean input, Object config)
			throws DataUnitCreateException {
		switch (type) {
			case RDF:
			case RDF_Local: // RDF_Local does't support non-default configuration
			case RDF_Virtuoso:
				throw new DataUnitCreateException(
						"Can't create RDF with configuration.");
		}
		throw new DataUnitCreateException("Unknown DataUnit type.");
	}
}
