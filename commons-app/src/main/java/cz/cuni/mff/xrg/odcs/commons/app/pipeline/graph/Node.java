package cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.*;

/**
 * Node represents DPURecord on the pipeline and holds information about its
 * position on the Pipeline canvas.
 *
 * @author Jiri Tomes
 * @author Bogo
 * @author Jan Vojt
 */
@Entity
@Table(name = "ppl_node")
public class Node implements Serializable {

	/**
	 * Primary key of graph stored in db
	 */
	@Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_ppl_node")
	@SequenceGenerator(name = "seq_ppl_node", allocationSize = 1)
	@SuppressWarnings("unused")
	private Long id;

	@OneToOne(optional = false, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "instance_id", unique = true, nullable = false)
	private DPUInstanceRecord dpuInstance;

	@OneToOne(optional = false, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "position_id", unique = true)
	private Position position;

	/**
	 * Reference to owning graph
	 */
	@ManyToOne
	@JoinColumn(name = "graph_id")
	private PipelineGraph graph;

	/**
	 * Empty constructor for JPA.
	 */
	public Node() {
	}

	/**
	 * Copy constructor. Creates a deep copy of given <code>Node</code>. Primary
	 * key {@link #id} and {@link #graph} of newly created object are both
	 * <code>null</code>.
	 * 
	 * @param node to copy
	 */
	public Node(Node node) {
		position = node.getPosition() == null
				? null : new Position(node.getPosition());
		dpuInstance = node.getDpuInstance() == null
				? null : new DPUInstanceRecord(node.getDpuInstance());
	}

	/**
	 * Constructor with corresponding DPUInstance
	 *
	 * @param dpuInstance
	 */
	public Node(DPUInstanceRecord dpuInstance) {
		this.dpuInstance = dpuInstance;
	}

	public DPUInstanceRecord getDpuInstance() {
		return dpuInstance;
	}

	public Position getPosition() {
		return position;
	}

	public void setDpuInstance(DPUInstanceRecord dpuInstance) {
		this.dpuInstance = dpuInstance;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public PipelineGraph getGraph() {
		return graph;
	}

	public void setGraph(PipelineGraph graph) {
		this.graph = graph;
	}

	/**
	 * Hashcode is compatible with {@link #equals(java.lang.Object)}.
	 * 
	 * @return hashcode
	 */
	@Override
	public int hashCode() {
		if (this.id == null) {
			return super.hashCode();
		}
		int hash = 8;
		hash = 97 * hash + Objects.hashCode(this.id);
		return hash;
	}

	/**
	 * Returns true if two objects represent the same node. This holds if
	 * and only if <code>this.id == null ? this == obj : this.id == o.id</code>.
	 * 
	 * @param o
	 * @return true if both objects represent the same node
	 */
	@Override
	public boolean equals(Object o) {
		
		if (o == null) {
			return false;
		}
		if (getClass() != o.getClass()) {
			return false;
		}
		
		final Node other = (Node) o;
		if (this.id == null) {
			return super.equals(other);
		}
		
		return Objects.equals(this.id, other.id);
	}
}
