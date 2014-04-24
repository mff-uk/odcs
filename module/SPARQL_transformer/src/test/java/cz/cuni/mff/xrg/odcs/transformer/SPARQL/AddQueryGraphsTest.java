package cz.cuni.mff.xrg.odcs.transformer.SPARQL;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;

/**
 * Test funcionality add default set graph for SPARQL update queries used in
 * SPARQL transformer.
 *
 * @author Jiri Tomes
 */
// TODO jan.marcek move this test to place where AddGraph...Query will reside
public class AddQueryGraphsTest {

	private final Logger logger = LoggerFactory.getLogger(
			AddQueryGraphsTest.class);

	private static RDFDataUnit repository;
	private static TestEnvironment testEnvironment;

	private static String GRAPH_NAME;

	@BeforeClass
	public static void initialize() {
		testEnvironment = new TestEnvironment();
		repository = testEnvironment.createRdfInput("LocalRepository", false);
		GRAPH_NAME = repository.getDataGraph().toString();
	}

	@AfterClass
	public static void clean() {
		testEnvironment.release();
	}
	// TODO jan.marcek move this test to place where AddGraph...Query will reside

	@Test
	public void addGraphToInsertDataQuery() {
		String originalQuery = "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n"
				+ "PREFIX ns: <http://example.org/ns#>"
				+ "INSERT DATA\n"
				+ "{ <http://example/book1>  ns:price  42 } ";

		String expectedQuery = String.format(
				"PREFIX dc: <http://purl.org/dc/elements/1.1/>\n"
				+ "PREFIX ns: <http://example.org/ns#>"
				+ "INSERT DATA\n"
				+ "{ GRAPH <%s> { <http://example/book1>  ns:price  42 } } ",
				GRAPH_NAME);
		// TODO jan.marcek move this test to place where AddGraph...Query will reside

//		compareQueries(originalQuery, expectedQuery);

	}
	// TODO jan.marcek move this test to place where AddGraph...Query will reside

	@Test
	public void addGraphToDeleteDataQuery() {
		String originalQuery = "PREFIX dc: <http://purl.org/dc/elements/1.1/>\n"
				+ "DELETE DATA\n"
				+ "{ <http://example/book2> dc:title \"David Copperfield\" ;\n"
				+ "dc:creator \"Edmund Wells\" . }";

		String expectedQuery = String.format(
				"PREFIX dc: <http://purl.org/dc/elements/1.1/>\n"
				+ "DELETE DATA\n"
				+ "{ GRAPH <%s> { <http://example/book2> dc:title \"David Copperfield\" ;\n"
				+ "dc:creator \"Edmund Wells\" . } }",
				GRAPH_NAME);

//		compareQueries(originalQuery, expectedQuery);

	}

	// TODO jan.marcek move this test to place where AddGraph...Query will reside
	@Test
	public void addGraphToInsertDeleteQuery() {
		String originalQuery = "PREFIX foaf:  <http://xmlns.com/foaf/0.1/>\n"
				+ "DELETE { ?person foaf:givenName 'Bill' }\n"
				+ "INSERT { ?person foaf:givenName 'William' }\n"
				+ "WHERE\n"
				+ "{ ?person foaf:givenName 'Bill' }";

		String expectedQuery = String.format(
				"PREFIX foaf:  <http://xmlns.com/foaf/0.1/>\n"
				+ " WITH <%s> "
				+ "DELETE { ?person foaf:givenName 'Bill' }\n"
				+ "INSERT { ?person foaf:givenName 'William' }\n"
				+ "WHERE\n"
				+ "{ ?person foaf:givenName 'Bill' }",
				GRAPH_NAME);

//		compareQueries(originalQuery, expectedQuery);

	}

// TODO jan.marcek move this test to place where AddGraph...Query will reside
//	private void compareQueries(String originalQuery, String expectedQuery) {
//
//		String returnedQuery = repository.AddGraphToUpdateQuery(originalQuery);
//
//		boolean areSame = expectedQuery.equals(returnedQuery);
//		assertTrue("Queries are not SAME", areSame);
//
//		boolean canBeExecuted = tryExecuteUpdateQuery(originalQuery);
//		assertTrue("This update query can not be executed by transformer",
//				canBeExecuted);
//	}

	private boolean tryExecuteUpdateQuery(String updateQuery) {
		try {
			repository.executeSPARQLUpdateQuery(updateQuery);
			return true;
		} catch (RDFException e) {
			logger.debug("Exception duering exectution query " + updateQuery + e
					.getMessage(), e);
			return false;
		}

	}
}
