package cz.cuni.mff.xrg.odcs.extractor.file;

import static junit.framework.Assert.assertEquals;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.openrdf.model.Model;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;

public class LocalRdfTest {
    private static final Logger LOG = LoggerFactory.getLogger(LocalRdfTest.class);

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
        config.setRDFFormatValue(RDFFormat.TURTLE);

        RDFParser rdfParser = Rio.createParser(RDFFormat.TURTLE);
        URL documentUrl = new URL(url.toExternalForm());
        RDFFormat format = Rio.getParserFormatForFileName(file.getName());

        InputStream inputStream = documentUrl.openStream();
        org.openrdf.model.Model myGraph = new org.openrdf.model.impl.LinkedHashModel();
        rdfParser.setRDFHandler(new StatementCollector(myGraph));
        Model results = Rio.parse(inputStream, fileUrl, format);
        int expectedSize = results.size();

        // prepare test environment
        TestEnvironment env = new TestEnvironment();
        RepositoryConnection connection = null;
        try {
            RDFDataUnit output = env.createRdfOutput("output", false);
            connection = output.getConnection();
            env.run(extractor);
            long actualSize = connection.size(output.getDataGraph());
            // verify result
            assertEquals(expectedSize, actualSize);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            }
            // release resources
            env.release();
        }
    }

}
