package cz.cuni.xrg.intlib.rdf.data;

import cz.cuni.xrg.intlib.rdf.impl.LocalRDFRepo;
import cz.cuni.xrg.intlib.rdf.impl.VirtuosoRDFRepo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

	private final static String repoFileName = "localRepository";

	/**
	 * Create RDFDataUnit as local RDF repository.
	 *
	 *
	 * @param repoPath     String path to directory where can be repository
	 *                     stored.
	 * @param id           String file name - unique ID, where is repository in
	 *                     directory stored.
	 * @param dataUnitName DataUnit's name. If not used in Pipeline can be empty
	 *                     String.
	 * @param namedGraph   String name of graph, where RDF data are saved.
	 * @return
	 */
	public static LocalRDFRepo createLocalRDFRepo(String repoPath, String id,
			String dataUnitName, String namedGraph) {

		LocalRDFRepo localRepo = new LocalRDFRepo(repoPath, id, dataUnitName);
		localRepo.setDataGraph(namedGraph);

		return localRepo;
	}

	/**
	 * Create RDFDataUnit as local RDF repository in default set temp directory
	 * path. Each directory path have to constains more that one RDF repository.
	 *
	 * @param dataUnitName DataUnit's name. If not used in Pipeline can be empty
	 *                     String.
	 * @throws RuntimeException if temp directory for repository can not create.
	 * @return
	 */
	public static LocalRDFRepo createLocalRDFRepo(String dataUnitName) throws RuntimeException {
		return RDFDataUnitFactory.createLocalRepoInTempDirectory(dataUnitName,
				repoDirName, repoFileName);
	}

	/**
	 * Create RDFDataUnit as local RDF repositorz in temp directory "dirName",
	 * in this directory create file with "fileName" a there is repository
	 * stored.
	 *
	 * @param dataUnitName DataUnit's name. If not used in Pipeline can be empty
	 *                     String.
	 * @param dirName      String name of dir.
	 * @param id           String value of id.
	 *
	 * @throws RuntimeException if temp directory for repository can not create.
	 * @return
	 */
	private static LocalRDFRepo createLocalRepoInTempDirectory(
			String dataUnitName,
			String dirName,
			String id) throws RuntimeException {
		Path repoPath = null;

		try {
			repoPath = Files.createTempDirectory(dirName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return RDFDataUnitFactory.createLocalRDFRepo(repoPath.toString(),
				id, dataUnitName, "http://default");
	}

	/**
	 * Create RDFDataUnitFactory as new instance of VirtuosoRepository as
	 * storage.
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
	 * @return
	 */
	public static VirtuosoRDFRepo createVirtuosoRDFRepo(String hostName,
			String port,
			String user, String password, String namedGraph, String dataUnitName) {

		//log_enable=2 -> disables logging, enables row-by-row autocommit, see
		//http://docs.openlinksw.com/virtuoso/fn_log_enable.html
		final String JDBC = "jdbc:virtuoso://" + hostName + ":"
				+ port + "/charset=UTF-8/log_enable=2";

		VirtuosoRDFRepo virtuosoRepo = new VirtuosoRDFRepo(JDBC, user, password,
				namedGraph, dataUnitName);

		return virtuosoRepo;
	}
}
