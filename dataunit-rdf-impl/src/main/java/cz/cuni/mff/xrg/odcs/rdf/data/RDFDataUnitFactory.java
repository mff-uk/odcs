package cz.cuni.mff.xrg.odcs.rdf.data;

import cz.cuni.mff.xrg.odcs.rdf.impl.FailureTolerantRepositoryWrapper;
import cz.cuni.mff.xrg.odcs.rdf.repositories.LocalRDFRepo;
import cz.cuni.mff.xrg.odcs.rdf.repositories.VirtuosoRDFRepo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Class provides factory methods for creating conrete instance of RDFDataUnit
 * interface.
 *
 * @author Jiri Tomes
 *
 */
public class RDFDataUnitFactory {

	/**
	 * Default name for temp directory, where this repository is placed.
	 */
	private final static String repoDirName = "intlib-repo";

	/**
	 * Default name for temp file, where this repository is saved.
	 */
	private final static String repoFileName = "localRepository";

	/**
	 * Create RDFDataUnit as Local RDF repository.
	 *
	 *
	 * @param repoPath     String path to directory where can be repository
	 *                     stored.
	 * @param id           String file name - unique ID, where is repository in
	 *                     directory stored.
	 * @param dataUnitName DataUnit's name. If not used in Pipeline can be empty
	 *                     String.
	 * @param namedGraph   String name of graph, where RDF data are saved.
	 * @return New {@link LocalRDFRepo} instance.
	 */
	public static LocalRDFRepo createLocalRDFRepo(String repoPath, String id,
			String dataUnitName, String namedGraph) {

		LocalRDFRepo localRepo = new LocalRDFRepo(repoPath, id, namedGraph,
				dataUnitName);
		return localRepo;
	}

	/**
	 * Create RDFDataUnit as Local RDF repository in default set temp directory
	 * path. Each directory path have to constains more that one RDF repository.
	 *
	 * @param dataUnitName DataUnit's name. If not used in Pipeline can be empty
	 *                     String.
	 * @throws RuntimeException if temp directory for repository can not create.
	 * @return New {@link LocalRDFRepo} instance in default set temp directory.
	 */
	public static LocalRDFRepo createLocalRDFRepo(String dataUnitName) throws RuntimeException {
		Path repoPath = null;

		try {
			repoPath = Files.createTempDirectory(repoDirName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return RDFDataUnitFactory.createLocalRDFRepo(repoPath.toString(),
				repoFileName, dataUnitName, "http://default");		
	}

	/**
	 * Create RDFDataUnit as new instance of VirtuosoRepository as storage.
	 *
	 * @param hostName     String name of host need to Virtuoso connection.
	 * @param port         String value of number of port need for connection to
	 *                     Virtuoso.
	 * @param user         A default graph name, used for Sesame calls, when
	 *                     contexts list is empty, exclude exportStatements,
	 *                     hasStatement, getStatements methods.
	 * @param password     The user's password.
	 * @param namedGraph   A default graph name, used for Sesame calls, when
	 *                     contexts list is empty, exclude exportStatements,
	 *                     hasStatement, getStatements methods.
	 * @param dataUnitName DataUnit's name. If not used in Pipeline can be empty
	 *                     String.
	 * @param config	      configuration for
	 *                     {@link FailureTolerantRepositoryWrapper}
	 * @return New {@link VirtuosoRDFRepo} instance.
	 */
	public static VirtuosoRDFRepo createVirtuosoRDFRepo(
			String hostName,
			String port,
			String user,
			String password,
			String namedGraph,
			String dataUnitName,
			Properties config) {

		//log_enable=2 -> disables logging, enables row-by-row autocommit, see
		//http://docs.openlinksw.com/virtuoso/fn_log_enable.html
		final String JDBC = "jdbc:virtuoso://" + hostName + ":"
				+ port + "/charset=UTF-8/log_enable=2";

		VirtuosoRDFRepo virtuosoRepo = new VirtuosoRDFRepo(
				JDBC, user, password, namedGraph, dataUnitName, config);

		return virtuosoRepo;
	}
}
