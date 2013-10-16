package cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUType;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.*;

/**
 * Oriented acyclic graph representation of pipeline. Each Node represents a DPURecord
 * instance, and each edge represents data flow.
 *
 * @author Jiri Tomes
 * @author Bogo
 * @author Jan Vojt
 *
 */
@Entity
@Table(name = "ppl_graph")
public class PipelineGraph implements Serializable {

    /**
     * Primary key of graph stored in db
     */
	@Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_ppl_graph")
	@SequenceGenerator(name = "seq_ppl_graph", allocationSize = 1)
	@SuppressWarnings("unused")
    private Long id;

	/**
	 * Pipeline this graph belongs to
	 */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="pipeline_id", unique=true, nullable=false)
	private Pipeline pipeline;

	/**
	 * Set of nodes which represent DPUs
	 * 
	 * <p>
	 * Nodes are eagerly loaded, because they are needed every time graph is
	 * loaded.
	 */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="graph", fetch=FetchType.EAGER, orphanRemoval = true)
    private Set<Node> nodes = new HashSet<>();

    /**
     * Set of edges which represent data flow between DPUs.
	 * 
	 * <p>
	 * Edges are eagerly loaded, because they are needed every time graph is
	 * loaded.
     */
	@OneToMany(cascade=CascadeType.ALL, mappedBy="graph", fetch= FetchType.EAGER, orphanRemoval = true)
    private Set<Edge> edges = new HashSet<>();

	/**
	 * Empty constructor for JPA.
	 */
	public PipelineGraph() {}

	/**
	 * Copy constructor. Note that newly created graph is NOT associated with
	 * any pipeline, as graph should always be unique.
	 * 
	 * @param graph 
	 */
	@SuppressWarnings("LeakingThisInConstructor")
	public PipelineGraph(PipelineGraph graph) {
		
		// create mapping from old nodes to new nodes to be able to correctly
		// reference source and target nodes for new edges
		Map<Node,Node> nMap = new HashMap<>(graph.nodes.size());
		for (Node oldNode : graph.getNodes()) {
			Node newNode = new Node(oldNode);
			newNode.setGraph(this);
			nMap.put(oldNode, newNode);
		}
		
		// create edges
		edges = new HashSet(graph.edges.size());
		for (Edge oldEdge : graph.getEdges()) {
			Edge newEdge = new Edge(
				nMap.get(oldEdge.getFrom()),
				nMap.get(oldEdge.getTo()),
				oldEdge.getScript()
			);
			newEdge.setGraph(this);
			edges.add(newEdge);
		}
		
		// assign nodes
		nodes = new HashSet(nMap.values());
	}
	
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

    public Set<Node> getNodes() {
        return nodes;
    }

    public void setNodes(Set<Node> newNodes) {
        nodes = newNodes;
		// update on owning side
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
			nodes.add(edge.getFrom());
			nodes.add(edge.getTo());
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
		
		// make sure nodes are members of this graph
		nodes.add(from);
		nodes.add(to);
		
		// update on owning side
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
    public Edge getEdgeById(int id) {
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

	/**
	 * Updates Node position in graph.
	 *
	 * @param dpuId
	 * @param newX
	 * @param newY
	 */
	public void moveNode(int dpuId, int newX, int newY) {
		Node node = getNodeById(dpuId);
                if(node == null) {
                    throw new IllegalArgumentException("Node with supplied id was not found!");
                }
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
	 * TODO refactor and use <code>Exception</code>s instead of <code>String</code>s.
	 * 
	 * @param fromId
	 * @param toId
	 * @return null on success, error message otherwise
	 */
	public String validateNewEdge(int fromId, int toId) {
		Node from = getNodeById(fromId);
        Node to = getNodeById(toId);

		// Rules validation with corresponding error messages.
		if(from.getDpuInstance().getType() == DPUType.LOADER) {
			return "Loader cannot have an output edge!";
		}
		if(from.equals(to)) {
			return "Loops are not allowed!";
		}
		
		//Same edge check
		if(sameEdgeExists(from, to)) {
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
	
	private boolean sameEdgeExists(Node from, Node to) {
		for(Edge e : edges) {
			if(e.getFrom().equals(from) &&  e.getTo().equals(to)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets maximum coordinates of DPUs in graph.
	 * @return 
	 */
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
        
        public PipelineGraph cloneGraph() {
            PipelineGraph clone = new PipelineGraph(this);
            clone.id = this.id;
            return clone;
        }
}
