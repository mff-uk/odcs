package cz.cuni.xrg.intlib.commons.app.pipeline.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Oriented acyclic graph representation of pipeline.
 * Each Node represents a DPU instance, and each edge represents data flow.
 *
 * @author Jiri Tomes
 * @author Bogo
 *
 */
public class PipelineGraph {

    private List<Node> nodes = new ArrayList<Node>();

    private Set<Edge> edges = new HashSet<Edge>();

    public void addNode(Node node) {
        nodes.add(node);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> newNodes) {
        nodes = newNodes;
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public void setEdges(Set<Edge> edges) {
        this.edges = edges;
    }
}
