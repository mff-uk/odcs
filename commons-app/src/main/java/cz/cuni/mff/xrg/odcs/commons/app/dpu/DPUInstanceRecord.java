package cz.cuni.mff.xrg.odcs.commons.app.dpu;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import cz.cuni.mff.xrg.odcs.commons.app.facade.ModuleFacade;
import cz.cuni.mff.xrg.odcs.commons.app.module.ModuleException;

/**
 * Represent the DPU instance pipeline placement in DB.
 *
 * @author Petyr
 * @author Jan Vojt
 */
@Entity
@Table(name = "dpu_instance")
public class DPUInstanceRecord extends DPURecord {

    /**
     * Template used for creating this instance.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "dpu_id")
    private DPUTemplateRecord template;

    /**
     * If true then this instance use owner template configuration.
     */
    @Column(name = "use_template_config")
    private boolean useTemplateConfig;

    /**
     * Empty constructor because of JPA.
     */
    public DPUInstanceRecord() {
    }

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
        this.useTemplateConfig = dpuInstance.useTemplateConfig;
    }

    /**
     * Create new DPUInstanceRecord with given name and type.
     *
     * @param name
     */
    public DPUInstanceRecord(String name) {
        super(name);
        this.useTemplateConfig = false;
    }

    /**
     * Create instance based on given template.
     *
     * @param template
     */
    public DPUInstanceRecord(DPUTemplateRecord template) {
        // construct DPURecord
        super(template);
        // and set out variables
        this.template = template;
        this.useTemplateConfig = false;
    }

    /**
     * @return Used {@link DPUTemplateRecord}.
     */
    public DPUTemplateRecord getTemplate() {
        return template;
    }

    /**
     * @param template
     *            New {@link DPUTemplateRecord}.
     */
    public void setTemplate(DPUTemplateRecord template) {
        this.template = template;
    }

    /**
     * @return true if dpu should use template configuration, false if dpu should use instance configuration
     */
    public boolean isUseTemplateConfig() {
        return useTemplateConfig;
    }

    /**
     * @param useTemplateConfig
     *            true if dpu should use template configuration, false if dpu should use instance configuration
     */
    public void setUseTemplateConfig(boolean useTemplateConfig) {
        this.useTemplateConfig = useTemplateConfig;
    }

    @Override
    public DPUType getType() {
        return template.getType();
    }

    /**
     * Load DPU's instance from associated jar file.
     *
     * @param moduleFacade
     *            ModuleFacade used to load DPU.
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
