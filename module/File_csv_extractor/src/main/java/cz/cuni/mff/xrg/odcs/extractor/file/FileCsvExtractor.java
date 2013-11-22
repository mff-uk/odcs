package cz.cuni.mff.xrg.odcs.extractor.file;

import au.com.bytecode.opencsv.CSVReader;
import cz.cuni.mff.xrg.odcs.commons.data.DataUnitException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.AsExtractor;
import cz.cuni.mff.xrg.odcs.commons.dpu.annotation.OutputDataUnit;
import cz.cuni.mff.xrg.odcs.commons.message.MessageType;
import cz.cuni.mff.xrg.odcs.commons.module.dpu.ConfigurableBase;
import cz.cuni.mff.xrg.odcs.commons.web.AbstractConfigDialog;
import cz.cuni.mff.xrg.odcs.commons.web.ConfigDialogProvider;
import cz.cuni.mff.xrg.odcs.extractor.data.OrganizationRecord;
import cz.cuni.mff.xrg.odcs.extractor.data.RdfData;
import cz.cuni.mff.xrg.odcs.extractor.data.SolrItem;
import cz.cuni.mff.xrg.odcs.rdf.enums.FileExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.HandlerExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import org.apache.commons.lang3.StringEscapeUtils;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Jiri Tomes
 * @author Petyr
 */
