package cz.cuni.mff.xrg.odcs.organizationExtractor.datanest;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import cz.cuni.mff.xrg.odcs.organizationExtractor.repository.FileSystemRepository;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.organizationExtractor.data.OrganizationRecord;
import cz.cuni.mff.xrg.odcs.organizationExtractor.serialization.OrganizationRdfSerializer;

public class OrganizationsDatanestHarvester extends AbstractDatanestHarvester<OrganizationRecord> {
    public final static String KEY_DATANEST_ORGANIZATIONS_URL_KEY = "datanest.organizations.url";
    protected final static int ATTR_INDEX_ID = 0;
    protected final static int ATTR_INDEX_NAME = 1;
    protected final static int ATTR_INDEX_SEAT = 4;
    protected final static int ATTR_INDEX_LEGAL_FORM = 5;
    protected final static int ATTR_INDEX_ICO = 2;
    protected final static int ATTR_INDEX_DATE_FROM = 8;
    protected final static int ATTR_INDEX_DATE_TO = 8;
    protected final static int ATTR_INDEX_SOURCE = 14;
    private static Logger logger = LoggerFactory.getLogger(OrganizationsDatanestHarvester.class);

    public OrganizationsDatanestHarvester(String targetRdf) throws ParserConfigurationException, TransformerConfigurationException, IOException {

        super(KEY_DATANEST_ORGANIZATIONS_URL_KEY);
        FileSystemRepository fileSystemRepository = FileSystemRepository.getInstance();
        fileSystemRepository.setTargetRDF(targetRdf);
        OrganizationRdfSerializer rdfSerializer = new OrganizationRdfSerializer(fileSystemRepository);
        addSerializer(rdfSerializer);

    }

    @Override
    public OrganizationRecord scrapOneRecord(String[] row) throws ParseException {
        OrganizationRecord record = new OrganizationRecord();
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
            Date dateTo = sdf.parse(row[ATTR_INDEX_DATE_TO]);
            record.setDateTo(dateTo);
        }

        logger.debug("scrapped record of: " + record.toString());

        return record;
    }
}
