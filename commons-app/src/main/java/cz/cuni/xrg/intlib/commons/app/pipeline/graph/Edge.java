/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.xrg.intlib.commons.app.pipeline.graph;

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
    private int id;

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

    public Edge(Node from, Node to) {
        this.from = from;
        this.to = to;
    }

    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        return to;
    }

    @Override
	public boolean equals(Object other) {
		if(other.getClass() != Edge.class) {
			return false;
		}
		Edge o = (Edge)other;
		if(this.id == o.id) {
			return true;
		} else if(this.from.getId() == o.from.getId()
				&& this.to.getId() == o.to.getId()) {
			return true;
		} else {
			return this.from == o.from && this.to == o.to;
		}
	}

    public int getId() {
        return id;
    }

    void setId(int GetUniquePipelineConnectionId) {
        id = GetUniquePipelineConnectionId;
    }

}
