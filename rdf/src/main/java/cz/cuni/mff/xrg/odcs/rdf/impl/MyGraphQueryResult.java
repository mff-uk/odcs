package cz.cuni.mff.xrg.odcs.rdf.impl;

import java.util.Map;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryResults;

/**
 * Extension of interface {@link GraphQueryResult} - add methods for creating
 * graph model. Need as result for SPARQL construct queries.
 *
 * @author Jiri Tomes
 */
public class MyGraphQueryResult implements GraphQueryResult {

	private GraphQueryResult result;

	public MyGraphQueryResult(GraphQueryResult result) {
		this.result = result;
	}

	@Override
	public Map<String, String> getNamespaces() throws QueryEvaluationException {
		return result.getNamespaces();
	}

	@Override
	public void close() throws QueryEvaluationException {
		result.close();
	}

	@Override
	public boolean hasNext() throws QueryEvaluationException {
		return result.hasNext();
	}

	@Override
	public Statement next() throws QueryEvaluationException {
		return result.next();
	}

	@Override
	public void remove() throws QueryEvaluationException {
		result.remove();
	}

	public Model asGraph() throws QueryEvaluationException {

		Model resultGraph = QueryResults.asModel(result);

		return resultGraph;
	}
}
