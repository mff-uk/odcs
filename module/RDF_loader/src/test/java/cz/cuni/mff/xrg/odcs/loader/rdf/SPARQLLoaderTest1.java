package cz.cuni.mff.xrg.odcs.loader.rdf;

import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

import org.junit.AfterClass;
import org.junit.BeforeClass;
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
import cz.cuni.mff.xrg.odcs.rdf.enums.InsertType;
import cz.cuni.mff.xrg.odcs.rdf.enums.WriteGraphType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.repositories.VirtuosoRDFDataUnit;

/**
 * Test funcionality loading to SPARQL endpoint.
 *
 * @author Jiri Tomes
 */
public class SPARQLLoaderTest1 {

	private final Logger logger = LoggerFactory.getLogger(
			SPARQLLoaderTest1.class);

	private LoaderEndpointParams virtuosoParams = new LoaderEndpointParams();

	private static RDFDataUnit repository;

        
//        private static final String HOST_NAME = "v7.xrg.cz";
//
//	private static final String PORT = "1121";
//
//	private static final String USER = "dba";
//
//	private static final String PASSWORD = "dba";
//
//	private static final String DEFAULT_GRAPH = "http://test/loader/speed/1";
//
//	private static final String UPDATE_ENDPOINT = "http://v7.xrg.cz:8901/sparql-auth";
//        //private static final String UPDATE_ENDPOINT = "http://v7.xrg.cz:8901/sparql-graph-crud-auth";
        
        
	private static final String URL = "jdbc:virtuoso://odcs.xrg.cz:1120/charset=UTF-8/log_enable=2";

	private static final String USER = "dba";

	private static final String PASSWORD = "dba01OD";

	private static final String INPUT_GRAPH = "http://test/loader/speed/16/input";
        private static final String OUTPUT_GRAPH = "http://test/loader/speed/16/output";

	private static final String UPDATE_ENDPOINT = "http://odcs.xrg.cz:8900/sparql-auth";
        //private static final String UPDATE_ENDPOINT = "http://odcs.xrg.cz:8900/sparql-graph-crud-auth";

        
        
//        private static final String HOST_NAME = "localhost";
//
//	private static final String PORT = "1111";
//
//	private static final String USER = "dba";
//
//	private static final String PASSWORD = "dba";
//
//	private static final String DEFAULT_GRAPH = "http://test/loader/speed/3/1";
//
//	private static final String UPDATE_ENDPOINT = "http://localhost:8890/sparql-auth";
////        private static final String UPDATE_ENDPOINT = "http://localhost:8890/sparql-graph-crud-auth";

	@BeforeClass
	public static void setRDFDataUnit() throws RDFException {

//		repository = new VirtuosoRDFDataUnit(URL, 
//				USER, PASSWORD, "input", INPUT_GRAPH);

	}

	@AfterClass
	public static void deleteRDFDataUnit() {
		//((ManagableRdfDataUnit) repository).delete();
	}

	
        //@Test
    public void InsertingTripleToEndpointCRUD() throws RepositoryException {
        //repository.cleanAllData();

        RepositoryConnection connection = repository.getConnection();
        ValueFactory factory = connection.getValueFactory();
        Resource subject = factory.createURI("http://my.subject");
        URI predicate = factory.createURI("http://my.predicate");
        Value object = factory.createLiteral("Mojefi resi ...");
        Value object2 = factory.createLiteral("Y");
        Value object3 = factory.createLiteral("ščřžýěéž");

        connection.add(subject, predicate, object, repository.getDataGraph());
        connection.add(subject, predicate, object3, repository.getDataGraph());

        tryInsertToSPARQLEndpoint();
    }
        

	private void tryInsertToSPARQLEndpoint() {
           
		String goalGraphName = OUTPUT_GRAPH;
		URL endpoint = getUpdateEndpoint();

		boolean isLoaded = false;

        SPARQLoader loader = new SPARQLoader(repository, getTestContext(),
                virtuosoParams, true, USER, PASSWORD);
		try {

                        
			loader.loadToSPARQLEndpoint(endpoint, goalGraphName, USER,
					PASSWORD,
					WriteGraphType.OVERRIDE, InsertType.SKIP_BAD_PARTS);
			isLoaded = true;

		} catch (RDFException e) {
			logger.error("INSERT  failed");

		} finally {
//			try {
//				loader.clearEndpointGraph(endpoint, goalGraphName);
//			} catch (RDFException e) {
//				logger.error(
//						"TEMP graph <" + goalGraphName + "> was not delete");
//			}
		}

		assertTrue(isLoaded);
            try {
                Thread.sleep(100000);
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(SPARQLLoaderTest1.class.getName()).log(Level.SEVERE, null, ex);
            }
                logger.info("Test finished!");
	}

	private DPUContext getTestContext() {
		TestEnvironment environment =  new TestEnvironment();
		return environment.getContext();
	}

	private URL getUpdateEndpoint() {

		URL endpoint = null;

		try {
			endpoint = new URL(UPDATE_ENDPOINT);

		} catch (MalformedURLException e) {
			logger.debug("Malformed URL to SPARQL update endpoint " + e
					.getMessage());

		} finally {
			return endpoint;
		}

	}
}
