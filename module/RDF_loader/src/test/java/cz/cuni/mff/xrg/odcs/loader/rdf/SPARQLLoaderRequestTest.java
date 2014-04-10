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
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;

/**
 *
 * @author Jiri Tomes
 */
@Category(IntegrationTest.class)
public class SPARQLLoaderRequestTest {

	private final Logger logger = LoggerFactory.getLogger(
			SPARQLLoaderRequestTest.class);

	private static final RDFDataUnit repository = RDFDataUnitFactory
			.createLocalRDFRepo("");

	private static final String ENDPOINT = "http://localhost:8890/sparql-auth";

	private static final String TARGET_GRAPH = "http://tempGraph";

	private static final int LOADED_TRIPLES = 20;

	private static final String USER = "dba";

	private static final String PASSWORD = "dba";

	@AfterClass
	public static void deleteRDFDataUnit() {
		((ManagableRdfDataUnit)repository).delete();
	}

	@Before
	public void cleanRepository() throws RepositoryException {
        RepositoryConnection connection = repository.getConnection();
        connection.clear(repository.getDataGraph());
	}

	private DPUContext getTestContext() {
		TestEnvironment environment = TestEnvironment.create();
		return environment.getContext();
	}

	private URL getEndpoint() {
		URL endpoint = null;
		try {
			endpoint = new URL(ENDPOINT);
		} catch (MalformedURLException ex) {
			logger.debug(ex.getMessage());
		}

		return endpoint;
	}

	private void loadToEndpoint(LoaderEndpointParams params,
			String defaultGraphURI) throws RepositoryException {

		addDataToRepository();

		URL endpoint = getEndpoint();
		SPARQLoader loader = new SPARQLoader(repository, getTestContext(),
				params, false, USER, PASSWORD);
		try {
			loader.loadToSPARQLEndpoint(endpoint, defaultGraphURI, USER,
					PASSWORD,
					WriteGraphType.OVERRIDE, InsertType.STOP_WHEN_BAD_PART);

            RepositoryConnection connection = repository.getConnection();
			assertEquals(connection.size(repository.getDataGraph()), loader
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
	}

    private void addDataToRepository() throws RepositoryException {

        RepositoryConnection connection = repository.getConnection();
        ValueFactory factory = connection.getValueFactory();
        for (int i = 0; i < LOADED_TRIPLES; i++) {
            Resource subject = factory.createURI("http://A" + String.valueOf(
                    i + 1));

            URI predicate = factory.createURI("http://B" + String.valueOf(
                    i + 1));

            Value object = factory.createLiteral("C" + String.valueOf(i + 1));

            connection.add(subject, predicate, object, repository.getDataGraph());
        }
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
