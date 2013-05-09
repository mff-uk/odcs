package cz.cuni.xrg.intlib.commons.app.pipeline.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.cuni.xrg.intlib.commons.app.dpu.DPU;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import javax.persistence.*;

/**
 * Oriented acyclic graph representation of pipeline. Each Node represents a DPU
 * instance, and each edge represents data flow.
 *
 * @author Jiri Tomes
 * @author Bogo
 * @author Jan Vojt <jan@vojt.net>
 *
 */
@Entity
@Table(name = "ppl_graph")
public class PipelineGraph {

    /**
     * Primary key of graph stored in db
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	@SuppressWarnings("unused")
    private int id;
	
	/**
	 * Pipeline this graph belongs to
	 */
	@OneToOne
	@JoinColumn(name="pipeline_id", unique=true, nullable=false)
	private Pipeline pipeline;

	/**
	 * List of nodes which represent DPUs
	 */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="graph", fetch=FetchType.EAGER)
    private List<Node> nodes = new ArrayList<>();

    /**
     * Set of edges which represent data flow between DPUs.
     */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="graph", fetch= FetchType.EAGER)
    private Set<Edge> edges = new HashSet<>();

    public void addNode(Node node) {
        nodes.add(node);
		node.setGraph(this);
    }
	
	/**
	 * Removes node from graph.
	 * 
	 * @param node
	 * @return <tt>true</tt> if graph contained given node
	 */
	public boolean removeNode(Node node) {
		node.setGraph(null);
		return nodes.remove(node);
	}

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> newNodes) {
        nodes = newNodes;
		for (Node node : nodes) {
			node.setGraph(this);
		}
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public void setEdges(Set<Edge> edges) {
        this.edges = edges;
		for (Edge edge : edges) {
			edge.setGraph(this);
		}
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
        addNode(node);
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
        Edge edge = new Edge(from, to);
        // adds unless it is present already
        boolean added = edges.add(edge);
		edge.setGraph(this);

        //TODO Find existing edge for this connection
        return added ? edge : null;
    }
	
	public boolean removeEdge(Edge edge) {
		edge.setGraph(null);
		return edges.remove(edge);
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

        Edge edge = addEdge(dpuFrom, dpuTo);
        return edge == null ? 0 : edge.hashCode();
    }

    /**
     * Removes edge from graph.
     *
     * @param edgeId
     * @return
     */
    public boolean removeEdge(int edgeId) {
        Edge edge = getEdgeById(edgeId);
        if (edge != null) {
            return removeEdge(edge);
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

	public Pipeline getPipeline() {
		return pipeline;
	}

	public void setPipeline(Pipeline pipeline) {
		this.pipeline = pipeline;
	}
	
}
