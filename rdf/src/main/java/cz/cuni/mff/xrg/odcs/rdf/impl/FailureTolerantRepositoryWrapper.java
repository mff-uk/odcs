package cz.cuni.mff.xrg.odcs.rdf.impl;

import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFRepositoryException;
import info.aduna.iteration.Iteration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Properties;
import org.openrdf.model.*;
import org.openrdf.query.*;
import org.openrdf.repository.*;
import org.openrdf.rio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jan Vojt
 * @author Jiri Tomes
 */
public class FailureTolerantRepositoryWrapper implements Repository, RepositoryConnection {

	private static final Logger LOG = LoggerFactory.getLogger(
			FailureTolerantRepositoryWrapper.class);

	private static final String RETRIES_KEY = "retries";

	private static final String WAIT_KEY = "wait";

	private static final String EXTENSION_KEY = "useExtension";

	/**
	 * Wrapped RDF repository.
	 */
	private Repository repository;

	/**
	 * Wrapped RDF connection.
	 */
	private RepositoryConnection connection;

	/**
	 * If trying to get connection was thrown {@link RepositoryException} or
	 * not.
	 */
	private boolean hasConnectionInterupted = false;

	/**
	 * Number of retries before giving up on reconnecting with db. Zero means
	 * immediate failure, -1 means never give up and keep trying.
	 */
	private int retries = -1;

	/**
	 * Number of milliseconds to wait before the next retrial.
	 */
	private int wait = 2000;

	/**
	 * If can be use virtuoso extension specific syntax, or not.
	 */
	private boolean extension = false;

	/**
	 * Wrapper constructor with configuration.
	 *
	 * @param repository to wrap
	 */
	public FailureTolerantRepositoryWrapper(Repository repository,
			Properties properties) {
		this(repository);
		configure(properties);
	}

	/**
	 * Wrapper constructor using default configuration. Does not log a notice.
	 *
	 * @param repository to wrap
	 */
	public FailureTolerantRepositoryWrapper(Repository repository) {
		this.repository = repository;
	}

	@Override
	public void setDataDir(File dataDir) {
		repository.setDataDir(dataDir);
	}

	@Override
	public File getDataDir() {
		return repository.getDataDir();
	}

