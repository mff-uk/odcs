package cz.cuni.mff.xrg.odcs.commons.app.dpu;

import java.io.File;

import cz.cuni.mff.xrg.odcs.commons.app.auth.SharedEntity;
import cz.cuni.mff.xrg.odcs.commons.app.auth.ShareType;
import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import cz.cuni.mff.xrg.odcs.commons.app.module.ModuleException;
import cz.cuni.mff.xrg.odcs.commons.app.facade.ModuleFacade;
import cz.cuni.mff.xrg.odcs.commons.app.user.OwnedEntity;
import cz.cuni.mff.xrg.odcs.commons.app.user.User;

import javax.persistence.*;

/**
 * Representation of template for creating {@link DPUInstanceRecord}s. The
 * purpose of templating DPUs is to unburden user from manually edit and
 * configure all DPU properties when creating pipelines. Template's properties
 * are propagated to {@link DPUInstanceRecord} everytime it is created as an
 * instance of given {@link DPUTemplateRecord}. Reference to template is
 * preserved in each DPU instance, so that updates of configuration can be
 * propagated to template's children.
 * 
 * @author Petyr
 * @author Jan Vojt
 * 
 */
@Entity
@Table(name = "dpu_template")
public class DPUTemplateRecord extends DPURecord
		implements OwnedEntity, SharedEntity, DataObject {

	/**
	 * Visibility in DPUTree.
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "visibility")
	private ShareType shareType;

	/**
	 * Description obtained from jar file manifest.
	 */
	@Column(name = "jar_description")
	private String jarDescription;

	/**
	 * DPURecord type, determined by associated jar file.
	 * It's transitive for non-root templates.
	 */
	@Enumerated(EnumType.ORDINAL)
	private DPUType type;

	/**
	 * Name of directory where {@link #jarName} is located.
	 * It's transitive for non-root templates.
	 * 
	 * @see AppConfig
	 */
	@Column(name = "jar_directory")
	private String jarDirectory;

	/**
	 * DPU's jar file name.
	 * It's transitive for non-root templates.
	 * 
	 * @see AppConfig
	 */
	@Column(name = "jar_name")
	private String jarName;

	/**
	 * Parent {@link DPUTemplateRecord}. If parent is set, this DPURecord is
	 * rendered under its parent in DPU tree.
	 */
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id", nullable = true)
	private DPUTemplateRecord parent;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User owner;

	/**
	 * Empty ctor for JPA.
	 */
	public DPUTemplateRecord() {
	}

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
	 * Copy constructor. New instance always has private shareType, no matter
 what setting was in original DPU.
	 * 
	 * @param dpu
	 */
	public DPUTemplateRecord(DPUTemplateRecord dpu) {
		super(dpu);
		shareType = ShareType.PRIVATE;		
		type = dpu.type;
		parent = dpu.parent;
		if (parent == null) {
			jarDirectory = dpu.jarDirectory;
			jarName = dpu.jarName;
			jarDescription = dpu.jarDescription;
		} else {
			jarDirectory = null;
			jarName = null;
			jarDescription = null;
		}
		
	}

	/**
	 * Create template from given instance.
	 * 
	 * @param dpuInstance
	 */
	public DPUTemplateRecord(DPUInstanceRecord dpuInstance) {
		super(dpuInstance);
		this.shareType = ShareType.PRIVATE;
		this.type = dpuInstance.getType();

		// copy jarDescription from template of previous one ..
		this.jarDescription = dpuInstance.getTemplate() == null
				? null
				: dpuInstance.getTemplate().getJarDescription();
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	@Override
	public User getOwner() {
		return owner;
	}

	@Override
	public ShareType getShareType() {
		return shareType;
	}

	public void setVisibility(ShareType visibility) {
		this.shareType = visibility;
	}

	public String getJarDescription() {
		if (parent == null) {
			// top level DPU
			return jarDescription;
		} else {
			return parent.getJarDescription();
		}
	}

	public void setJarDescription(String jarDescription) {
		if (parent == null) {
			// top level DPU
			this.jarDescription = jarDescription;
		} else {
			// ignore ..
		}
	}

	public DPUTemplateRecord getParent() {
		return parent;
	}

	public void setParent(DPUTemplateRecord parent) {
		if (parent == null) {
			// we are going under someone .. we use it's name and directory
			jarDirectory = null;
			jarName = null;
			type = null;
		} else {
			// we will be the top one .. if we are not now,
			// store jar name and directory etc .. 
			if (this.parent != null) {
				jarDirectory = parent.jarDirectory;
				jarName = parent.jarName;
				type = parent.type;
			}
		}
		this.parent = parent;
	}

	public void setType(DPUType type) {
		if (parent == null) {
			this.type = type;
		} else {
			parent.setType(type);
		}
	}

	@Override
	public DPUType getType() {
		if (parent == null) {
			return this.type;
		} else {
			return parent.getType();
		}
	}

	/**
	 * Load DPU's instance from associated jar file.
	 * 
	 * @param moduleFacade ModuleFacade used to load DPU.
	 * @throws ModuleException
	 */
	@Override
	public void loadInstance(ModuleFacade moduleFacade) throws ModuleException {
		instance = moduleFacade.getInstance(this);
	}

	@Override
	public String getJarPath() {
		if (parent == null) {
			// to level DPU
			if (jarDirectory == null || jarDirectory.isEmpty()) {
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
	 * 
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
	 * 
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
	 * 
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
	 * Set jar name for given DPU template. If the DPU has parent then nothing
	 * happened.
	 * 
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
	 * 
	 * @return
	 */
	public boolean jarFileReplacable() {
		return parent == null;
	}

}
