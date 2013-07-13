package cz.cuni.xrg.intlib.commons.app.pipeline.graph;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import java.io.Serializable;

import javax.persistence.*;

/**
 * Node represents DPURecord on the pipeline and holds information about its
 * position on the Pipeline canvas.
 *
 * @author Jiri Tomes
 * @author Bogo
 * @author Jan Vojt <jan@vojt.net>
 */
@Entity
@Table(name = "ppl_node")
public class Node implements Serializable {

	/**
	 * Primary key of graph stored in db
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@SuppressWarnings("unused")
	private Long id;

	@OneToOne(optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "instance_id", unique = true, nullable = false)
	private DPUInstanceRecord dpuInstance;

	@OneToOne(optional = false, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
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
}
