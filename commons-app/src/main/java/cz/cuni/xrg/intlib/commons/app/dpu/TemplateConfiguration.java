package cz.cuni.xrg.intlib.commons.app.dpu;

import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
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
	public Object getValue(String parameter) {
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

	public void setId(Long id) {
		this.id = id;
	}
	
}
