package cz.cuni.xrg.intlib.commons.app.dpu;

import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.*;

/**
 * Configuration setting for DPU component on the canvas.
 *
 * @author Jiri Tomes
 * @author Jan Vojt
 */
@Entity
@Table(name = "dpu_instance_config")
public class InstanceConfiguration implements Configuration {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ElementCollection
    @MapKeyColumn(name="c_property")
    @Column(name="c_value")
    @CollectionTable(name="dpu_iconfig_pairs", joinColumns=@JoinColumn(name="conf_id"))
	private Map<String, Serializable> config = new HashMap<>();

	@OneToOne(optional = false)
	@JoinColumn(name = "instance_id", unique = true)
	private DPUInstance dpuInstance;

	/**
	 * Return value for given id.
	 *
	 * @param parameter id
	 * @return null if there is not object stored under given id
	 */
	@Override
	public Serializable getValue(String parameter) {
		return this.config.get(parameter);
	}

	/**
	 * Store given object under given id. If object already exist then it's
	 * rewritten.
	 *
	 * @param parameter object id
	 * @param value object to store
	 */
	@Override
	public void setValue(String parameter, Serializable value) {
		this.config.put(parameter, value);
	}

	public DPUInstance getDpuInstance() {
		return dpuInstance;
	}

	public void setDpuInstance(DPUInstance dpuInstance) {
		this.dpuInstance = dpuInstance;
	}
	
	public Long getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Serializable> getValues() {
		return config;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValues(Map<String, Serializable> values) {
		this.config = values;
	}
}
