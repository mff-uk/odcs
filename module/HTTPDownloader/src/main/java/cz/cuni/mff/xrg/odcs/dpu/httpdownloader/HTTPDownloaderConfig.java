package cz.cuni.mff.xrg.odcs.dpu.httpdownloader;

import java.util.LinkedHashMap;
import java.util.Map;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;

public class HTTPDownloaderConfig extends DPUConfigObjectBase {

    /**
     * 
     */
    private static final long serialVersionUID = 4485340550430571717L;

    private int connectionTimeout = 2000;

    private int readTimeout = 2000;

    private Map<String, String> symbolicNameToURIMap;

    // DPUTemplateConfig must provide public non-parametric constructor
    public HTTPDownloaderConfig() {
        this.symbolicNameToURIMap = new LinkedHashMap<>();
    }

    public Map<String, String> getSymbolicNameToURIMap() {
        return symbolicNameToURIMap;
    }

    public void setSymbolicNameToURIMap(Map<String, String> symbolicNameToURIMap) {
        this.symbolicNameToURIMap = symbolicNameToURIMap;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
}
