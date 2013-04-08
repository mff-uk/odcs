/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.xrg.intlib.commons.app.pipeline.graph;

import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;

/**
 * Edge represents oriented connection between nodes of the graph.
 *
 * @author Bogo
 */
public class Edge {

    private int id;

    private Node from;

    private Node to;

    public Edge(Pipeline pipeline, Node from, Node to) {
        //this.id = pipeline.GetUniquePipelineConnectionId();
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
		Edge otherConnection = (Edge)other;
		if(this.id == otherConnection.id) {
			return true;
		} else if(this.from.getId() == otherConnection.from.getId() && this.to.getId() == otherConnection.to.getId()) {
			return true;
		}
		return false;
	}

    public int getId() {
        return id;
    }

}
