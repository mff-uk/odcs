package cz.cuni.xrg.intlib.commons.app.dpu;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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

	/**
	 * Empty ctor because of JPA.
	 */
	public DPUInstanceRecord() { }
	
	/**
	 * Create new DPUInstanceRecord with given name and type.
	 * @param name
	 * @param type
	 */
	public DPUInstanceRecord(String name, DPUType type) {
		super(name, type);
	}
	
	/**
	 * Create instance based on given template.
	 * @param template
	 */
	public DPUInstanceRecord(DPUTemplateRecord template) {
		// construct DPURecord
		super(template);
		// and set out variables
		this.template = template;
	}
	
	public DPUTemplateRecord getTemplate() {
		return template;
	}

	public void setTemplate(DPUTemplateRecord template) {
		this.template = template;
	}
}
