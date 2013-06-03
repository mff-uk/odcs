package cz.cuni.xrg.intlib.commons.app.dpu;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import cz.cuni.xrg.intlib.commons.configuration.Configuration;

/**
 * Represent the DPU instance pipeline placement in DB.
 * 
 * @author Petyr
 *
 */
public class DPUInstanceRecord extends DPURecord {
	
	/**
	 * DPU's configuration.
	 */
	@Column(name="config")
	private Configuration conf;
	
	/**
	 * Template used for creating this instance. 
	 */
	@ManyToOne
	@JoinColumn(name="")
	private DPUTemplateRecord template;

	public Configuration getConf() {
		return conf;
	}

	public void setConf(Configuration conf) {
		this.conf = conf;
	}

	public DPUTemplateRecord getTemplate() {
		return template;
	}

	public void setTemplate(DPUTemplateRecord template) {
		this.template = template;
	}
}
