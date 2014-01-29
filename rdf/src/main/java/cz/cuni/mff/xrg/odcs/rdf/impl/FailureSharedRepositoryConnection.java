package cz.cuni.mff.xrg.odcs.rdf.impl;

import info.aduna.iteration.Iteration;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import org.openrdf.model.*;
import org.openrdf.query.*;
import org.openrdf.repository.*;
import org.openrdf.rio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class responsible for getting shared connection if DB connection failured.
 *
 * @author Jiri Tomes
 */
public class FailureSharedRepositoryConnection implements RepositoryConnection {

	private static final Logger LOG = LoggerFactory.getLogger(
			FailureSharedRepositoryConnection.class);

	private Repository repository;

	/**
	 * Shared RDF connection.
	 */
	private RepositoryConnection sharedConnection;

	/**
	 * If trying to get connection was thrown {@link RepositoryException} or
	 * not.
	 */
	private boolean hasConnectionInterupted = false;

	public FailureSharedRepositoryConnection(Repository repository) {
		this.repository = repository;
	}

	private synchronized RepositoryConnection getSharedConnection() throws RepositoryException {
		if (sharedConnection == null || !sharedConnection.isOpen() || hasConnectionInterupted) {

			if (sharedConnection != null) {
				sharedConnection.close();
			}

			sharedConnection = repository.getConnection();
			hasConnectionInterupted = false;
		}
		return sharedConnection;
	}

	/**
	 * Method for loging db reconnect event - count o attempts.
	 *
	 * @param attempts number of attempts to calling method
	 * @param ex       exception to be thrown in case we give up
	 */
	private void handleRetries(int attempts, RepositoryException ex,
			String methodName) {

		LOG.warn("Database is down after {} attempts while calling method {}.",
				attempts, methodName);
		LOG.debug("The reson is: {} Stack trace: {}", ex.getLocalizedMessage(),
				ex.fillInStackTrace());

		hasConnectionInterupted = true;
	}

	@Override
	public Repository getRepository() {
		return repository;
	}

	@Override
	public void setParserConfig(ParserConfig config) {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				getSharedConnection().setParserConfig(config);
				return;
			} catch (RepositoryException e) {
				handleRetries(attempts, e, "setParserConfig");
			}
		}
	}

	@Override
	public ParserConfig getParserConfig() {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				return getSharedConnection().getParserConfig();
			} catch (RepositoryException e) {
				handleRetries(attempts, e, "getParserConfig");
			}
		}
	}

	@Override
	public boolean isOpen() throws RepositoryException {
		int attempts = 0;
		while (true) {
			try {
				attempts++;
				return getSharedConnection().isOpen();
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
				getSharedConnection().close();
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
				return getSharedConnection().prepareQuery(ql, query);
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
				return getSharedConnection().prepareQuery(ql, query, baseURI);

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
				return getSharedConnection().prepareTupleQuery(ql, query);
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
				return getSharedConnection()
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
				return getSharedConnection().prepareGraphQuery(ql, query);
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
				return getSharedConnection()
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
				return getSharedConnection().prepareBooleanQuery(ql, query);
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
				return getSharedConnection().prepareBooleanQuery(ql, query,
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
				return getSharedConnection().prepareUpdate(ql, update);
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
				return getSharedConnection().prepareUpdate(ql, update, baseURI);
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
				return getSharedConnection().getContextIDs();
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
				return getSharedConnection().getStatements(subj, pred, obj,
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
				return getSharedConnection().hasStatement(subj, pred, obj,
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
				return getSharedConnection().hasStatement(st, includeInferred,
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
				getSharedConnection().exportStatements(subj, pred, obj,
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
				getSharedConnection().export(handler, contexts);
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
				return getSharedConnection().size(contexts);
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
				return getSharedConnection().isEmpty();
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
				getSharedConnection().setAutoCommit(autoCommit);
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
				return getSharedConnection().isAutoCommit();
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
				return getSharedConnection().isActive();
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
				getSharedConnection().begin();
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
				getSharedConnection().commit();
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
				getSharedConnection().rollback();
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
				getSharedConnection().add(in, baseURI, dataFormat,
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
				getSharedConnection().add(reader, baseURI, dataFormat, contexts);
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
				getSharedConnection().add(url, baseURI, dataFormat, contexts);
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
				getSharedConnection().add(file, baseURI, dataFormat, contexts);
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
				getSharedConnection().add(subject, predicate, object, contexts);
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
				getSharedConnection().add(st, contexts);
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
				getSharedConnection().add(statements, contexts);
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
				getSharedConnection().add(statements, contexts);
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
				getSharedConnection()
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
				getSharedConnection().remove(st, contexts);
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
				getSharedConnection().remove(statements, contexts);
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
				getSharedConnection().remove(statements, contexts);
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
				getSharedConnection().clear(contexts);
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
				return getSharedConnection().getNamespaces();
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
				return getSharedConnection().getNamespace(prefix);
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
				getSharedConnection().setNamespace(prefix, name);
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
				getSharedConnection().removeNamespace(prefix);
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
				getSharedConnection().clearNamespaces();
				return;
			} catch (RepositoryException ex) {
				handleRetries(attempts, ex, "clearNamespaces()");
			}
		}
	}

	@Override
	public ValueFactory getValueFactory() {
		return repository.getValueFactory();
	}
}
