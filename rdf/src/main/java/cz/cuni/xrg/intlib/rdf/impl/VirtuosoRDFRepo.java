package cz.cuni.xrg.intlib.rdf.impl;

import cz.cuni.xrg.intlib.commons.data.DataUnitType;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataRepository;
import java.io.File;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.LoggerFactory;
import virtuoso.sesame2.driver.VirtuosoRepository;

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
	public void delete() {
		shutDown();
		logger.info("Virtuoso repository succesfully shut down");
		// TODO Jirka: Delete used graph here 
	}	
	
	@Override
	public void release() {
		shutDown();
		logger.info("Virtuoso repository succesfully shut down");
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

		RepositoryConnection targetConnection = null;

		try {

			targetConnection = repository.getConnection();

			if (targetConnection != null) {

				String sourceGraphName = second.getDataGraph().stringValue();
				String targetGraphName = getDataGraph().stringValue();

				String mergeQuery = String
						.format("ADD <%s> TO <%s>", sourceGraphName,
						targetGraphName);

				try {
					GraphQuery result = targetConnection.prepareGraphQuery(
							QueryLanguage.SPARQL, mergeQuery);

					logger.info("START merging " + second
							.getTripleCount()
							+ " triples from <" + sourceGraphName + "> "
							+ "TO <" + targetGraphName + ">.");

					result.evaluate();

					logger.info("Merged SUCESSFULL");

				} catch (MalformedQueryException ex) {
					logger.debug("NOT VALID QUERY: " + ex.getMessage());
				} catch (QueryEvaluationException ex) {
					logger.error("MERGING STOPPED" + ex.getMessage());
				}

			}

		} catch (RepositoryException ex) {
			logger.error(ex.getMessage(), ex);

		} finally {
			if (targetConnection != null) {
				try {
					targetConnection.close();
				} catch (RepositoryException ex) {
					logger.error(ex.getMessage(), ex);
				}
			}
		}
	}

	@Override
	public void copyAllDataToTargetRepository(RDFDataRepository targetRepo) {

		if (targetRepo == null) {
			throw new IllegalArgumentException(
					"Instance of RDFDataRepository is null");
		}

		Repository targetRepository = targetRepo.getDataRepository();
		RepositoryConnection targetConnection = null;

		try {

			targetConnection = targetRepository.getConnection();

			if (targetConnection != null) {

				String sourceGraphName = getDataGraph().stringValue();
				String targetGraphName = targetRepo.getDataGraph().stringValue();

				String mergeQuery = String
						.format("ADD <%s> TO <%s>", sourceGraphName,
						targetGraphName);

				try {
					GraphQuery result = targetConnection.prepareGraphQuery(
							QueryLanguage.SPARQL, mergeQuery);

					logger.info("START merging " + getTripleCount()
							+ " triples from <" + sourceGraphName + "> "
							+ "TO <" + targetGraphName + ">.");

					result.evaluate();

					logger.info("Merged SUCESSFULL");

				} catch (MalformedQueryException ex) {
					logger.debug("NOT VALID QUERY: " + ex.getMessage());
				} catch (QueryEvaluationException ex) {
					logger.error("MERGING STOPPED" + ex.getMessage());
				}

			}

		} catch (RepositoryException ex) {
			logger.error(ex.getMessage(), ex);

		} finally {
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
