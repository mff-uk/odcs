package cz.cuni.mff.xrg.odcs.dpu.fileuploader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsExtractor;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import cz.cuni.mff.xrg.odcs.files.WritableFilesDataUnit;

@AsExtractor
public class FileUploader extends ConfigurableBase<FileUploaderConfig> implements ConfigDialogProvider<FileUploaderConfig> {
    private static final Logger LOG = LoggerFactory.getLogger(FileUploader.class);

    @OutputDataUnit(name = "fileOutput")
    public WritableFilesDataUnit fileOutput;

    public FileUploader() {
        super(FileUploaderConfig.class);
    }

    @Override
    public void execute(DPUContext dpuContext) throws DPUException, DataUnitException, InterruptedException {
    }

    @Override
    public AbstractConfigDialog<FileUploaderConfig> getConfigurationDialog() {
        return new FileUploaderConfigDialog();
    }
}
