package cz.cuni.xrg.intlib.commons.app.pipeline.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.cuni.xrg.intlib.commons.app.dpu.DPU;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;

/**
 * Oriented acyclic graph representation of pipeline.
 * Each Node represents a DPU instance, and each edge represents data flow.
 *
 * @author Jiri Tomes
 * @author Bogo
 * @author Jan Vojt <jan@vojt.net>
 *
 */
public class PipelineGraph {

	/**
	 * List of nodes which represent DPUs
	 */
    private List<Node> nodes = new ArrayList<Node>();

    /**
     * Set of edges which represent data flow between DPUs.
     */
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

    public int addDpu(DPU dpu) {
		DPUInstance dpuInstance = new DPUInstance(dpu);
		Node node = new Node(dpuInstance);
        nodes.add(node);
		return node.getId();
	}

	public boolean removeDpu(int dpuId) {
		Node node = getNodeById(dpuId);
		if(node != null) {
			return nodes.remove(node);
		}
		return false;
	}

	public int addEdge(int fromId, int toId) {
		Node dpuFrom = getNodeById(fromId);
		Node dpuTo = getNodeById(toId);

		//TODO: Check if same connection doesn't exist already!
		//If it does - add to Set fails and returns false
		//TODO: 2. Find Id of equal existing connection

		Edge edge = new Edge(dpuFrom, dpuTo);
		boolean newElement = edges.add(edge);
		if(!newElement) {
			return 0;
		}
		return edge.getId();
	}

	public boolean removeEdge(int pcId) {
		Edge pc = getEdgeById(pcId);
		if(pc != null) {
			return edges.remove(pc);
		}
		return false;
	}

	private Edge getEdgeById(int id) {
		for(Edge el : edges) {
			if(el.getId() == id) {
				return el;
			}
		}
		return null;
	}

	private Node getNodeById(int id) {
		for(Node el : nodes) {
			if(el.getId() == id) {
				return el;
			}
		}
		return null;
	}
}
