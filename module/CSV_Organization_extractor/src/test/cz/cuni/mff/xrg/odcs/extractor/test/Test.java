package cz.cuni.mff.xrg.odcs.extractor.test;

import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.organizationExtractor.core.CsvOrganizationExtractor;
import cz.cuni.mff.xrg.odcs.organizationExtractor.core.CsvOrganizationExtractorConfig;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;

public class Test {

    @org.junit.Test
    public void test() throws ConfigException {
        CsvOrganizationExtractor extractor = new CsvOrganizationExtractor();
        CsvOrganizationExtractorConfig config = new CsvOrganizationExtractorConfig();
        extractor.configureDirectly(config);

        TestEnvironment env = TestEnvironment.create();
        try {
            RDFDataUnit output = env.createRdfOutput("output", false);
            // run the execution
            String input = null;
            env.run(extractor);
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            // release resources
            env.release();
        }
    }
}
