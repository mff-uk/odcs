package cz.cuni.mff.xrg.odcs.extractor.rdf;

import cz.cuni.mff.xrg.odcs.commons.IntegrationTest;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.repositories.ManagableRdfDataUnit;

import java.net.MalformedURLException;
import java.net.URL;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.*;
import org.junit.experimental.categories.Category;
import org.openrdf.rio.RDFFormat;

import static org.junit.Assert.*;

/**
 * Test extraction funcionality for Local RDF storage from SPARQL endpoint.
 *
 * @author Jiri Tomes
 */
@Category(IntegrationTest.class)
public class SPARQLExtractorLocalTest {

	private final Logger logger = LoggerFactory.getLogger(
			SPARQLExtractorLocalTest.class);

	private static TestEnvironment testEnvironment = new TestEnvironment();

	private static final String QUERY_ENDPOINT = "http://localhost:8890/sparql";


	private ExtractorEndpointParams getVirtuosoEndpoint() {
		return new ExtractorEndpointParams();
	}

	@AfterClass
	public static void deleteRDFDataUnit() {
		testEnvironment.release();
	}

	//@Test
	public void extractBigDataFromEndpoint() throws RepositoryException {

		try {
			RDFDataUnit repository = testEnvironment.createRdfFDataUnit("");
			URL endpointURL = new URL("http://internal.opendata.cz:8890/sparql");
			String defaultGraphUri = "http://linked.opendata.cz/resource/dataset/seznam.gov.cz/ovm/list/notransform";
			String query = "CONSTRUCT {?s ?p ?o} where {?s ?p ?o}";

            RepositoryConnection connection = null;
            try {
            	connection = repository.getConnection();
	            long sizeBefore = connection.size(repository.getDataGraph());
				ExtractorEndpointParams virtuoso = getVirtuosoEndpoint();
				virtuoso.addDefaultGraph(defaultGraphUri);
	
				try {
					SPARQLExtractor extractor = new SPARQLExtractor(repository,
							testEnvironment.getContext(), virtuoso);
	
					extractor.extractFromSPARQLEndpoint(endpointURL, query);
	
	
				} catch (RDFException e) {
					fail(e.getMessage());
				}
	
				long sizeAfter = connection.size(repository.getDataGraph());
	
				assertTrue(sizeBefore < sizeAfter);
            } finally {
            	if (connection !=null) { try { connection.close(); } catch (Throwable e) {}}
            }

		} catch (MalformedURLException ex) {
			logger.error("Bad URL for SPARQL endpoint: " + ex.getMessage());
		}
	}

	//@Test
	public void extractDataFromSPARQLEndpointTest() throws RepositoryException {

		try {
			RDFDataUnit repository = testEnvironment.createRdfFDataUnit("");
			URL endpointURL = new URL("http://dbpedia.org/sparql");
			String defaultGraphUri = "http://dbpedia.org";
			String query = "construct {?s ?o ?p} where {?s ?o ?p} LIMIT 50";

            RepositoryConnection connection = null;
            try {
            	connection = repository.getConnection();
	            long sizeBefore = connection.size(repository.getDataGraph());
	
				ExtractorEndpointParams virtuoso = getVirtuosoEndpoint();
				virtuoso.addDefaultGraph(defaultGraphUri);
	
				try {
					SPARQLExtractor extractor = new SPARQLExtractor(repository,
							testEnvironment.getContext(), virtuoso);
	
					extractor
							.extractFromSPARQLEndpoint(endpointURL, query);
				} catch (RDFException e) {
					fail(e.getMessage());
				}
	
				long sizeAfter = connection.size(repository.getDataGraph());
	
				assertTrue(sizeBefore < sizeAfter);
            } finally {
            	if (connection !=null) { try { connection.close(); } catch (Throwable e) {}}
            }

		} catch (MalformedURLException ex) {
			logger.error("Bad URL for SPARQL endpoint: " + ex.getMessage());
		}
	}

	/**
	 * This is not unit test, as it depends on remote server -> commented out
	 * for build, use only when debugging
	 */
	//@Test
	public void extractDataFromSPARQLEndpointNamePasswordTest() throws RepositoryException {
		try {
			RDFDataUnit repository = testEnvironment.createRdfFDataUnit("");
			URL endpoint = new URL(QUERY_ENDPOINT.toString());
			String query = "construct {?s ?o ?p} where {?s ?o ?p} LIMIT 10";

			RDFFormat format = RDFFormat.N3;

            RepositoryConnection connection = null;
            try {
            	connection = repository.getConnection();
	            long sizeBefore = connection.size(repository.getDataGraph());
	
				ExtractorEndpointParams virtuoso = getVirtuosoEndpoint();
				virtuoso.addDefaultGraph("http://BigGraph");
	
				try {
					SPARQLExtractor extractor = new SPARQLExtractor(repository,
							testEnvironment.getContext(), virtuoso);
					extractor.extractFromSPARQLEndpoint(
							endpoint, query, "", "", format);
				} catch (RDFException e) {
					fail(e.getMessage());
				}
	
				long sizeAfter = connection.size(repository.getDataGraph());
	
				assertTrue(sizeBefore < sizeAfter);

            } finally {
            	if (connection !=null) { try { connection.close(); } catch (Throwable e) {}}
            }
		} catch (MalformedURLException ex) {
			logger.error("Bad URL for SPARQL endpoint: " + ex.getMessage());
		}
	}
}
