package cz.cuni.xrg.intlib.commons.app.pipeline.graph;

import java.util.Objects;
import javax.persistence.*;

/**
 * Edge represents oriented connection between nodes of the graph.
 *
 * @author Bogo
 */
@Entity
@Table(name="ppl_edge")
public class Edge {

    /**
     * Primary key of graph stored in db
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	@OneToOne(optional=false)
	@JoinColumn(name="node_from_id")
    private Node from;

	@OneToOne(optional=false)
	@JoinColumn(name="node_to_id")
    private Node to;
	
	/**
	 * Reference to owning graph
	 */
	@ManyToOne
	@JoinColumn(name="graph_id")
	private PipelineGraph graph;
	
	/**
	 * No-arg public constructor for JPA
	 */
	public Edge() {}

    /**
     * Constructor with specification of connecting nodes.
     *
     * @param from
     * @param to
     */
    public Edge(Node from, Node to) {
        this.from = from;
        this.to = to;
    }

    /**
     * Returns start node of edge
     *
     * @return
     */
    public Node getFrom() {
        return from;
    }

    /**
     * Returns end node of edge
     *
     * @return
     */
    public Node getTo() {
        return to;
    }

	public PipelineGraph getGraph() {
		return graph;
	}

	public void setGraph(PipelineGraph graph) {
		this.graph = graph;
	}
	
    @Override
    public boolean equals(Object other) {
        if (other.getClass() != Edge.class) {
            return false;
        }
        Edge o = (Edge) other;
        if (this.id != null && this.id.equals(o.getId())) {
            return true;
        } else if (this.from.hashCode() == o.from.hashCode()
                && this.to.hashCode() == o.to.hashCode()) {
            return true;
        } else {
            return this.from == o.from && this.to == o.to;
        }
    }

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + Objects.hashCode(this.from);
		hash = 97 * hash + Objects.hashCode(this.to);
		return hash;
	}

    public Long getId() {
        return id;
    }

    /**
     * Temporary solution of id generation.
     *
     * @param getUniquePipelineConnectionId
     */
//    void setId(int getUniquePipelineConnectionId) {
//        id = getUniquePipelineConnectionId;
//    }
}
