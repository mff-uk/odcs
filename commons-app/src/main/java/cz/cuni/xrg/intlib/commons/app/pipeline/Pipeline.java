package cz.cuni.xrg.intlib.commons.app.pipeline;

import javax.persistence.*;

import cz.cuni.xrg.intlib.commons.app.pipeline.graph.PipelineGraph;
import cz.cuni.xrg.intlib.commons.app.user.Resource;
import java.io.Serializable;

/**
 * Represents a fixed workflow composed of one or several {@link Extract}s,
 * {@link Transform}s and {@link Load}s organized in acyclic graph. <br/>
 * Processing will always take place in the following order: <ol> <li>Execute
 * all {@link Extract}s</li> <ul> <li>If an Extractor throws an error publish an
 * {@link ExtractFailedEvent} - otherwise publish an
 * {@link ExtractCompletedEvent}</li> <li>If an Extractor requests cancellation
 * of the pipeline through {@link ProcessingContext#cancelPipeline} publish a
 * {@link PipelineAbortedEvent} and exit</li> </ul> <li>Execute all
 * {@link Transform}s in the order of their dependences given by graph</li>
 * <ul> <li>If a Transformer throws an error publish an
 * {@link TransformFailedEvent} - otherwise publish an
 * {@link TransformCompletedEvent}</li>
 * <li>If a Transformer requests cancellation of the pipeline through
 * {@link ProcessingContext#cancelPipeline} publish a
 * {@link PipelineAbortedEvent} and exit</li> </ul> <li>Execute all
 * {@link Load}s</li> <ul> <li>If a Loader throws an error publish an
 * {@link LoadFailedEvent} - otherwise publish an {@link LoadCompletedEvent}
 * </li>
 * <li>If a Loader requests cancellation of the pipeline through
 * {@link ProcessingContext#cancelPipeline} publish a
 * {@link PipelineAbortedEvent} and exit</li> </ul> <li>Publish a
 * {@link PipelineCompletedEvent}
 * </ol> <br/> A Spring {@link ApplicationEventPublisher} is required for
 * propagation of important events occurring throughout the pipeline.
 *
 * @see Extract
 * @see Transform
 * @see Load
 * @author Jiri Tomes
 * @author Jan Vojt
 * @author Bogo
 */
@Entity
@Table(name = "ppl_model")
public class Pipeline implements Resource, Serializable {

	/**
	 * Unique ID for each pipeline
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * Human-readable pipeline name
	 */
	@Column
	private String name;

	/**
	 * Human-readable pipeline description
	 */
	@Column
	private String description;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "pipeline", fetch = FetchType.EAGER)
	private PipelineGraph graph;

	//@ManyToOne(cascade=CascadeType.ALL, mappedBy="pipeline", fetch= FetchType.EAGER)
	//private Plan plan;
	/**
	 * Default constructor for JPA
	 */
	public Pipeline() {
		graph = new PipelineGraph();
	}

	/**
	 * Copy constructor. Creates a deep copy of given pipeline.
	 * 
	 * @param pipeline to copy
	 */
	public Pipeline(Pipeline pipeline) {
		name = pipeline.getName();
		description = pipeline.getDescription();
		graph = new PipelineGraph(pipeline.graph);
	}

	/**
	 * Constructor with given pipeline name and description.
	 *
	 * @param name
	 * @param description
	 */
	public Pipeline(String name, String description) {
		this();
		this.name = name;
		this.description = description;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String newName) {
		name = newName;
	}

	public String getDescription() {

		return description;
	}

	public void setDescription(String newDescription) {
		description = newDescription;
	}

	public PipelineGraph getGraph() {
		return graph;
	}

	public void setGraph(PipelineGraph graph) {
		this.graph = graph;
		graph.setPipeline(this);
	}

	public Long getId() {
		return id;
	}

	@Override
	public String getResourceId() {
		return Pipeline.class.toString();
	}
}
