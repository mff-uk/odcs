package cz.cuni.mff.xrg.odcs.loader.rdf;

import cz.cuni.mff.xrg.odcs.commons.IntegrationTest;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.data.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.enums.InsertType;
import cz.cuni.mff.xrg.odcs.rdf.enums.WriteGraphType;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.ManagableRdfDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;

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
        
        
	private static final String HOST_NAME = "odcs.xrg.cz";

	private static final String PORT = "1120";

	private static final String USER = "dba";

	private static final String PASSWORD = "dba01OD";

	private static final String DEFAULT_GRAPH = "http://test/loader/speed/2";

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
//	private static final String DEFAULT_GRAPH = "http://test/loader/speed/1";
//
//	//private static final String UPDATE_ENDPOINT = "http://odcs.xrg.cz:8900/sparql-auth";
//        private static final String UPDATE_ENDPOINT = "http://localhost:8890/sparql-auth";

	@BeforeClass
	public static void setRDFDataUnit() throws RDFException {

		repository = RDFDataUnitFactory.createVirtuosoRDFRepo(HOST_NAME, PORT,
				USER, PASSWORD, DEFAULT_GRAPH, "input", new Properties());

	}

	@AfterClass
	public static void deleteRDFDataUnit() {
		((ManagableRdfDataUnit) repository).delete();
	}

	
        //@Test
	public void InsertingTripleToEndpointCRUD() {
		//repository.cleanAllData();

		Resource subject = repository.createURI("http://my.subject");
		URI predicate = repository.createURI("http://my.predicate");
		Value object = repository.createLiteral("My company s.r.o.");

                repository.addTriple(subject, predicate, object);
                
		tryInsertToSPARQLEndpoint();
	}
        
        @Test
	public void InsertingFileToEndpointCRUD() {
		//repository.cleanAllData();

	
            logger.info("Data extracted from file to the graph Started");
            
             //File f = new File("nsoud20000.ttl");
            try {
                //repository.addTriple(subject, predicate, object);
                repository.addFromTurtleFile(new File("src/test/resources/nsoud20000.ttl"));
            } catch (RDFException ex) {
                logger.error(ex.getLocalizedMessage());
            }

            logger.info("Data extracted from file to the graph DONE");
            

		tryInsertToSPARQLEndpoint();
	}

	


	

	private void tryInsertToSPARQLEndpoint() {
           
		String goalGraphName = "http://tempGraph/test/loader";
		URL endpoint = getUpdateEndpoint();

		boolean isLoaded = false;

		SPARQLoader loader = new SPARQLoader(repository, getTestContext(),
				virtuosoParams);
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
		TestEnvironment environment = TestEnvironment.create();
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
