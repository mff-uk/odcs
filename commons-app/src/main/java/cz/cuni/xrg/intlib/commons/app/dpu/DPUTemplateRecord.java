package cz.cuni.xrg.intlib.commons.app.dpu;

import javax.persistence.*;

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
	 * Parent DPURecord. If parent is set, this DPURecord is under its parent in DPU tree.
	 * 
	 */
	//@Transient
	@ManyToOne(optional = true)
	@JoinColumn(name="parent_id", nullable = true)
	private DPUTemplateRecord parent;
	
	
	
//	@Column(name="parent_id", nullable = true)
//	private Long parentId;
	
	
	/**
	 * Empty ctor for JPA.
	 */
	public DPUTemplateRecord() { }
	
	/**
	 * Create DPUTemplateRecord.
	 * @param name Template name.
	 * @param type {@link DPUType} of the template. 
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
		this.jarDescription = dpuInstance.getTemplate() == null
				? null : dpuInstance.getTemplate().getJarDescription();
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
	
	public DPUTemplateRecord getParent() {
		return parent;
	}
	
	public void setParent(DPUTemplateRecord parent) {
		this.parent = parent;
	}
}
