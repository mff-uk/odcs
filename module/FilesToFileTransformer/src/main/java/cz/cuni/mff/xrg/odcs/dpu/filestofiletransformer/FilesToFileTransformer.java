package cz.cuni.mff.xrg.odcs.dpu.filestofiletransformer;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Iterator;

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
import cz.cuni.mff.xrg.odcs.dataunit.file.handlers.DirectoryHandler;
import cz.cuni.mff.xrg.odcs.dataunit.file.handlers.FileHandler;
import cz.cuni.mff.xrg.odcs.dataunit.file.options.OptionsAdd;
import cz.cuni.mff.xrg.odcs.files.FilesDataUnit;
import cz.cuni.mff.xrg.odcs.files.FilesDataUnit.FilesDataUnitEntry;
import cz.cuni.mff.xrg.odcs.files.FilesDataUnit.FilesIteration;

@AsTransformer
public class FilesToFileTransformer extends NonConfigurableBase {
    private static final Logger LOG = LoggerFactory.getLogger(FilesToFileTransformer.class);

    @InputDataUnit(name = "filesInput")
    public FilesDataUnit filesInput;

    @OutputDataUnit(name = "fileOutput")
    public FileDataUnit fileOutput;

    public FilesToFileTransformer() {
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, DataUnitException, InterruptedException {
        String shortMessage = this.getClass().getName() + " starting.";
        dpuContext.sendMessage(MessageType.INFO, shortMessage);
        LOG.info(shortMessage);

        FilesIteration filesIteration = filesInput.getFiles();
        try {
            while (filesIteration.hasNext()) {
                checkCancelled(dpuContext);

                FilesDataUnitEntry entry = filesIteration.next();
                String inSymbolicName = entry.getSymbolicName();

                if (dpuContext.isDebugging()) {
                    LOG.debug("Adding symbolic name {} file URI {}", inSymbolicName, entry.getFilesystemURI());
                }

                Path p = FileSystems.getDefault().getPath(inSymbolicName);
                Iterator<Path> it = p.iterator();
                DirectoryHandler currentHandler = fileOutput.getRootDir();
                while (it.hasNext()) {
                    String next = it.next().toString();
                    boolean lastOne = !it.hasNext();
                    if (lastOne) {
                        FileHandler result = currentHandler.addExistingFile(new File(entry.getFilesystemURI()), new OptionsAdd(true, false));
                        if (dpuContext.isDebugging()) {
                            LOG.debug("Added {}", result.getRootedPath());
                        }
                    } else {
                        currentHandler = currentHandler.addNewDirectory(next);
                    }
                }
            }
        } finally {
            filesIteration.close();
        }
    }

    private void checkCancelled(DPUContext dpuContext) throws DPUCancelledException {
        if (dpuContext.canceled()) {
            throw new DPUCancelledException();
        }
    }
}
