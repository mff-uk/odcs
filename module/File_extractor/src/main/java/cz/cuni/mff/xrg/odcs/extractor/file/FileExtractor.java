package cz.cuni.mff.xrg.odcs.extractor.file;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsExtractor;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import cz.cuni.mff.xrg.odcs.rdf.enums.FileExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;

import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jiri Tomes
 * @author Petyr
 */
@AsExtractor
public class FileExtractor extends ConfigurableBase<FileExtractorConfig>
		implements ConfigDialogProvider<FileExtractorConfig> {

	private final Logger LOG = LoggerFactory.getLogger(FileExtractor.class);

	@OutputDataUnit
	public RDFDataUnit rdfDataUnit;

	public FileExtractor() {
		super(FileExtractorConfig.class);
	}

	@Override
	public void execute(DPUContext context) throws DataUnitException, DPUException {

		final String baseURI = "";
		final FileExtractType extractType = config.fileExtractType;
		final String path = config.Path;
		final String fileSuffix = config.FileSuffix;
		final boolean onlyThisSuffix = config.OnlyThisSuffix;
		final boolean useStatisticHandler = config.UseStatisticalHandler;

		RDFFormatType formatType = config.RDFFormatValue;
		final RDFFormat format = RDFFormatType.getRDFFormatByType(formatType);

		LOG.debug("extractType: {}", extractType);
		LOG.debug("format: {}", format);
		LOG.debug("path: {}", path);
		LOG.debug("fileSuffix: {}", fileSuffix);
		LOG.debug("baseURI: {}", baseURI);
		LOG.debug("onlyThisSuffix: {}", onlyThisSuffix);
		LOG.debug("useStatisticHandler: {}", useStatisticHandler);

		try {
			rdfDataUnit.extractFromFile(extractType, format, path, fileSuffix,
					baseURI, onlyThisSuffix, useStatisticHandler);
		} catch (RDFException e) {
			context.sendMessage(MessageType.ERROR, e.getMessage());
			throw new DPUException(e.getMessage(), e);
		}
		final long triplesCount = rdfDataUnit.getTripleCount();
		LOG.info("Extracted {} triples", triplesCount);
	}

	@Override
	public AbstractConfigDialog<FileExtractorConfig> getConfigurationDialog() {
		return new FileExtractorDialog();
	}
}
