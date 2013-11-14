package cz.cuni.mff.xrg.odcs.rdf.help;

import java.util.ArrayList;
import java.util.List;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class responsible for lazy returning RDF triples keeping in repository - data
 * are split on parts. It can be useful, when all data collection is too big to
 * return at once. The class behaves as RDF data part iterator, where each data
 * part containt given size of RDF triples.
 *
 * Each calling method {@link #getTriples()} return next data part. Size of data
 * parts is defined using splitSize variable in constructor or in not, then are
 * used default splitSize value.
 *
 * @author Jiri Tomes
 */
public class LazyTriples {

	private static Logger logger = LoggerFactory.getLogger(LazyTriples.class);

	private static long DEFAULT_SPLIT_SIZE = 10;

	private long splitSize;

	private RepositoryResult<Statement> lazy;

	public LazyTriples(RepositoryResult<Statement> lazy) {
		this.lazy = lazy;
		splitSize = DEFAULT_SPLIT_SIZE;

	}

	public LazyTriples(RepositoryResult<Statement> lazy, long splitSize) {
		this.lazy = lazy;
		this.splitSize = splitSize;
	}

	/**
	 *
	 * @return true if reposiotory keeps some RDF data, which were not return
	 *         yet, false otherwise.
	 */
	public boolean hasNextTriples() {
		if (lazy == null) {
			return false;
		}
		try {
			return lazy.hasNext();
		} catch (RepositoryException ex) {
			logger.debug(ex.getMessage());
			return false;
		}
	}

	/**
	 *
	 * @return List of RDF triples in repository for next data part defined by
	 *         split RDF data keeps in repository using splitSize. If there is
	 *         no triples to return yet, it returns empty collection.
	 */
	public List<Statement> getTriples() {
		List<Statement> result = new ArrayList<>();

		if (hasNextTriples()) {
			try {
				long count = 0;
				while (lazy.hasNext() && count < splitSize) {
					Statement nextStatement = lazy.next();
					result.add(nextStatement);
				}
			} catch (RepositoryException e) {
				logger.debug(e.getMessage());
			}
		}

		return result;
	}
}
