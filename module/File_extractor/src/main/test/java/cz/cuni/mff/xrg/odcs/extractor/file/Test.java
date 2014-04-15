package cz.cuni.mff.xrg.odcs.extractor.file;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.extractor.file.FileExtractor;
import cz.cuni.mff.xrg.odcs.extractor.file.FileExtractorConfig;
import cz.cuni.mff.xrg.odcs.rdf.enums.FileExtractType;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;

import java.net.URL;

import static org.junit.Assert.assertTrue;

public class Test {

    @org.junit.Test
    public void test() throws Exception {
        // prepare dpu
        FileExtractor extractor = new FileExtractor();
        FileExtractorConfig config = new FileExtractorConfig();
        extractor.configureDirectly(config);
        URL url = this.getClass().getResource("/metadata.ttl");
        String fileUrl = url.toURI().getPath();
        config.setPath(fileUrl);
        config.setFileExtractType(FileExtractType.PATH_TO_FILE);
        // prepare test environment
        TestEnvironment env = TestEnvironment.create();
        // prepare data units
        // run
        try {
            RDFDataUnit output = env.createRdfOutput("output", false);
            RepositoryConnection connection = output.getConnection();
            env.run(extractor);
            System.out.println(connection.size(output.getDataGraph()));
            // verify result
        } finally {
            // release resources
            env.release();
        }
    }

}
