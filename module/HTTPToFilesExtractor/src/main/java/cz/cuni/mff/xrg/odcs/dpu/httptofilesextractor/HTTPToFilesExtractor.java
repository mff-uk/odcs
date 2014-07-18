package cz.cuni.mff.xrg.odcs.dpu.httptofilesextractor;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;

@DPU.AsExtractor
public class HTTPToFilesExtractor extends ConfigurableBase<HTTPToFilesExtractorConfig> implements ConfigDialogProvider<HTTPToFilesExtractorConfig> {

    private static final Logger LOG = LoggerFactory.getLogger(HTTPToFilesExtractor.class);

    @DataUnit.AsOutput(name = "filesOutput")
    public WritableFilesDataUnit filesOutput;

    public HTTPToFilesExtractor() {
        super(HTTPToFilesExtractorConfig.class);
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, InterruptedException {
        Map<String, String> symbolicNameToURIMap = config.getSymbolicNameToURIMap();
        int connectionTimeout = config.getConnectionTimeout();
        int readTimeout = config.getReadTimeout();
        String shortMessage = this.getClass().getSimpleName() + " starting.";
        String longMessage = String.format("Configuration: files to download: %d, connectionTimeout: %d, readTimeout: %d", symbolicNameToURIMap.size(), connectionTimeout, readTimeout);
        dpuContext.sendMessage(DPUContext.MessageType.INFO, shortMessage, longMessage);
        LOG.info(shortMessage + " " + longMessage);

        boolean shouldContinue = !dpuContext.canceled();
        for (String symbolicName : symbolicNameToURIMap.keySet()) {
            if (!shouldContinue) {
                break;
            }

            String downloadedFilename = null;
            File downloadedFile = null;
            String downloadFromLocation = null;
            try {
                downloadedFilename = filesOutput.createFile(symbolicName);
                downloadedFile = new File(URI.create(downloadedFilename));
                downloadFromLocation = symbolicNameToURIMap.get(symbolicName);
                FileUtils.copyURLToFile(new java.net.URL(downloadFromLocation), downloadedFile, connectionTimeout, readTimeout);
                filesOutput.addExistingFile(symbolicName, downloadedFilename);
                if (dpuContext.isDebugging()) {
                    LOG.debug("Downloaded " + symbolicName + " from " + downloadFromLocation + " to " + downloadedFilename);
                }
            } catch (DataUnitException ex) {
                dpuContext.sendMessage(DPUContext.MessageType.ERROR, "Error when downloading.", "Symbolic name " + symbolicName + " from location ", ex);
            } catch (IOException ex) {
                dpuContext.sendMessage(DPUContext.MessageType.ERROR, "Error when downloading.", "Symbolic name " + symbolicName + " from location " + downloadFromLocation + " could not be saved to " + downloadedFilename, ex);
            }
            shouldContinue = !dpuContext.canceled();
        }
    }

    @Override
    public AbstractConfigDialog<HTTPToFilesExtractorConfig> getConfigurationDialog() {
        return new HTTPToFilesExtractorConfigDialog();
    }

}
