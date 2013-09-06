package cz.cuni.xrg.intlib.rdf.impl;

import cz.cuni.xrg.intlib.commons.configuration.AppConfig;
import cz.cuni.xrg.intlib.commons.configuration.ConfigFileNotFoundException;
import cz.cuni.xrg.intlib.commons.configuration.ConfigProperty;
import cz.cuni.xrg.intlib.commons.configuration.MalformedConfigFileException;
import cz.cuni.xrg.intlib.commons.configuration.MissingConfigPropertyException;
import cz.cuni.xrg.intlib.rdf.exceptions.RDFRepositoryException;
import java.io.File;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jan Vojt
 */
public class FailureTolerantRepositoryWrapper implements Repository {
	
	private static final Logger LOG = LoggerFactory.getLogger(FailureTolerantRepositoryWrapper.class);
	
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
	 * Wrapper constructor.
	 * 
	 * @param repository to wrap
	 */
	public FailureTolerantRepositoryWrapper(Repository repository) {
		configure();
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
		while (true) try {
			attempts++;
			repository.initialize();
			return;
		} catch (RepositoryException ex) {
			handleRetries(attempts, ex);
		}
	}

	@Override
	public boolean isInitialized() {
		return repository.isInitialized();
	}

	@Override
	public void shutDown() throws RepositoryException {
		int attempts = 0;
		while (true) try {
			attempts++;
			repository.shutDown();
			return;
		} catch (RepositoryException ex) {
			handleRetries(attempts, ex);
		}
	}

	@Override
	public boolean isWritable() throws RepositoryException {
		int attempts = 0;
		while (true) try {
			attempts++;
			return repository.isWritable();
		} catch (RepositoryException ex) {
			handleRetries(attempts, ex);
		}
	}

	@Override
	public RepositoryConnection getConnection() throws RepositoryException {
		return repository.getConnection();
	}

	@Override
	public ValueFactory getValueFactory() {
		return repository.getValueFactory();
	}
	
	/**
	 * Configures reconnection trials.
	 */
	private void configure() {
		try {
			AppConfig config = new AppConfig();
			try {
				retries = config.getInteger(ConfigProperty.VIRTUOSO_RETRIES);
			} catch (MissingConfigPropertyException ex) {
				LOG.info(String.format("Missing config property %s, using default value ?.", ex.getProperty(), retries));
			}
			try {
				wait = config.getInteger(ConfigProperty.VIRTUOSO_WAIT);
			} catch (MissingConfigPropertyException ex) {
				LOG.info(String.format("Missing config property %s, using default value ?.", ex.getProperty(), wait));
			}
		} catch (MalformedConfigFileException ex) {
			LOG.info("Configuration file has malformed syntax, using defaults.");
		} catch (ConfigFileNotFoundException ex) {
			LOG.info("Configuration file could not be read, using defaults.");
		}
		
	}
	
	/**
	 * Logic for deciding whether to continue db reconnect attempts. If we
	 * decide to stop trying, exception is thrown.
	 * 
	 * TODO create a handler interface and implementation and merge common logic
	 *		with PipelineFacade#handleRetries() in backend.
	 * 
	 * @param attempts number of attempts so far
	 * @param ex exception to be thrown in case we give up
	 */
	private void handleRetries(int attempts, RepositoryException ex) {
		
		LOG.warn(String.format("Database is down after %d attempts.", attempts));
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
			LOG.error(String.format("Giving up on database after %d retries.", attempts));
			throw new RDFRepositoryException(ex);
		}
	}
	
}
