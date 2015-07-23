package cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import eu.unifiedviews.dataunit.DataUnit;

/**
 * Edge represents oriented connection between nodes of the graph.
 *
 * @author Bogo
 */
@Entity
@Table(name = "ppl_edge")
public class Edge implements DataObject {

    /**
     * Primary key of graph stored in db
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_ppl_edge")
    @SequenceGenerator(name = "seq_ppl_edge", allocationSize = 1)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "node_from_id")
    private Node from;

    @ManyToOne(optional = false)
    @JoinColumn(name = "node_to_id")
    private Node to;

    /**
     * Reference to owning graph
     */
    @ManyToOne
    @JoinColumn(name = "graph_id")
    private PipelineGraph graph;

    @Column(name = "data_unit_name", nullable = true)
    private String script;

    /**
     * Set script defining outputs to inputs mappings.
     *
     * @param script
     */
    public void setScript(String script) {
        this.script = script;
    }

    /**
     * Get script defining outputs to inputs mappings.
     *
     * @return script defining outputs to inputs mappings.
     */
    public String getScript() {
        return script;
    }

    /**
     * No-arg public constructor for JPA
     */
    public Edge() {
    }

    /**
     * Constructor with specification of connecting nodes.
     *
     * @param from
     * @param to
     */
    public Edge(Node from, Node to) {
        this(from, to, "");
    }

    /**
     * Constructor with specification of connecting nodes and {@link DataUnit} name.
     *
     * @param from
     * @param to
     * @param script
     */
    public Edge(Node from, Node to, String script) {
        this.from = from;
        this.to = to;
        this.script = script;
    }

    /**
     * @return start node of edge
     */
    public Node getFrom() {
        return from;
    }

    public void setFrom(Node from) {
        this.from = from;
    }

    /**
     * @return end node of edge
     */
    public Node getTo() {
        return to;
    }

    public void setTo(Node to) {
        this.to = to;
    }

    /**
     * Get graph this edge is in.
     *
     * @return graph
     */
    public PipelineGraph getGraph() {
        return graph;
    }

    /**
     * Set graph this edge is in.
     *
     * @param graph
     */
    public void setGraph(PipelineGraph graph) {
        this.graph = graph;
    }

    @Override
    public int hashCode() {
        if (this.id == null) {
            return super.hashCode();
        }
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }

        final Edge other = (Edge) o;
        if (this.id == null) {
            return super.equals(other);
        }

        return Objects.equals(this.id, other.id);
    }

    @Override
    public Long getId() {
        return id;
    }

}
