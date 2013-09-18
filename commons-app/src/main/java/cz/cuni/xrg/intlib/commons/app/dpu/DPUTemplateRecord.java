package cz.cuni.xrg.intlib.commons.app.dpu;

import java.io.File;

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
     * DPURecord type, determined by associated jar file.
     */
	@Enumerated(EnumType.ORDINAL)
    private DPUType type;	
	
    /**
     * Name of directory where {@link #jarName} is located.
	 * 
     * @see AppConfig
     */
	@Column(name="jar_directory")
    private String jarDirectory;	
	
    /**
     * DPU's jar file name.
	 * 
     * @see AppConfig
     */
	@Column(name="jar_name")
    private String jarName;		
	
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
		super(name);
		this.type = type;
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
		type = dpu.type;
		jarDescription = dpu.jarDirectory;
		jarName = dpu.jarName;
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

	@Override
	public DPUType getType() {
		return this.type;
	}

	@Override
	public String getJarPath() {
		if (parent == null) {
			// to level DPU
			if (jarDirectory.isEmpty()) {
				return jarName;
			} else {				
				return jarDirectory + File.separator + jarName;
			}
		} else {
			return parent.getJarPath();
		}
	}

	/**
	 * Return name of jar-sub directory.
	 * @return
	 */
	public String getJarDirectory() {
		if (parent == null) {
			// top level DPU
			return jarDirectory;
		} else {
			return parent.getJarDirectory();
		}		
	}
	
	/**
	 * Set jar directory for given DPU template. If the DPU has parent then
	 * nothing happened.
	 * @param jarDirectory
	 */
	public void setJarDirectory(String jarDirectory) {
		if (parent == null) {
			// top level DPU
			this.jarDirectory = jarDirectory;
		} else {
			// ignore .. 
		}		
	}	
	
	/**
	 * Return name of given jar file.
	 * @return
	 */
	public String getJarName() {
		if (parent == null) {
			// top level DPU
			return jarName;
		} else {
			return parent.getJarName();
		}
	}
	
	/**
	 * Set jar name for given DPU template. If the DPU has parent then
	 * nothing happened.
	 * @param jarName
	 */
	public void setJarName(String jarName) {
		if (parent == null) {
			// top level DPU
			this.jarName = jarName;
		} else {
			// ignore .. 
		}		
	}
	
	/**
	 * Return true if DPU jar can be replaced.
	 * @return
	 */
	public boolean jarFileReplacable() {
		return parent == null;
	}
	
}
