package cz.cuni.intlib.xrg.commons.app.data.pipeline;

import java.util.ArrayList;
import java.util.List;

/**
 * Graph representation as set of nodes.
 *
 * @author Jiri Tomes
 *
 */
public class Graph {

    private List<Node> nodes = new ArrayList<Node>();

    public void addNode(Node node) {
        nodes.add(node);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> newNodes) {
        nodes = newNodes;
    }
}
