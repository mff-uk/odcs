package cz.cuni.mff.xrg.odcs.dpu.virtuosoloader;

import cz.cuni.mff.xrg.odcs.commons.module.config.DPUConfigObjectBase;

public class VirtuosoLoaderConfig extends DPUConfigObjectBase {
    /**
     * 
     */
    private static final long serialVersionUID = -31611625503740405L;

    private String virtuosoUrl = "";

    private String username = "";

    private String password = "";

    private boolean clearDestinationGraph = false;

    private String loadDirectoryPath = "";

    private boolean includeSubdirectories = true;

    private String loadFilePattern = "%";

    private String targetContext = "";

//    private String targetTempContext = "";

    private long statusUpdateInterval = 60L;

    private int threadCount = 1;

    private boolean skipOnError = false;
    
    public VirtuosoLoaderConfig() {
    }

    public String getVirtuosoUrl() {
        return virtuosoUrl;
    }

    public void setVirtuosoUrl(String virtuosoUrl) {
        this.virtuosoUrl = virtuosoUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isClearDestinationGraph() {
        return clearDestinationGraph;
    }

    public void setClearDestinationGraph(boolean clearDestinationGraph) {
        this.clearDestinationGraph = clearDestinationGraph;
    }

    public String getLoadDirectoryPath() {
        return loadDirectoryPath;
    }

    public void setLoadDirectoryPath(String loadDirectoryPath) {
        this.loadDirectoryPath = loadDirectoryPath;
    }

    public boolean isIncludeSubdirectories() {
        return includeSubdirectories;
    }

    public void setIncludeSubdirectories(boolean includeSubdirectories) {
        this.includeSubdirectories = includeSubdirectories;
    }

    public String getLoadFilePattern() {
        return loadFilePattern;
    }

    public void setLoadFilePattern(String loadFilePattern) {
        this.loadFilePattern = loadFilePattern;
    }

    public String getTargetContext() {
        return targetContext;
    }

    public void setTargetContext(String targetContext) {
        this.targetContext = targetContext;
    }

    public long getStatusUpdateInterval() {
        return statusUpdateInterval;
    }

    public void setStatusUpdateInterval(long statusUpdateInterval) {
        this.statusUpdateInterval = statusUpdateInterval;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public boolean isSkipOnError() {
        return skipOnError;
    }

    public void setSkipOnError(boolean skipOnError) {
        this.skipOnError = skipOnError;
    }

}
