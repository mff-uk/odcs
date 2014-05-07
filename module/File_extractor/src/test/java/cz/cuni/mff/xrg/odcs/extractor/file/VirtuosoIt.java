package cz.cuni.mff.xrg.odcs.extractor.file;

import cz.cuni.mff.xrg.odcs.commons.IntegrationTest;
import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;
import org.openrdf.model.Model;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import static junit.framework.Assert.assertEquals;

@Category(IntegrationTest.class)
public class VirtuosoIt {
    private static final String HOST_NAME = "localhost";
    private static final String PORT = "1111";
    private static final String USER = "dba";
    private static final String PASSWORD = "dba";
    private static final String DEFAULT_GRAPH = "http://default";
    private static final String QUERY_ENDPOINT = "http://localhost:8890/sparql";
    private static RDFDataUnit repository;
    private static final Logger LOG = LoggerFactory.getLogger(
            VirtuosoIt.class);

    @org.junit.Test
    public void test() throws Exception {
        // prepare dpu
        FileExtractor extractor = new FileExtractor();
        FileExtractorConfig config = new FileExtractorConfig();
        extractor.configureDirectly(config);
        URL url = this.getClass().getResource("/metadata.ttl");
        File file = new File(String.valueOf(url));

        String fileUrl = url.toURI().getPath();
        config.setPath(fileUrl);
        config.setFileExtractType(FileExtractType.PATH_TO_FILE);

        RDFParser rdfParser = Rio.createParser(RDFFormat.TURTLE);
        URL documentUrl = new URL(url.toExternalForm());
        RDFFormat format = Rio.getParserFormatForFileName(file.getName());

        InputStream inputStream = documentUrl.openStream();
        org.openrdf.model.Model myGraph = new org.openrdf.model.impl.LinkedHashModel();
        rdfParser.setRDFHandler(new StatementCollector(myGraph));
        Model results = Rio.parse(inputStream, fileUrl, format);
        int expectedSize = results.size();

        // prepare test environment
        TestEnvironment env =  new TestEnvironment();
        RepositoryConnection connection = null;
        try {
            RDFDataUnit output = env.createRdfOutput("output", false);
            connection = output.getConnection();
            env.run(extractor);
            long actualSize = connection.size(output.getDataGraph());
            // verify result
            assertEquals(expectedSize, actualSize);
        } finally {
        	if (connection != null) { try { connection.close(); } catch (Throwable ex) {LOG.warn("Error closing connection", ex);}}
            // release resources
            env.release();
        }
    }
}
