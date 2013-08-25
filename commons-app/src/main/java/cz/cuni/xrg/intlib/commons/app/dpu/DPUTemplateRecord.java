package cz.cuni.xrg.intlib.commons.app.dpu;

import cz.cuni.xrg.intlib.commons.app.auth.SharedEntity;
import cz.cuni.xrg.intlib.commons.app.auth.VisibilityType;
import cz.cuni.xrg.intlib.commons.app.user.OwnedEntity;
import cz.cuni.xrg.intlib.commons.app.user.User;
import javax.persistence.*;

/**
 * Representation of template for creating {@link DPUInstanceRecord}s.
 * The purpose of templating DPUs is to unburden user from manually edit and
 * configure all DPU properties when creating pipelines. Template's properties
 * are propagated to {@link DPUInstanceRecord} everytime it is created as
 * an instance of given {@link DPUTemplateRecord}. Reference to template is
 * preserved in each DPU instance, so that updates of configuration can be
 * propagated to template's children.
 * 
 * @author Petyr
 * 
 */
@Entity
@Table(name = "dpu_template")
public class DPUTemplateRecord extends DPURecord implements OwnedEntity, SharedEntity {

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
	 * Parent {@link DPUTemplateRecord}. If parent is set, this DPURecord is
	 * rendered under its parent in DPU tree.
	 */
	@ManyToOne(optional = true)
	@JoinColumn(name="parent_id", nullable = true)
	private DPUTemplateRecord parent;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User owner;
		
	/**
	 * Empty ctor for JPA.
	 */
	public DPUTemplateRecord() { }
	
	/**
	 * Creates new {@link DPUTemplateRecord}.
	 * 
	 * @param name Template name.
	 * @param type {@link DPUType} of the template. 
	 */
	public DPUTemplateRecord(String name, DPUType type) {
		super(name, type);
	}

	/**
	 * Copy constructor. New instance always has private visibility, no matter
	 * what setting was in original DPU.
	 * 
	 * @param dpu 
	 */
	public DPUTemplateRecord(DPUTemplateRecord dpu) {
		super(dpu);
		visibility = VisibilityType.PRIVATE;
		jarDescription = dpu.jarDescription;
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

	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	@Override
	public User getOwner() {
		return owner;
	}
	
	@Override
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
