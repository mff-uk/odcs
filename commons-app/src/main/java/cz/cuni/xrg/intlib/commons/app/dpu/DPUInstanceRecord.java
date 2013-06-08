package cz.cuni.xrg.intlib.commons.app.dpu;

import javax.persistence.Entity;
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
@Entity
@Table(name = "dpu_instance")
public class DPUInstanceRecord extends DPURecord {
		
	/**
	 * Template used for creating this instance. 
	 */
	@ManyToOne(optional = false)
	@JoinColumn(name="dpu_id")
	private DPUTemplateRecord template;

	public DPUTemplateRecord getTemplate() {
		return template;
	}

	public void setTemplate(DPUTemplateRecord template) {
		this.template = template;
	}
}
