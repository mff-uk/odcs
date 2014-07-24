package cz.cuni.mff.xrg.odcs.dpu.filestordftransformer;

import java.util.LinkedHashMap;
import java.util.Map;

public class FilesToRDFTransformerConfig {
    public static final String STOP_EXTRACTION_ERROR_HANDLING = "STOP_EXTRACTION";
    public static final String SKIP_CONTINUE_THIS_FILE_ERROR_HANDLING = "SKIP_CONTINUE_THIS_FILE";
    public static final String SKIP_CONTINUE_NEXT_FILE_ERROR_HANDLING = "SKIP_CONTINUE_NEXT_FILE";
    
    private Map<String, String> symbolicNameToBaseURIMap;

    private Map<String, String> symbolicNameToFormatMap;

    private int commitSize = 1;
    
    private String fatalErrorHandling = STOP_EXTRACTION_ERROR_HANDLING;
    
    private String errorHandling = SKIP_CONTINUE_THIS_FILE_ERROR_HANDLING;
    
    private String warningHandling = SKIP_CONTINUE_THIS_FILE_ERROR_HANDLING;

    // DPUTemplateConfig must provide public non-parametric constructor
    public FilesToRDFTransformerConfig() {
        this.symbolicNameToBaseURIMap = new LinkedHashMap<>();
        this.symbolicNameToFormatMap = new LinkedHashMap<>();
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

    public int getCommitSize() {
        return commitSize;
    }

    public void setCommitSize(int commitSize) {
        this.commitSize = commitSize;
    }

    public String getFatalErrorHandling() {
        return fatalErrorHandling;
    }

    public void setFatalErrorHandling(String fatalErrorHandling) {
        this.fatalErrorHandling = fatalErrorHandling;
    }

    public String getErrorHandling() {
        return errorHandling;
    }

    public void setErrorHandling(String errorHandling) {
        this.errorHandling = errorHandling;
    }

    public String getWarningHandling() {
        return warningHandling;
    }

    public void setWarningHandling(String warningHandling) {
        this.warningHandling = warningHandling;
    }
}
