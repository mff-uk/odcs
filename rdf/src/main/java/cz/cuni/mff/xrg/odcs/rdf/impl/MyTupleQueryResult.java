package cz.cuni.mff.xrg.odcs.rdf.impl;

import java.util.Collection;
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
public class MyTupleQueryResult implements TupleQueryResult {

	private static Logger logger = LoggerFactory.getLogger(
			MyTupleQueryResult.class);

	private TupleQueryResult result;

	private RepositoryConnection connection;

	public MyTupleQueryResult(RepositoryConnection connection,
			TupleQueryResult result) {
		this.connection = connection;
		this.result = result;
	}

	@Override
	public List<String> getBindingNames() throws QueryEvaluationException {
		return result.getBindingNames();
	}

	@Override
	public BindingSet singleResult() throws QueryEvaluationException {
		return result.singleResult();
	}

	@Override
	public void close() throws QueryEvaluationException {
		closeConnection();
		result.close();
	}

	@Override
	public <C extends Collection<? super BindingSet>> C addTo(C arg0) throws QueryEvaluationException {
		return result.addTo(arg0);
	}

	@Override
	public List<BindingSet> asList() throws QueryEvaluationException {
		return result.asList();
	}

	@Override
	public Set<BindingSet> asSet() throws QueryEvaluationException {
		return result.asSet();
	}

	@Override
	public boolean hasNext() throws QueryEvaluationException {
		return result.hasNext();
	}

	@Override
	public BindingSet next() throws QueryEvaluationException {
		return result.next();
	}

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
						"Failed to close connection to RDF repository while querying."
						+ ex.getMessage(), ex);
			}
		}
	}
}
