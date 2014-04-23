package cz.cuni.mff.xrg.odcs.rdf.repositories;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfig;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.manager.LocalRepositoryManager;
import org.openrdf.repository.manager.RepositoryProvider;
import org.openrdf.repository.sail.config.SailRepositoryConfig;
import org.openrdf.sail.nativerdf.config.NativeStoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitType;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;

/**
 * Implementation of local RDF repository - RDF data are saved in files on hard
 * disk in computer, intermediate results are keeping in computer memory.
 *
 * @author Jiri Tomes
 */
public class LocalRDFDataUnit extends BaseRDFRepo {
	private static final Logger LOG = LoggerFactory.getLogger(LocalRDFDataUnit.class);
	
	public static final String GLOBAL_REPOSITORY_ID = "sdavhniw2uv3ni32u3fkhj";
	
	private String dataUnitName;
	
	private Repository repository;

	private List<RepositoryConnection> requestedConnections;
	
	private Thread ownerThread;
	
	/**
	 * Public constructor - create new instance of repository in defined
	 * repository Path.
	 * 
	 * @param repositoryPath
	 *            String value of path to directory where will be repository
	 *            stored.
	 * @param namedGraph
	 *            String value of URI graph that will be set to repository.
	 * @param dataUnitName
	 *            DataUnit's name. If not used in Pipeline can be empty String.
	 */
	public LocalRDFDataUnit(String repositoryPath, String dataUnitName,
			String dataGraph) {
		this.dataUnitName = dataUnitName;
		this.requestedConnections = new ArrayList<>();
		this.ownerThread = Thread.currentThread();

		setDataGraph(dataGraph);
		try {
			File managerDir = new File(repositoryPath);
			if (!managerDir.mkdirs()) {
				throw new RuntimeException("Could not create repository manager directory.");
			}
			LocalRepositoryManager localRepositoryManager = RepositoryProvider.getRepositoryManager(managerDir);
			repository = localRepositoryManager
					.getRepository(GLOBAL_REPOSITORY_ID);
			if (repository == null) {
				localRepositoryManager.addRepositoryConfig(
						new RepositoryConfig(GLOBAL_REPOSITORY_ID, new SailRepositoryConfig(new NativeStoreConfig()))
						);
				repository = localRepositoryManager.getRepository(GLOBAL_REPOSITORY_ID);
			}
			if (repository == null) {
				throw new RuntimeException("Could not initialize repository");
			}
		} catch (RepositoryConfigException | RepositoryException ex) {
			throw new RuntimeException("Could not initialize repository", ex);
		}
		
		RepositoryConnection connection = null; 
		try {
            connection = getConnection();
			LOG.info("Initialized Local RDF DataUnit named '{}'w with data graph <{}> containing {} triples.",
					dataUnitName, dataGraph, connection.size(this.getDataGraph()));
		} catch (RepositoryException ex) {
			throw new RuntimeException("Could not test initial connect to repository", ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					LOG.warn("Error when closing connection", ex);
					// eat close exception, we cannot do anything clever here
				}
			}
		}		
	}

	@Override
	public DataUnitType getType() {
		return DataUnitType.RDF_Local;
	}
	
	@Override
	public String getDataUnitName() {
		return dataUnitName;
	}
	
	@Override
	public RepositoryConnection getConnection() throws RepositoryException {
		if (!ownerThread.equals(Thread.currentThread())) {
			throw new RuntimeException("Constraint violation, only one thread can access this data unit");
		}

		RepositoryConnection connection = repository.getConnection();
		requestedConnections.add(connection);
		return connection;
	}

	@Override
	public void clear() {
		/**
		 * Beware! Clean is called from different thread then all other operations (pipeline executor thread).
		 * That is the reason why we cannot obtain connection using this.getConnection(), it would throw an Exception.
		 * This connection has to be obtained directly from repository and we take care to close it properly. 
		 */
		RepositoryConnection connection = null; 
		try {
			connection = repository.getConnection();
			connection.clear(dataGraph);
		} catch (RepositoryException ex) {
			throw new RuntimeException("Could not delete repository", ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					LOG.warn("Error when closing connection", ex);
					// eat close exception, we cannot do anything clever here
				}
			}
		}
	}

	@Override
	public void release() {
		List<RepositoryConnection> openedConnections = new ArrayList<>();
		for (RepositoryConnection connection : requestedConnections) {
			try {
				if (connection.isOpen()) {
					openedConnections.add(connection);
				}
			} catch (RepositoryException ex) {
				try {
					connection.close();
				} catch (RepositoryException ex1) {
					LOG.warn("Error when closing connection", ex1);
					// eat close exception, we cannot do anything clever here
				}
			}
		}
		
		if (!openedConnections.isEmpty()) {
			LOG.error(String.valueOf(openedConnections.size()) + " connections remained opened after DPU execution.");
			for (RepositoryConnection connection : openedConnections) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					LOG.warn("Error when closing connection", ex);
					// eat close exception, we cannot do anything clever here
				}
			}
		}
	}

	/**
	 * Make RDF data merge over repository - data in repository merge with data
	 * in second defined repository.
	 *
	 *
	 * @param otherDataUnit Type of repository contains RDF data as implementation of
	 *               RDFDataUnit interface.
	 * @throws IllegalArgumentException if otherDataUnit repository is not of compatible type (#RDFDataUnit).
	 */
	@Override
	public void merge(DataUnit otherDataUnit) throws IllegalArgumentException {
		if (!(otherDataUnit instanceof LocalRDFDataUnit)) {
			throw new IllegalArgumentException("Incompatible repository type");
		}
		
		final RDFDataUnit otherRDFDataUnit = (RDFDataUnit) otherDataUnit;
		RepositoryConnection connection = null;
		try {
			connection = getConnection();

			String sourceGraphName = otherRDFDataUnit.getDataGraph().stringValue();
			String targetGraphName = getDataGraph().stringValue();

			String mergeQuery = String.format("ADD <%s> TO <%s>", sourceGraphName,
					targetGraphName);

			Update update =connection.prepareUpdate(
					QueryLanguage.SPARQL, mergeQuery);
	
			update.execute();
			
			LOG.info("Merged {} triples from <{}> to <{}>.",
					connection.size(getDataGraph()), sourceGraphName,
					targetGraphName);
		} catch (MalformedQueryException ex) {
			LOG.error("NOT VALID QUERY: {}", ex);
		} catch (RepositoryException ex) {
			LOG.error(ex.getMessage(), ex);
		} catch (UpdateExecutionException ex) {
			LOG.error(ex.getMessage(), ex);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					LOG.warn("Error when closing connection", ex);
					// eat close exception, we cannot do anything clever here
				}
			}
		}
	}
}