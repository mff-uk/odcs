package cz.cuni.mff.xrg.odcs.dpu.filestovfsloader;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Date;

import org.apache.commons.vfs2.AllFileSelector;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.ftps.FtpsFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.helpers.dpu.config.AbstractConfigDialog;
import eu.unifiedviews.helpers.dpu.config.ConfigDialogProvider;
import eu.unifiedviews.helpers.dpu.config.ConfigurableBase;

@DPU.AsLoader
public class FilesToVFSLoader extends
        ConfigurableBase<FilesToVFSLoaderConfig> implements
        ConfigDialogProvider<FilesToVFSLoaderConfig> {
    private static final Logger LOG = LoggerFactory
            .getLogger(FilesToVFSLoader.class);

    @DataUnit.AsInput(name = "filesInput")
    public FilesDataUnit filesInput;

    public FilesToVFSLoader() {
        super(FilesToVFSLoaderConfig.class);
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException,
            InterruptedException {
        String shortMessage = this.getClass().getSimpleName() + " starting.";
        String longMessage = String.valueOf(config);
        dpuContext.sendMessage(DPUContext.MessageType.INFO, shortMessage, longMessage);

        FilesDataUnit.Iteration filesIteration;
        try {
            filesIteration = filesInput.getIteration();
        } catch (DataUnitException ex) {
            throw new DPUException("Could not obtain filesInput", ex);
        }

        FileSystemManager fileSystemManager = null;
        FileObject destinationFileObject = null;
        FileSystemOptions options = null;
        try {
            options = new FileSystemOptions();
            if (config.getUsername() != null && !config.getUsername().isEmpty() && config.getPassword() != null) {
                StaticUserAuthenticator auth = new StaticUserAuthenticator(null, config.getUsername(), config.getPassword());
                DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(options, auth);
            }
            FtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(options, false);
            SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(options, false);
            FtpsFileSystemConfigBuilder.getInstance().setUserDirIsRoot(options, false);
            fileSystemManager = VFS.getManager();
            destinationFileObject = fileSystemManager.resolveFile(config.getDestination());
            destinationFileObject.createFolder();
        } catch (FileSystemException ex) {
            throw new DPUException("Error creating default filesystem manager or resolving destination", ex);
        }

        boolean moveFiles = config.isMoveFiles();
        boolean replaceExisting = config.isReplaceExisting();

        long index = 0L;
        boolean shouldContinue = !dpuContext.canceled();

        try {
            while ((shouldContinue) && (filesIteration.hasNext())) {
                index++;

                FilesDataUnit.Entry entry;
                try {
                    entry = filesIteration.next();
                    FileObject inputFileObject = null;
                    FileObject outputFileObject = null;
                    try {
                        inputFileObject = fileSystemManager.resolveFile(entry.getFileURIString());
                        outputFileObject = destinationFileObject.resolveFile(entry.getSymbolicName());

                        Date start = new Date();
                        if (dpuContext.isDebugging()) {
                            LOG.debug("Processing {} file {}", appendNumber(index), entry);
                        }
                        if (!replaceExisting && outputFileObject.exists()) {
                            throw new FileAlreadyExistsException(outputFileObject.toString());
                        }
                        if (moveFiles) {
                            inputFileObject.moveTo(outputFileObject);
                        } else {
                            outputFileObject.copyFrom(inputFileObject, new AllFileSelector());
                        }
                        if (dpuContext.isDebugging()) {
                            LOG.debug("Processed {} file in {}s", appendNumber(index), (System.currentTimeMillis() - start.getTime()) / 1000);
                        }
                    } catch (IOException ex) {
                        dpuContext.sendMessage(
                                config.isSkipOnError() ? DPUContext.MessageType.WARNING : DPUContext.MessageType.ERROR,
                                "Error processing " + appendNumber(index) + " file",
                                String.valueOf(entry),
                                ex);
                    } finally {
                        if (inputFileObject != null) {
                            try {
                                inputFileObject.close();
                            } catch (FileSystemException ex) {
                                LOG.warn("Error closing", ex);
                            }
                        }
                        if (outputFileObject != null) {
                            try {
                                outputFileObject.close();
                            } catch (FileSystemException ex) {
                                LOG.warn("Error closing", ex);
                            }
                        }
                    }
                } catch (DataUnitException ex) {
                    dpuContext.sendMessage(
                            config.isSkipOnError() ? DPUContext.MessageType.WARNING : DPUContext.MessageType.ERROR,
                            "DataUnit exception.",
                            "",
                            ex);
                }

                shouldContinue = !dpuContext.canceled();
            }
        } catch (DataUnitException ex) {
            throw new DPUException("Error iterating filesInput.", ex);
        } finally {
            try {
                filesIteration.close();
            } catch (DataUnitException ex) {
                LOG.warn("Error closing filesInput", ex);
            }
            try {
                destinationFileObject.close();
            } catch (FileSystemException ex) {
                LOG.warn("Error closing", ex);
            }
        }
    }

    @Override
    public AbstractConfigDialog<FilesToVFSLoaderConfig> getConfigurationDialog() {
        return new FilesToVFSLoaderConfigDialog();
    }

    public static String appendNumber(long number) {
        String value = String.valueOf(number);
        if (value.length() > 1) {
            // Check for special case: 11 - 13 are all "th".
            // So if the second to last digit is 1, it is "th".
            char secondToLastDigit = value.charAt(value.length() - 2);
            if (secondToLastDigit == '1')
                return value + "th";
        }
        char lastDigit = value.charAt(value.length() - 1);
        switch (lastDigit) {
            case '1':
                return value + "st";
            case '2':
                return value + "nd";
            case '3':
                return value + "rd";
            default:
                return value + "th";
        }
    }
}
