package cz.cuni.mff.xrg.odcs.extractor;

import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.extractor.file.FileCsvExtractor;
import cz.cuni.mff.xrg.odcs.extractor.file.FileCsvExtractorConfig;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import org.openrdf.model.Statement;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: janci
 * Date: 22.11.2013
 * Time: 14:32
 * To change this template use File | Settings | File Templates.
 */
public class Test {

    @org.junit.Test
    public void test() throws ConfigException {
        System.out.println("aaa");
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
            for (Statement triplet: triplets) {
                System.out.println(triplet.getObject());

            }

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
