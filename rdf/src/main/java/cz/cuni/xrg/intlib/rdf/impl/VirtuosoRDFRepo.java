package cz.cuni.xrg.intlib.rdf.impl;

import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataRepository;
import java.io.File;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.RDFHandlerException;
import org.slf4j.LoggerFactory;
import virtuoso.sesame2.driver.VirtuosoRepository;

import static cz.cuni.xrg.intlib.rdf.impl.LocalRDFRepo.logger;

/**
 *
 * @author Jiri Tomes
 */
public class VirtuosoRDFRepo extends LocalRDFRepo implements RDFDataRepository {

	private String URL_Host_List;

	private String user;

	private String password;

	private String defaultGraph;

	/**
	 * DataUnit's name.
	 */
	private String dataUnitName;

	static {
		logger = LoggerFactory.getLogger(VirtuosoRDFRepo.class);
	}

	public static VirtuosoRDFRepo createVirtuosoRDFRepo(String hostName,
			String port, String user, String password, String defaultGraph,
			String dataUnitName) {
		final String JDBC = "jdbc:virtuoso://" + hostName + ":" + port + "/charset=UTF-8/log_enable=2";

		VirtuosoRDFRepo virtuosoRepo = new VirtuosoRDFRepo(JDBC, user, password,
				defaultGraph, dataUnitName);
		return virtuosoRepo;
	}

	/**
	 * Construct a VirtuosoRepository with a specified parameters.
	 *
	 * @param URL_Host_List the Virtuoso JDBC URL connection string or hostlist
	 *                      for poolled connection.
	 *
	 * @param user          the database user on whose behalf the connection is
	 *                      being made.
	 *
	 * @param password      the user's password.
	 *
	 * @param defaultGraph  a default Graph name, used for Sesame calls, when
	 *                      contexts list is empty, exclude exportStatements,
	 *                      hasStatement, getStatements methods.
	 * @param dataUnitName	 DataUnit's name. If not used in Pipeline can be
	 *                      empty String.
	 */
	public VirtuosoRDFRepo(String URL_Host_List, String user, String password,
			String defaultGraph, String dataUnitName) {

		this.URL_Host_List = URL_Host_List;
		this.user = user;
		this.password = password;
		this.defaultGraph = defaultGraph;
		this.dataUnitName = dataUnitName;

		setDataGraph(defaultGraph);

		repository = new VirtuosoRepository(URL_Host_List, user, password,
				defaultGraph);

		try {
			repository.initialize();
			logger.info("Virtuoso repository successfully incicialized");

		} catch (RepositoryException ex) {
			logger.warn("Your Virtuoso is maybe turn off.");
			logger.debug(ex.getMessage());

		}
	}

	/**
	 *
	 * @return the Virtuoso JDBC URL connection string or hostlist for poolled
	 *         connection.
	 */
	public String getURL_Host_List() {
		return URL_Host_List;
	}

	/**
	 *
	 * @return User name to Virtuoso connection.
	 */
	public String getUser() {
		return user;
	}

	/**
	 *
	 * @return Password to virtuoso connection.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 *
	 * @return Default graph name
	 */
	public String getDefaultGraph() {
		return defaultGraph;
	}

	/**
	 * Set new graph as default for working data in RDF format.
	 *
	 * @param defaultGraph String name of graph as URI - starts with prefix
	 *                     http://).
	 */
	@Override
	public final void setDataGraph(String newStringDataGraph) {
		this.defaultGraph = newStringDataGraph;
		setDataGraph(createNewGraph(defaultGraph));
	}

	private VirtuosoRDFRepo getCopyOfVirtuosoReposiotory() {
		// TODO Jirka: (from Petyr) This method is not used. Why is here? How should be used?
		VirtuosoRDFRepo newCopy = new VirtuosoRDFRepo(URL_Host_List, user,
				password, defaultGraph, "");
		copyAllDataToTargetRepository(newCopy);

		return newCopy;
	}

	@Override
	public void release() {
		shutDown();
	}

	@Override
	public DataUnitType getType() {
		return DataUnitType.RDF_Virtuoso;
	}

	@Override
	public String getName() {
		return dataUnitName;
	}

	@Override
	public void load(File directory) {
		//no load from file - using Virtuoso for intermediate results.
	}

	@Override
	public void save(File directory) {
		//no save to file - using Virtuoso for intermediate results.
	}

	@Override
	public void mergeRepositoryData(RDFDataRepository second) throws IllegalArgumentException {
		if (second == null) {
			throw new IllegalArgumentException(
					"Instance of RDFDataRepository is null");
		}
		Repository secondRepository = second.getDataRepository();

		RepositoryConnection sourceConnection = null;
		RepositoryConnection targetConnection = null;

		try {
			sourceConnection = secondRepository.getConnection();

			if (!sourceConnection.isEmpty()) {

				targetConnection = repository.getConnection();

				if (targetConnection != null) {

					RDFInserter inserter = new RDFInserter(targetConnection);
					inserter.enforceContext(getDataGraph());

					try {
						logger.info("Merging " + second.getTripleCountInRepository()
						+ " triples from <" + second.getDataGraph().stringValue() + "> "
						+ "TO <" + getDataGraph().stringValue() + ">.");
						
						sourceConnection.export(inserter, second.getDataGraph());
						
						logger.info("Merged SUCESSFULL");
						

					} catch (RDFHandlerException ex) {
						logger.error(ex.getMessage(), ex);
					}
				}
			}
		} catch (RepositoryException ex) {
			logger.error(ex.getMessage(), ex);

		} finally {
			if (sourceConnection != null) {
				try {
					sourceConnection.close();
				} catch (RepositoryException ex) {
					logger.error(ex.getMessage(), ex);
				}
			}
			if (targetConnection != null) {
				try {
					targetConnection.close();
				} catch (RepositoryException ex) {
					logger.error(ex.getMessage(), ex);
				}
			}
		}
	}
}
