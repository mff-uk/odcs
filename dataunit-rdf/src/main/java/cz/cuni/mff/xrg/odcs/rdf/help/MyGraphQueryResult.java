package cz.cuni.mff.xrg.odcs.rdf.help;

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
//TODO jan.marcek i don't believe that we need this class. I suggest to remove after analysis
// We don't have an access to cz.cuni.mff.xrg.odcs.rdf.repositories. so that's why MyGraphQueryResult is here.
public class MyGraphQueryResult implements GraphQueryResult {

    private GraphQueryResult result;

    /**
     * Create new instance of {@link MyGraphQueryResult} based on interface {@link GraphQueryResult}.
     * 
     * @param result
     *            instance of interface {@link GraphQueryResult} that will be
     *            used in no extented method.
     */
    public MyGraphQueryResult(GraphQueryResult result) {
        this.result = result;
    }

    /**
     * Retrieves relevant namespaces from the query result.
     * 
     * @return namespaces from the query result.
     * @throws QueryEvaluationException
     *             An exception indicating that the
     *             evaluation of a query failed.
     */
    @Override
    public Map<String, String> getNamespaces() throws QueryEvaluationException {
        return result.getNamespaces();

    }

    /**
     * Closes this iteration, freeing any resources that it is holding. If the
     * iteration has already been closed then invoking this method has no
     * effect.
     * 
     * @throws QueryEvaluationException
     *             An exception indicating that the
     *             evaluation of a query failed.
     */
    @Override
    public void close() throws QueryEvaluationException {
        result.close();
    }

    /**
     * Returns true if the iteration has more elements, false otherwise.
     * 
     * @return true if the iteration has more elements, false otherwise.
     * @throws QueryEvaluationException
     *             An exception indicating that the
     *             evaluation of a query failed.
     */
    @Override
    public boolean hasNext() throws QueryEvaluationException {
        return result.hasNext();
    }

    /**
     * Returns the next Statement element in the iteration.
     * 
     * @return the next Statement element in the iteration.
     * @throws QueryEvaluationException
     *             An exception indicating that the
     *             evaluation of a query failed.
     */
    @Override
    public Statement next() throws QueryEvaluationException {
        return result.next();
    }

    /**
     * Removes from the underlying collection the last element returned by the
     * iteration (optional operation). This method can be called only once per
     * call to next.
     * 
     * @throws QueryEvaluationException
     *             An exception indicating that the
     *             evaluation of a query failed.
     */
    @Override
    public void remove() throws QueryEvaluationException {
        result.remove();
    }

    /**
     * Get a Model containing all elements obtained from the specified query
     * result.
     * 
     * @return Model containing all elements obtained from the specified query
     *         result.
     * @throws QueryEvaluationException
     *             An exception indicating that the
     *             evaluation of a query failed.
     */
    public Model asGraph() throws QueryEvaluationException {

        Model resultGraph = QueryResults.asModel(result);

        return resultGraph;
    }
}
