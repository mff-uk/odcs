package eu.unifiedviews.plugins.transformer.filesmerger;

import eu.unifiedviews.dataunit.DataUnit;
import eu.unifiedviews.dpu.DPU;
import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.dataunit.files.FilesDataUnit;
import eu.unifiedviews.dataunit.files.WritableFilesDataUnit;

import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.helpers.dpu.NonConfigurableBase;

import eu.unifiedviews.commons.dataunit.ManagableDataUnit;

@DPU.AsTransformer
public class FilesMerger extends NonConfigurableBase {

    @DataUnit.AsInput(name = "filesInput")
    public FilesDataUnit filesInput;

    @DataUnit.AsOutput(name = "filesOutput")
    public WritableFilesDataUnit filesOutput;

    public FilesMerger() {
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
