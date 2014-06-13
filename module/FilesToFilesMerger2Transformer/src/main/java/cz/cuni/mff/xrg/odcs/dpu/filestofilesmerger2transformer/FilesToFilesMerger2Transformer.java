package cz.cuni.mff.xrg.odcs.dpu.filestofilesmerger2transformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.data.ManagableDataUnit;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsTransformer;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.InputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.NonConfigurableBase;
import cz.cuni.mff.xrg.odcs.files.FilesDataUnit;
import cz.cuni.mff.xrg.odcs.files.WritableFilesDataUnit;

@AsTransformer
public class FilesToFilesMerger2Transformer extends NonConfigurableBase {
    private static final Logger LOG = LoggerFactory.getLogger(FilesToFilesMerger2Transformer.class);

    @InputDataUnit(name = "filesInput")
    public FilesDataUnit filesInput;

    @OutputDataUnit(name = "filesOutput")
    public WritableFilesDataUnit filesOutput;

    public FilesToFilesMerger2Transformer() {
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, DataUnitException, InterruptedException {
        String shortMessage = this.getClass().getSimpleName() + " starting.";
        dpuContext.sendMessage(MessageType.INFO, shortMessage);
        LOG.info(shortMessage);
        
        ((ManagableDataUnit) filesOutput).merge(filesInput);
    }
}
