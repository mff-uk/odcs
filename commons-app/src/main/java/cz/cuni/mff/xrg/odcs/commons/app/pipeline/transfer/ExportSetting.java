package cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer;

/**
 * Configuration class for {@link ExportService}.
 * 
 * @author Škoda Petr
 */
public class ExportSetting {

    private boolean exportDPUUserData;

    public ExportSetting(boolean exportDPUUserData) {
        this.exportDPUUserData = exportDPUUserData;
    }

    public boolean isExportDPUUserData() {
        return exportDPUUserData;
    }

    public void setExportDPUUserData(boolean exportDPUUserData) {
        this.exportDPUUserData = exportDPUUserData;
    }

}
