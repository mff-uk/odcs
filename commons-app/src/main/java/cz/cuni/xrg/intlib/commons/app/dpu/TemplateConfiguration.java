package cz.cuni.xrg.intlib.commons.app.dpu;

import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.persistence.*;

/**
 * Base configuration setting for concrete DPU type.
 *
 * @author Jiri Tomes
 * @author Petyr
 */
@Entity
@Table(name = "dpu_template_config")
public class TemplateConfiguration implements Configuration {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ElementCollection
    @MapKeyColumn(name="c_property")
    @Column(name="c_value")
    @CollectionTable(name="dpu_tconfig_pairs", joinColumns=@JoinColumn(name="conf_id"))
	private Map<String, Serializable> config = new HashMap<>();
	
	@OneToOne(optional = false)
	@JoinColumn(name = "dpu_id", unique = true)
	private DPU dpu;

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

	public DPU getDpu() {
		return dpu;
	}

	public void setDpu(DPU dpu) {
		this.dpu = dpu;
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

	/**
	 * Hash code is generated from primary key, if it is available. Otherwise
	 * it is generated from configuration hash map.
	 * 
	 * @return 
	 */
	@Override
	public int hashCode() {
		int hash = 7;
		if (this.id == null) {
			hash = 79 * hash + Objects.hashCode(this.config);
		} else {
			hash = 79 * hash + Objects.hashCode(this.id);
		}
		return hash;
	}

	/**
	 * Compares this configuration to other object. Two
	 * <code>TemplateConfiguration</code>s are equal, if their primary keys are
	 * non-null and equal, or if their primary keys are both null and the rest
	 * of attributes are equal.
	 *
	 * @param obj
	 * @return 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final TemplateConfiguration other = (TemplateConfiguration) obj;
		
		// try primary key comparison
		if (this.id != null && other.id != null) {
			// both have primary keys
			return Objects.equals(this.id, other.id);
		}
		if (this.id == null ^ other.id == null) {
			// only one has primary key
			return false;
		}
		
		// compare attributes
		return Objects.equals(this.config, other.config);
	}
	
}
