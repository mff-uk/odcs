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
	 *                   {@link TupleQueryResult} interface was created by.
	 * @param result     instance of interface {@link TupleQueryResult} that
	 *                   will be used in no extented method.
	 */
	public MyTupleQueryResult(RepositoryConnection connection,
			TupleQueryResult result) {
		this.connection = connection;
		this.result = result;
	}

	/**
	 *
	 * @return @throws QueryEvaluationException
	 */
	@Override
	public List<String> getBindingNames() throws QueryEvaluationException {
		return result.getBindingNames();
	}

	/**
	 * Close used connection and holding resource.
	 *
	 * @throws QueryEvaluationException An exception indicating that the
	 *                                  evaluation of a query failed.
	 */
	@Override
	public void close() throws QueryEvaluationException {
		closeConnection();
		result.close();
	}

	/**
	 * Get a list containing all elements obtained from the
	 * {@link TupleQueryResult} instance.
	 *
	 * @return List containing all elements obtained from the
	 *         {@link TupleQueryResult} instance.
	 * @throws QueryEvaluationException An exception indicating that the
	 *                                  evaluation of a query failed.
	 */
	public List<BindingSet> asList() throws QueryEvaluationException {
		return Iterations.asList(result);

	}

	/**
	 * Get a set containing all elements obtained from the
	 * {@link TupleQueryResult} instance.
	 *
	 * @return Set containing all elements obtained from the
	 *         {@link TupleQueryResult} instance.
	 * @throws QueryEvaluationException An exception indicating that the
	 *                                  evaluation of a query failed.
	 */
	public Set<BindingSet> asSet() throws QueryEvaluationException {
		return Iterations.asSet(result);

	}

	/**
	 * Returns true if the iteration has more elements, false otherwise.
	 *
	 * @return true if the iteration has more elements, false otherwise.
	 * @throws QueryEvaluationException An exception indicating that the
	 *                                  evaluation of a query failed.
	 */
	@Override
	public boolean hasNext() throws QueryEvaluationException {
		return result.hasNext();
	}

	/**
	 * Returns the next BindingSet element in the iteration.
	 *
	 * @return the next Statement element in the iteration.
	 * @throws QueryEvaluationException An exception indicating that the
	 *                                  evaluation of a query failed.
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
	 * @throws QueryEvaluationException An exception indicating that the
	 *                                  evaluation of a query failed.
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
