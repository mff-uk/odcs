package cz.cuni.xrg.intlib.commons.app;

/**
 * Contains application configuration.
 *
 * @author Petyr
 */
public class AppConfiguration {

    /**
     * Path to the dpu directory folder.
     */
    private String dpuDirectory = "file:///C:/MyGit/intlib/module/";

    public String getDpuDirectory() {
        return this.dpuDirectory;
    }

    public void setDpuDirectory(String dpuDirectory) {
        this.dpuDirectory = dpuDirectory;
    }
}
