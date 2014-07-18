package cz.cuni.mff.xrg.odcs.dpu.filetofilestransformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.module.dpu.NonConfigurableBase;
import cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnit;
import cz.cuni.mff.xrg.odcs.dataunit.file.handlers.FileHandler;
import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;

@DPU.AsTransformer
public class FileToFilesTransformer extends NonConfigurableBase {
    private static final Logger LOG = LoggerFactory.getLogger(FileToFilesTransformer.class);

    @DataUnit.AsInput(name = "fileInput")
    public FileDataUnit fileInput;

    @DataUnit.AsOutput(name = "filesOutput")
    public WritableFilesDataUnit filesOutput;

    public FileToFilesTransformer() {
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, InterruptedException {
        String shortMessage = this.getClass().getSimpleName() + " starting.";
        dpuContext.sendMessage(DPUContext.MessageType.INFO, shortMessage);

        FileDataUnitOnlyFilesIterator fileInputIterator = new FileDataUnitOnlyFilesIterator(fileInput.getRootDir());
        long index = 0L;
        boolean shouldContinue = !dpuContext.canceled();
        while ((shouldContinue) && (fileInputIterator.hasNext())) {
            index++;

            FileHandler handlerItem = fileInputIterator.next();
            String canonicalPath;
            canonicalPath = handlerItem.asFile().toURI().toASCIIString();
            try {
                filesOutput.addExistingFile(handlerItem.getRootedPath(), canonicalPath);
            } catch (DataUnitException ex) {
                throw new DPUException(ex.getMessage(), ex.getCause());
            }
            if (dpuContext.isDebugging()) {
                LOG.trace("Added " + appendNumber(index) + " symbolic name " + handlerItem.getRootedPath() + " path URI " + canonicalPath + " to destination data unit.");
            }
            shouldContinue = !dpuContext.canceled();
        }
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
