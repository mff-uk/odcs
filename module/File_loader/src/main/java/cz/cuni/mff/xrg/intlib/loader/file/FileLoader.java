package cz.cuni.mff.xrg.intlib.loader.file;

import cz.cuni.xrg.intlib.commons.dpu.DPU;
import cz.cuni.xrg.intlib.commons.dpu.DPUContext;
import cz.cuni.xrg.intlib.commons.dpu.DPUException;
import cz.cuni.xrg.intlib.commons.dpu.annotation.AsLoader;
import cz.cuni.xrg.intlib.commons.dpu.annotation.InputDataUnit;
import cz.cuni.xrg.intlib.commons.module.dpu.ConfigurableBase;
import cz.cuni.xrg.intlib.commons.web.*;
import cz.cuni.xrg.intlib.rdf.enums.RDFFormatType;
import cz.cuni.xrg.intlib.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.xrg.intlib.rdf.exceptions.RDFException;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataUnit;

/**
 * @author Jiri Tomes
 * @author Petyr
 */
@AsLoader
public class FileLoader extends ConfigurableBase<FileLoaderConfig>
		implements DPU, ConfigDialogProvider<FileLoaderConfig> {

	@InputDataUnit
	public RDFDataUnit rdfDataUnit;

	public FileLoader() {
		super(FileLoaderConfig.class);
	}

	@Override
	public void execute(DPUContext context) throws DPUException {

		final String directoryPath = config.DirectoryPath;
		final String fileName = config.FileName;
		final RDFFormatType formatType = config.RDFFileFormat;
		final boolean isNameUnique = config.DiffName;
		final boolean canFileOverwritte = true;

		try {
			rdfDataUnit.loadToFile(directoryPath, fileName, formatType,
					canFileOverwritte, isNameUnique);
		} catch (RDFException | CannotOverwriteFileException ex) {
			throw new DPUException(ex);
		}
	}

	@Override
	public AbstractConfigDialog<FileLoaderConfig> getConfigurationDialog() {
		return new FileLoaderDialog();
	}

	@Override
	public void cleanUp() {	}
	
}
