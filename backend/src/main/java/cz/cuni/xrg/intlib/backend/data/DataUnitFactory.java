package cz.cuni.xrg.intlib.backend.data;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;

import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import cz.cuni.xrg.intlib.commons.app.conf.ConfigProperty;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.DataUnitCreateException;
import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.rdf.impl.LocalRDFRepo;
import cz.cuni.xrg.intlib.rdf.impl.VirtuosoRDFRepo;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataRepository;

/**
 * Create new DataUnits based on given id, name and type in given working
 * directory.
 * 
 * The class is suppose to be use as spring bean and it's methods can be run
 * concurrently.
 * 
 * @author Petyr
 * 
 */
public class DataUnitFactory {

	/**
	 * Application configuration.
	 */
	@Autowired
	private AppConfig appConfig;

	public DataUnitFactory() {
		
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
	 * Create {@link DataUnit} and store information about it into the context.
	 * 
	 * @param type Requested type of data unit.
	 * @param id DataUnit's id assigned by application, must be unique!
	 * @param name DataUnit's name, can't be changed in future.
	 * @param directory DataUnit's working directory.
	 * @param isInput True if created DataUnit will be used as input.
	 * @return DataUnit
	 * @throws DataUnitCreateException
	 */
	public DataUnit create(DataUnitType type,
			String id,
			String name,
			File directory) throws DataUnitCreateException {
		switch (type) {
		case RDF:
			throw new DataUnitCreateException("Pure RDF DataUnit can't " 
					+ "be created.");
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
	 * 
	 * @param type Requested type of data unit.
	 * @param id DataUnit's id assigned by application, must be unique!
	 * @param name DataUnit's name, can't be changed in future.
	 * @param directory DataUnit's working directory.
	 * @param configObject Configuration object for DataUnit.
	 * @return Container with created DataUnit.
	 * @throws DataUnitCreateException
	 */
	public DataUnit create(DataUnitType type,
			String id,
			String name,
			File directory,
			Object configObject) throws DataUnitCreateException {
		//
		throw new DataUnitCreateException(
				"Required DataUnit does not support configuration.");
	}
}
