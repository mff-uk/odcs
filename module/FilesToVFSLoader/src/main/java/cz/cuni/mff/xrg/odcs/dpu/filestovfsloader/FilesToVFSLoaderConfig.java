package cz.cuni.mff.xrg.odcs.dpu.filestovfsloader;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FilesToVFSLoaderConfig {
    /**
     * 
     */
    private static final long serialVersionUID = -3161162556703740405L;

    private String destination = "file:///tmp/";

    private boolean moveFiles = false;

    private boolean replaceExisting = false;

    private boolean skipOnError = false;
    
    private String username = null;
    
    private String password = null;

    // DPUTemplateConfig must provide public non-parametric constructor
    public FilesToVFSLoaderConfig() {
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public boolean isMoveFiles() {
        return moveFiles;
    }

    public void setMoveFiles(boolean moveFiles) {
        this.moveFiles = moveFiles;
    }

    public boolean isReplaceExisting() {
        return replaceExisting;
    }

    public void setReplaceExisting(boolean replaceExisting) {
        this.replaceExisting = replaceExisting;
    }

    public boolean isSkipOnError() {
        return skipOnError;
    }

    public void setSkipOnError(boolean skipOnError) {
        this.skipOnError = skipOnError;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
    }
}
