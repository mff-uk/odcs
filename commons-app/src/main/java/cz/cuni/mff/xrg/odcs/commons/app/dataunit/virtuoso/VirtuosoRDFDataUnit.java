package cz.cuni.mff.xrg.odcs.commons.app.dataunit.virtuoso;

import java.util.ArrayList;
import java.util.List;

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
import cz.cuni.mff.xrg.odcs.commons.app.dataunit.AbstractRDFDataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitType;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;

/**
 * Implementation of Virtuoso repository - RDF data and intermediate results are
 * saved in Virtuoso storage.
 *
 * @author Jiri Tomes
 */
public final class VirtuosoRDFDataUnit extends AbstractRDFDataUnit {
	
	private static final Logger LOG = LoggerFactory.getLogger(VirtuosoRDFDataUnit.class);

	/**
	 * DataUnit's name.
	 */
	private String dataUnitName;
	
	private Repository repository;

	private List<RepositoryConnection> requestedConnections;
	
	private Thread ownerThread;

	/**
	 * Construct a VirtuosoRepository with a specified parameters.
	 *
	 * @param url the Virtuoso JDBC URL connection string or hostlist
	 *                      for pooled connection.
	 *
	 * @param user          the database user on whose behalf the connection is
	 *                      being made.
	 *
	 * @param password      the user's password.
	 *
	 * @param dataGraph  a default Graph name, used for Sesame calls, when
	 *                      contexts list is empty, exclude exportStatements,
	 *                      hasStatement, getStatements methods.
	 * @param dataUnitName	 DataUnit's name. If not used in Pipeline can be
	 *                      empty String.
	 * @throws RepositoryException 
	 */
	public VirtuosoRDFDataUnit(String url, String user, String password,
			String dataUnitName, String dataGraph) {
		this.dataUnitName = dataUnitName;
		this.requestedConnections = new ArrayList<>();
		this.ownerThread = Thread.currentThread();
		
		setDataGraph(dataGraph);

		this.repository = new VirtuosoRepository(url, user, password);
		try {
			repository.initialize();
		} catch (RepositoryException ex) {
			throw new RuntimeException("Could not initialize repository", ex);
		}
		RepositoryConnection connection = null; 
		try {
            connection = getConnection();
			LOG.info("Initialized Virtuoso RDF DataUnit named '{}' with data graph <{}> containing {} triples.",
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
	
	/**
	 * Return type of data unit interface implementation.
	 *
	 * @return DataUnit type.
	 */
	@Override
	public DataUnitType getType() {
		return DataUnitType.RDF;
	}

	/**
	 *
	 * @return String name of data unit.
	 */
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
		
		try {
			repository.shutDown();
			LOG.info("Virtuoso RDF DataUnit with data graph <"
					+ getDataGraph()
					+ "> succesfully shut down");
		} catch (RepositoryException ex) {
			LOG.error("Error in repository shutdown", ex);
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
		if (!(otherDataUnit instanceof VirtuosoRDFDataUnit)) {
			throw new IllegalArgumentException("Incompatible repository type");
		}
		
		final RDFDataUnit otherRDFDataUnit = (RDFDataUnit) otherDataUnit;
		RepositoryConnection connection = null;
		try {
			connection = getConnection();

			String sourceGraphName = otherRDFDataUnit.getDataGraph().stringValue();
			String targetGraphName = getDataGraph().stringValue();


			// mergeQuery = String.format("DEFINE sql:log-enable %d \n"
			// + "ADD <%s> TO <%s>",
			// LOG_LEVEL,
			// sourceGraphName,
			// targetGraphName);
			String mergeQuery = String.format("ADD <%s> TO <%s>", sourceGraphName,
					targetGraphName);

			GraphQuery result = connection.prepareGraphQuery(
					QueryLanguage.SPARQL, mergeQuery);

			result.evaluate();

			LOG.info("Merged {} triples from <{}> to <{}>.",
					connection.size(getDataGraph()), sourceGraphName,
					targetGraphName);

		} catch (MalformedQueryException ex) {
			LOG.error("NOT VALID QUERY: {}", ex);
		} catch (QueryEvaluationException ex) {
			LOG.error("MERGING STOPPED: {}", ex);
		} catch (RepositoryException ex) {
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

	@Override
	public void isReleaseReady() {
		int count = 0;
		for (RepositoryConnection connection : requestedConnections) {
			try {
				if (connection.isOpen()) {
					count++;
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
		
		if (count > 0) {
			LOG.error("{} connections remained opened after DPU execution on graph <{}>, dataUnitName '{}'.", count, this.getDataGraph(), this.getDataUnitName());
		}
	}
}
