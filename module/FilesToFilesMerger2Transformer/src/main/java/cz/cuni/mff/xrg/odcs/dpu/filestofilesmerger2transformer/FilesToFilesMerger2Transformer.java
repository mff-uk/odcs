package cz.cuni.mff.xrg.odcs.dpu.filestofilesmerger2transformer;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;
import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.NonConfigurableBase;
import eu.unifiedviews.dataunit.DataUnitException;
import java.util.logging.Level;
import java.util.logging.Logger;

@DPU.AsTransformer
public class FilesToFilesMerger2Transformer extends NonConfigurableBase {

    @DataUnit.AsInput(name = "filesInput")
    public FilesDataUnit filesInput;

    @DataUnit.AsOutput(name = "filesOutput")
    public WritableFilesDataUnit filesOutput;

    public FilesToFilesMerger2Transformer() {
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, InterruptedException {
        String shortMessage = this.getClass().getSimpleName() + " starting.";
        dpuContext.sendMessage(DPUContext.MessageType.INFO, shortMessage);
        
        try {
            ((ManagableDataUnit) filesOutput).merge(filesInput);
        } catch (DataUnitException ex) {
            throw new DPUException(ex);
        }
    }
}
