package cz.cuni.mff.xrg.intlib.loader.file;

import cz.cuni.xrg.intlib.commons.dpu.annotation.InputDataUnit;
import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.commons.loader.LoadContext;
import cz.cuni.xrg.intlib.commons.loader.LoadException;
import cz.cuni.xrg.intlib.commons.module.dpu.ConfigurableBase;
import cz.cuni.xrg.intlib.commons.web.*;
import cz.cuni.xrg.intlib.rdf.enums.RDFFormatType;
import cz.cuni.xrg.intlib.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.xrg.intlib.rdf.exceptions.RDFDataUnitException;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataUnit;

/**
 * @author Jiri Tomes
 * @author Petyr
 */
public class FileLoader extends ConfigurableBase<FileLoaderConfig>
		implements Load, ConfigDialogProvider<FileLoaderConfig> {

	@InputDataUnit
	public RDFDataUnit rdfDataUnit;

	public FileLoader() {
		super(FileLoaderConfig.class);
	}

	@Override
	public void load(LoadContext context) throws LoadException {

		final String directoryPath = config.DirectoryPath;
		final String fileName = config.FileName;
		final RDFFormatType formatType = config.RDFFileFormat;
		final boolean isNameUnique = config.DiffName;
		final boolean canFileOverwritte = true;

		try {
			rdfDataUnit.loadToFile(directoryPath, fileName, formatType,
					canFileOverwritte, isNameUnique);
		} catch (RDFDataUnitException | CannotOverwriteFileException ex) {
			throw new LoadException(ex);
		}
	}

	@Override
	public AbstractConfigDialog<FileLoaderConfig> getConfigurationDialog() {
		return new FileLoaderDialog();
	}
}
