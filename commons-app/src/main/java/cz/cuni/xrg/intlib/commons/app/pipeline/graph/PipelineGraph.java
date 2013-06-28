package cz.cuni.xrg.intlib.commons.app.pipeline.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUType;
import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import java.awt.Point;
import javax.persistence.*;

/**
 * Oriented acyclic graph representation of pipeline. Each Node represents a DPURecord
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
    private Long id;

	/**
	 * Pipeline this graph belongs to
	 */
	@OneToOne
	@JoinColumn(name="pipeline_id", unique=true, nullable=false)
	private Pipeline pipeline;

	/**
	 * List of nodes which represent DPUs
	 */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="graph", fetch=FetchType.EAGER, orphanRemoval = true)
    private List<Node> nodes = new ArrayList<>();

    /**
     * Set of edges which represent data flow between DPUs.
     */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="graph", fetch= FetchType.EAGER, orphanRemoval = true)
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
	 * Adds a new node created from given DPUInstance.
	 * 
	 * @param dpuInstance
	 * @return node instance
	 */
	public Node addDpuInstance(DPUInstanceRecord dpuInstance) {
        Node node = new Node(dpuInstance);
		addNode(node);
		return node;
	} 

    /**
     * Removes DPURecord from graph.
     *
     * @param dpuId
     * @return
     */
    public Node removeDpu(int dpuId) {
        Node node = getNodeById(dpuId);
        if (node != null) {
            nodes.remove(node);
        }
        return node;
    }

    /**
     * Adds a single edge into pipeline graph, unless it exists already.
     *
     * @param from source DPURecord
     * @param to target DPURecord
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
    public Edge removeEdge(int edgeId) {
        Edge edge = getEdgeById(edgeId);
        if (edge != null) {
            boolean result = removeEdge(edge);
        }
        return edge;
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

	/**
	 * Validates new edge in graph.
	 * @param fromId
	 * @param toId
	 * @return null on success, error message otherwise
	 */
	public String validateNewEdge(int fromId, int toId) {
		Node from = getNodeById(fromId);
        Node to = getNodeById(toId);

		// Rules validation with corresponding error messages.
		if(to.getDpuInstance().getType() == DPUType.Extractor) {
			return "Extractor cannot have an input edge!";
		}
		if(from.getDpuInstance().getType() == DPUType.Loader) {
			return "Loader cannot have an output edge!";
		}
		if(from.equals(to)) {
			return "Loops are not allowed!";
		}
		
		//Same edge check
		if(sameEdgeExists(fromId, toId)) {
			return "Same edge already exists in graph!";
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
	
	public Position getBounds() {
		Position bounds = new Position(0, 0);
		for(Node node : nodes) {
			Position nodePosition = node.getPosition();
			if(nodePosition.getX() > bounds.getX()) {
				bounds.setX(nodePosition.getX());
			}
			if(nodePosition.getY() > bounds.getY()) {
				bounds.setY(nodePosition.getY());
			}
		}
		return bounds;
	}

	private boolean sameEdgeExists(int fromId, int toId) {
		for(Edge e : edges) {
			if(e.getFrom().getDpuInstance().getId() == fromId &&  e.getTo().getDpuInstance().getId() == toId) {
				return true;
			}
		}
		return false;
	}

}
