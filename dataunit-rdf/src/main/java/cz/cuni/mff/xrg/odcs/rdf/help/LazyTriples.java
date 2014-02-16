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
 * are split into the parts. It can be useful, when all data collection is too
 * big to return at once. The class has behavior as RDF data part iterator,
 * where each data part contains given count of RDF triples.
 *
 * Each calling method {@link #getTriples()} returns next data part. The size of
 * data parts is defined in splitSize variable in constructor, otherwise it is
 * used default splitSize value.
 *
 * @author Jiri Tomes
 */
public class LazyTriples {

	private static Logger logger = LoggerFactory.getLogger(LazyTriples.class);

	private static long DEFAULT_SPLIT_SIZE = 10;

	private long splitSize;

	private RepositoryResult<Statement> lazy;

	/**
	 * Create new constructor for {@link LazyTriples} based on lazy RDF data
	 * iterator
	 * <code> RepositoryResult&lt;Statement&gt;</code> used
	 * {@link #DEFAULT_SPLIT_SIZE} for one data part.
	 *
	 * @param lazy RDF data lazy iterator
	 *             <code> RepositoryResult&lt;Statement&gt;</code>
	 */
	public LazyTriples(RepositoryResult<Statement> lazy) {
		this.lazy = lazy;
		splitSize = DEFAULT_SPLIT_SIZE;

	}

	/**
	 * Create new constructor for {@link LazyTriples} based on lazy RDF data
	 * iterator
	 * <code> RepositoryResult&lt;Statement&gt;</code> and size for one data
	 * part.
	 *
	 * @param lazy      RDF data lazy iterator
	 *                  <code> RepositoryResult&lt;Statement&gt;</code>
	 * @param splitSize define size of one data part as long number.
	 */
	public LazyTriples(RepositoryResult<Statement> lazy, long splitSize) {
		this.lazy = lazy;
		this.splitSize = splitSize;
	}

	/**
	 * Returns true if the repository keeps some RDF data, which were not be
	 * returned yet, false otherwise.
	 *
	 * @return true if repository keeps some RDF data, which were not be
	 *         returned yet, false otherwise.
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
	 * Returns list of RDF triples in the repository for next data part defined
	 * by split RDF data. If there is no triples to return, it is returned empty
	 * collection.
	 *
	 * @return List of RDF triples in the repository for next data part defined
	 *         by split RDF data. If there is no triples to return, it is
	 *         returned empty collection.
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
