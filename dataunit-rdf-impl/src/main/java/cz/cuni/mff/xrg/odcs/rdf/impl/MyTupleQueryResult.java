package cz.cuni.mff.xrg.odcs.rdf.impl;

import cz.cuni.mff.xrg.odcs.rdf.help.MyTupleQueryResultIf;
import info.aduna.iteration.Iterations;
import java.util.List;
import java.util.Set;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class responsible for right closing repository connection after using method
 * from interface TupleQueryResult. At the end of using is needed to call method
 * close on this object.
 *
 * @author Jiri Tomes
 */
public class MyTupleQueryResult implements MyTupleQueryResultIf {

	private static Logger logger = LoggerFactory.getLogger(
			MyTupleQueryResult.class);

	private TupleQueryResult result;

	private RepositoryConnection connection;

	/**
	 * Create new instance of {@link MyTupleQueryResult} based on repository
	 * connection you used to get instance of {@link TupleQueryResult} and this
	 * concrete instance.
	 *
	 * @param connection connection of repository where instance result of
	 *                   {@link TupleQueryResult} interface was created.
	 * @param result     instance of interface {@link TupleQueryResult} that
	 *                   will be used in no extented methods.
	 */
	public MyTupleQueryResult(RepositoryConnection connection,
			TupleQueryResult result) {
		this.connection = connection;
		this.result = result;
	}

	/**
	 * Returns list of variable names defined in query.
	 *
	 * @return List of variable names defined in query.
	 * @throws QueryEvaluationException The exception describes the reason why
	 *                                  the evaluation of the query fails.
	 */
	@Override
	public List<String> getBindingNames() throws QueryEvaluationException {
		return result.getBindingNames();
	}

	/**
	 * Close used connection and holding resource.
	 *
	 * @throws QueryEvaluationException The exception describes the reason why
	 *                                  the evaluation of the query fails.
	 */
	@Override
	public void close() throws QueryEvaluationException {
		closeConnection();
		result.close();
	}

	/**
	 * Get the list with all elements obtained from the {@link TupleQueryResult}
	 * instance.
	 *
	 * @return The list with all elements obtained from the
	 *         {@link TupleQueryResult} instance.
	 * @throws QueryEvaluationException The exception describes the reason why
	 *                                  the evaluation of the query fails.
	 */
	@Override
	public List<BindingSet> asList() throws QueryEvaluationException {
		return Iterations.asList(result);

	}

	/**
	 * Get the set containing all elements obtained from the
	 * {@link TupleQueryResult} instance.
	 *
	 * @return The set containing all elements obtained from the
	 *         {@link TupleQueryResult} instance.
	 * @throws QueryEvaluationException The exception describes the reason why
	 *                                  the evaluation of the query fails.
	 */
	public Set<BindingSet> asSet() throws QueryEvaluationException {
		return Iterations.asSet(result);

	}

	/**
	 * Returns true if the iteration has more elements, false otherwise.
	 *
	 * @return true if the iteration has more elements, false otherwise.
	 * @throws QueryEvaluationException The exception describes the reason why
	 *                                  the evaluation of the query fails.
	 */
	@Override
	public boolean hasNext() throws QueryEvaluationException {
		return result.hasNext();
	}

	/**
	 * Returns the next BindingSet element in the iteration.
	 *
	 * @return the next BindingSet element in the iteration.
	 * @throws QueryEvaluationException The exception describes the reason why
	 *                                  the evaluation of the query fails.
	 */
	@Override
	public BindingSet next() throws QueryEvaluationException {
		return result.next();
	}

	/**
	 * Removes from the underlying collection the last element returned by the
	 * iteration (optional operation). This method can be called only once per
	 * call to next.
	 *
	 * @throws QueryEvaluationException The exception describes the reason why
	 *                                  the evaluation of the query fails.
	 */
	@Override
	public void remove() throws QueryEvaluationException {
		result.remove();
	}

	private void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
			} catch (RepositoryException ex) {
				logger.warn(
						"Failed to close connection to RDF repository while querying. {}",
						ex.getMessage(), ex);
			}
		}
	}
}
