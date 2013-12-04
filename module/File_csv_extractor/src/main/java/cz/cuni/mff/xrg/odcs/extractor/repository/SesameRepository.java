package cz.cuni.mff.xrg.odcs.extractor.repository;

import cz.cuni.mff.xrg.odcs.extractor.data.RdfData;
import org.openrdf.repository.http.HTTPRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SesameRepository implements OdnRepositoryStoreInterface<RdfData> {

    public final static String SESAME_REPOSITORY_PROPERTIES_NAME = "/repo-sesame.properties";
    public final static String KEY_DEBUG_DUMP_RDF = "sesame.debug.dump_rdf";
    public final static String PREFIX_KEY_REPO = "sesame.repo.";
    public final static String KEY_SERVER = PREFIX_KEY_REPO + "server";
    public final static String KEY_ID = PREFIX_KEY_REPO + "id";
    public final static String PREFIX_KEY_CONTEXTS = PREFIX_KEY_REPO + "contexts.";
    private static Logger logger = LoggerFactory.getLogger(SesameRepository.class);
    private static SesameRepository instance = null;
    private HTTPRepository sesameRepo = null;

    /**
     * Initialize Sesame back-end.
     *
     * @throws java.io.IOException when error occurs while loading properties
     */
    private SesameRepository() throws IOException {
    }

    /**
     * Get the instance of Sesame repository singleton.
     *
     * @return instance of Sesame repository
     * @throws java.io.IOException when error occurs while loading properties
     */
    public static SesameRepository getInstance() throws IOException {

        if (instance == null)
            instance = new SesameRepository();

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
     * @param records records to store (in RDF format with additional info)
     * @throws IllegalArgumentException if repository with given name does not exists
     * @throws org.openrdf.repository.RepositoryException
     *                                  when initialization fails
     */
    @Override
    public void store(RdfData records)
            throws IllegalArgumentException {
        debugRdfDump(records.getRdfData());
    }

    @Override
    public void shutDown() {
    }
}