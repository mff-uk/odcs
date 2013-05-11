package cz.cuni.xrg.intlib.commons.app.pipeline.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Graph of DPU dependencies.
 *
 * @author Jan Vojt
 *
 */
public class DependencyGraph implements Iterable<Node> {

    /**
     * DPU Graph
     */
    private PipelineGraph graph;
    /**
     * Structure for building dependency graph with dependency nodes indexed by
     * nodes
     */
    private Map<Node, DependencyNode> dGraph = new HashMap<>();
    /**
     * List of Extractor nodes - nodes without dependencies
     */
    private List<DependencyNode> extractors = new ArrayList<>();

    /**
     * Constructs dependency graph from given pipeline graph.
     *
     * @param graph
     */
    public DependencyGraph(PipelineGraph graph) {
        this.graph = graph;
        buildDependencyGraph();
        findExtractors();
    }

    /**
     * Returns iterator, which iterates over pipeline graph in a way that all
     * dependencies come before the nodes they depend on.
     */
    @Override
    public GraphIterator iterator() {
        return new GraphIterator(this);
    }

    /**
     * @return the extractors
     */
    public List<DependencyNode> getExtractors() {
        return extractors;
    }

    /**
     * Return all direct ancestors to the given node.
     * @param node
     * @return
     */
    public List<Node> getAncestors(Node node) {
    	//TODO: implement
    	return null;
    }
    
    /**
     * Finds extractors in the dependency graph. Always call after dependency
     * graph is built!
     */
    private void findExtractors() {
        for (DependencyNode node : dGraph.values()) {
            // extractors have no dependencies
            if (node.getDependencies().isEmpty()) {
                extractors.add(node);
            }
        }
    }

    /**
     * Builds dependency graph.
     */
    private void buildDependencyGraph() {

        // iterate over all edges
        for (Edge e : graph.getEdges()) {

            // find the target node in the dependency graph
            DependencyNode tNode = addDependency(e.getTo());

            // find the source node in the dependency graph
            DependencyNode sNode = addDependency(e.getFrom());

            // add the dependency
            tNode.addDependency(sNode);
            sNode.addDependant(tNode);
        }
    }

    /**
     * Adds a dependency into dependency graph and returns it. If dependency
     * does not exist, it is created.
     *
     * @param node
     * @return the dependency
     */
    private DependencyNode addDependency(Node node) {

        // find the node in dependency graph
        DependencyNode dNode = dGraph.get(node);

        // if we have not encountered this node yet, create it
        if (dNode == null) {
            dNode = new DependencyNode(node);
            dGraph.put(node, dNode);
        }

        return dNode;
    }
}
