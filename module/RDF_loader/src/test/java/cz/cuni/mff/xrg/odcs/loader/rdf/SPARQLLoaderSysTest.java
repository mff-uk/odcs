package cz.cuni.mff.xrg.odcs.loader.rdf;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;

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

import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.RDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.WritableRDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.enums.InsertType;
import cz.cuni.mff.xrg.odcs.rdf.enums.WriteGraphType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;

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
    public void InsertingToEndpointTest1() throws RepositoryException {
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
    public void InsertingToEndpointTest2() throws RepositoryException {
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
    public void InsertingToEndpointTest3() throws RepositoryException {
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
        try {
            RDFDataUnit repository = testEnvironment.createRdfFDataUnit("");

            URL endpointURL = new URL("http://ld.opendata.cz:8894/sparql-auth");
            String defaultGraphUri = "http://ld.opendata.cz/resource/myGraph/001";
            String name = "SPARQL";
            String password = "nejlepsipaper";
            WriteGraphType graphType = WriteGraphType.MERGE;
            InsertType insertType = InsertType.SKIP_BAD_PARTS;

            try {
                SPARQLoader loader = new SPARQLoader(repository,
                        getTestContext(), virtuosoParams, false, name, password);

                loader.loadToSPARQLEndpoint(endpointURL, defaultGraphUri, name,
                        password, graphType, insertType);
            } catch (RDFException e) {
                fail(e.getMessage());
            }

        } catch (MalformedURLException ex) {
            logger.error("Bad URL for SPARQL endpoint: " + ex.getMessage());
        }

    }

    private void tryInsertToSPARQLEndpoint(Resource subject, URI predicate,
            Value object) throws RepositoryException {
        RDFDataUnit repository = testEnvironment.createRdfFDataUnit("");

        RepositoryConnection connection = repository.getConnection();
        connection.add(subject, predicate, object);

        String goalGraphName = "http://tempGraph";
        URL endpoint = getUpdateEndpoint();

        boolean isLoaded = false;

        SPARQLoader loader = new SPARQLoader(repository, getTestContext(),
                virtuosoParams, false, USER, PASSWORD);
        try {

            loader.loadToSPARQLEndpoint(endpoint, goalGraphName, USER,
                    PASSWORD,
                    WriteGraphType.OVERRIDE, InsertType.SKIP_BAD_PARTS);
            isLoaded = true;

        } catch (RDFException e) {
            logger.error("INSERT triple: {} {} {} to SPARQL endpoint failed",
                    subject.stringValue(),
                    predicate.stringValue(),
                    object.stringValue());

        } finally {
            try {
                loader.clearEndpointGraph(endpoint, goalGraphName);
            } catch (RDFException e) {
                logger.error(
                        "TEMP graph <" + goalGraphName + "> was not delete");
            }
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
