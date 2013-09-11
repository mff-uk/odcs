package cz.cuni.mff.xrg.intlib.extractor.file;

import cz.cuni.xrg.intlib.commons.data.DataUnitException;
import cz.cuni.xrg.intlib.commons.dpu.DPUContext;
import cz.cuni.xrg.intlib.commons.dpu.annotation.AsExtractor;
import cz.cuni.xrg.intlib.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.xrg.intlib.commons.module.dpu.ConfigurableBase;
import cz.cuni.xrg.intlib.commons.web.AbstractConfigDialog;
import cz.cuni.xrg.intlib.commons.web.ConfigDialogProvider;
import cz.cuni.xrg.intlib.rdf.enums.FileExtractType;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataUnit;

/**
 * 
 * @author Jiri Tomes
 * @author Petyr
 */
@AsExtractor
public class FileExtractor extends ConfigurableBase<FileExtractorConfig>
		implements ConfigDialogProvider<FileExtractorConfig> {

	@OutputDataUnit
	public RDFDataUnit rdfDataUnit;

	public FileExtractor() {
		super(FileExtractorConfig.class);
	}

	@Override
	public void execute(DPUContext context) throws DataUnitException {

		final String baseURI = "";
		final FileExtractType extractType = config.fileExtractType;
		final String path = config.Path;
		final String fileSuffix = config.FileSuffix;
		final boolean onlyThisSuffix = config.OnlyThisSuffix;
		final boolean useStatisticHandler = config.UseStatisticalHandler;

		rdfDataUnit.extractFromFile(extractType, path, fileSuffix, baseURI,
				onlyThisSuffix, useStatisticHandler);
	}

	@Override
	public AbstractConfigDialog<FileExtractorConfig> getConfigurationDialog() {
		return new FileExtractorDialog();
	}
	
}
