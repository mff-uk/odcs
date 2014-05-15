package cz.cuni.mff.xrg.odcs.dpu.httpdownloader;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsExtractor;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import cz.cuni.mff.xrg.odcs.filelist.WritableFileListDataUnit;

@AsExtractor
public class HTTPDownloader extends ConfigurableBase<HTTPDownloaderConfig> implements ConfigDialogProvider<HTTPDownloaderConfig> {
    private static final Logger LOG = LoggerFactory.getLogger(HTTPDownloader.class);

    @OutputDataUnit(name = "fileOutput")
    public WritableFileListDataUnit fileOutput;

    public HTTPDownloader() {
        super(HTTPDownloaderConfig.class);
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, DataUnitException, InterruptedException {
        Map<String, String> symbolicNameToURIMap = config.getSymbolicNameToURIMap();
        int connectionTimeout = 1000;//config.getConnectionTimeout();
        int readTimeout = 1000; //config.getReadTimeout();
        String shortMessage = this.getClass().getName() + " starting.";
        String longMessage = String.format("Configuration: files to download: %d, connectionTimeout: %d, readTimeout: %d", symbolicNameToURIMap.size(), connectionTimeout, readTimeout);
        dpuContext.sendMessage(MessageType.INFO, shortMessage, longMessage);
        LOG.info(shortMessage + " " + longMessage);
        
        for (String symbolicName : symbolicNameToURIMap.keySet()) {
            String downloadedFilename = fileOutput.createFilename(symbolicName);
            File downloadedFile = new File(downloadedFilename);
            String downloadFromLocation = symbolicNameToURIMap.get(symbolicName);
            try {
                FileUtils.copyURLToFile(new java.net.URL(downloadFromLocation), downloadedFile, connectionTimeout, readTimeout);
                fileOutput.addExistingFile(symbolicName, downloadedFilename);
                if (dpuContext.isDebugging()) {
                    LOG.debug("Downloaded " + symbolicName + " from " + downloadFromLocation + " to " + downloadedFilename);
                }
            } catch (IOException ex) {
                dpuContext.sendMessage(MessageType.ERROR, "Error when downloading.", "Symbolic name " + symbolicName + " from location " + downloadFromLocation + " could not be saved to " + downloadedFilename, ex);
            }
        }
    }

    @Override
    public AbstractConfigDialog<HTTPDownloaderConfig> getConfigurationDialog() {
        return new HTTPDownloaderConfigDialog();
    }
}
