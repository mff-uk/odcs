package cz.cuni.mff.xrg.odcs.loader.rdf;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.unifiedviews.dpu.DPUContext;
import eu.unifiedviews.dpu.DPUException;
import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.enums.InsertType;
import cz.cuni.mff.xrg.odcs.rdf.enums.WriteGraphType;
import eu.unifiedviews.dataunit.DataUnitException;
import eu.unifiedviews.dataunit.rdf.RDFDataUnit;
import eu.unifiedviews.dataunit.rdf.WritableRDFDataUnit;

/**
 * Test funcionality loading to SPARQL endpoint.
 * 
 * @author Jiri Tomes
 */
public class SPARQLLoaderSysTest {

    private final Logger logger = LoggerFactory.getLogger(
            SPARQLLoaderSysTest.class);

    private LoaderEndpointParams virtuosoParams = new LoaderEndpointParams();

    private static TestEnvironment testEnvironment = new TestEnvironment();

    private static final String URL = "jdbc:virtuoso://localhost:1111/charset=UTF-8/log_enable=2";

    private static final String USER = "dba";

    private static final String PASSWORD = "dba";

    private static final String DEFAULT_GRAPH = "http://default";

    private static final String UPDATE_ENDPOINT = "http://localhost:8890/sparql-auth";

    @AfterClass
    public static void deleteRDFDataUnit() {
        testEnvironment.release();
    }

    @Test
    public void InsertingToEndpointTest1() throws RepositoryException, DataUnitException {
        fail();
        WritableRDFDataUnit repository = testEnvironment.createRdfFDataUnit("");
        RepositoryConnection connection = repository.getConnection();
        ValueFactory factory = connection.getValueFactory();
        Resource subject = factory.createURI("http://my.subject");
        URI predicate = factory.createURI("http://my.predicate");
        Value object = factory.createLiteral("My company s.r.o. \"HOME\"");
        connection.close();
        tryInsertToSPARQLEndpoint(subject, predicate, object);
    }

    @Test
    public void InsertingToEndpointTest2() throws RepositoryException, DataUnitException {
        WritableRDFDataUnit repository = testEnvironment.createRdfFDataUnit("");

        RepositoryConnection connection = repository.getConnection();
        ValueFactory factory = connection.getValueFactory();
        Resource subject = factory.createURI("http://my.subject");
        URI predicate = factory.createURI("http://my.predicate");
        Value object = factory.createLiteral(
                "This \"firma has 'firma' company\" Prague");
        connection.close();
        tryInsertToSPARQLEndpoint(subject, predicate, object);
    }

    @Test
    public void InsertingToEndpointTest3() throws RepositoryException, DataUnitException {
        RDFDataUnit repository = testEnvironment.createRdfFDataUnit("");

        RepositoryConnection connection = repository.getConnection();
        ValueFactory factory = connection.getValueFactory();
        Resource subject = factory.createURI("http://my.subject");
        URI predicate = factory.createURI("http://my.predicate");
        Value object = factory.createLiteral(
                "Test char <and > in <my text1> as example.");
        connection.close();
        tryInsertToSPARQLEndpoint(subject, predicate, object);
    }

    //@Test
    public void loadDataToSPARQLEndpointTest() {
        RDFDataUnit repository = testEnvironment.createRdfFDataUnit("");

        RDFLoaderConfig c = new RDFLoaderConfig();
        c.setEndpointParams(virtuosoParams);
        c.setHost_name(USER);
        c.setPassword(PASSWORD);
        c.setSPARQL_endpoint(UPDATE_ENDPOINT);
        c.setGraphsUri(Arrays.asList("http://ld.opendata.cz/resource/myGraph/001"));
        c.setInsertOption(InsertType.SKIP_BAD_PARTS);
        c.setGraphOption(WriteGraphType.MERGE);

        try {
            SPARQLoader loader = new SPARQLoader(repository,
                    getTestContext(), c);

            loader.loadToSPARQLEndpoint();
        } catch (DPUException e) {
            fail(e.getMessage());
        }
    }

    private void tryInsertToSPARQLEndpoint(Resource subject, URI predicate,
            Value object) throws RepositoryException, DataUnitException {
        RDFDataUnit repository = testEnvironment.createRdfFDataUnit("");

        RepositoryConnection connection = repository.getConnection();
        connection.add(subject, predicate, object);

        String goalGraphName = "http://tempGraph";

        boolean isLoaded = false;
        RDFLoaderConfig c = new RDFLoaderConfig();
        c.setEndpointParams(virtuosoParams);
        c.setHost_name(USER);
        c.setPassword(PASSWORD);
        c.setSPARQL_endpoint(UPDATE_ENDPOINT);
        c.setGraphsUri(Arrays.asList(goalGraphName));
        c.setInsertOption(InsertType.SKIP_BAD_PARTS);
        c.setGraphOption(WriteGraphType.OVERRIDE);
        
        SPARQLoader loader = new SPARQLoader(repository, getTestContext(),
                c);
        try {

            loader.loadToSPARQLEndpoint();
            isLoaded = true;

        } catch (DPUException e) {
            logger.error("INSERT triple: {} {} {} to SPARQL endpoint failed",
                    subject.stringValue(),
                    predicate.stringValue(),
                    object.stringValue());

        } 
        connection.close();
        assertTrue(isLoaded);
    }

    private DPUContext getTestContext() {
        TestEnvironment environment = new TestEnvironment();
        return environment.getContext();
    }

    private URL getUpdateEndpoint() {

        URL endpoint = null;

        try {
            endpoint = new URL(UPDATE_ENDPOINT);

        } catch (MalformedURLException e) {
            logger.debug("Malformed URL to SPARQL update endpoint " + e
                    .getMessage());

        }
        return endpoint;

    }
}
