package cz.cuni.mff.xrg.odcs.extractor.file;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsExtractor;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.module.file.FileManager;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import cz.cuni.mff.xrg.odcs.extractor.data.OrganizationRecord;
import cz.cuni.mff.xrg.odcs.extractor.data.RdfData;
import cz.cuni.mff.xrg.odcs.rdf.enums.FileExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.HandlerExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Vector;

/**
 * @author Jan Marcek
 */
@AsExtractor
public class FileCsvExtractor3 extends ConfigurableBase<FileCsvExtractorConfig>
        implements ConfigDialogProvider<FileCsvExtractorConfig> {

    public final static String ORGANIZATIONS_BASE_URI = "http://data.gov.sk/id/interior/organization/";
    public final static String OPENDATA_ORGANIZATIONS_CONTEXTS_KEY = "organizations";
    private final Logger LOG = LoggerFactory.getLogger(FileCsvExtractor3.class);
    @OutputDataUnit
    public RDFDataUnit outputRdfData;

    public FileCsvExtractor3() {
        super(FileCsvExtractorConfig.class);
    }

    @Override
    public void execute(DPUContext context) throws DataUnitException, DPUException {
        final String baseURI = "";
        final FileExtractType extractType = config.fileExtractType;
        final String path = config.Path;
        final String fileSuffix = config.FileSuffix;
        final boolean onlyThisSuffix = config.OnlyThisSuffix;

        boolean useStatisticHandler = config.UseStatisticalHandler;
        boolean failWhenErrors = config.failWhenErrors;

        final HandlerExtractType handlerExtractType = HandlerExtractType
                .getHandlerType(useStatisticHandler, failWhenErrors);


        RDFFormatType formatType = config.RDFFormatValue;
        File file = new File(path);
        TransformCsvToRdf transformCsvToRdf = new TransformCsvToRdf();
        Vector<OrganizationRecord> records = transformCsvToRdf.readCsvToOrganizationRecords(file.getAbsolutePath());

        FileManager fileManager = new FileManager(context);

        File fileRdf = null;

        try {
            RdfData rdfData = new RdfData(
                    transformCsvToRdf.serializeRepository(records),
                    ORGANIZATIONS_BASE_URI,
                    OPENDATA_ORGANIZATIONS_CONTEXTS_KEY);

            fileRdf = transformCsvToRdf.createRdf(rdfData.getRdfData());

        } catch (Exception e) {
            LOG.error("Problem while transforming csv to rdf", e);
        }
        final RDFFormat format = RDFFormatType.getRDFFormatByType(formatType);

        LOG.debug("extractType: {}", extractType);
        LOG.debug("format: {}", format);
        LOG.debug("path: {}", path);
        LOG.debug("fileSuffix: {}", fileSuffix);
        LOG.debug("baseURI: {}", baseURI);
        LOG.debug("onlyThisSuffix: {}", onlyThisSuffix);
        LOG.debug("useStatisticHandler: {}", useStatisticHandler);

        try {
            outputRdfData.extractFromFile(extractType, format, fileRdf.getAbsolutePath(), fileSuffix,
                    baseURI, onlyThisSuffix, handlerExtractType);
        } catch (RDFException e) {
            context.sendMessage(MessageType.ERROR, e.getMessage());
            throw new DPUException(e.getMessage(), e);
        }
        final long triplesCount = outputRdfData.getTripleCount();
        LOG.info("Extracted {} triples", triplesCount);
    }

    @Override
    public AbstractConfigDialog<FileCsvExtractorConfig> getConfigurationDialog() {
        return new FileCsvExtractorDialog();
    }

}