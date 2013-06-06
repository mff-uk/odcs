package cz.cuni.xrg.intlib.commons.app.dpu;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cz.cuni.xrg.intlib.commons.configuration.Config;
import javax.persistence.Transient;

/**
 * Represent the DPU instance pipeline placement in DB.
 * 
 * @author Petyr
 *
 */
@Table(name = "dpu_instance")
public class DPUInstanceRecord extends DPURecord {
	
	/**
	 * DPU's configuration.
	 * TODO serializing whole configuration into DB is a very bad idea...
	 */
	@Transient
	private Config conf;
	
	/**
	 * Template used for creating this instance. 
	 */
	@ManyToOne
	@JoinColumn(name="dpu_id")
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
