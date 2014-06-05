package cz.cuni.mff.xrg.odcs.dpu.fileuploader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsExtractor;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import cz.cuni.mff.xrg.odcs.filelist.WritableFileListDataUnit;

@AsExtractor
public class FileUploader extends ConfigurableBase<FileUploaderConfig> implements ConfigDialogProvider<FileUploaderConfig> {
    private static final Logger LOG = LoggerFactory.getLogger(FileUploader.class);

    @OutputDataUnit(name = "fileOutput")
    public WritableFilesDataUnit fileOutput;

    public FileUploader() {
        super(FileUploaderConfig.class);
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, DataUnitException, InterruptedException {
//        String shortMessage = this.getClass().getName() + " starting.";
//        String longMessage = String.format("Configuration: files to download: %d, connectionTimeout: %d, readTimeout: %d", symbolicNameToURIMap.size(), connectionTimeout, readTimeout);
//        dpuContext.sendMessage(MessageType.INFO, shortMessage, longMessage);
//        LOG.info(shortMessage + " " + longMessage);
//        FileListIteration fileListIteration = fileInput.getFileList();
//
//        RepositoryConnection connection = null;
//        try {
//            while (fileListIteration.hasNext()) {
//                connection = rdfOutput.getConnection();
//                FileListDataUnitEntry entry = fileListIteration.next();
//                if (dpuContext.isDebugging()) {
//                    LOG.debug("Starting extraction of file " + entry.getSymbolicName() + " path URI " + entry.getFilesystemURI());
//                }
//                try {
//                    connection.add(new File(entry.getFilesystemURI()), null, null, rdfOutput.getWriteContext());
//                } catch (RepositoryException | RDFParseException | IOException ex) {
//                    dpuContext.sendMessage(MessageType.ERROR, "Error when extracting.", "Symbolic name " + entry.getSymbolicName() + " path URI " + entry.getFilesystemURI(), ex);
//                } finally {
//                    if (connection != null) {
//                        try {
//                            connection.close();
//                        } catch (RepositoryException ex) {
//                            dpuContext.sendMessage(MessageType.WARNING, ex.getMessage(), ex.fillInStackTrace().toString());
//                        }
//                    }
//                }
//                if (dpuContext.isDebugging()) {
//                    LOG.debug("Finished extraction of file " + entry.getSymbolicName() + " path URI " + entry.getFilesystemURI());
//                }
//            }
//        } catch (RepositoryException ex) {
//            dpuContext.sendMessage(MessageType.ERROR, "Error when extracting.", "", ex);
//        } finally {
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (RepositoryException ex) {
//                    dpuContext.sendMessage(MessageType.WARNING, ex.getMessage(), ex.fillInStackTrace().toString());
//                }
//            }
//        }
    }

    @Override
    public AbstractConfigDialog<FileUploaderConfig> getConfigurationDialog() {
        return new FileUploaderConfigDialog();
    }
}
