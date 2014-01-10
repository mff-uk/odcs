package cz.cuni.mff.xrg.odcs.organizationExtractor.repository;

import java.io.*;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.organizationExtractor.data.RdfData;

public class FileSystemRepository implements OdnRepositoryStoreInterface<RdfData> {

    private static Logger logger = LoggerFactory.getLogger(FileSystemRepository.class);
    private static FileSystemRepository instance = null;
    public String targetRDF = "";

    /**
     * Initialize FileSystem back-end.
     * 
     * @throws java.io.IOException
     *             when error occurs while loading properties
     */
    private FileSystemRepository() throws IOException {
        logger.debug("FileSystemRepository targetRDF is: " + this.getTargetRDF());

    }

    /**
     * Get the instance of FileSystem repository singleton.
     * 
     * @return instance of FileSystem repository
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
            dumpFile = File.createTempFile("odn-", ".rdf");
            FileWriter writer = new FileWriter(dumpFile);
            BufferedWriter out = new BufferedWriter(writer);

            out.write(rdfData);
            out.close();
            logger.info("RDF dump saved to file " + dumpFile);
        } catch (IOException e) {
            logger.error("IO exception", e);
        }
    }

    /**
     * Store given record into FileSystem repository with given name.
     * 
     * @param records
     *            records to store (in RDF format with additional info)
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
