package cz.cuni.mff.xrg.odcs.backend.data;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;

import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitCreateException;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitType;
import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.GraphUrl;
import cz.cuni.mff.xrg.odcs.rdf.data.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;

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
	 * Create {@link DataUnit} and store information about it into the context.
	 *
	 * @param type      Requested type of data unit.
	 * @param id        DataUnit's id assigned by application, must be unique!
	 * @param name      DataUnit's name, can't be changed in future.
	 * @param directory DataUnit's working directory.
	 * @return DataUnit
	 * @throws DataUnitCreateException
	 */
	public ManagableDataUnit create(DataUnitType type,
			String id,
			String name,
			File directory) throws DataUnitCreateException {
		switch (type) {
			case RDF:
				throw new DataUnitCreateException("Pure RDF DataUnit can't "
						+ "be created.");
			case RDF_Local:
				// create DataUnit
				RDFDataUnit localRepository = RDFDataUnitFactory
						.createLocalRDFRepo(directory.getAbsolutePath(), id,
						name, GraphUrl.translateDataUnitId(id));

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

				// create repository
				RDFDataUnit virtosoRepository = RDFDataUnitFactory
						.createVirtuosoRDFRepo(
						hostName,
						port,
						user,
						password,
						GraphUrl.translateDataUnitId(id),
						name,
						appConfig.getProperties());
				return virtosoRepository;
			default:
				throw new DataUnitCreateException("Unknown DataUnit type.");
		}
	}
}
