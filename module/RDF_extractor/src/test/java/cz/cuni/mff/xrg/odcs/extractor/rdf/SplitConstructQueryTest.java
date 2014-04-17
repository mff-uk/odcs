package cz.cuni.mff.xrg.odcs.extractor.rdf;

import cz.cuni.mff.xrg.odcs.rdf.data.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.ManagableRdfDataUnit;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author Jiri Tomes
 */
public class SplitConstructQueryTest {

	private static ManagableRdfDataUnit repository;

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
		repository.clean();
		repository.release();
	}

	/**
	 * Cleaning repository before each test execution.
	 */
	@Before
	public void cleaning() {
		repository.clean();
	}

	/**
	 * Split test for SPARQL CONSTRUCT query with LIMIT clause in subquery.
	 */
	@Test
	public void constructSplitTest1() {

		String constructQuery = "PREFIX pred: <http://xmlns.com/foaf/0.1/> "
				+ "CONSTRUCT {\n"
				+ "  ?focal pred:icate \"Object\" .\n"
				+ "  ?other pred:icate ?shared .}\n"
				+ "WHERE {\n"
				+ "    ?focal pred:icate ?shared ;\n"
				+ "           pred:info ?etc ;\n"
				+ "           a \"foobar\" .\n"
				+ "    { \n"
				+ "      SELECT ?shared {\n"
				+ "        ?other pred:icate ?shared .\n"
				+ "      }\n"
				+ "    LIMIT 500\n"
				+ "    }\n"
				+ "}";

		String expectedQuery = "PREFIX pred: <http://xmlns.com/foaf/0.1/> "
				+ "CONSTRUCT {\n"
				+ "  ?focal pred:icate \"Object\" .\n"
				+ "  ?other pred:icate ?shared .}\n"
				+ "WHERE {\n"
				+ "    ?focal pred:icate ?shared ;\n"
				+ "           pred:info ?etc ;\n"
				+ "           a \"foobar\" .\n"
				+ "    { \n"
				+ "      SELECT ?shared {\n"
				+ "        ?other pred:icate ?shared .\n"
				+ "      }\n"
				+ "    LIMIT 500\n"
				+ "    }\n"
				+ "} ORDER BY  ?focal ?other ?shared LIMIT 10 OFFSET 0";

		int splitSize = 10;

		SplitConstructQueryHelper helper = new SplitConstructQueryHelper(
				constructQuery, splitSize);

		try {
			String splitConstructQuery = helper.getSplitConstructQuery();
			assertEquals("Result split CONSTRUCT queries are not same",
					expectedQuery,
					splitConstructQuery);
		} catch (InvalidQueryException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Spit test for SPARQL CONSTRUCT query cointains OFFSET clause not in
	 * subquery.
	 */
	@Test
	public void constructSplitTest2() {

		String constructQuery = "PREFIX pred: <http://xmlns.com/foaf/0.1/> "
				+ "CONSTRUCT {\n"
				+ "  ?focal pred:icate \"Object\" .\n"
				+ "  ?other pred:icate ?shared .}\n"
				+ "WHERE {\n"
				+ "    ?focal pred:icate ?shared ;\n"
				+ "           pred:info ?etc ;\n"
				+ "           a \"foobar\" .\n"
				+ "    { \n"
				+ "      SELECT ?shared {\n"
				+ "        ?other pred:icate ?shared .\n"
				+ "      }\n"
				+ "    LIMIT 500\n"
				+ "    }\n"
				+ "} OFFSET 8";

		int splitSize = 10;

		SplitConstructQueryHelper helper = new SplitConstructQueryHelper(
				constructQuery, splitSize);

		boolean passed = false;
		try {
			passed = true;
			String splitConstructQuery = helper.getSplitConstructQuery();
			fail("SPARQL construct query contains OFFSET clause");
		} catch (InvalidQueryException e) {
			assertTrue("SPARQL construct query contains OFFSET clause", passed);
		}
	}
}
