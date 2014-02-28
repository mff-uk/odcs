package cz.cuni.mff.xrg.odcs.rdf.help;

import java.util.List;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

/**
 * Define behavior of result SPARQL SELECT queries as iterator over
 * {@link TupleQueryResult}.
 *
 * @author tomasknap
 */
public interface MyTupleQueryResultIf extends TupleQueryResult {

	/**
	 * Returns true if the iteration has more elements, false otherwise.
	 *
	 * @return true if the iteration has more elements, false otherwise.
	 * @throws QueryEvaluationException The exception describes the reason why
	 *                                  the evaluation of the query fails.
	 */
	@Override
	public boolean hasNext() throws QueryEvaluationException;

	/**
	 * Returns the next BindingSet element in the iteration.
	 *
	 * @return the next BindingSet element in the iteration.
	 * @throws QueryEvaluationException The exception describes the reason why
	 *                                  the evaluation of the query fails.
	 */
	@Override
	public BindingSet next() throws QueryEvaluationException;

	/**
	 * Get a list with all elements obtained from the {@link TupleQueryResult}
	 * instance.
	 *
	 * @return List with all elements obtained from the {@link TupleQueryResult}
	 *         instance.
	 * @throws QueryEvaluationException The exception describes the reason why
	 *                                  the evaluation of the query fails.
	 */
	public List<BindingSet> asList() throws QueryEvaluationException;
}
