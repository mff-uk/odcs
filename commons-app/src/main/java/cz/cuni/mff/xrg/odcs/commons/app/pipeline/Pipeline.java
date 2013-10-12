package cz.cuni.mff.xrg.odcs.commons.app.pipeline;

import javax.persistence.*;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.PipelineGraph;
import cz.cuni.mff.xrg.odcs.commons.app.user.OwnedEntity;
import cz.cuni.mff.xrg.odcs.commons.app.user.Resource;
import cz.cuni.mff.xrg.odcs.commons.app.user.User;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
public class Pipeline implements OwnedEntity, Resource, Serializable {

	/**
	 * Unique ID for each pipeline
	 */
	@Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_ppl_model")
	@SequenceGenerator(name = "seq_ppl_model", allocationSize = 1)
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

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "pipeline")
	private PipelineGraph graph;
	
	/**
	 * User who created and owns this pipeline.
	 */
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User owner;

	/**
	 * List pipelines that must not run in order to run this pipeline.
	 */
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "ppl_ppl_conflicts",
			joinColumns = @JoinColumn(name = "pipeline_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "pipeline_conflict_id", referencedColumnName = "id"))
	private Set<Pipeline> conflicts = new HashSet<>();
	
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
	@SuppressWarnings("LeakingThisInConstructor")
	public Pipeline(Pipeline pipeline) {
		name = pipeline.getName();
		description = pipeline.getDescription();
		graph = new PipelineGraph(pipeline.graph);
		graph.setPipeline(this);
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
	
	public void setUser(User owner) {
		this.owner = owner;
	}

	@Override
	public User getOwner() {
		return owner;
	}

	public Set<Pipeline> getConflicts() {
		return conflicts;
	}	
	
	@Override
	public String getResourceId() {
		return Pipeline.class.toString();
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
		int hash = 7;
		hash = 97 * hash + Objects.hashCode(this.id);
		return hash;
	}

	/**
	 * Returns true if two objects represent the same pipeline. This holds if
	 * and only if <code>this.id == null ? this == obj : this.id == o.id</code>.
	 * 
	 * @param o
	 * @return true if both objects represent the same pipeline
	 */
	@Override
	public boolean equals(Object o) {
		
		if (o == null) {
			return false;
		}
		if (getClass() != o.getClass()) {
			return false;
		}
		
		final Pipeline other = (Pipeline) o;
		if (this.id == null) {
			return super.equals(other);
		}
		
		return Objects.equals(this.id, other.id);
	}
	
}
