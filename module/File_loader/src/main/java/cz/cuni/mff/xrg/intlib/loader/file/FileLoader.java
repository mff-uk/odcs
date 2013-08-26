package cz.cuni.mff.xrg.intlib.loader.file;

import cz.cuni.xrg.intlib.commons.data.DataUnitException;
import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.commons.loader.LoadContext;
import cz.cuni.xrg.intlib.commons.loader.LoadException;
import cz.cuni.xrg.intlib.commons.module.data.DataUnitList;
import cz.cuni.xrg.intlib.commons.module.data.RDFDataUnitList;
import cz.cuni.xrg.intlib.commons.module.dpu.ConfigurableBase;
import cz.cuni.xrg.intlib.commons.web.*;
import cz.cuni.xrg.intlib.rdf.data.RDFDataUnit;
import cz.cuni.xrg.intlib.rdf.enums.RDFFormatType;
import cz.cuni.xrg.intlib.rdf.exceptions.CannotOverwriteFileException;
import cz.cuni.xrg.intlib.rdf.exceptions.RDFDataUnitException;

/**
 * @author Jiri Tomes
 * @author Petyr
 */
public class FileLoader extends ConfigurableBase<FileLoaderConfig>
		implements Load, ConfigDialogProvider<FileLoaderConfig> {

	public FileLoader() {
		super(new FileLoaderConfig());
	}

	@Override
	public void load(LoadContext context)
			throws LoadException,
			DataUnitException {
		DataUnitList<RDFDataUnit> dataUnitList = RDFDataUnitList
				.create(context);

		RDFDataUnit rdfDataUnit;
		
		if (dataUnitList.filterByName("input").isEmpty()) {
			// no named  use first
			rdfDataUnit = dataUnitList.getFirst();
		} else {
			// there is DU with name input use it!
			rdfDataUnit = dataUnitList.filterByName("input").getFirst();
		}

		final String directoryPath = config.DirectoryPath;
		final String fileName = config.FileName;
		final RDFFormatType formatType = config.RDFFileFormat;
		final boolean isNameUnique = config.DiffName;
		final boolean canFileOverwritte = true;

		try {
			rdfDataUnit.saveTriplesToFile(directoryPath, fileName, formatType,
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
