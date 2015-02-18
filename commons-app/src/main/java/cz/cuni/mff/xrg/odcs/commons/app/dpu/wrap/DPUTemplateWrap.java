package cz.cuni.mff.xrg.odcs.commons.app.dpu.wrap;

import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.facade.ModuleFacade;

import java.util.Locale;

/**
 * Wrap {@link cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord} to made work with configuration and
 * configuration dialog easier.
 *
 * @author Petyr
 */
public class DPUTemplateWrap extends DPURecordWrap {

    /**
     * Wrapped DPUTemplateRecord.
     */
    private final DPUTemplateRecord dpuTemplate;

    /**
     * Create wrap for DPUTemplateRecord.
     *
     * @param dpuTemplate
     */
    public DPUTemplateWrap(DPUTemplateRecord dpuTemplate, Locale locale, ModuleFacade moduleFacade) {
        super(dpuTemplate, true, locale, moduleFacade);
        this.dpuTemplate = dpuTemplate;
    }

    /**
     * Get DPU template record.
     *
     * @return DPU template record
     */
    public DPUTemplateRecord getDPUTemplateRecord() {
        return dpuTemplate;
    }

}
