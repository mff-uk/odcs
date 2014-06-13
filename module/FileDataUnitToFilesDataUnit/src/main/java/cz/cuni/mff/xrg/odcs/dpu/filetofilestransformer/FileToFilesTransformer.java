package cz.cuni.mff.xrg.odcs.dpu.filetofilestransformer;

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
public class FileToFilesTransformer extends NonConfigurableBase {
    private static final Logger LOG = LoggerFactory.getLogger(FileToFilesTransformer.class);

    @InputDataUnit(name = "fileInput")
    public FileDataUnit fileInput;

    @OutputDataUnit(name = "filesOutput")
    public WritableFilesDataUnit filesOutput;

    public FileToFilesTransformer() {
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, DataUnitException, InterruptedException {
        String shortMessage = this.getClass().getName() + " starting.";
        dpuContext.sendMessage(MessageType.INFO, shortMessage);
        LOG.info(shortMessage);

        FileDataUnitOnlyFilesIterator fileInputIterator = new FileDataUnitOnlyFilesIterator(fileInput.getRootDir());
        long all = 0L;
        while (fileInputIterator.hasNext()) {
        	all++;
            checkCancelled(dpuContext);

            FileHandler handlerItem = fileInputIterator.next();
            String canonicalPath;
            try {
                canonicalPath = handlerItem.asFile().getCanonicalPath();
                filesOutput.addExistingFile(handlerItem.getRootedPath(), canonicalPath);
                if (dpuContext.isDebugging()) {
                    LOG.trace("Added #" + String.valueOf(all) + " symbolic name " + handlerItem.getRootedPath() + " path URI " + canonicalPath + " to destination data unit.");
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