	@Override
	public void initialize() throws RDFRepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				repository.initialize();
				return;
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex, "initialize()");
			}
		}
	}

	@Override
	public boolean isInitialized() {
		return repository.isInitialized();
	}

	@Override
	public void shutDown() throws RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				repository.shutDown();
				return;
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex, "shutDown()");
			}
		}
	}

	@Override
	public boolean isWritable() throws RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				return repository.isWritable();
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex, "isWritable()");
			}
		}
	}

	private synchronized RepositoryConnection repoConnection() throws RepositoryException {
		if (connection == null || !connection.isOpen() || hasConnectionInterupted) {
			connection = getRepoConnection();
			hasConnectionInterupted = false;
		}
		return connection;
	}

	private synchronized RepositoryConnection getRepoConnection() {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				return repository.getConnection();
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex, "getRepoConnection()");
			}
		}
	}

	@Override
	public RepositoryConnection getConnection() throws RepositoryException {
		return repoConnection();
	}

	@Override
	public ValueFactory getValueFactory() {
		return repository.getValueFactory();
	}

	/**
	 * Configures reconnection trials.
	 *
	 * @throws NumberFormatException if configuration values are specified, but
	 *                               invalid
	 */
	private void configure(Properties properties) {
		String sRetries = properties.getProperty(RETRIES_KEY);
		if (sRetries == null) {
			LOG.info("Missing config property {}, using default value {}.",
					RETRIES_KEY, retries);
		} else {
			retries = Integer.parseInt(sRetries);
		}

		String sWait = properties.getProperty(WAIT_KEY);
		if (sWait == null) {
			LOG.info("Missing config property {}, using default value {}.",
					WAIT_KEY, wait);
		} else {
			wait = Integer.parseInt(sWait);
		}

		String sExtension = properties.getProperty(EXTENSION_KEY);
		if (sExtension == null) {
			LOG.info("Missing config property {}, using default value {}.",
					EXTENSION_KEY, extension);
		} else {
			extension = Boolean.parseBoolean(sExtension);
		}
	}

	/**
	 *
	 * @return If is used virtuoso extension specific syntax, or not.
	 */
	public boolean useVirtuosoExtension() {
		return extension;
	}

	/**
	 * Logic for deciding whether to continue db reconnect attempts. If we
	 * decide to stop trying, exception is thrown.
	 *
	 * TODO create a handler interface and implementation and merge common logic
	 * with PipelineFacade#handleRetries() in backend.
	 *
	 * @param attempts number of attempts so far
	 * @param ex       exception to be thrown in case we give up
	 */
	private void handleRetries(int attempts, RepositoryException ex,
			String methodName) {

		LOG.warn("Database is down after {} attempts while calling method {}.",
				attempts, methodName);
		LOG.debug("The reson is: {}", ex.getLocalizedMessage());

		if (attempts == 1) {
			// TODO send notification after first error only
			//notify(ex);
		}

		boolean loop = true;
		if (retries >= 0) {
			loop = attempts <= retries;
		}
		if (loop && wait > 0) {
			try {
				Thread.sleep(wait);
			} catch (InterruptedException e) {
				LOG.error("Thread interrupted while sleeping.", e);
			}
		} else if (!loop) {
			LOG.error("Giving up on database after {} retries.", attempts);
			throw new RDFRepositoryException(ex);
		}

		hasConnectionInterupted = true;
	}

	@Override
	public Repository getRepository() {
		return repository;
	}

	@Override
	public void setParserConfig(ParserConfig config) {
		try {
			repoConnection().setParserConfig(config);
		} catch (RepositoryException e) {
			LOG.debug(e.getMessage());
		}
	}

	@Override
	public ParserConfig getParserConfig() {
		try {
			return repoConnection().getParserConfig();
		} catch (RepositoryException e) {
			LOG.debug(e.getMessage());
			return null;
		}
	}

	@Override
	public boolean isOpen() throws RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				return repoConnection().isOpen();
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex, "isOpen()");
			}
		}
	}

	@Override
	public void close() throws RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				this.repoConnection().close();
				return;
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex, "close()");
			}
		}
	}

	@Override
	public Query prepareQuery(QueryLanguage ql, String query) throws RepositoryException, MalformedQueryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				return repoConnection().prepareQuery(ql, query);
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex,
						"prepareQuery(QueryLanguage ql, String query)");
			}
		}
	}

	@Override
	public Query prepareQuery(QueryLanguage ql, String query, String baseURI)
			throws RepositoryException, MalformedQueryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				return repoConnection().prepareQuery(ql, query, baseURI);

			} catch (RepositoryException ex) {
				handleRetries(attempts, ex,
						"prepareQuery(QueryLanguage ql, String query, String baseURI)");
			}
		}
	}

	@Override
	public TupleQuery prepareTupleQuery(QueryLanguage ql, String query) throws RepositoryException, MalformedQueryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				return repoConnection().prepareTupleQuery(ql, query);
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex,
						"prepareTupleQuery(QueryLanguage ql, String query)");
			}
		}
	}

	@Override
	public TupleQuery prepareTupleQuery(QueryLanguage ql, String query,
			String baseURI) throws RepositoryException, MalformedQueryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				return repoConnection()
						.prepareTupleQuery(ql, query, baseURI);
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex,
						"prepareTupleQuery(QueryLanguage ql, String query,String baseURI)");
			}
		}
	}

	@Override
	public GraphQuery prepareGraphQuery(QueryLanguage ql, String query) throws RepositoryException, MalformedQueryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				return repoConnection().prepareGraphQuery(ql, query);
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex,
						"prepareGraphQuery(QueryLanguage ql, String query)");
			}
		}
	}

	@Override
	public GraphQuery prepareGraphQuery(QueryLanguage ql, String query,
			String baseURI) throws RepositoryException, MalformedQueryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				return repoConnection()
						.prepareGraphQuery(ql, query, baseURI);
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex,
						"prepareGraphQuery(QueryLanguage ql, String query,String baseURI)");
			}
		}
	}

	@Override
	public BooleanQuery prepareBooleanQuery(QueryLanguage ql, String query)
			throws RepositoryException, MalformedQueryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				return repoConnection().prepareBooleanQuery(ql, query);
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex,
						"prepareBooleanQuery(QueryLanguage ql, String query)");
			}
		}
	}

	@Override
	public BooleanQuery prepareBooleanQuery(QueryLanguage ql, String query,
			String baseURI) throws RepositoryException, MalformedQueryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				return repoConnection().prepareBooleanQuery(ql, query,
						baseURI);
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex,
						"prepareBooleanQuery(QueryLanguage ql, String query, String baseURI)");
			}
		}
	}

	@Override
	public Update prepareUpdate(QueryLanguage ql, String update) throws RepositoryException, MalformedQueryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				return repoConnection().prepareUpdate(ql, update);
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex,
						"prepareUpdate(QueryLanguage ql, String update)");
			}
		}
	}

	@Override
	public Update prepareUpdate(QueryLanguage ql, String update, String baseURI)
			throws RepositoryException, MalformedQueryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				return repoConnection().prepareUpdate(ql, update, baseURI);
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex,
						"prepareUpdate(QueryLanguage ql, String update, String baseURI)");
			}
		}
	}

	@Override
	public RepositoryResult<Resource> getContextIDs() throws RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				return repoConnection().getContextIDs();
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex, "getContextIDs()");
			}
		}
	}

	@Override
	public RepositoryResult<Statement> getStatements(Resource subj, URI pred,
			Value obj, boolean includeInferred, Resource... contexts) throws RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				return repoConnection().getStatements(subj, pred, obj,
						includeInferred, contexts);
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex, "getStatements()");
			}
		}
	}

	@Override
	public boolean hasStatement(Resource subj, URI pred, Value obj,
			boolean includeInferred, Resource... contexts) throws RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				return repoConnection().hasStatement(subj, pred, obj,
						includeInferred, contexts);
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex,
						"hasStatement(Resource subj, URI pred, Value obj,boolean includeInferred, Resource... contexts)");
			}
		}
	}

	@Override
	public boolean hasStatement(Statement st, boolean includeInferred,
			Resource... contexts) throws RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				return repoConnection().hasStatement(st, includeInferred,
						contexts);
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex,
						"hasStatement(Statement st, boolean includeInferred,Resource... contexts)");
			}
		}
	}

	@Override
	public void exportStatements(Resource subj, URI pred, Value obj,
			boolean includeInferred, RDFHandler handler, Resource... contexts)
			throws RepositoryException, RDFHandlerException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				repoConnection().exportStatements(subj, pred, obj,
						includeInferred, handler, contexts);
				return;
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex, "exportStatements()");
			}
		}
	}

	@Override
	public void export(RDFHandler handler, Resource... contexts) throws RepositoryException, RDFHandlerException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				repoConnection().export(handler, contexts);
				return;
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex, "export()");
			}
		}
	}

	@Override
	public long size(Resource... contexts) throws RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				return repoConnection().size(contexts);
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex, "size(Resource... contexts)");
			}
		}
	}

	@Override
	public boolean isEmpty() throws RepositoryException {

		int attempts = 0;
		while (true) {
			try {
				attempts++;
				return repoConnection().isEmpty();
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex, "isEmpty()");
			}
		}
	}

	@Deprecated
	@Override
	public void setAutoCommit(boolean autoCommit) throws RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				repoConnection().setAutoCommit(autoCommit);
				return;
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex, "setAutoCommit(boolean autoCommit)");
			}
		}
	}

	@Deprecated
	@Override
	public boolean isAutoCommit() throws RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				return repoConnection().isAutoCommit();
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex, "isAutoCommit()");
			}
		}
	}

	@Override
	public boolean isActive() throws UnknownTransactionStateException, RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				return repoConnection().isActive();
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex, "isActive()");
			}
		}
	}

	@Override
	public void begin() throws RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				repoConnection().begin();
				return;
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex, "begin()");
			}
		}
	}

	@Override
	public void commit() throws RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				repoConnection().commit();
				return;
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex, "comit()");
			}
		}
	}

	@Override
	public void rollback() throws RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				repoConnection().rollback();
				return;
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex, "rollback()");
			}
		}
	}

	@Override
	public void add(InputStream in, String baseURI, RDFFormat dataFormat,
			Resource... contexts) throws IOException, RDFParseException, RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				repoConnection().add(in, baseURI, dataFormat,
						contexts);
				return;
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex,
						"add(InputStream in, String baseURI, RDFFormat dataFormat,Resource... contexts)");
			}
		}
	}

	@Override
	public void add(Reader reader, String baseURI, RDFFormat dataFormat,
			Resource... contexts) throws IOException, RDFParseException, RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				repoConnection().add(reader, baseURI, dataFormat, contexts);
				return;
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex,
						"add(Reader reader, String baseURI, RDFFormat dataFormat,Resource... contexts");
			}
		}
	}

	@Override
	public void add(URL url, String baseURI, RDFFormat dataFormat,
			Resource... contexts) throws IOException, RDFParseException, RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				this.getConnection().add(url, baseURI, dataFormat, contexts);
				return;
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex,
						"add(URL url, String baseURI, RDFFormat dataFormat,Resource... contexts)");
			}
		}
	}

	@Override
	public void add(File file, String baseURI, RDFFormat dataFormat,
			Resource... contexts) throws IOException, RDFParseException, RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				repoConnection().add(file, baseURI, dataFormat, contexts);
				return;
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex,
						"add(File file, String baseURI, RDFFormat dataFormat,Resource... contexts)");
			}
		}
	}

	@Override
	public void add(Resource subject, URI predicate, Value object,
			Resource... contexts) throws RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				repoConnection().add(subject, predicate, object, contexts);
				return;
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex,
						"add(Resource subject, URI predicate, Value object,Resource... contexts)");
			}
		}
	}

	@Override
	public void add(Statement st, Resource... contexts) throws RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				repoConnection().add(st, contexts);
				return;
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex,
						"add(Statement st, Resource... contexts)");
			}
		}
	}

	@Override
	public void add(
			Iterable<? extends Statement> statements, Resource... contexts)
			throws RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				repoConnection().add(statements, contexts);
				return;
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex,
						"add(Iterable<? extends Statement> statements, Resource... contexts)");
			}
		}
	}

	@Override
	public <E extends Exception> void add(
			Iteration<? extends Statement, E> statements, Resource... contexts)
			throws RepositoryException, E {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				repoConnection().add(statements, contexts);
				return;
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex,
						"add(Iteration<? extends Statement, E> statements, Resource... contexts)");
			}
		}
	}

	@Override
	public void remove(Resource subject, URI predicate, Value object,
			Resource... contexts) throws RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				repoConnection()
						.remove(subject, predicate, object, contexts);
				return;
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex,
						"remove(Resource subject, URI predicate, Value object,Resource... contexts)");
			}
		}
	}

	@Override
	public void remove(Statement st, Resource... contexts) throws RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				repoConnection().remove(st, contexts);
				return;
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex,
						"remove(Statement st, Resource... contexts)");
			}
		}
	}

	@Override
	public void remove(
			Iterable<? extends Statement> statements, Resource... contexts)
			throws RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				repoConnection().remove(statements, contexts);
				return;
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex,
						"remove(Iterable<? extends Statement> statements, Resource... contexts)");
			}
		}
	}

	@Override
	public <E extends Exception> void remove(
			Iteration<? extends Statement, E> statements, Resource... contexts)
			throws RepositoryException, E {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				repoConnection().remove(statements, contexts);
				return;
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex,
						"remove(Iteration<? extends Statement, E> statements, Resource... contexts)");
			}
		}
	}

	@Override
	public void clear(Resource... contexts) throws RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				repoConnection().clear(contexts);
				return;
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex, "clear()");
			}
		}
	}

	@Override
	public RepositoryResult<Namespace> getNamespaces() throws RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				return repoConnection().getNamespaces();
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex, "getNamespaces()");
			}
		}
	}

	@Override
	public String getNamespace(String prefix) throws RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				return repoConnection().getNamespace(prefix);
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex, "getNamespace(String prefix)");
			}
		}
	}

	@Override
	public void setNamespace(String prefix, String name) throws RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				this.getConnection().setNamespace(prefix, name);
				return;
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex, "setNamespace()");
			}
		}
	}

	@Override
	public void removeNamespace(String prefix) throws RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				repoConnection().removeNamespace(prefix);
				return;
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex, "removeNamespace()");
			}
		}
	}

	@Override
	public void clearNamespaces() throws RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				repoConnection().clearNamespaces();
				return;
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex, "clearNamespaces()");
			}
		}
	}
}
