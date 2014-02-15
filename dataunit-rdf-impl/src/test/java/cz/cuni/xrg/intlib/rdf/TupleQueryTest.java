package cz.cuni.xrg.intlib.rdf;

import cz.cuni.mff.xrg.odcs.rdf.data.RDFDataUnitFactory;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.help.MyTupleQueryResultIf;
import cz.cuni.mff.xrg.odcs.rdf.help.OrderTupleQueryResult;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.ManagableRdfDataUnit;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;

import org.junit.*;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.Binding;
import static org.junit.Assert.*;

/**
 *
 * @author Jiri Tomes
 */
public class TupleQueryTest {

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
	 * Test executing ordered SELECT query for object.
	 */
	@Test
	public void OrderTupleQueryResultTestForObject() {
		String orderSelectQuery = "SELECT ?x ?y ?z where {?x ?y ?z} ORDER BY ?x ?y ?z";

		String[] expectedNames = {"x", "y", "z"};

		String expectedVarName = "z";

		String[] expectedDataForVar = {
			"\"object\"", "http://object", "_:b23",
			"\"ob\"@en", "\"25\"^^<http://www.w3.org/2001/XMLSchema#integer>"};

		Resource subject = repository.createURI("http://subject");
		Resource subjectBlank = repository.createBlankNode("id");

		URI predicate = repository.createURI("http://predicate");

		Value objectLiteral = repository.createLiteral("object");
		Value object = repository.createURI("http://object");
		Value objectBlank = repository.createBlankNode("b23");
		Value objectLanguageLiteral = repository.createLiteral("ob", "en");
		Value objectTypedLiteral = repository.createLiteral("25",
				repository.createURI("http://www.w3.org/2001/XMLSchema#integer"));

		repository.addTriple(subject, predicate, objectLiteral);
		repository.addTriple(subjectBlank, predicate, object);
		repository.addTriple(subjectBlank, predicate, objectBlank);
		repository.addTriple(subject, predicate, objectLanguageLiteral);
		repository.addTriple(subject, predicate, objectTypedLiteral);

		assertEquals(5L, repository.getTripleCount());

		try {
			OrderTupleQueryResult result = repository
					.executeOrderSelectQueryAsTuples(orderSelectQuery);

			List<BindingSet> bindings = new ArrayList<>();

			List<String> names = result.getBindingNames();
			boolean sameNames = sameNames(names, expectedNames);
			assertTrue("Name of variables are not same", sameNames);

			while (result.hasNext()) {
				bindings.add(result.next());
			}

			boolean sameData = sameData(bindings, expectedVarName,
					expectedDataForVar);
			assertTrue("Expected data for object are not same", sameData);

		} catch (InvalidQueryException | QueryEvaluationException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test executing ordered SELECT query for predicate.
	 */
	@Test
	public void OrderTupleQueryResultTestForPredicate() {
		String orderSelectQuery = "SELECT ?b ?c where {?a ?b ?c} ORDER BY ?a ?b ?c";
		String[] expectedNames = {"b", "c"};

		String expectedVarName = "b";
		String[] expectedDataForVar = {"http://predicate", "http://p", "http://pred"};

		Resource subject = repository.createURI("http://subject");
		Resource subjectBlank = repository.createBlankNode("id");

		URI predicate = repository.createURI(expectedDataForVar[0]);
		URI p = repository.createURI(expectedDataForVar[1]);
		URI pred = repository.createURI(expectedDataForVar[2]);

		Value object = repository.createLiteral("object");
		Value objectBlank = repository.createBlankNode("blank");

		repository.addTriple(subject, predicate, object);
		repository.addTriple(subjectBlank, p, object);
		repository.addTriple(subject, pred, objectBlank);

		assertEquals(3L, repository.getTripleCount());

		try {
			OrderTupleQueryResult result = repository
					.executeOrderSelectQueryAsTuples(orderSelectQuery);

			List<BindingSet> bindings = new ArrayList<>();

			List<String> names = result.getBindingNames();
			boolean sameNames = sameNames(names, expectedNames);
			assertTrue("Name of variables are not same", sameNames);

			while (result.hasNext()) {
				bindings.add(result.next());
			}

			boolean sameData = sameData(bindings, expectedVarName,
					expectedDataForVar);
			assertTrue("Expected data for predicate are not same", sameData);

		} catch (InvalidQueryException | QueryEvaluationException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test executing ordered SELECT query for subject.
	 */
	@Test
	public void OrderTupleQueryResultTestForSubject() {
		String orderSelectQuery = "SELECT ?x ?y WHERE {?x ?y ?z} ORDER BY ?x ?y ?z";

		String[] expectedNames = {"x", "y"};

		String expectedVarName = "x";
		String[] expectedDataForVar = {"http://subject", "http://s"};

		Resource subject = repository.createURI(expectedDataForVar[0]);
		Resource s = repository.createURI(expectedDataForVar[1]);

		URI predicate = repository.createURI("http://predicate");
		Value object = repository.createLiteral("object");

		repository.addTriple(subject, predicate, object);
		repository.addTriple(s, predicate, object);

		assertEquals(2L, repository.getTripleCount());


		try {
			OrderTupleQueryResult result = repository
					.executeOrderSelectQueryAsTuples(orderSelectQuery);

			List<BindingSet> bindings = new ArrayList<>();

			List<String> names = result.getBindingNames();
			boolean sameNames = sameNames(names, expectedNames);
			assertTrue("Name of variables are not same", sameNames);

			while (result.hasNext()) {
				bindings.add(result.next());
			}

			boolean sameData = sameData(bindings, expectedVarName,
					expectedDataForVar);
			assertTrue("Expected data for subject are not same", sameData);

		} catch (InvalidQueryException | QueryEvaluationException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test executing SELECT query for object.
	 */
	@Test
	public void TupleQueryResultTestForObject() {

		String selectQuery = "SELECT ?x ?y ?z where {?x ?y ?z}";
		String[] expectedNames = {"x", "y", "z"};

		String expectedVarName = "z";

		String[] expectedDataForVar = {
			"\"object\"", "http://object", "_:b23",
			"\"ob\"@en", "\"25\"^^<http://www.w3.org/2001/XMLSchema#integer>"};

		Resource subject = repository.createURI("http://subject");
		Resource subjectBlank = repository.createBlankNode("id");

		URI predicate = repository.createURI("http://predicate");

		Value objectLiteral = repository.createLiteral("object");
		Value object = repository.createURI("http://object");
		Value objectBlank = repository.createBlankNode("b23");
		Value objectLanguageLiteral = repository.createLiteral("ob", "en");
		Value objectTypedLiteral = repository.createLiteral("25",
				repository.createURI("http://www.w3.org/2001/XMLSchema#integer"));

		repository.addTriple(subject, predicate, objectLiteral);
		repository.addTriple(subjectBlank, predicate, object);
		repository.addTriple(subjectBlank, predicate, objectBlank);
		repository.addTriple(subject, predicate, objectLanguageLiteral);
		repository.addTriple(subject, predicate, objectTypedLiteral);

		assertEquals(5L, repository.getTripleCount());

		try {
			List<BindingSet> bindings = new ArrayList<>();
			MyTupleQueryResultIf tupleQueryResult = repository
					.executeSelectQueryAsTuples(selectQuery);

			List<String> names = tupleQueryResult.getBindingNames();
			boolean sameNames = sameNames(names, expectedNames);
			assertTrue("Name of variables are not same", sameNames);

			while (tupleQueryResult.hasNext()) {
				bindings.add(tupleQueryResult.next());
			}

			boolean sameData = sameData(bindings, expectedVarName,
					expectedDataForVar);
			assertTrue("Expected data for object are not same", sameData);

		} catch (InvalidQueryException | QueryEvaluationException e) {
			fail(e.getMessage());
		}

	}

	/**
	 * Test executing SELECT query for predicate.
	 */
	@Test
	public void TupleQueryResultTestForPredicate() {

		String selectQuery = "SELECT ?b ?c where {?a ?b ?c}";
		String[] expectedNames = {"b", "c"};

		String expectedVarName = "b";
		String[] expectedDataForVar = {"http://predicate", "http://p", "http://pred"};

		Resource subject = repository.createURI("http://subject");
		Resource subjectBlank = repository.createBlankNode("id");

		URI predicate = repository.createURI(expectedDataForVar[0]);
		URI p = repository.createURI(expectedDataForVar[1]);
		URI pred = repository.createURI(expectedDataForVar[2]);

		Value object = repository.createLiteral("object");
		Value objectBlank = repository.createBlankNode("blank");

		repository.addTriple(subject, predicate, object);
		repository.addTriple(subjectBlank, p, object);
		repository.addTriple(subject, pred, objectBlank);

		assertEquals(3L, repository.getTripleCount());

		try {
			List<BindingSet> bindings = new ArrayList<>();
			MyTupleQueryResultIf tupleQueryResult = repository
					.executeSelectQueryAsTuples(selectQuery);

			List<String> names = tupleQueryResult.getBindingNames();
			boolean sameNames = sameNames(names, expectedNames);
			assertTrue("Name of variables are not same", sameNames);

			while (tupleQueryResult.hasNext()) {
				bindings.add(tupleQueryResult.next());
			}

			boolean sameData = sameData(bindings, expectedVarName,
					expectedDataForVar);
			assertTrue("Expected data for predicate are not same", sameData);

		} catch (InvalidQueryException | QueryEvaluationException e) {
			fail(e.getMessage());
		}

	}

	/**
	 * Test executing SELECT query for subject.
	 */
	@Test
	public void TupleQueryResultTestForSubject() {

		String selectQuery = "SELECT ?x ?y where {?x ?y ?z}";
		String[] expectedNames = {"x", "y"};

		String expectedVarName = "x";
		String[] expectedDataForVar = {"http://subject", "http://s"};

		Resource subject = repository.createURI(expectedDataForVar[0]);
		Resource s = repository.createURI(expectedDataForVar[1]);

		URI predicate = repository.createURI("http://predicate");
		Value object = repository.createLiteral("object");

		repository.addTriple(subject, predicate, object);
		repository.addTriple(s, predicate, object);

		assertEquals(2L, repository.getTripleCount());

		try {
			List<BindingSet> bindings = new ArrayList<>();
			MyTupleQueryResultIf tupleQueryResult = repository
					.executeSelectQueryAsTuples(selectQuery);

			List<String> names = tupleQueryResult.getBindingNames();
			boolean sameNames = sameNames(names, expectedNames);
			assertTrue("Name of variables are not same", sameNames);

			while (tupleQueryResult.hasNext()) {
				bindings.add(tupleQueryResult.next());
			}

			boolean sameData = sameData(bindings, expectedVarName,
					expectedDataForVar);
			assertTrue("Expected data for subject are not same", sameData);

		} catch (InvalidQueryException | QueryEvaluationException e) {
			fail(e.getMessage());
		}

	}

	private boolean sameData(List<BindingSet> bindings, String varName,
			String[] expectedData) {

		for (BindingSet nextBinding : bindings) {
			Binding binding = nextBinding.getBinding(varName);

			Value value = binding.getValue();
			if (value == null) {
				return false;
			} else {
				boolean dataFound = false;
				String stringValue = value.toString();

				for (String next : expectedData) {
					if (next.equals(stringValue)) {
						dataFound = true;
						break;
					}
				}
				if (!dataFound) {
					return false;
				}
			}
		}

		return true;
	}

	private boolean sameNames(List<String> names, String[] expectedNames) {
		if (expectedNames == null) {
			return false;
		} else {

			boolean definedAll = true;

			for (String expectedName : expectedNames) {
				if (!names.contains(expectedName)) {
					definedAll = false;
					break;
				}
			}
			return definedAll;
		}
	}
}
