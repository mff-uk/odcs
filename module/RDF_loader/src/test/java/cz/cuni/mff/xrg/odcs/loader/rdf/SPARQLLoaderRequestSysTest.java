package cz.cuni.mff.xrg.odcs.loader.rdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;

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

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.WritableRDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.enums.InsertType;
import cz.cuni.mff.xrg.odcs.rdf.enums.WriteGraphType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;

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

    private URL getEndpoint() {
        URL endpoint = null;
        try {
            endpoint = new URL(
                    //					testEnvironment.createSPARQLEndpoint("http://kuku"));
                    ENDPOINT);
        } catch (MalformedURLException ex) {
            logger.debug(ex.getMessage());
        }

        return endpoint;
    }

    private void loadToEndpoint(LoaderEndpointParams params,
            String defaultGraphURI) throws RepositoryException {
        WritableRDFDataUnit repository = testEnvironment.createRdfInput("testInnn", false);
        RepositoryConnection connection = repository.getConnection();
        ValueFactory factory = connection.getValueFactory();
        for (int i = 0; i < LOADED_TRIPLES; i++) {
            Resource subject = factory.createURI("http://A" + String.valueOf(
                    i + 1));

            URI predicate = factory.createURI("http://B" + String.valueOf(
                    i + 1));

            Value object = factory.createLiteral("C" + String.valueOf(i + 1));

            connection.add(subject, predicate, object, repository.getWriteContext());
        }

        URL endpoint = getEndpoint();
        SPARQLoader loader = new SPARQLoader(repository, testEnvironment.getContext(),
                params, false, USER, PASSWORD);
        try {
            loader.loadToSPARQLEndpoint(endpoint, defaultGraphURI, USER,
                    PASSWORD,
                    WriteGraphType.OVERRIDE, InsertType.STOP_WHEN_BAD_PART);

            assertEquals(connection.size(repository.getWriteContext()), loader
                    .getSPARQLEndpointGraphSize(endpoint, defaultGraphURI));

        } catch (RDFException e) {
            fail(e.getMessage());
        } finally {
            try {
                loader.clearEndpointGraph(endpoint, defaultGraphURI);
            } catch (RDFException e) {
                logger.debug(e.getMessage());
            }
        }
        connection.close();
    }

    @Test
    public void POSTEncodeTest() throws RepositoryException {
        String graphParam = "query";
        String defaultGraphParam = "using-graph-uri";

        LoaderPostType requestType = LoaderPostType.POST_URL_ENCODER;

        LoaderEndpointParams params = new LoaderEndpointParams(graphParam,
                defaultGraphParam, requestType);

        loadToEndpoint(params, TARGET_GRAPH);
    }
}
