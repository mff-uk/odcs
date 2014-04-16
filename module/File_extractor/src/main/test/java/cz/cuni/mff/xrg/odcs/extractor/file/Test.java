package cz.cuni.mff.xrg.odcs.extractor.file;

import java.net.URL;

import org.openrdf.repository.RepositoryConnection;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;

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
