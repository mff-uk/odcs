package cz.cuni.mff.xrg.odcs.commons.app.dpu;

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
public class DPUInstanceRecord extends DPURecord {
		
	/**
	 * Template used for creating this instance. 
	 */
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name="dpu_id")
	private DPUTemplateRecord template;

	/**
	 * Empty constructor because of JPA.
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
	}
	
	/**
	 * Create new DPUInstanceRecord with given name and type.
	 * @param name
	 */
	public DPUInstanceRecord(String name) {
		super(name);
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

	/**
	 * 
	 * @return Used {@link DPUTemplateRecord}.
	 */
	public DPUTemplateRecord getTemplate() {
		return template;
	}

	/**
	 * 
	 * @param template New {@link DPUTemplateRecord}.
	 */
	public void setTemplate(DPUTemplateRecord template) {
		this.template = template;
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
