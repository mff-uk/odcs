package cz.cuni.mff.xrg.odcs.dpu.filestofilesrenametransformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;

import cz.cuni.mff.xrg.odcs.commons.dpu.DPUCancelledException;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.NonConfigurableBase;

@DPU.AsTransformer
public class FilesToFilesRenameTransformer extends NonConfigurableBase {
    private static final Logger LOG = LoggerFactory.getLogger(FilesToFilesRenameTransformer.class);

    @DataUnit.AsInput(name = "filesInput")
    public FilesDataUnit filesInput;

    @DataUnit.AsOutput(name = "filesOutput")
    public WritableFilesDataUnit filesOutput;

    public FilesToFilesRenameTransformer() {
//        super(FilesToFilesRenameTransformerConfig.class);
    }

//    @Override
//    public AbstractConfigDialog<FilesToFilesRenameTransformerConfig> getConfigurationDialog() {
//        return new FilesToFilesRenameTransformerConfigDialog();
//    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException {
        //check that XSLT is available 

        String shortMessage = this.getClass().getSimpleName() + " starting.";
//        String longMessage = String.valueOf(config);
//        dpuContext.sendMessage(MessageType.INFO, shortMessage, longMessage);
      dpuContext.sendMessage(DPUContext.MessageType.INFO, shortMessage, "");

        FilesDataUnit.Iteration filesIteration;
        try {
            filesIteration = filesInput.getIteration();
        } catch (DataUnitException ex) {
            throw new DPUException("Could not obtain filesInput", ex);
        }
        long filesSuccessfulCount = 0L;
        long index = 0L;

        try {
            while (filesIteration.hasNext()) {
                checkCancelled(dpuContext);

                FilesDataUnit.Entry entry;
                try {
                    entry = filesIteration.next();
                    index++;

                    filesOutput.addExistingFile( entry.getSymbolicName() + ".ttl", entry.getFileURIString());
                    filesSuccessfulCount++;
                } catch (DataUnitException ex) {
                    dpuContext.sendMessage(
                            DPUContext.MessageType.ERROR,
                            "DataUnit exception.",
                            "",
                            ex);
                }
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
        String message = String.format("Processed %d/%d", filesSuccessfulCount, index);
        dpuContext.sendMessage(filesSuccessfulCount < index ? DPUContext.MessageType.WARNING : DPUContext.MessageType.INFO, message);
    }

    private void checkCancelled(DPUContext dpuContext) throws DPUCancelledException {
        if (dpuContext.canceled()) {
            throw new DPUCancelledException();
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
