package cz.cuni.mff.xrg.odcs.dpu.filestolocaldirectoryloader;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.CopyOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;

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
public class FilesToLocalDirectoryLoader extends
        ConfigurableBase<FilesToLocalDirectoryLoaderConfig> implements
        ConfigDialogProvider<FilesToLocalDirectoryLoaderConfig> {
    private static final Logger LOG = LoggerFactory
            .getLogger(FilesToLocalDirectoryLoader.class);

    @DataUnit.AsInput(name = "filesInput")
    public FilesDataUnit filesInput;

    public FilesToLocalDirectoryLoader() {
        super(FilesToLocalDirectoryLoaderConfig.class);
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
        File destinationDirFile = new File(config.getDestination());
        destinationDirFile.mkdirs();
        String destinationAbsolutePath = destinationDirFile
                .getAbsolutePath();

        boolean moveFiles = config.isMoveFiles();
        ArrayList<CopyOption> copyOptions = new ArrayList<>(1);
        if (config.isReplaceExisting()) {
            copyOptions.add(StandardCopyOption.REPLACE_EXISTING);
        }
        CopyOption[] copyOptionsArray = copyOptions.toArray(new CopyOption[copyOptions.size()]);

        long index = 0L;
        boolean shouldContinue = !dpuContext.canceled();
        try {
            while ((shouldContinue)&& (filesIteration.hasNext())) {
                index++;

                FilesDataUnit.Entry entry;
                try {
                    entry = filesIteration.next();
                    Path inputPath = new File(URI.create(entry.getFileURIString())).toPath();
                    Path outputPath = new File(destinationAbsolutePath + '/'
                            + entry.getSymbolicName()).toPath();
                    try {
                        Date start = new Date();
                        if (dpuContext.isDebugging()) {
                            LOG.debug("Processing {} file {}", appendNumber(index), entry);
                        }
                        if (moveFiles) {
                            java.nio.file.Files.move(inputPath, outputPath, copyOptionsArray);
                        } else {
                            java.nio.file.Files.copy(inputPath, outputPath, copyOptionsArray);
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
        }
    }

    @Override
    public AbstractConfigDialog<FilesToLocalDirectoryLoaderConfig> getConfigurationDialog() {
        return new FilesToLocalDirectoryLoaderConfigDialog();
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
