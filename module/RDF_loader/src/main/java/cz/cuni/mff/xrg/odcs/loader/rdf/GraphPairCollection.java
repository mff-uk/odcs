package cz.cuni.mff.xrg.odcs.loader.rdf;

import java.util.LinkedList;
import java.util.List;

/**
 * Class responsible for operation over {@link GraphPair} class collection.
 * 
 * @author Jiri Tomes
 */
public class GraphPairCollection {

    List<GraphPair> graphPairs;

    /**
     * Create new empty collection.
     */
    public GraphPairCollection() {
        graphPairs = new LinkedList<>();
    }

    /**
     * Create new {@link GraphPair} from given parameters and add it to
     * collection.
     * 
     * @param graphName
     *            String value of graph name (URI type)
     * @param tempGraphName
     *            String value of temp graph name (URI type)
     */
    public void add(String graphName, String tempGraphName) {
        graphPairs.add(new GraphPair(graphName, tempGraphName));
    }

    /**
     * Add instance of {@link GraphPair} to collection.
     * 
     * @param graphPair
     *            instance of {@link GraphPair} for adding to collection.
     */
    public void add(GraphPair graphPair) {
        graphPairs.add(graphPair);
    }

    /**
     * Returns collection of {@link GraphPair}.
     * 
     * @return collection of {@link GraphPair}.
     */
    public List<GraphPair> getGraphPairs() {
        return graphPairs;
    }

    /**
     * Returns collection of graphs named extracted from all contained {@link GraphPair}.
     * return collection of graphs named extracted from all contained {@link GraphPair}.
     */
    public List<String> getGraphs() {
        List<String> result = new LinkedList<>();

        for (GraphPair nextPair : graphPairs) {
            result.add(nextPair.getGraphName());
        }

        return result;
    }

    /**
     * Returns collection of temp graphs extracted from all contained {@link GraphPair}.
     * return collection of temp graphs extracted from all contained {@link GraphPair}.
     */
    public List<String> getTempGraphs() {
        List<String> result = new LinkedList<>();

        for (GraphPair nextPair : graphPairs) {
            result.add(nextPair.getTempGraphName());
        }

        return result;
    }
}
