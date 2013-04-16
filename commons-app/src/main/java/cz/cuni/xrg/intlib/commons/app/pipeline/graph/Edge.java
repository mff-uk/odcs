/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.xrg.intlib.commons.app.pipeline.graph;

/**
 * Edge represents oriented connection between nodes of the graph.
 *
 * @author Bogo
 */
public class Edge {

    private int id;

    private Node from;

    private Node to;

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
