package cz.cuni.xrg.intlib.commons.app.dpu;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

import cz.cuni.xrg.intlib.commons.configuration.Config;

/**
 * Represent the DPU instance pipeline placement in DB.
 * 
 * @author Petyr
 *
 */
@MappedSuperclass
@Table(name = "dpu_instance")
public class DPUInstanceRecord extends DPURecord {
	
	/**
	 * DPU's configuration.
	 */
	@Column(name="config")
	private Config conf;
	
	/**
	 * Template used for creating this instance. 
	 */
	@ManyToOne
	@JoinColumn(name="")
	private DPUTemplateRecord template;

	public Config getConf() {
		return conf;
	}

	public void setConf(Config conf) {
		this.conf = conf;
	}

	public DPUTemplateRecord getTemplate() {
		return template;
	}

	public void setTemplate(DPUTemplateRecord template) {
		this.template = template;
	}
}
