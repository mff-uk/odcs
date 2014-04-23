package cz.cuni.mff.xrg.odcs.transformer.SPARQL;

import cz.cuni.mff.xrg.odcs.rdf.data.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.repositories.LocalRDFDataUnit;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;

/**
 * Test funcionality add default set graph for SPARQL update queries used in
 * SPARQL transformer.
 *
 * @author Jiri Tomes
 */
public class AddQueryGraphsTest {

	private final Logger logger = LoggerFactory.getLogger(
			AddQueryGraphsTest.class);

	private static LocalRDFDataUnit repository;

	private static String GRAPH_NAME;

	@BeforeClass
	public static void initialize() {
		repository = RDFDataUnitFactory.createLocalRDFRepo("Local repository");
		GRAPH_NAME = repository.getDataGraph().toString();
	}

	@AfterClass
	public static void clean() {
		repository.clear();
		repository.release();
	}

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

		compareQueries(originalQuery, expectedQuery);

	}

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

		compareQueries(originalQuery, expectedQuery);

	}

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

		compareQueries(originalQuery, expectedQuery);

	}

	private void compareQueries(String originalQuery, String expectedQuery) {

		String returnedQuery = repository.AddGraphToUpdateQuery(originalQuery);

		boolean areSame = expectedQuery.equals(returnedQuery);
		assertTrue("Queries are not SAME", areSame);

		boolean canBeExecuted = tryExecuteUpdateQuery(originalQuery);
		assertTrue("This update query can not be executed by transformer",
				canBeExecuted);
	}

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
