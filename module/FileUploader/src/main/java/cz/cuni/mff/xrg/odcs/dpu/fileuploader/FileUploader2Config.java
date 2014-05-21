package cz.cuni.mff.xrg.odcs.dpu.fileuploader;

import java.util.LinkedHashMap;
import java.util.Map;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;

public class FileUploader2Config extends DPUConfigObjectBase {

    /**
     * 
     */
    private static final long serialVersionUID = -1976290368429585223L;

    private Map<String, String> symbolicNameToBaseURIMap;
    private Map<String, String> symbolicNameToFormatMap;
    private Map<String, String> symbolicNameToPathURI;
    
    

    // DPUTemplateConfig must provide public non-parametric constructor
    public FileUploader2Config() {
        this.symbolicNameToBaseURIMap = new LinkedHashMap<>();
        this.symbolicNameToFormatMap = new LinkedHashMap<>();
        this.symbolicNameToPathURI = new LinkedHashMap<>();
    }

    public Map<String, String> getSymbolicNameToBaseURIMap() {
        return symbolicNameToBaseURIMap;
    }


    public void setSymbolicNameToBaseURIMap(Map<String, String> symbolicNameToBaseURIMap) {
        this.symbolicNameToBaseURIMap = symbolicNameToBaseURIMap;
    }


    public Map<String, String> getSymbolicNameToFormatMap() {
        return symbolicNameToFormatMap;
    }

    public void setSymbolicNameToFormatMap(Map<String, String> symbolicNameToFormatMap) {
        this.symbolicNameToFormatMap = symbolicNameToFormatMap;
    }

    public Map<String, String> getSymbolicNameToPathURI() {
        return symbolicNameToPathURI;
    }

    public void setSymbolicNameToPathURI(Map<String, String> symbolicNameToPathURI) {
        this.symbolicNameToPathURI = symbolicNameToPathURI;
    }
}
