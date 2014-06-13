package cz.cuni.mff.xrg.odcs.dpu.filestofilesxslt2transformer;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;

public class FilesToFilesXSLT2Config extends DPUConfigObjectBase {

	/**
     * 
     */
	private static final long serialVersionUID = 4343875087541528977L;

	private String xslTemplate = "";
	private String xslTemplateFileNameShownInDialog = "";
	private String xslTemplateFileName;
	private String outputXSLTMethod = "text";

	public FilesToFilesXSLT2Config() {
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

	public String getOutputXSLTMethod() {
		return outputXSLTMethod;
	}

	public void setOutputXSLTMethod(String outputXSLTMethod) {
		this.outputXSLTMethod = outputXSLTMethod;
	}

	public String getXslTemplateFileName() {
		return xslTemplateFileName;
	}

	public void setXslTemplateFileName(String xslTemplateFileName) {
		this.xslTemplateFileName = xslTemplateFileName;
	}

}
