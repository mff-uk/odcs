package cz.cuni.xrg.intlib.commons.app.pipeline.graph;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstance;
import javax.persistence.*;

/**
 * Node represents DPU on the pipeline and holds information
 * about its position on the Pipeline canvas.
 *
 * @author Jiri Tomes
 * @author Bogo
 * @author Jan Vojt <jan@vojt.net>
 */
@Entity
@Table(name="ppl_node")
public class Node {

    /**
     * Primary key of graph stored in db
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

	@OneToOne(optional=false, cascade=CascadeType.ALL)
	@JoinColumn(name="instance_id", unique=true, nullable=false)
    private DPUInstance dpuInstance;

	@OneToOne(optional=false, mappedBy="node", cascade=CascadeType.ALL)
    private Position position;
	
	/**
	 * Reference to owning graph
	 */
	@ManyToOne
	@JoinColumn(name="graph_id")
	private PipelineGraph graph;
    
    /**
     * Empty constructor for JPA.
     */
    public Node() {}

    public Node(DPUInstance dpuInstance) {
        this.dpuInstance = dpuInstance;
    }

    public DPUInstance getDpuInstance() {
        return dpuInstance;
    }

    public Position getPosition() {
        return position;
    }

    public void setDpuInstance(DPUInstance dpuInstance) {
        this.dpuInstance = dpuInstance;
    }

    public void setPosition(Position newPosition) {
        this.position = newPosition;
    }

    public int getId() {
        return id;
    }

    void setId(int GetUniqueDpuInstanceId) {
        id = GetUniqueDpuInstanceId;
    }

}
