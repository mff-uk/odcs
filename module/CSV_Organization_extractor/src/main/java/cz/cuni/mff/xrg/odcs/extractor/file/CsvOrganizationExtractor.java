package cz.cuni.mff.xrg.odcs.extractor.file;

import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsExtractor;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.module.file.FileManager;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import cz.cuni.mff.xrg.odcs.extractor.data.AbstractRecord;
import cz.cuni.mff.xrg.odcs.extractor.datanest.AbstractDatanestHarvester;
import cz.cuni.mff.xrg.odcs.extractor.datanest.OrganizationsDatanestHarvester;
import cz.cuni.mff.xrg.odcs.rdf.enums.FileExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.HandlerExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

/**
 * @author Jan Marcek
 */
//TODO change name of a class. Replace the "extractor" with something more appropriate

@AsExtractor
public class CsvOrganizationExtractor extends ConfigurableBase<CsvOrganizationExtractorConfig>
        implements ConfigDialogProvider<CsvOrganizationExtractorConfig> {

    public final static String ORGANIZATIONS_BASE_URI = "http://data.gov.sk/id/interior/organization/";
    public final static String OPENDATA_ORGANIZATIONS_CONTEXTS_KEY = "organizations";
    private final Logger LOG = LoggerFactory.getLogger(CsvOrganizationExtractor.class);

    @OutputDataUnit
    public RDFDataUnit outputRdfData;

    public CsvOrganizationExtractor() {
        super(CsvOrganizationExtractorConfig.class);
    }

    @Override
    public void execute(DPUContext context) throws DataUnitException, DPUException {
        final String baseURI = "";
        final FileExtractType extractType = config.fileExtractType;

        String path = null;
        Properties prop = new Properties();
        try {
            //load a properties file from class path, inside static method
            prop.load(CsvOrganizationExtractor.class.getClassLoader().getResourceAsStream("config.properties"));
            //get the property value and print it out
            path = prop.getProperty("sourceCSV");
            LOG.debug("sourceCSV is: " + path);

        } catch (IOException e) {
            LOG.error("error was occoured while it was reading property file", e);
        }


        final String fileSuffix = config.FileSuffix;
        final boolean onlyThisSuffix = config.OnlyThisSuffix;

        boolean useStatisticHandler = config.UseStatisticalHandler;
        boolean failWhenErrors = config.failWhenErrors;

        final HandlerExtractType handlerExtractType = HandlerExtractType
                .getHandlerType(useStatisticHandler, failWhenErrors);


        RDFFormatType formatType = config.RDFFormatValue;

        File file = new File(path);
        String filename = file.getName();

        AbstractDatanestHarvester<?> harvester = null;

        LOG.info("create ORGANIZATION harvester");
        try {
            harvester = new OrganizationsDatanestHarvester();
        } catch (Exception e) {
            LOG.error("Problem.", e);
        }

        if (harvester != null) {
            try {
                Vector<? extends AbstractRecord> records = harvester.performEtl(file);
            } catch (Exception e) {
                LOG.error("Problem while transforming csv to rdf", e);
            }
            FileManager fileManager = new FileManager(context);

        }
    }

    @Override
    public AbstractConfigDialog<CsvOrganizationExtractorConfig> getConfigurationDialog() {
        return new CsvOrganizationExtractorDialog();
    }

}