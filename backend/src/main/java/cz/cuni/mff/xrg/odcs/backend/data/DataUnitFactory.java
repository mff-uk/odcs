package cz.cuni.mff.xrg.odcs.backend.data;

import java.io.File;

import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;

import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitCreateException;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitType;
import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.GraphUrl;
import cz.cuni.mff.xrg.odcs.rdf.repositories.LocalRDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.repositories.VirtuosoRDFDataUnit;

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
	//@Value( "${jdbc.url}" ) private String jdbcUrl;

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
	 * @throws RepositoryException 
	 */
	public ManagableDataUnit create(DataUnitType type,
			String id,
			String name,
			File directory) {
		switch (type) {
			case RDF:
				throw new RuntimeException("Pure RDF DataUnit can't "
						+ "be created.");
			case RDF_Local:
				// create DataUnit
				ManagableDataUnit localRepository = new LocalRDFDataUnit(
								appConfig.getString(ConfigProperty.GENERAL_WORKINGDIR)
								
								, 
						name, GraphUrl.translateDataUnitId(id));

				// create container with DataUnit and index
				return localRepository;
			case RDF_Virtuoso:
				// load configuration from appConfig
				AppConfig config = appConfig.getSubConfiguration(
						ConfigProperty.RDF
				);
				
				final String url = "jdbc:virtuoso://" + config.getString(ConfigProperty.DATABASE_HOSTNAME) + ":"
						+ config.getString(ConfigProperty.DATABASE_PORT) + "/charset=UTF-8/log_enable=2";
				// create repository
				ManagableDataUnit virtosoRepository = new VirtuosoRDFDataUnit(
						url,
						config.getString(ConfigProperty.DATABASE_USER),
						config.getString(ConfigProperty.DATABASE_PASSWORD),
						name, 
						GraphUrl.translateDataUnitId(id));
				
				return virtosoRepository;
			case FILE:
				// create the DataUnit and return it
				return FileDataUnitFactory.create(name, directory);
			default:
				throw new RuntimeException("Unknown DataUnit type.");
		}
	}
}
