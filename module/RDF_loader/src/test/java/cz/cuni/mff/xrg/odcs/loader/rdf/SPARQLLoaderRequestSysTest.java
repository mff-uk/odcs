package cz.cuni.mff.xrg.odcs.loader.rdf;

import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import eu.unifiedviews.dpu.DPUException;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;
import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.enums.InsertType;
import cz.cuni.mff.xrg.odcs.rdf.enums.WriteGraphType;
import eu.unifiedviews.dataunit.DataUnitException;

/**
 * @author Jiri Tomes
 */
public class SPARQLLoaderRequestSysTest {

    private final Logger logger = LoggerFactory.getLogger(
            SPARQLLoaderRequestSysTest.class);

    private static TestEnvironment testEnvironment = null;

    private static final String ENDPOINT = "http://localhost:8890/sparql-auth";

    private static final String TARGET_GRAPH = "http://tempGraph";

    private static final int LOADED_TRIPLES = 20;

    private static final String USER = "dba";

    private static final String PASSWORD = "dba";

    @AfterClass
    public static void deleteRDFDataUnit() {
        testEnvironment.release();
    }

    @BeforeClass
    public static void cleanRepository() {
        testEnvironment = new TestEnvironment();
    }

    private void loadToEndpoint(LoaderEndpointParams params,
            String defaultGraphURI) throws RepositoryException, DataUnitException {
        WritableRDFDataUnit repository = testEnvironment.createRdfInput("testInnn", false);
        RepositoryConnection connection = repository.getConnection();
        ValueFactory factory = connection.getValueFactory();
        for (int i = 0; i < LOADED_TRIPLES; i++) {
            Resource subject = factory.createURI("http://A" + String.valueOf(
                    i + 1));

            URI predicate = factory.createURI("http://B" + String.valueOf(
                    i + 1));

            Value object = factory.createLiteral("C" + String.valueOf(i + 1));

            connection.add(subject, predicate, object, repository.getWriteDataGraph());
        }
        RDFLoaderConfig c = new RDFLoaderConfig();
        c.setEndpointParams(params);
        c.setHost_name(USER);
        c.setPassword(PASSWORD);
        c.setSPARQL_endpoint(ENDPOINT);
        c.setGraphsUri(Arrays.asList(defaultGraphURI));
        c.setInsertOption(InsertType.STOP_WHEN_BAD_PART);
        c.setGraphOption(WriteGraphType.OVERRIDE);

        SPARQLoader loader = new SPARQLoader(repository, testEnvironment.getContext(),
                c);
        try {
            loader.loadToSPARQLEndpoint();

//            assertEquals(connection.size(repository.getWriteContext()), loader
//                    .getSPARQLEndpointGraphSize(defaultGraphURI));

        } catch (DPUException e) {
            fail(e.getMessage());
        } 
        connection.close();
    }

    @Test
    public void POSTEncodeTest() throws RepositoryException, DataUnitException {
        String graphParam = "query";
        String defaultGraphParam = "using-graph-uri";

        LoaderPostType requestType = LoaderPostType.POST_URL_ENCODER;

        LoaderEndpointParams params = new LoaderEndpointParams(graphParam,
                defaultGraphParam, requestType);

        loadToEndpoint(params, TARGET_GRAPH);
    }
}
