package cz.cuni.mff.xrg.odcs.transformer.SPARQL;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.data.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.ManagableRdfDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.*;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jiri Tomes
 */
public class DPUReplacementTest {

	private static ManagableRdfDataUnit repository;

    protected final Logger LOG = LoggerFactory.getLogger(getClass());


    /**
	 * Basic repository inicializing before test execution.
	 */
	@BeforeClass
	public static void inicialize() {
		repository = RDFDataUnitFactory.createLocalRDFRepo("");
	}

	/**
	 * The repository is destroyed at the end of working.
	 */
	@AfterClass
	public static void deleting() {
		repository.delete();
	}

	/**
	 * Cleaning repository before each test execution.
	 */
	@Before
	public void cleaning() {
		repository.clean();
	}

	/**
	 * Test DPU replacement on SPARQL CONSTRUCT query.
	 */
	@Test
	public void constructQueryTest() {

		String query = "CONSTRUCT {?s ?p ?o. ?x ?y ?z} where "
				+ "{ graph ?g_input {?s ?p ?o} graph ?g_optional1 {?x ?y ?z}}";

		boolean isConstruct = true;

		// prepare test environment
		TestEnvironment env = TestEnvironment.create();

		try {

			SPARQLTransformer transformer = new SPARQLTransformer();
			SPARQLTransformerConfig config = new SPARQLTransformerConfig(
					query, isConstruct);

			transformer.configureDirectly(config);

			RDFDataUnit input = env.createRdfInput("input", false);
			RDFDataUnit optional = env.createRdfInput("optional1", false);
            RepositoryConnection connection = null;
            try {
                connection = input.getConnection();
                connection.add(input.createURI("http://s"), input.createURI(
                        "http://p"), input.createURI("http://o"), input.getDataGraph());
                connection.add(input.createURI("http://subject"), input.createURI(
                        "http://predicate"), input.createURI("http://object"), input.getDataGraph());

            } catch (RepositoryException e) {
                LOG.error("Error", e);
            }


            try {
                RepositoryConnection connection2 = optional.getConnection();
                ValueFactory factory = connection2.getValueFactory();
                connection2.add(factory.createBNode("n25"), optional
                        .createURI("http://hasName"), optional.createLiteral("NAME"), optional.getDataGraph());

            } catch (RepositoryException e) {
                LOG.error("Error", e);
            }

			assertEquals(2L, input.getTripleCount());
			assertEquals(1L, optional.getTripleCount());

			RDFDataUnit output = env.createRdfOutput("output", false);

			env.run(transformer);

			assertEquals("Count of triples are not same", 3L, output
					.getTripleCount());
			env.release();


		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			env.release();
		}
	}

	/**
	 * Test DPU replacement on SPARQL UPDATE query.
	 */
	@Test
	public void updateQueryTest() {
		String query = "PREFIX foaf:  <http://xmlns.com/foaf/0.1/> \n"
				+ "INSERT { ?person foaf:givenName \"William\" }\n"
				+ "WHERE {"
				+ " graph ?g_optional1 {?person foaf:givenName \"Bill\" }"
				+ " graph ?g_input {?person ?x ?y }"
				+ "} ";

		boolean isConstruct = false;

		String expectedObjectName = "William";

		// prepare test environment
		TestEnvironment env = TestEnvironment.create();

		try {

			SPARQLTransformer transformer = new SPARQLTransformer();
			SPARQLTransformerConfig config = new SPARQLTransformerConfig(
					query, isConstruct);

			transformer.configureDirectly(config);

			RDFDataUnit input = env.createRdfInput("input", false);
			RDFDataUnit optional = env.createRdfInput("optional1", false);

            RepositoryConnection connection = null;
            connection = input.getConnection();
            connection.add(input.createURI("http://person"), input.createURI(
					"http://predicate"), input.createURI("http://object"), input.getDataGraph());


            RepositoryConnection connection2 = null;
            connection2 = optional.getConnection();
            connection2.add(optional.createURI("http://person"), optional
					.createURI("http://xmlns.com/foaf/0.1/givenName"), optional
					.createLiteral("Bill"),optional.getDataGraph());

			assertEquals(1L, input.getTripleCount());
			assertEquals(1L, optional.getTripleCount());

			RDFDataUnit output = env.createRdfOutput("output", false);

			env.run(transformer);

			assertEquals("Count of triples are not same", 3L, output
					.getTripleCount());

			List<Statement> outputTriples = output.getTriples();

			boolean newInsertedTripleFound = false;
			for (Statement next : outputTriples) {
				if (expectedObjectName.equals(next.getObject().stringValue())) {
					newInsertedTripleFound = true;
					break;
				}
			}

			assertTrue("New inserted triple not found", newInsertedTripleFound);


		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			env.release();
		}
	}
}
