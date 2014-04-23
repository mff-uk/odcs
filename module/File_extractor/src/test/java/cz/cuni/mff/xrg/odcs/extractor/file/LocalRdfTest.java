package cz.cuni.mff.xrg.odcs.extractor.file;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.openrdf.model.Model;
import org.openrdf.repository.RepositoryConnection;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;

import static junit.framework.Assert.assertEquals;

public class LocalRdfTest {

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
        TestEnvironment env = TestEnvironment.create();
        try {
            RDFDataUnit output = env.createRdfOutput("output", false);
            RepositoryConnection connection = output.getConnection();
            env.run(extractor);
            long actualSize = connection.size(output.getDataGraph());
            // verify result
            assertEquals(expectedSize, actualSize);
        } finally {
            // release resources
            env.release();
        }
    }

}
