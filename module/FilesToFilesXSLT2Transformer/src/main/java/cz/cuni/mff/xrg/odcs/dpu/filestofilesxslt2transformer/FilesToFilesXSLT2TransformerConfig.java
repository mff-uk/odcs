package cz.cuni.mff.xrg.odcs.dpu.filestofilesxslt2transformer;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;

public class FilesToFilesXSLT2TransformerConfig extends DPUConfigObjectBase {

    /**
     * 
     */
    private static final long serialVersionUID = 4343875087541528977L;

    private String xslTemplate = "";

    private String xslTemplateFileNameShownInDialog = "";

    private boolean skipOnError = false;

    public FilesToFilesXSLT2TransformerConfig() {
    }

    public String getXslTemplate() {
        return xslTemplate;
    }

    public void setXslTemplate(String xslTemplate) {
        this.xslTemplate = xslTemplate;
    }

    public String getXslTemplateFileNameShownInDialog() {
        return xslTemplateFileNameShownInDialog;
    }

    public void setXslTemplateFileNameShownInDialog(
            String xslTemplateFileNameShownInDialog) {
        this.xslTemplateFileNameShownInDialog = xslTemplateFileNameShownInDialog;
    }

    public boolean isSkipOnError() {
        return skipOnError;
    }

    public void setSkipOnError(boolean skipOnError) {
        this.skipOnError = skipOnError;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[templateFile=" + xslTemplateFileNameShownInDialog + ",skipOnError=" + String.valueOf(skipOnError) + "]";
    }
}
