package cz.cuni.mff.xrg.odcs.dpu.filestofilesmerger2transformer;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.NonConfigurableBase;
import cz.cuni.mff.xrg.odcs.files.FilesDataUnit;
import cz.cuni.mff.xrg.odcs.files.WritableFilesDataUnit;

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
        
        ((ManagableDataUnit) filesOutput).merge(filesInput);
    }
}
