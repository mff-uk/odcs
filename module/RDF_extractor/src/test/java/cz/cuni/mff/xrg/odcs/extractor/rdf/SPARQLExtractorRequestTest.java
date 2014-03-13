package cz.cuni.mff.xrg.odcs.extractor.rdf;

import cz.cuni.mff.xrg.odcs.commons.IntegrationTest;
import cz.cuni.mff.xrg.odcs.commons.dpu.DPUContext;
import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.data.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.ManagableRdfDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;

/**
 *
 * @author Jiri Tomes
 */
@Category(IntegrationTest.class)
public class SPARQLExtractorRequestTest {

	private final Logger logger = LoggerFactory.getLogger(
			SPARQLExtractorRequestTest.class);

	private static final RDFDataUnit repository = RDFDataUnitFactory
			.createLocalRDFRepo("");

	private static final String ENDPOINT = "http://dbpedia.org/sparql";

	private static final String DEFAULT_GRAPH = "http://dbpedia.org";
	
	private static final String NAMED_GRAPH = "http://dbpedia.org/void";

	private int EXTRACTED_TRIPLES = 15;

	private URL getEndpoint() {
		URL endpoint = null;
		try {
			endpoint = new URL(ENDPOINT);
		} catch (MalformedURLException ex) {
			logger.debug(ex.getMessage());
		}

		return endpoint;
	}

	private DPUContext getTestContext() {
		TestEnvironment environment = TestEnvironment.create();
		return environment.getContext();
	}

	private void extractFromEndpoint(ExtractorEndpointParams params) {
		URL endpoint = getEndpoint();
		String query = String.format(
				"CONSTRUCT {?x ?y ?z} WHERE {?x ?y ?z} LIMIT %s",
				EXTRACTED_TRIPLES);

		SPARQLExtractor extractor = new SPARQLExtractor(repository,
				getTestContext(), params);

		try {
			extractor.extractFromSPARQLEndpoint(endpoint, query);
			assertEquals(repository.getTripleCount(), EXTRACTED_TRIPLES);
		} catch (RDFException e) {
			fail(e.getMessage());
		}
	}

	@AfterClass
	public static void deleteRDFDataUnit() {
		((ManagableRdfDataUnit)repository).delete();
	}

	@Before
	public void cleanRepository() {
		repository.cleanAllData();
	}

	@Test
	public void GetSimpleTest() {
		String graphParam = "query";
		String defaultGraphParam = "";
		String namedGraphParam = "";
		ExtractorRequestType requestType = ExtractorRequestType.GET_URL_ENCODER;

		ExtractorEndpointParams params = new ExtractorEndpointParams(graphParam,
				defaultGraphParam, namedGraphParam, requestType);

		extractFromEndpoint(params);
	}
	
	@Test
	public void GetDefaultGraphParamTest() {
		String graphParam = "query";
		String defaultGraphParam = "default-graph-uri";
		String namedGraphParam = "";
		ExtractorRequestType requestType = ExtractorRequestType.GET_URL_ENCODER;

		ExtractorEndpointParams params = new ExtractorEndpointParams(graphParam,
				defaultGraphParam, namedGraphParam, requestType);
		
		params.addDefaultGraph(DEFAULT_GRAPH);

		extractFromEndpoint(params);
	}
	
	@Test
	public void GetAllGraphParamTest() {
		String graphParam = "query";
		String defaultGraphParam = "default-graph-uri";
		String namedGraphParam = "named-graph-uri";
		ExtractorRequestType requestType = ExtractorRequestType.GET_URL_ENCODER;

		ExtractorEndpointParams params = new ExtractorEndpointParams(graphParam,
				defaultGraphParam, namedGraphParam, requestType);
		
		params.addDefaultGraph(DEFAULT_GRAPH);
		params.addNamedGraph(NAMED_GRAPH);

		extractFromEndpoint(params);
	}
	
	@Test
	public void POSTEncodeSimpleTest() {
		String graphParam = "query";
		String defaultGraphParam = "";
		String namedGraphParam = "";
		ExtractorRequestType requestType = ExtractorRequestType.POST_URL_ENCODER;

		ExtractorEndpointParams params = new ExtractorEndpointParams(graphParam,
				defaultGraphParam, namedGraphParam, requestType);

		extractFromEndpoint(params);
	}
	
	@Test
	public void POSTEncodeDefaultGraphParamTest() {
		String graphParam = "query";
		String defaultGraphParam = "default-graph-uri";
		String namedGraphParam = "";
		ExtractorRequestType requestType = ExtractorRequestType.POST_URL_ENCODER;

		ExtractorEndpointParams params = new ExtractorEndpointParams(graphParam,
				defaultGraphParam, namedGraphParam, requestType);
		
		params.addDefaultGraph(DEFAULT_GRAPH);

		extractFromEndpoint(params);
	}
	
	@Test
	public void POSTEncodeAllGraphParamTest() {
		String graphParam = "query";
		String defaultGraphParam = "default-graph-uri";
		String namedGraphParam = "named-graph-uri";
		ExtractorRequestType requestType = ExtractorRequestType.POST_URL_ENCODER;

		ExtractorEndpointParams params = new ExtractorEndpointParams(graphParam,
				defaultGraphParam, namedGraphParam, requestType);
		
		params.addDefaultGraph(DEFAULT_GRAPH);
		params.addNamedGraph(NAMED_GRAPH);

		extractFromEndpoint(params);
	}

}
