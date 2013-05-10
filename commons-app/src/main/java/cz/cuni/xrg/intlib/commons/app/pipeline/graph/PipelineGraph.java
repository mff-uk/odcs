package cz.cuni.xrg.intlib.commons.app.pipeline.graph;

import cz.cuni.xrg.intlib.commons.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.cuni.xrg.intlib.commons.app.dpu.DPU;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;

/**
 * Oriented acyclic graph representation of pipeline. Each Node represents a DPU
 * instance, and each edge represents data flow.
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
    private List<Node> nodes = new ArrayList<>();
    /**
     * Set of edges which represent data flow between DPUs.
     */
    private Set<Edge> edges = new HashSet<>();

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

    /**
     * Adds new DPU to graph.
     *
     * @param dpu
     * @return
     */
    public int addDpu(DPU dpu) {
        DPUInstance dpuInstance = new DPUInstance(dpu);
        Node node = new Node(dpuInstance);
        nodes.add(node);
        return node.hashCode();
    }

    /**
     * Removes DPU from graph.
     *
     * @param dpuId
     * @return
     */
    public boolean removeDpu(int dpuId) {
        Node node = getNodeById(dpuId);
        if (node != null) {
            return nodes.remove(node);
        }
        return false;
    }

    /**
     * Adds a single edge into pipeline graph, unless it exists already.
     *
     * @param from source DPU
     * @param to target DPU
     * @return newly created edge or null TODO find and return edge even if it
     * was present before
     */
    public Edge addEdge(Node from, Node to) {
        Edge e = new Edge(from, to);
        // adds unless it is present already
        boolean added = edges.add(e);
        return added ? e : null;
    }

    /**
     * Duplicate methdod from adding edge to graph. Probably only one shall
     * remain.
     *
     * @param fromId
     * @param toId
     * @return
     */
    public int addEdge(int fromId, int toId) {
        Node dpuFrom = getNodeById(fromId);
        Node dpuTo = getNodeById(toId);

        //TODO: Check if same connection doesn't exist already!
        //If it does - add to Set fails and returns false
        //TODO: 2. Find Id of equal existing connection

        Edge edge = new Edge(dpuFrom, dpuTo);
        boolean newElement = edges.add(edge);
        if (!newElement) {
            return 0;
        }
        return edge.hashCode();
    }

    /**
     * Removes edge from graph.
     *
     * @param pcId
     * @return
     */
    public boolean removeEdge(int pcId) {
        Edge pc = getEdgeById(pcId);
        if (pc != null) {
            return edges.remove(pc);
        }
        return false;
    }

    /**
     * Gets edge with given id.
     *
     * @param id
     * @return
     */
    private Edge getEdgeById(int id) {
        for (Edge el : edges) {
            if (el.hashCode() == id) {
                return el;
            }
        }
        return null;
    }

    /**
     * Gets node with given id.
     *
     * @param id
     * @return
     */
    public Node getNodeById(int id) {
        for (Node el : nodes) {
            if (el.hashCode() == id) {
                return el;
            }
        }
        return null;
    }

//    /**
//     * Gets DPUInstance of Node with given ID
//     *
//     * @param id
//     * @return DPUIntance of Node with given id
//     */
//    public DPUInstance getDPUInstanceById(int id) {
//        Node node = getNodeById(id);
//        return (node == null) ? null : node.getDpuInstance();
//    }
    /**
     * Updates Node position in graph.
     *
     * @param dpuId
     * @param newX
     * @param newY
     */
    public void moveNode(int dpuId, int newX, int newY) {
        Node node = getNodeById(dpuId);
        node.setPosition(new Position(newX, newY));
    }
//    /**
//     * Hack for IDs for Nodes and Edges - replace with IDs from db ASAP
//     */
//    private int dpuCounter = 0;
//    private int connectionCounter = 0;
//    private int CONNECTION_SEED = 1000;
//
//    public int getCONNECTION_SEED() {
//        return CONNECTION_SEED;
//    }
//
//    public int GetUniqueDpuInstanceId() {
//        return ++dpuCounter;
//    }
//
//    public int GetUniquePipelineConnectionId() {
//        return ++connectionCounter + CONNECTION_SEED;
//    }
	    /**
     * End of hack
     */

	/**
	 * Validates new edge in graph.
	 * @param fromId
	 * @param toId
	 * @return null on success, error message otherwise
	 */
	public String validateNewEdge(int fromId, int toId) {
		Node from = getNodeById(fromId);
        Node to = getNodeById(toId);

		//Rules validation with corresponding error messages.
		if(to.getDpuInstance().getDpu().getType() == Type.EXTRACTOR) {
			return "Extractor cannot have an input edge!";
		}
		if(from.getDpuInstance().getDpu().getType() == Type.LOADER) {
			return "Loader cannot have an output edge!";
		}
		if(from.equals(to)) {
			return "Loops are not allowed!";
		}

		//Same edge, other direction check
		for(Edge e : edges) {
			if(e.getFrom().equals(to) && e.getTo().equals(from)) {
				return "Two-way edges are not allowed!";
			}
		}

		//Cycle check
		Edge newEdge = new Edge(from, to);
		if(newEdgeCreateCycle(newEdge)) {
			return "Cycles are not allowed!";
		}

		return null;
	}

	private boolean newEdgeCreateCycle(Edge newEdge) {
		List<Node> v = new ArrayList<>(nodes);
        List<Edge> e = new ArrayList<>(edges);
		e.add(newEdge);

		while(!v.isEmpty()) {
			Node candidate = null;
			//Find vertex with no out edges
			for(Node vertex : v) {
				boolean hasOutEdge = false;
				for(Edge edge : e) {
					if(edge.getFrom().equals(vertex)) {
						hasOutEdge = true;
						break;
					}
				}
				if(!hasOutEdge) {
					candidate = vertex;
					break;
				}
			}

			if(candidate == null) {
				return true;
			}

			//Remove candidate and edges with candidate
			v.remove(candidate);
			for (int i = 0; i < e.size();) {
				Edge edge = e.get(i);
				if(edge.getTo().equals(candidate)) {
					e.remove(i);
				} else {
					++i;
				}

			}
		}
		return false;
	}

}
