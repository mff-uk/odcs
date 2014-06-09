package cz.cuni.mff.xrg.odcs.dpu.filedataunittofilesdataunit;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUCancelledException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsTransformer;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.InputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.NonConfigurableBase;
import cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnit;
import cz.cuni.mff.xrg.odcs.dataunit.file.handlers.FileHandler;
import cz.cuni.mff.xrg.odcs.files.WritableFilesDataUnit;

@AsTransformer
public class FileDataUnitToFilesDataUnit extends NonConfigurableBase {
    private static final Logger LOG = LoggerFactory.getLogger(FileDataUnitToFilesDataUnit.class);

    @InputDataUnit(name = "fileInput")
    public FileDataUnit fileInput;

    @OutputDataUnit(name = "filesOutput")
    public WritableFilesDataUnit filesOutput;

    public FileDataUnitToFilesDataUnit() {
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, DataUnitException, InterruptedException {
        String shortMessage = this.getClass().getName() + " starting.";
        dpuContext.sendMessage(MessageType.INFO, shortMessage);
        LOG.info(shortMessage);

        FileDataUnitOnlyFilesIterator fileInputIterator = new FileDataUnitOnlyFilesIterator(fileInput.getRootDir());
        while (fileInputIterator.hasNext()) {
            checkCancelled(dpuContext);

            FileHandler handlerItem = fileInputIterator.next();
            String canonicalPath;
            try {
                canonicalPath = handlerItem.asFile().getCanonicalPath();
                filesOutput.addExistingFile(canonicalPath, canonicalPath);
                if (dpuContext.isDebugging()) {
                    LOG.trace("Added symbolic name " + canonicalPath + " path URI " + canonicalPath + " to destination data unit.");
                }
            } catch (IOException ex) {
                dpuContext.sendMessage(MessageType.ERROR, "Error when adding.", "Handler item rooted path: " + handlerItem.getRootedPath(), ex);
            }
        }
    }

    private void checkCancelled(DPUContext dpuContext) throws DPUCancelledException {
        if (dpuContext.canceled()) {
            throw new DPUCancelledException();
        }
    }
}
