package cz.cuni.mff.xrg.odcs.rdf.repositories;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitType;
import cz.cuni.mff.xrg.odcs.rdf.impl.FailureSharedRepositoryConnection;
import cz.cuni.mff.xrg.odcs.rdf.impl.FailureTolerantRepositoryWrapper;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.ManagableRdfDataUnit;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import virtuoso.sesame2.driver.VirtuosoRepository;

/**
 * Implementation of Virtuoso repository - RDF data and intermediate results are
 * saved in Virtuoso storage.
 *
 * @author Jiri Tomes
 */
public final class VirtuosoRDFRepo extends BaseRDFRepo {
	
	private static final Logger LOG = LoggerFactory.getLogger(VirtuosoRDFRepo.class);

	private static final String VIRTUOSO_SYNTAX_KEY = "useExtension";

	private String URL_Host_List;

	private String user;

	private String password;

	private String defaultGraph;
	
	/**
	 * Virtuoso allows setting log level explicitly in a query.
	 * If {@link #virtuosoSyntax Virtuoso specific syntax} is enabled, we set the
	 * log level explicitly.
	 */
	private static final int LOG_LEVEL = 2;

	/**
	 * Use Virtuoso specific syntax for SPARQL if true. Parameter log-enable
	 * with value {@value #LOG_LEVEL} disables logging and enables row-by-row
	 * autocommit.
	 */
	private boolean virtuosoSyntax;

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
	 * @param config        configuration properties you want to set to
	 *                      repository.
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
				getDefaultGraph()),
				config);

		this.configureVirtuosoSyntax(config);
		
		this.repository = repoWrapper;
		this.repoConnection = new FailureSharedRepositoryConnection(repoWrapper);

		try {
			repository.initialize();
			logger.info(
					"Virtuoso repository with data graph <{}> successfully initialized.",
					defaultGraph);
			logger.info("Virtuoso repository contains {} TRIPLES",
					getTripleCount());


		} catch (RepositoryException ex) {
			logger.warn("Your Virtuoso might be offline.", ex);
		}
	}

	/**
	 * Set new graph as default for working data in RDF format.
	 *
	 * @param newStringDataGraph String name of graph as URI - starts with
	 *                           prefix http://).
	 */
	@Override
	public void setDataGraph(String newStringDataGraph) {
		setDataGraph(createNewGraph(newStringDataGraph));
		this.defaultGraph=getDataGraph().toString();
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

	/**
	 * Return type of data unit interface implementation.
	 *
	 * @return DataUnit type.
	 */
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

	/**
	 * Make RDF data merge over repository - data in repository merge with data
	 * in second defined repository.
	 *
	 *
	 * @param second Type of repository contains RDF data as implementation of
	 *               RDFDataUnit interface.
	 * @throws IllegalArgumentException if second repository as param is null.
	 */
	@Override
	public void mergeRepositoryData(ManagableRdfDataUnit second) throws IllegalArgumentException {
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

				if (virtuosoSyntax) {
					mergeQuery = String.format("DEFINE sql:log-enable %d \n"
							+ "ADD <%s> TO <%s>",
							LOG_LEVEL,
							sourceGraphName,
							targetGraphName);
				} else {
					mergeQuery = String
							.format("ADD <%s> TO <%s>", sourceGraphName,
							targetGraphName);
				}

				try {
					GraphQuery result = targetConnection.prepareGraphQuery(
							QueryLanguage.SPARQL, mergeQuery);

					logger.info("START merging {} triples from <{}> TO <{}>.",
							second.getTripleCount(), sourceGraphName,
							targetGraphName);

					result.evaluate();

					logger.info("Merged SUCCESSFUL");

				} catch (MalformedQueryException ex) {
					logger.debug("NOT VALID QUERY: {}", ex.getMessage());
				} catch (QueryEvaluationException ex) {
					logger.error("MERGING STOPPED: {}", ex.getMessage());
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
	 * Copy all data from repository to targetRepository.
	 *
	 * @param targetRepo goal repository where RDF data are added.
	 */
	@Override
	public void copyAllDataToTargetDataUnit(RDFDataUnit targetRepo) {

		if (targetRepo == null) {
			throw new IllegalArgumentException(
					"Instance of RDFDataRepository is null");
		}

		Repository targetRepository = ((ManagableRdfDataUnit) targetRepo).getDataRepository();
		RepositoryConnection targetConnection = null;

		try {

			targetConnection = targetRepository.getConnection();

			if (targetConnection != null) {

				String sourceGraphName = getDataGraph().stringValue();
				String targetGraphName = ((ManagableRdfDataUnit) targetRepo).getDataGraph().stringValue();

				String mergeQuery;

				if (virtuosoSyntax) {
					mergeQuery = String.format("DEFINE sql:log-enable %d \n"
							+ "ADD <%s> TO <%s>",
							LOG_LEVEL,
							sourceGraphName,
							targetGraphName);
				} else {
					mergeQuery = String
							.format("ADD <%s> TO <%s>", sourceGraphName,
							targetGraphName);
				}

				try {
					GraphQuery result = targetConnection.prepareGraphQuery(
							QueryLanguage.SPARQL, mergeQuery);

					logger.info("START merging {} triples from <{}> TO <{}>.",
							getTripleCount(), sourceGraphName, targetGraphName);

					result.evaluate();

					logger.info("Merged SUCCESSFUL");

				} catch (MalformedQueryException ex) {
					logger.debug("NOT VALID QUERY: {}", ex.getMessage());
				} catch (QueryEvaluationException ex) {
					logger.error("MERGING STOPPED: {}", ex.getMessage());
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
	 * Tells whether Virtuoso specific syntax for SPARQL is used. In such case
	 * parameter log-enable with value {@value #LOG_LEVEL} disables logging and
	 * enables row-by-row autocommit.
	 *
	 * @return whether Virtuoso specific syntax for SPARQL is used.
	 *
	 */
	public boolean isUsedExtension() {
		return virtuosoSyntax;
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
	
	
	private void configureVirtuosoSyntax(Properties config) {
		String sExtension = config.getProperty(VIRTUOSO_SYNTAX_KEY);
		if (sExtension == null) {
			LOG.info("Missing config property {}, using default value '{}'.",
					VIRTUOSO_SYNTAX_KEY, virtuosoSyntax);
		} else {
			virtuosoSyntax = Boolean.parseBoolean(sExtension);
		}
	}

}
