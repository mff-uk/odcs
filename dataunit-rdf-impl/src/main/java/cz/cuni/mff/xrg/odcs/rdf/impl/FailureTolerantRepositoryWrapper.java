package cz.cuni.mff.xrg.odcs.rdf.impl;

import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFRepositoryException;
import java.io.File;
import java.util.Properties;
import org.openrdf.model.*;
import org.openrdf.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used repository with methods resistant to DB failure.
 *
 * @author Jan Vojt
 * @author Jiri Tomes
 */
public class FailureTolerantRepositoryWrapper implements Repository {

	private static final Logger LOG = LoggerFactory.getLogger(
			FailureTolerantRepositoryWrapper.class);

	private static final String RETRIES_KEY = "retries";

	private static final String WAIT_KEY = "wait";

	/**
	 * Wrapped RDF repository.
	 */
	private Repository repository;

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
	 * Wrapper constructor with configuration.
	 *
	 * @param repository to wrap
	 * @param properties reconnect configuration
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

	/**
	 * Set the directory where data and logging for this repository is stored.
	 *
	 * @param dataDir the directory where data for this repository is stored.
	 */
	@Override
	public void setDataDir(File dataDir) {
		repository.setDataDir(dataDir);
	}

	/**
	 * Get the directory where data and logging for this repository is stored.
	 *
	 * @return the directory where data for this repository is stored.
	 */
	@Override
	public File getDataDir() {
		return repository.getDataDir();
	}

	/**
	 * Initializes this repository. A repository needs to be initialized before
	 * it can be used.
	 *
	 * @throws RDFRepositoryException if the initialization failed.
	 */
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

	/**
	 * Indicates if the Repository has been initialized. Note that the
	 * initialization status may change if the Repository is shut down.
	 *
	 * @return true iff the repository has been initialized, false otherwise.
	 */
	@Override
	public boolean isInitialized() {
		return repository.isInitialized();
	}

	/**
	 * Shuts the repository down, releasing any resources that it keeps hold of.
	 * Once shut down, the repository can no longer be used until it is
	 * re-initialized.
	 *
	 * @throws RepositoryException
	 */
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

	/**
	 * Checks whether this repository is writable, i.e. if the data contained in
	 * this repository can be changed. The writability of the repository is
	 * determined by the writability of the Sail that this repository operates
	 * on.
	 *
	 * @return true if this repository is writable, false otherwise.
	 * @throws RepositoryException
	 */
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

	/**
	 * Opens a connection to this repository that can be used for querying and
	 * updating the contents of the repository. Created connections need to be
	 * closed to make sure that any resources they keep hold of are released.
	 *
	 * @return A connection that allows operations on this repository.
	 * @throws RepositoryException if something went wrong during the creation
	 *                             of the connection.
	 */
	@Override
	public RepositoryConnection getConnection() throws RepositoryException {
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

	/**
	 * Gets the ValueFactory for this Repository.
	 *
	 * @return The repository-specific ValueFactory.
	 */
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
			LOG.info("Missing config property {}, using default value '{}'.",
					RETRIES_KEY, retries);
		} else {
			retries = Integer.parseInt(sRetries);
		}

		String sWait = properties.getProperty(WAIT_KEY);
		if (sWait == null) {
			LOG.info("Missing config property {}, using default value '{}'.",
					WAIT_KEY, wait);
		} else {
			wait = Integer.parseInt(sWait);
		}
	}

	/**
	 * Logic for deciding whether to continue db reconnect attempts. If we
	 * decide to stop trying, exception is thrown.
	 *
	 * @param attempts number of attempts so far
	 * @param ex       exception to be thrown in case we give up
	 */
	private void handleRetries(int attempts, RepositoryException ex,
			String methodName) {

		LOG.warn("Database is down after {} attempts while calling method {}.",
				attempts, methodName);
		LOG.debug("The reson is: {} Stack trace: {}", ex.getLocalizedMessage(),
				ex.fillInStackTrace());

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

	}
}
