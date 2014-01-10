package cz.cuni.mff.xrg.odcs.organizationExtractor.datanest;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;
import cz.cuni.mff.xrg.odcs.organizationExtractor.data.AbstractRecord;

public abstract class AbstractDatanestHarvester<RecordType extends AbstractRecord> extends AbstractHarvester<RecordType> {

    public final static String DATANEST_DATE_FORMAT = "yyyy-MM-dd";
    protected final static SimpleDateFormat sdf = new SimpleDateFormat(DATANEST_DATE_FORMAT);
    private static Logger LOG = LoggerFactory.getLogger(AbstractDatanestHarvester.class);
    public Integer debugProcessOnlyNItems = new Integer(0);

    public Integer getDebugProcessOnlyNItems() {
        return debugProcessOnlyNItems;
    }

    public void setDebugProcessOnlyNItems(int debugProcessOnlyNItems) {
        this.debugProcessOnlyNItems = debugProcessOnlyNItems;
    }

    /**
     * @param sourceUrlKey
     *            key used to get the source URL from Datanest properties
     * @throws java.io.IOException
     *             when initialization of primary repository fails
     */
    public AbstractDatanestHarvester(String sourceUrlKey) {

        super();

    }

    abstract public RecordType scrapOneRecord(String[] row) throws ParseException;

    /**
     * Most common implementation of harvesting code in our current Datanest harvesters.
     * 
     * @param sourceFile
     *            temporary file holding freshly obtained data to harvest from when some repository error occurs
     * 
     */
    // TODO Now it reads from a csv file. In next step, it could be great to reaad the csv files from a directory.
    @Override
    public Vector<RecordType> performEtl(File sourceFile) throws Exception {
        LOG.info("start performEtl on the path sourceFile");

        Vector<RecordType> records = new Vector<RecordType>();
        try {
            Charset charset = Charset.forName("UTF8");
            CSVReader csvReader = new CSVReader(new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile), charset)));

            // TODO: If we store also the original copy of the data (say in
            // Jacrabbit) and perform a "diff" on that and previous version we can:
            // a) determine also removed records (which current implementation
            // does not know to do)
            // b) determine new and updated records without downloading records
            // for all IDs ...
            // c) ... instead noting only changed records in say vectors and
            // processing only those (thus saving a LOT of resources assuming
            // changes and additions are small and infrequent)

            // TODO: check the header - for now we simply skip it
            csvReader.readNext();
            int recordCounter = 0;
            long timeCurrent = -1;
            long timeStart = Calendar.getInstance().getTimeInMillis();

            Integer batchSize = getBatchSize();
            Integer processOnlyNItems = getDebugProcessOnlyNItems();
            LOG.debug("process only " + processOnlyNItems + " Items");
            LOG.debug("process only " + batchSize + "  Items");

            // read the rows
            String[] row;
            while ((row = csvReader.readNext()) != null) {
                try {

                    RecordType record = scrapOneRecord(row);
                    recordCounter++;

                    // add new data
                    records.add(record);
                } catch (ArrayIndexOutOfBoundsException e) {
                    // happens when connection with source server cuts
                    // prematurely - this will cause last fetched line of CSV to
                    // be incomplete
                    LOG.warn("index out of bound exception (broken connection?)", e);
                    LOG.warn("skipping following record: " + Arrays.deepToString(row));
                } catch (ParseException e) {
                    LOG.warn("parse exception", e);
                    LOG.warn("skipping following record: " + Arrays.deepToString(row));
                }

                if (records.size() >= batchSize) {
                    store(records);

                    // report current harvesting status
                    timeCurrent = Calendar.getInstance().getTimeInMillis();
                    float harvestingSpeed = 1000f * (float) recordCounter / (float) (timeCurrent - timeStart);
                    LOG.info("harvested " + recordCounter + " records (" + harvestingSpeed + "/s) so far ...");

                    records.clear();
                }

                if (processOnlyNItems > 0 && recordCounter >= processOnlyNItems)
                    break;
            }

            // store the results
            store(records);
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

}
