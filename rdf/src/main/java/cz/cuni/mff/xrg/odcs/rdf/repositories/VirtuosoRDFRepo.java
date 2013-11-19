package cz.cuni.mff.xrg.odcs.rdf.repositories;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitType;
import cz.cuni.mff.xrg.odcs.rdf.impl.FailureTolerantRepositoryWrapper;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import java.io.File;
import java.util.Properties;
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
 * Implementation of Virtuoso repository - RDF data and intermediate results are
 * saved in Virtuoso storage.
 *
 * @author Jiri Tomes
 */
public final class VirtuosoRDFRepo extends BaseRDFRepo {

	private String URL_Host_List;

	private String user;

	private String password;

	private String defaultGraph;

	private boolean useExtension;

	/**
	 * Construct a VirtuosoRepository with a specified parameters.
	 *
	 * @param URL_Host_List the Virtuoso JDBC URL connection string or hostlist
	 *                      for pooled connection.
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
			String defaultGraph, String dataUnitName, Properties config) {

		this.URL_Host_List = URL_Host_List;
		this.user = user;
		this.password = password;
		this.defaultGraph = defaultGraph;
		this.dataUnitName = dataUnitName;

		logger = LoggerFactory.getLogger(VirtuosoRDFRepo.class);

		setDataGraph(defaultGraph);

		FailureTolerantRepositoryWrapper repoWrapper = new FailureTolerantRepositoryWrapper(
				new VirtuosoRepository(URL_Host_List, user, password,
				defaultGraph),
				config);

		this.useExtension = repoWrapper.useVirtuosoExtension();
		this.repository = repoWrapper;

		try {
			repository.initialize();
			logger.info(
					"Virtuoso repository with data graph <{}> successfully initialized.",
					defaultGraph);

		} catch (RepositoryException ex) {
			logger.warn("Your Virtuoso might be offline.", ex);
		}
	}

	/**
	 * Set new graph as default for working data in RDF format.
	 *
	 * @param defaultGraph String name of graph as URI - starts with prefix
	 *                     http://).
	 */
	@Override
	public void setDataGraph(String newStringDataGraph) {
		this.defaultGraph = newStringDataGraph;
		setDataGraph(createNewGraph(defaultGraph));
	}

	@Override
	public void delete() {
		if (repository.isInitialized()) {
			cleanAllData();
		}

		release();
	}

	@Override
	public void release() {
		shutDown();
		logger.info("Virtuoso repository with data graph <"
				+ getDefaultGraph()
				+ "> succesfully shut down");
	}

	@Override
	public DataUnitType getType() {
		return DataUnitType.RDF_Virtuoso;
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
	public void mergeRepositoryData(RDFDataUnit second) throws IllegalArgumentException {
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

				String mergeQuery;

				if (useExtension) {
					/**
					 * Virtuoso specific syntax for SPARQL. Parameter log-enable
					 * with value 2 disables logging and enables row-by-row
					 * autocommit.
					 */
					mergeQuery = String
							.format("DEFINE sql:log-enable 2 \n"
							+ "ADD <%s> TO <%s>", sourceGraphName,
							targetGraphName);
				} else {
					mergeQuery = String
							.format("ADD <%s> TO <%s>", sourceGraphName,
							targetGraphName);
				}

				try {
					GraphQuery result = targetConnection.prepareGraphQuery(
							QueryLanguage.SPARQL, mergeQuery);

					logger.info("START merging " + second
							.getTripleCount()
							+ " triples from <" + sourceGraphName + "> "
							+ "TO <" + targetGraphName + ">.");

					result.evaluate();

					logger.info("Merged SUCCESSFUL");

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
	public void copyAllDataToTargetDataUnit(RDFDataUnit targetRepo) {

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

				String mergeQuery;

				if (useExtension) {
					/**
					 * Virtuoso specific syntax for SPARQL. Parameter log-enable
					 * with value 2 disables logging and enables row-by-row
					 * autocommit.
					 */
					mergeQuery = String
							.format("DEFINE sql:log-enable 2 \n"
							+ "ADD <%s> TO <%s>", sourceGraphName,
							targetGraphName);
				} else {
					mergeQuery = String
							.format("ADD <%s> TO <%s>", sourceGraphName,
							targetGraphName);
				}

				try {
					GraphQuery result = targetConnection.prepareGraphQuery(
							QueryLanguage.SPARQL, mergeQuery);

					logger.info("START merging " + getTripleCount()
							+ " triples from <" + sourceGraphName + "> "
							+ "TO <" + targetGraphName + ">.");

					result.evaluate();

					logger.info("Merged SUCCESSFUL");

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
}
