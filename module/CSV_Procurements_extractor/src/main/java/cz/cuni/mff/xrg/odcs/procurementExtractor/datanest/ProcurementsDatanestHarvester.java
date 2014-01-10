package cz.cuni.mff.xrg.odcs.procurementExtractor.datanest;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import cz.cuni.mff.xrg.odcs.procurementExtractor.repository.FileSystemRepository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.procurementExtractor.data.Currency;
import cz.cuni.mff.xrg.odcs.procurementExtractor.data.ProcurementRecord;
import cz.cuni.mff.xrg.odcs.procurementExtractor.serialization.ProcurementRdfSerializer;

public class ProcurementsDatanestHarvester extends AbstractDatanestHarvester<ProcurementRecord> {

    public final static String KEY_DATANEST_PROCUREMENTS_URL_KEY = "datanest.procurements.url";

    public final static String SC_MISSING_CURRENCY = "missing currency";
    public final static String SC_MISSING_CURRENCY_FOR_NON_ZERO_PRICE = "missing currency (for price which is non-zero)";
    public final static String SC_UNKNOWN_CURRENCY = "unknown currency: ";
    public final static String SC_MISSING_PRICE = "missing price";

    protected final static int ATTR_INDEX_ID = 0;
    protected final static int ATTR_INDEX_NOTE = 5;
    protected final static int ATTR_INDEX_YEAR = 6;
    protected final static int ATTR_INDEX_BULLETIN_ID = 7;
    protected final static int ATTR_INDEX_PROCUREMENT_ID = 8;
    protected final static int ATTR_INDEX_PROCUREMENT_SUBJECT = 9;
    protected final static int ATTR_INDEX_PRICE = 10;
    protected final static int ATTR_INDEX_CURRENCY = 11;
    protected final static int ATTR_INDEX_IS_VAT_INCLUDED = 12;
    protected final static int ATTR_INDEX_CUSTOMER_ICO = 15;
    protected final static int ATTR_INDEX_SUPPLIER_ICO = 17;

    private static Logger logger = LoggerFactory.getLogger(ProcurementsDatanestHarvester.class);
    private DecimalFormat priceFormat = null;

    public ProcurementsDatanestHarvester(String targetRdf) throws IOException, RepositoryConfigException, RepositoryException, ParserConfigurationException,
            TransformerConfigurationException {

        super(KEY_DATANEST_PROCUREMENTS_URL_KEY);
        FileSystemRepository fileSystemRepository = FileSystemRepository.getInstance();
        fileSystemRepository.setTargetRDF(targetRdf);
        ProcurementRdfSerializer rdfSerializer = new ProcurementRdfSerializer(fileSystemRepository);
        addSerializer(rdfSerializer);

        // note: Following would be "clean":
        // NumberFormat.getNumberInstance(new Locale("sk", "SK"))
        // but it requires source data to use non-breaking space instead of
        // regular space.
        priceFormat = new DecimalFormat();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator(',');
        priceFormat.setDecimalFormatSymbols(symbols);
    }

    @Override
    public ProcurementRecord scrapOneRecord(String[] row) throws ParseException {
        ProcurementRecord record = new ProcurementRecord();

        record.setId("procurement_" + row[ATTR_INDEX_ID]);
        record.setDatanestId(row[ATTR_INDEX_ID]);
        record.setNote(row[ATTR_INDEX_NOTE]);
        record.setYear(row[ATTR_INDEX_YEAR]);
        record.setBulletinId(row[ATTR_INDEX_BULLETIN_ID]);
        record.setProcurementId(row[ATTR_INDEX_PROCUREMENT_ID]);
        record.setProcurementSubject(row[ATTR_INDEX_PROCUREMENT_SUBJECT]);

        if (row[ATTR_INDEX_PRICE].isEmpty())
            // some entries (like ID 49338, from
            // http://www.e-vestnik.sk/EVestnik/Detail/29531) have empty string
            // for price
            record.addScrapNote(SC_MISSING_PRICE);
        else
            record.setPrice(priceFormat.parse(row[ATTR_INDEX_PRICE]).floatValue());

        if (!row[ATTR_INDEX_CURRENCY].isEmpty()) {
            try {
                Currency currency = Currency.parse(row[ATTR_INDEX_CURRENCY]);
                record.setCurrency(currency);
            } catch (IllegalArgumentException e) {
                // unknown currencies
                record.addScrapNote(SC_UNKNOWN_CURRENCY + row[ATTR_INDEX_CURRENCY]);
            }
        } else {
            // sometimes the currency is not filled in the source (so far only
            // for cases where the price was 0)
            record.setCurrency(Currency.UNDEFINED);
            if (record.getPrice() == 0)
                record.addScrapNote(SC_MISSING_CURRENCY);
            else
                record.addScrapNote(SC_MISSING_CURRENCY_FOR_NON_ZERO_PRICE);
        }

        record.setVatIncluded(Boolean.valueOf(row[ATTR_INDEX_IS_VAT_INCLUDED]));
        record.setCustomerIco(row[ATTR_INDEX_CUSTOMER_ICO]);
        record.setSupplierIco(row[ATTR_INDEX_SUPPLIER_ICO]);

        logger.debug("scrapped record of: " + record.getDatanestId());

        return record;
    }

}