@AsExtractor
public class FileCsvExtractor extends ConfigurableBase<FileCsvExtractorConfig>
        implements ConfigDialogProvider<FileCsvExtractorConfig> {

    public final static String NS_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public final static String NS_DC = "http://purl.org/dc/elements/1.1/";
    public final static String NS_LOCN = "http://www.w3.org/ns/locn#";
    public final static String NS_ORG = "http://www.w3.org/ns/org#";
    public final static String NS_ROV = "http://www.w3.org/TR/vocab-regorg/";
    public final static String NS_SKOS = "http://www.w3.org/2004/02/skos/core#";
    public final static String NS_OPENDATA = "http://sk.eea.opendata/2011/02/opendicts#";
    public final static String TAG_NAME_SKOS_CONCEPT = "skos:Concept";
    public final static String OPENDATA_DATE_FORMAT = "dd.MM.yyyy";
    public final static String OPENDATA_COMBINED_REPO_NAME = "all";
    public final static String OPENDATA_COMBINED_BASE_URI = "http://opendata.sk/dataset/all/";
    public final static String ERR_CONVERSION = "unable to convert the data into RDF";
    public final static String NS_ADMS = "http://www.w3.org/ns/adms#";
    public final static String NS_DCTERMS = "http://purl.org/dc/terms/";
    // One thing should have one URI. For Slovak companies it maybe will be this one even officially.
    // URI might be understood this way:
    // a) Slovak government has a dataset (http://data.gov.sk/...)
    // b) where we're distinguishing real things by ID (.../id/...)
    // c) which (the dataset) is being curated by Ministry of Interior (../interior/...)
    // d) and contains information about organizations (.../organization/...)
    // e) distinguished by ICO (the last thing appended to base URI: .../<ico>)
    public final static String ORGANIZATIONS_BASE_URI = "http://data.gov.sk/id/interior/organization/";
    public final static String IDENTIFIERS_BASE_URI = "http://data.gov.sk/id/interior/identifier/";
    public final static String OPENDATA_ORGANIZATIONS_CONTEXTS_KEY = "organizations";
    public final static String TAG_NAME_ADMS_IDENTIFIER = "adms:Identifier";
    public final static String TAG_NAME_ORG_REGORG = "rov:RegisteredOrganization";
    public final static String ORG_SCHEMA_AGENCY = "Ministry of Interior, Slovak Republic";
    public final static String IDENTIFIERS_TYPE_URI = "http://data.gov.sk/def/interior/identifier/ico";
    public final static String KEY_DATANEST_ORGANIZATIONS_URL_KEY = "datanest.organizations.url";
    protected final static SimpleDateFormat sdf = new SimpleDateFormat(
            OPENDATA_DATE_FORMAT);
    protected final static int ATTR_INDEX_ID = 0;
    protected final static int ATTR_INDEX_NAME = 1;
    protected final static int ATTR_INDEX_SEAT = 4;
    protected final static int ATTR_INDEX_LEGAL_FORM = 5;
    protected final static int ATTR_INDEX_ICO = 2;
    protected final static int ATTR_INDEX_DATE_FROM = 8;
    protected final static int ATTR_INDEX_DATE_TO = 8;
    protected final static int ATTR_INDEX_SOURCE = 14;
    private static final int KEY_DATANEST_BATCH_SIZE = 1000;
    public final static String DATANEST_DATE_FORMAT = "yyyy-MM-dd";

    private final Logger LOG = LoggerFactory.getLogger(FileCsvExtractor.class);
    @OutputDataUnit
    public RDFDataUnit rdfDataUnit;

    public FileCsvExtractor() {
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
        Vector<OrganizationRecord> records = readCsvToClass(file.getAbsolutePath());
        System.out.println("execute");
        File fileRdf = null;
        try {
            RdfData rdfData = new RdfData(
                    serializeRepository(records),
                    ORGANIZATIONS_BASE_URI,
                    OPENDATA_ORGANIZATIONS_CONTEXTS_KEY);
            fileRdf = createRdf(rdfData.getRdfData());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
            rdfDataUnit.extractFromFile(extractType, format,  fileRdf.getAbsolutePath(), fileSuffix,
                    baseURI, onlyThisSuffix, handlerExtractType);
        } catch (RDFException e) {
            context.sendMessage(MessageType.ERROR, e.getMessage());
            throw new DPUException(e.getMessage(), e);
        }
        final long triplesCount = rdfDataUnit.getTripleCount();
        LOG.info("Extracted {} triples", triplesCount);
    }

    @Override
    public AbstractConfigDialog<FileCsvExtractorConfig> getConfigurationDialog() {
        return new FileCsvExtractorDialog();
    }

    private void addCustomRdfNsElements(Element rdfElement) {
        rdfElement.setAttribute("xmlns:adms", NS_ADMS);
        rdfElement.setAttribute("xmlns:dcterms", NS_DCTERMS);
    }

    private Element appendTextNode(Document doc, String name, String value) {
        Element element = doc.createElement(name);
        Text textNode = doc.createTextNode(value);
        element.appendChild(textNode);
        return element;
    }

    private Element appendResourceNode(Document doc, String name,
                                       String attr, String value) {

        Element element = doc.createElement(name);
        element.setAttribute(attr, value);
        return element;
    }

    private void serializeRecord(Document doc, Element rdfElement, OrganizationRecord record) {
        // *** organization ***
        Element concept1 = doc.createElement(TAG_NAME_ORG_REGORG);
        concept1.setAttribute("rdf:about", ORGANIZATIONS_BASE_URI + record.getIco());

        concept1.appendChild(appendTextNode(doc, "rov:legalName", record.getName()));
        concept1.appendChild(appendResourceNode(doc, "dc:source", "rdf:resource", record.getSource()));
        concept1.appendChild(appendTextNode(doc, "dc:type", record.getLegalForm()));
        if (record.getDateFrom() != null) {
            String dateFrom = sdf.format(record.getDateFrom());
            concept1.appendChild(appendTextNode(doc, "opendata:dateFrom", dateFrom));
        }
        if (record.getDateTo() != null) {
            String dateTo = sdf.format(record.getDateTo());
            concept1.appendChild(appendTextNode(doc, "opendata:dateTo", dateTo));
        }
        //concept.appendChild(appendTextNode(doc, "opendata:seat", record.getSeat()));
        Element fullAddress = appendTextNode(doc, "locn:fullAddress", record.getSeat());
        fullAddress.setAttribute("rdf:datatype", "xsd:string");
        Element address = doc.createElement("locn:address");
        // TODO: does the fullAddress have to contain also the organization name
        // (i.e. is it as written on envelope)?public final static String
        address.appendChild(fullAddress);
        // TODO: parse out PSC from full address
        //address.appendChild(appendTextNode(doc, "locn:postCode", record.getSeat()));
        Element primarySite = doc.createElement("org:registeredSite");
        primarySite.appendChild(address);
        concept1.appendChild(primarySite);
        concept1.appendChild(appendResourceNode(
                doc,
                "opendata:ico",
                "rdf:resource",
                IDENTIFIERS_BASE_URI + record.getIco()));

        rdfElement.appendChild(concept1);

        // *** organization's ICO ***
        Element concept2 = doc.createElement(TAG_NAME_ADMS_IDENTIFIER);
        concept2.setAttribute("rdf:about", IDENTIFIERS_BASE_URI + record.getIco());

        Element notation = appendTextNode(doc, "skos:notation", record.getIco());
        notation.setAttribute("rdf:datatype", "xsd:string");
        concept2.appendChild(notation);

        Element schemaAgency = appendTextNode(doc, "adms:schemaAgency", ORG_SCHEMA_AGENCY);
        schemaAgency.setAttribute("rdf:datatype", "xsd:string");
        concept2.appendChild(schemaAgency);

        concept2.appendChild(appendResourceNode(doc, "dcterms:type", "rdf:resource", IDENTIFIERS_TYPE_URI));

        rdfElement.appendChild(concept2);
    }

    private Vector<OrganizationRecord> readCsvToClass(String sourceFile) {
        Vector<OrganizationRecord> records = new Vector<OrganizationRecord>();
        try {
            // "open" the CSV dump
            CSVReader csvReader = new CSVReader(new BufferedReader(
                    new FileReader(sourceFile)));
            // TODO: If we store also the original copy of the data (say in
            // Jacrabbit) and perform a "diff" on that and previous version we can:
            // a) determine also removed records (which current implementation
            //    does not know to do)
            // b) determine new and updated records without downloading records
            //    for all IDs ...
            // c) ... instead noting only changed records in say vectors and
            //    processing only those (thus saving a LOT of resources assuming
            //    changes and additions are small and infrequent)


            // TODO: check the header - for now we simply skip it
            csvReader.readNext();
            int recordCounter = 0;
            long timeCurrent = -1;
            long timeStart = Calendar.getInstance().getTimeInMillis();
            int debugProcessOnlyNItems = 2;

            // read the rows
            String[] row;
            int batchSize = KEY_DATANEST_BATCH_SIZE;
            while ((row = csvReader.readNext()) != null) {
                try {
                    OrganizationRecord record = scrapOneRecord(row);
                    recordCounter++;

                    // clean-up data related to old record, if necessary

                    // add new data
                    records.add(record);
                } catch (ArrayIndexOutOfBoundsException e) {
                    // happens when connection with source server cuts
                    // prematurely - this will cause last fetched line of CSV to
                    // be incomplete
                    LOG.warn("index out of bound exception (broken connection?)", e);
                    LOG.warn("skipping following record: "
                            + Arrays.deepToString(row));
                } catch (ParseException e) {
                    LOG.warn("parse exception", e);
                    LOG.warn("skipping following record: "
                            + Arrays.deepToString(row));
                }

                if (records.size() >= batchSize) {
                    //store(records);

                    // report current harvesting status
                    timeCurrent = Calendar.getInstance().getTimeInMillis();
                    float harvestingSpeed = 1000f * (float) recordCounter
                            / (float) (timeCurrent - timeStart);
                    LOG.info("harvested " + recordCounter + " records ("
                            + harvestingSpeed + "/s) so far ...");

                    records.clear();
                }

                if (debugProcessOnlyNItems > 0 &&
                        recordCounter >= debugProcessOnlyNItems)
                    break;
            }

            // store the results
            //store(records);

            csvReader.close();

            // TODO: If there wont be any more specialized error handling here
            // in the future, try catching only 'Exception' to simplify the
            // code.
        } catch (MalformedURLException e) {
            LOG.error("malformed URL exception", e);
        } catch (IOException e) {
            LOG.error("IO exception", e);
        }
        return records;

    }

    private OrganizationRecord scrapOneRecord(String[] row) throws ParseException {
        OrganizationRecord record = new OrganizationRecord();
        //TODO
        SimpleDateFormat sdf = new SimpleDateFormat(DATANEST_DATE_FORMAT);
        record.setId("org_" + row[ATTR_INDEX_ID]);
        record.setDatanestId(row[ATTR_INDEX_ID]);
        record.setSource(row[ATTR_INDEX_SOURCE]);
        record.setName(StringEscapeUtils.escapeXml(row[ATTR_INDEX_NAME]));
        record.setLegalForm(row[ATTR_INDEX_LEGAL_FORM]);
        record.setSeat(row[ATTR_INDEX_SEAT]);
        record.setIco(row[ATTR_INDEX_ICO]);
        Date dateFrom;
        try {
            dateFrom = sdf.parse(row[ATTR_INDEX_DATE_FROM]);
        } catch (Exception e) {
            dateFrom = null;

        }
        record.setDateFrom(dateFrom);

        if (!row[ATTR_INDEX_DATE_TO].isEmpty()) {
            try {
                Date dateTo = sdf.parse(row[ATTR_INDEX_DATE_TO]);
                record.setDateTo(dateTo);
            } catch (Exception e) {
                LOG.error("Exception exception", e);

            }
        }

        LOG.debug("scrapped record of: " + record.getName());

        return record;
    }

    private File createRdf(String rdfData) {
        File dumpFile = null;
        try {
            dumpFile = File.createTempFile("odn-", ".rdf");
            BufferedWriter out = new BufferedWriter(new FileWriter(dumpFile));
            out.write(rdfData);
            out.close();
            LOG.info("RDF dump saved to file " + dumpFile);
        } catch (IOException e) {
            LOG.error("IO exception", e);
        }
        return dumpFile;
    }

    public List<SolrItem> serializeSerializer(List<OrganizationRecord> records)
            throws Exception {

        Vector<SolrItem> solrItems = new Vector<SolrItem>(records.size());
        for (OrganizationRecord record : records) {
            SolrItem solrItem = SolrItem.createSolrItem(record);
            solrItems.add(solrItem);
        }

        return solrItems;
    }

    private String serializeRepository(List<OrganizationRecord> records)
            throws Exception {

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        TransformerFactory transformerFactory = TransformerFactory
                .newInstance();
        Transformer transformer = transformerFactory.newTransformer();


        Element rdfElement = doc.createElementNS(NS_RDF, "rdf:RDF");
        rdfElement.setAttribute("xmlns:rdf", NS_RDF);
        rdfElement.setAttribute("xmlns:dc", NS_DC);
        rdfElement.setAttribute("xmlns:locn", NS_LOCN);
        rdfElement.setAttribute("xmlns:org", NS_ORG);
        rdfElement.setAttribute("xmlns:rov", NS_ROV);
        rdfElement.setAttribute("xmlns:skos", NS_SKOS);
        rdfElement.setAttribute("xmlns:opendata", NS_OPENDATA);
        addCustomRdfNsElements(rdfElement);
        doc.appendChild(rdfElement);

        for (OrganizationRecord record : records)
            serializeRecord(doc, rdfElement, record);

        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = new DOMSource(doc);
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            throw new Exception(e.getMessage(), e);
        }

        return sw.toString();
    }
}