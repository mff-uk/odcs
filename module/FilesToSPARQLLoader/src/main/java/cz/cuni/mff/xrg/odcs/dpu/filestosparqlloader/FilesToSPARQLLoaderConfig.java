package cz.cuni.mff.xrg.odcs.dpu.filestosparqlloader;

import java.util.Collections;
import java.util.Set;

public class FilesToSPARQLLoaderConfig {
    /**
     * 
     */
    private static final long serialVersionUID = -3161162556703740405L;

    private String queryEndpointUrl = "";

    private String updateEndpointUrl = "";

    private int commitSize = 10000;

    private Set<String> targetContexts = Collections.<String> emptySet();

    private boolean skipOnError = false;

    // DPUTemplateConfig must provide public non-parametric constructor
    public FilesToSPARQLLoaderConfig() {
    }

    public String getQueryEndpointUrl() {
        return queryEndpointUrl;
    }

    public void setQueryEndpointUrl(String queryEndpointUrl) {
        this.queryEndpointUrl = queryEndpointUrl;
    }

    public String getUpdateEndpointUrl() {
        return updateEndpointUrl;
    }

    public void setUpdateEndpointUrl(String updateEndpointUrl) {
        this.updateEndpointUrl = updateEndpointUrl;
    }

    public int getCommitSize() {
        return commitSize;
    }

    public void setCommitSize(int commitSize) {
        this.commitSize = commitSize;
    }

    public Set<String> getTargetContexts() {
        return targetContexts;
    }

    public void setTargetContexts(Set<String> targetContexts) {
        this.targetContexts = targetContexts;
    }

    public boolean isSkipOnError() {
        return skipOnError;
    }

    public void setSkipOnError(boolean skipOnError) {
        this.skipOnError = skipOnError;
    }

}
