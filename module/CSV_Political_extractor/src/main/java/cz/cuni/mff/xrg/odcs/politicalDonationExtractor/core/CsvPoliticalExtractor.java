package cz.cuni.mff.xrg.odcs.politicalDonationExtractor.core;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsExtractor;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.module.file.FileManager;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import cz.cuni.mff.xrg.odcs.politicalDonationExtractor.datanest.AbstractDatanestHarvester;
import cz.cuni.mff.xrg.odcs.politicalDonationExtractor.datanest.PoliticalPartyDonationsDatanestHarvester;
import cz.cuni.mff.xrg.odcs.rdf.enums.FileExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.HandlerExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;

/**
 * @author Jan Marcek
 */
@AsExtractor
public class CsvPoliticalExtractor extends ConfigurableBase<CsvPoliticalExtractorConfig> implements ConfigDialogProvider<CsvPoliticalExtractorConfig> {

    public final static String ORGANIZATIONS_BASE_URI = "http://data.gov.sk/id/interior/organization/";
    public final static String OPENDATA_ORGANIZATIONS_CONTEXTS_KEY = "organizations";
    private final Logger LOG = LoggerFactory.getLogger(CsvPoliticalExtractor.class);

    @OutputDataUnit
    public RDFDataUnit outputRdfData;

    public CsvPoliticalExtractor() {
        super(CsvPoliticalExtractorConfig.class);
    }

    @Override
    public void execute(DPUContext context) throws DataUnitException, DPUException {
        final String baseURI = "";
        final FileExtractType extractType = config.fileExtractType;
        final String sourceCSV = config.Path;
        final String targetRdf = config.TargetRDF;
        final Integer batchSize = config.BatchSize;
        final Integer debugProcessOnlyNItems = config.DebugProcessOnlyNItems;
        final String fileSuffix = config.FileSuffix;
        final boolean onlyThisSuffix = config.OnlyThisSuffix;
        final RDFFormatType formatType = config.RDFFormatValue;

        boolean useStatisticHandler = config.UseStatisticalHandler;
        boolean failWhenErrors = config.failWhenErrors;

        final HandlerExtractType handlerExtractType = HandlerExtractType.getHandlerType(useStatisticHandler, failWhenErrors);

        AbstractDatanestHarvester<?> harvester = null;
        URL url = null;
        try {
            url = new URL(sourceCSV);
        } catch (MalformedURLException e) {
            LOG.error("Error occoured when path: " + sourceCSV + " was parsing.", e);
        } catch (IOException e) {
            LOG.error("Error occoured when path: " + sourceCSV + " was parsing.", e);
        }

        LOG.info("create ORGANIZATION harvester");

        try {
            harvester = new PoliticalPartyDonationsDatanestHarvester(targetRdf);
            harvester.setDebugProcessOnlyNItems(debugProcessOnlyNItems);
            harvester.setBatchSize(batchSize);
            harvester.setSourceUrl(url);

        } catch (Exception e) {
            LOG.error("Problem.", e);
        }

        if (harvester != null) {
            try {
                harvester.update();
            } catch (Exception e) {
                LOG.error("Problem while transforming csv to rdf", e);
            }
            FileManager fileManager = new FileManager(context);

        }
    }

    @Override
    public AbstractConfigDialog<CsvPoliticalExtractorConfig> getConfigurationDialog() {
        return new CsvPoliticalExtractorDialog();
    }

}
