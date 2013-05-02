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
 * Oriented acyclic graph representation of pipeline.
 * Each Node represents a DPU instance, and each edge represents data flow.
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
	@OneToMany(cascade=CascadeType.ALL, mappedBy="graph")
    private List<Node> nodes = new ArrayList<>();

    /**
     * Set of edges which represent data flow between DPUs.
     */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="graph")
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
    
    public int addDpu(DPU dpu) {
		DPUInstance dpuInstance = new DPUInstance(dpu);
		Node node = new Node(dpuInstance);
        node.setId(GetUniqueDpuInstanceId());
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
	
	/**
	 * Adds a single edge into pipeline graph, unless it exists already.
	 * @param from source DPU
	 * @param to target DPU
	 * @return newly created edge or null
	 * TODO find and return edge even if it was present before
	 */
	public Edge addEdge(Node from, Node to) {
		Edge e = new Edge(from, to);
		// adds unless it is present already
		boolean added = edges.add(e);
		return added ? e : null;
	}

	public int addEdge(int fromId, int toId) {
		Node dpuFrom = getNodeById(fromId);
		Node dpuTo = getNodeById(toId);

		//TODO: Check if same connection doesn't exist already!
		//If it does - add to Set fails and returns false
		//TODO: 2. Find Id of equal existing connection

		Edge edge = new Edge(dpuFrom, dpuTo);
        edge.setId(GetUniquePipelineConnectionId());
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

	public Node getNodeById(int id) {
		for(Node el : nodes) {
			if(el.getId() == id) {
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

    /** TODO remove Hack for IDs for Nodes and Edges - replace with IDs from db ASAP */
	@Transient
    private int dpuCounter = 0;

	@Transient
	private int connectionCounter = 0;

	@Transient
	private int CONNECTION_SEED = 1000;

	public int getCONNECTION_SEED() {
		return CONNECTION_SEED;
	}

    public int GetUniqueDpuInstanceId() {
		return ++dpuCounter;
	}

	public int GetUniquePipelineConnectionId() {
		return ++connectionCounter + CONNECTION_SEED;
	}

    /** End of hack */
}
