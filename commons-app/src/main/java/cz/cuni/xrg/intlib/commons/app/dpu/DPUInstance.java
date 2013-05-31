package cz.cuni.xrg.intlib.commons.app.dpu;

import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import javax.persistence.*;

/**
 * For representing concrete DPU component in the pipeline.
 *
 * @author Jiri Tomes
 */
@Entity
@Table(name = "dpu_instance")
public class DPUInstance {

	/**
	 * Primary key of graph stored in db
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * Data Processing Unit
	 */
	@ManyToOne(optional = false)
	@JoinColumn(name = "dpu_id", nullable = false)
	private DPU dpu;
	
	/**
	 * Configuration setting for this component.
	 */
	@OneToOne(mappedBy = "dpuInstance", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private InstanceConfiguration instanceConfig;

	private String name;

	private String description;

	/**
	 * No-arg constructor for JPA
	 */
	public DPUInstance() {
	}

	public DPUInstance(DPU dpu) {
		this.dpu = dpu;
		this.name = dpu.getName();
		this.description = dpu.getDescription();
	}
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the dpu
	 */
	public DPU getDpu() {
		return dpu;
	}

	/**
	 * @param dpu the dpu to set
	 */
	public void setDpu(DPU dpu) {
		this.dpu = dpu;
	}
	
	/**
	 * @return the instanceConfig
	 */
	public Configuration getInstanceConfig() {
		return instanceConfig;
	}

	/**
	 * @param instanceConfig the instanceConfig to set
	 */
	public void setInstanceConfig(InstanceConfiguration instanceConfig) {
		instanceConfig.setDpuInstance(this);
		this.instanceConfig = instanceConfig;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
