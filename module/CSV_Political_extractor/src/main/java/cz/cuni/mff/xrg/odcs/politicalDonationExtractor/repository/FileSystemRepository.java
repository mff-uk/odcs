package cz.cuni.mff.xrg.odcs.politicalDonationExtractor.repository;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Properties;

import cz.cuni.mff.xrg.odcs.politicalDonationExtractor.core.CsvPoliticalExtractor;
import cz.cuni.mff.xrg.odcs.politicalDonationExtractor.data.RdfData;
import org.openrdf.repository.http.HTTPRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemRepository implements OdnRepositoryStoreInterface<RdfData> {

    public final static String SESAME_REPOSITORY_PROPERTIES_NAME = "/repo-sesame.properties";
    public final static String KEY_DEBUG_DUMP_RDF = "sesame.debug.dump_rdf";
    public final static String PREFIX_KEY_REPO = "sesame.repo.";
    public final static String KEY_SERVER = PREFIX_KEY_REPO + "server";
    public final static String KEY_ID = PREFIX_KEY_REPO + "id";
    public final static String PREFIX_KEY_CONTEXTS = PREFIX_KEY_REPO + "contexts.";
    private static Logger logger = LoggerFactory.getLogger(FileSystemRepository.class);
    private static FileSystemRepository instance = null;
    public String targetRDF = "";
    private HTTPRepository sesameRepo = null;

    /**
     * Initialize Sesame back-end.
     * 
     * @throws java.io.IOException
     *             when error occurs while loading properties
     */
    private FileSystemRepository() throws IOException {

        Properties prop = new Properties();
        try {
            // load a properties file from class path, inside static method
            prop.load(CsvPoliticalExtractor.class.getClassLoader().getResourceAsStream("config.properties"));
            // get the property value and print it out
            String target = prop.getProperty("targetRDF");
            setTargetRDF(target);
            logger.debug("targetRDF is: " + target);

        } catch (IOException e) {
            logger.error("error was occoured while it was reading property file", e);
        }

    }

    /**
     * Get the instance of Sesame repository singleton.
     * 
     * @return instance of Sesame repository
     * @throws java.io.IOException
     *             when error occurs while loading properties
     */
    public static FileSystemRepository getInstance() throws IOException {

        if (instance == null)
            instance = new FileSystemRepository();

        return instance;
    }

    private void debugRdfDump(String rdfData) {
        try {
            File dumpFile = File.createTempFile("odn-", ".rdf");
            BufferedWriter out = new BufferedWriter(new FileWriter(dumpFile));
            out.write(rdfData);
            out.close();
            logger.info("RDF dump saved to file " + dumpFile);
        } catch (IOException e) {
            logger.error("IO exception", e);
        }
    }

    /**
     * Store given record into Sesame repository with given name.
     * 
     * @param records
     *            records to store (in RDF format with additional info)
     * @throws IllegalArgumentException
     *             if repository with given name does not exists
     * @throws org.openrdf.repository.RepositoryException
     *             when initialization fails
     */
    @Override
    public void store(RdfData records) throws IllegalArgumentException {
        saveRdf(records.getRdfData());
    }

    public String getTargetRDF() {
        return targetRDF;
    }

    public void setTargetRDF(String targetRDF) {
        this.targetRDF = targetRDF;
    }

    private void saveRdf(String rdfData) {
        try {
            File directory = new File(this.targetRDF);
            if (!directory.exists()) {
                boolean result = directory.mkdir();
                if (!result) {
                    logger.warn("A directory:" + this.getTargetRDF() + " is not created. You should check what happened. This is an error probably.");
                }
            }

            File fileRdf = File.createTempFile("odn-", ".rdf", directory);
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileRdf), Charset.forName("UTF-8")));

            BufferedWriter out = new BufferedWriter(writer);
            out.write(rdfData);
            out.close();
            logger.info("RDF saved to file " + fileRdf);
        } catch (IOException e) {
            logger.error("IO exception", e);
        }
    }

    @Override
    public void shutDown() {
    }
}
