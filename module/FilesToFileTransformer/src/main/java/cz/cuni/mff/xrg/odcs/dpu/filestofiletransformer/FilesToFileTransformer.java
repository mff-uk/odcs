package cz.cuni.mff.xrg.odcs.dpu.filestofiletransformer;

import java.io.File;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUCancelledException;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.NonConfigurableBase;
import cz.cuni.mff.xrg.odcs.dataunit.file.FileDataUnit;
import cz.cuni.mff.xrg.odcs.dataunit.file.handlers.DirectoryHandler;
import cz.cuni.mff.xrg.odcs.dataunit.file.handlers.FileHandler;
import cz.cuni.mff.xrg.odcs.dataunit.file.options.OptionsAdd;

@DPU.AsTransformer
public class FilesToFileTransformer extends NonConfigurableBase {

    private static final Logger LOG = LoggerFactory.getLogger(FilesToFileTransformer.class);

    @DataUnit.AsInput(name = "filesInput")
    public FilesDataUnit filesInput;

    @DataUnit.AsOutput(name = "fileOutput")
    public FileDataUnit fileOutput;

    public FilesToFileTransformer() {
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, InterruptedException {
        String shortMessage = this.getClass().getSimpleName() + " starting.";
        dpuContext.sendMessage(DPUContext.MessageType.INFO, shortMessage);

        FilesDataUnit.Iteration filesIteration = null;
        try {
            filesIteration = filesInput.getFiles();
            while (filesIteration.hasNext()) {
                checkCancelled(dpuContext);

                FilesDataUnit.Entry entry = filesIteration.next();
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
                        FileHandler result = currentHandler.addExistingFile(new File(URI.create(entry.getFilesystemURI())), new OptionsAdd(true, false));
                        if (dpuContext.isDebugging()) {
                            LOG.debug("Added {}", result.getRootedPath());
                        }
                    } else {
                        currentHandler = currentHandler.addNewDirectory(next);
                    }
                }
            }
        } catch (DataUnitException ex) {
            throw new DPUException(ex.getMessage(), ex.getCause());
        } finally {
            if (filesIteration != null) {
                try {
                    filesIteration.close();
                } catch (DataUnitException ex) {
                    throw new DPUException(ex.getMessage(), ex.getCause());
                }
            }
        }
    }

    private void checkCancelled(DPUContext dpuContext) throws DPUCancelledException {
        if (dpuContext.canceled()) {
            throw new DPUCancelledException();
        }
    }
}
