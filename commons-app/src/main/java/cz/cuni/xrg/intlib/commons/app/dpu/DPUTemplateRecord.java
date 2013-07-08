package cz.cuni.xrg.intlib.commons.app.dpu;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * 
 * 
 * @author Petyr
 * 
 */
@Entity
@Table(name = "dpu_template")
public class DPUTemplateRecord extends DPURecord {

	/**
	 * Visibility in DPUTree.
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(name="visibility")
	private VisibilityType visibility;
	
	/**
	 * Description obtained from jar file manifest.
	 */
	@Column(name="jar_description")
	private String jarDescription;
	
	/**
	 * Empty ctor for JPA.
	 */
	public DPUTemplateRecord() { }
	
	/**
	 * Create DPUTemplateRecord.
	 * @param name Template name.
	 * @param type {@linkDPUType} of the template. 
	 */
	public DPUTemplateRecord(String name, DPUType type) {
		super(name, type);
	}
	
	/**
	 * Create template from given instance.
	 * @param dpuInstance
	 */
	public DPUTemplateRecord(DPUInstanceRecord dpuInstance) {
		super(dpuInstance);
		this.visibility = VisibilityType.PRIVATE;
		// copy jarDescription from template of previous one ..
		DPUTemplateRecord dpuInstanceTemplate =
				dpuInstance.getTemplate();
		if (dpuInstanceTemplate == null) {
			// TODO Petyr, Honza: This should not happen .. use some default jarDescription 
		} else {
			this.jarDescription = dpuInstanceTemplate.getJarDescription();
		}		 
	}
	
	public VisibilityType getVisibility() {
		return visibility;
	}

	public void setVisibility(VisibilityType visibility) {
		this.visibility = visibility;
	}

	public String getJarDescription() {
		return jarDescription;
	}

	public void setJarDescription(String jarDescription) {
		this.jarDescription = jarDescription;
	}
}
