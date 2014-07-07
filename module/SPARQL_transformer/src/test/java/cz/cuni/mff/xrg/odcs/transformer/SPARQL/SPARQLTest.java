package cz.cuni.mff.xrg.odcs.transformer.SPARQL;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Test;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;

public class SPARQLTest {
    private static final Logger LOG = LoggerFactory.getLogger(SPARQLTest.class);

    @Test
    public void constructAllTest() throws Exception {
        // prepare dpu
        SPARQLTransformer trans = new SPARQLTransformer();

        String SPARQL_Update_Query = "CONSTRUCT {?s ?p ?o} where {?s ?p ?o }";
        boolean isConstructType = true;

        SPARQLTransformerConfig config = new SPARQLTransformerConfig(
                SPARQL_Update_Query, isConstructType);

        trans.configureDirectly(config);

        // prepare test environment
        TestEnvironment env = new TestEnvironment();

        // prepare data units
        WritableRDFDataUnit input = env.createRdfInput("input", false);
        WritableRDFDataUnit output = env.createRdfOutput("output", false);

        InputStream inputStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("metadata.ttl");

        RepositoryConnection connection = null;
        RepositoryConnection connection2 = null;
        try {
            connection = input.getConnection();
            String baseURI = "";
            connection.add(inputStream, baseURI, RDFFormat.TURTLE, input.getWriteContext());

            // some triples has been loaded
            assertTrue(connection.size(input.getWriteContext()) > 0);
            // run
            env.run(trans);
            connection2 = output.getConnection();
            // verify result
            assertTrue(connection.size(input.getWriteContext()) == connection2.size(output.getWriteContext()));
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            }
            if (connection2 != null) {
                try {
                    connection2.close();
                } catch (Throwable ex) {
                    LOG.warn("Error closing connection", ex);
                }
            } // release resources
            env.release();
        }
    }
}
