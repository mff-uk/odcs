package cz.cuni.mff.xrg.odcs.commons.app.dpu;

import cz.cuni.mff.xrg.odcs.commons.app.dao.DataObject;
import javax.persistence.*;

import cz.cuni.mff.xrg.odcs.commons.app.module.ModuleException;
import cz.cuni.mff.xrg.odcs.commons.app.facade.ModuleFacade;

/**
 * Represent the DPU instance pipeline placement in DB.
 * 
 * @author Petyr
 * @author Jan Vojt
 *
 */
@Entity
@Table(name = "dpu_instance")
public class DPUInstanceRecord extends DPURecord implements DataObject {
		
	/**
	 * Template used for creating this instance. 
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="dpu_id")
	private DPUTemplateRecord template;

	/**
	 * DPURecord tool tip.
	 */
	@Column(name="tool_tip")
	private String toolTip;	
	
	/**
	 * Empty ctor because of JPA.
	 */
	public DPUInstanceRecord() {}
	
	/**
	 * Copy constructor. Creates a copy of given <code>DPUInstanceRecord</code>.
	 * Primary key {@link #id} of newly created object is <code>null</code>.
	 * Copying is NOT propagated on {@link #template}, original reference is
	 * preserved.
	 * 
	 * @param dpuInstance 
	 */
	public DPUInstanceRecord(DPUInstanceRecord dpuInstance) {
		super(dpuInstance);
		template = dpuInstance.getTemplate();
		toolTip = dpuInstance.getToolTip();
	}
	
	/**
	 * Create new DPUInstanceRecord with given name and type.
	 * @param name
	 */
	public DPUInstanceRecord(String name) {
		super(name);
		toolTip = null;
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
		this.toolTip = null;
	}

	public DPUTemplateRecord getTemplate() {
		return template;
	}

	public void setTemplate(DPUTemplateRecord template) {
		this.template = template;
	}

	public String getToolTip() {
		return toolTip;
	}

	public void setToolTip(String toolTip) {
		this.toolTip = toolTip;
	}

	@Override
	public DPUType getType() {
		return template.getType();
	}

    /**
     * Load DPU's instance from associated jar file.
     * @param moduleFacade ModuleFacade used to load DPU.
     * @throws ModuleException
     */
	@Override
    public void loadInstance(ModuleFacade moduleFacade) throws ModuleException {
    	instance = moduleFacade.getInstance(template);
    }
	
	
	@Override
	public String getJarPath() {
		return template.getJarPath();
	}
	
}
