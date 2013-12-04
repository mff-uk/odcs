package cz.cuni.mff.xrg.odcs.extractor;

import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.commons.module.file.FileManager;
import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.extractor.file.FileCsvExtractor;
import cz.cuni.mff.xrg.odcs.extractor.file.FileCsvExtractorConfig;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import org.openrdf.model.Statement;

import java.util.List;

public class Test {

    @org.junit.Test
    public void test() throws ConfigException {
        FileCsvExtractor extractor = new FileCsvExtractor();
        FileCsvExtractorConfig config = new FileCsvExtractorConfig();
        extractor.configureDirectly(config);

        TestEnvironment env = TestEnvironment.create();
        try {
            RDFDataUnit output = env.createRdfOutput("output", false);
            // run the execution
            String input = null;
            env.run(extractor);
            List<Statement> triplets = output.getTriples();
            String rdfPath = "e://eea//comsode//test.rdf";
            output.loadToFile(rdfPath, RDFFormatType.RDFXML);
            // verify result
//            assertTrue(input == output);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            // release resources
            env.release();
        }
    }
}
