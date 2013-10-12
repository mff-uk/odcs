package cz.cuni.mff.xrg.odcs.loader.file;

import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsLoader;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.InputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.web.*;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;

/**
 * @author Jiri Tomes
 * @author Petyr
 */
@AsLoader
public class FileLoader extends ConfigurableBase<FileLoaderConfig>
		implements ConfigDialogProvider<FileLoaderConfig> {

	@InputDataUnit
	public RDFDataUnit rdfDataUnit;

	public FileLoader() {
		super(FileLoaderConfig.class);
	}

	@Override
	public void execute(DPUContext context) throws DPUException {

		final String filePath = config.FilePath;
		final RDFFormatType formatType = config.RDFFileFormat;
		final boolean isNameUnique = config.DiffName;
		final boolean canFileOverwritte = true;

		try {
			rdfDataUnit.loadToFile(filePath, formatType,
					canFileOverwritte, isNameUnique);
		} catch (RDFException | CannotOverwriteFileException ex) {
			throw new DPUException(ex);
		}
	}

	@Override
	public AbstractConfigDialog<FileLoaderConfig> getConfigurationDialog() {
		return new FileLoaderDialog();
	}
	
}
