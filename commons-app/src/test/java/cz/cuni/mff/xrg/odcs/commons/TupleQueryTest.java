package cz.cuni.mff.xrg.odcs.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import cz.cuni.mff.xrg.odcs.commons.app.dataunit.rdf.localrdf.LocalRDFDataUnit;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.InvalidQueryException;
import cz.cuni.mff.xrg.odcs.rdf.help.OrderTupleQueryResult;

/**
 * @author Jiri Tomes
 */
public class TupleQueryTest {

    private static LocalRDFDataUnit repository;

    /**
     * Basic repository inicializing before test execution.
     * 
     * @throws IOException
     */
    @BeforeClass
    public static void inicialize() throws IOException {
        repository = new LocalRDFDataUnit(Files.createTempDirectory(null).toFile().getAbsolutePath(), "", "http://default");
    }

    /**
     * The repository is destroyed at the end of working.
     */
    @AfterClass
    public static void deleting() {
        repository.clear();
        repository.release();
    }

    /**
     * Cleaning repository before each test execution.
     */
    @Before
    public void cleaning() {
        repository.clear();
    }

    /**
     * Test executing ordered SELECT query for object.
     */
    @Test
    public void OrderTupleQueryResultTestForObject() throws RepositoryException {
        String orderSelectQuery = "SELECT ?x ?y ?z where {?x ?y ?z} ORDER BY ?x ?y ?z";

        String[] expectedNames = { "x", "y", "z" };

        String expectedVarName = "z";

        String[] expectedDataForVar = {
                "\"object\"", "http://object", "_:b23",
                "\"ob\"@en", "\"25\"^^<http://www.w3.org/2001/XMLSchema#integer>" };

        RepositoryConnection connection = repository.getConnection();
        ValueFactory factory = connection.getValueFactory();
        Resource subject = factory.createURI("http://subject");
        Resource subjectBlank = factory.createBNode("id");

        URI predicate = factory.createURI("http://predicate");

        Value objectLiteral = factory.createLiteral("object");
        Value object = factory.createURI("http://object");
        Value objectBlank = factory.createBNode("b23");
        Value objectLanguageLiteral = factory.createLiteral("ob", "en");
        Value objectTypedLiteral = factory.createLiteral("25",
                factory.createURI("http://www.w3.org/2001/XMLSchema#integer"));

        connection.add(subject, predicate, object, repository.getContexts());
        connection.add(subjectBlank, predicate, object, repository.getContexts());
        connection.add(subjectBlank, predicate, objectBlank, repository.getContexts());
        connection.add(subject, predicate, objectLanguageLiteral, repository.getContexts());
        connection.add(subject, predicate, objectTypedLiteral, repository.getContexts());

        assertEquals(5L, connection.size(repository.getContexts()));

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
        connection.close();
    }

    /**
     * Test executing ordered SELECT query for predicate.
     */
    @Test
    public void OrderTupleQueryResultTestForPredicate() throws RepositoryException {
        String orderSelectQuery = "SELECT ?b ?c where {?a ?b ?c} ORDER BY ?a ?b ?c";
        String[] expectedNames = { "b", "c" };

        String expectedVarName = "b";
        String[] expectedDataForVar = { "http://predicate", "http://p", "http://pred" };

        RepositoryConnection connection = repository.getConnection();
        ValueFactory factory = connection.getValueFactory();
        Resource subjectBlank = factory.createBNode("id");
        Resource subject = factory.createURI("http://subject");

        URI predicate = factory.createURI(expectedDataForVar[0]);
        URI p = factory.createURI(expectedDataForVar[1]);
        URI pred = factory.createURI(expectedDataForVar[2]);

        Value object = factory.createLiteral("object");
        Value objectBlank = factory.createBNode("blank");

        connection.add(subject, predicate, object, repository.getContexts());
        connection.add(subjectBlank, p, object, repository.getContexts());
        connection.add(subject, pred, objectBlank, repository.getContexts());

        assertEquals(3L, connection.size(repository.getContexts()));

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
        connection.close();
    }

    /**
     * Test executing ordered SELECT query for subject.
     */
    @Test
    public void OrderTupleQueryResultTestForSubject() throws RepositoryException {
        String orderSelectQuery = "SELECT ?x ?y WHERE {?x ?y ?z} ORDER BY ?x ?y ?z";

        String[] expectedNames = { "x", "y" };

        String expectedVarName = "x";
        String[] expectedDataForVar = { "http://subject", "http://s" };

        RepositoryConnection connection = repository.getConnection();
        ValueFactory factory = connection.getValueFactory();
        Resource subject = factory.createURI(expectedDataForVar[0]);
        Resource s = factory.createURI(expectedDataForVar[1]);

        URI predicate = factory.createURI("http://predicate");
        Value object = factory.createLiteral("object");

        connection.add(subject, predicate, object, repository.getContexts());
        connection.add(s, predicate, object, repository.getContexts());
        assertEquals(2L, connection.size(repository.getContexts()));

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
        connection.close();
    }

    /**
     * Test executing SELECT query for object.
     */
    @Test
    public void TupleQueryResultTestForObject() throws RepositoryException {

        String selectQuery = "SELECT ?x ?y ?z where {?x ?y ?z}";
        String[] expectedNames = { "x", "y", "z" };

        String expectedVarName = "z";

        String[] expectedDataForVar = {
                "\"object\"", "http://object", "_:b23",
                "\"ob\"@en", "\"25\"^^<http://www.w3.org/2001/XMLSchema#integer>" };

        RepositoryConnection connection = repository.getConnection();
        ValueFactory factory = connection.getValueFactory();
        Resource subject = factory.createURI("http://subject");

        Resource subjectBlank = factory.createBNode("id");

        URI predicate = factory.createURI("http://predicate");
        Value objectLiteral = factory.createLiteral("object");
        Value object = factory.createURI("http://object");

        factory = connection.getValueFactory();
        Value objectBlank = factory.createBNode("b23");
        Value objectLanguageLiteral = factory.createLiteral("ob", "en");
        Value objectTypedLiteral = factory.createLiteral("25",
                factory.createURI("http://www.w3.org/2001/XMLSchema#integer"));

        connection.add(subject, predicate, objectLiteral, repository.getContexts());
        connection.add(subjectBlank, predicate, object, repository.getContexts());
        connection.add(subjectBlank, predicate, objectBlank, repository.getContexts());
        connection.add(subject, predicate, objectLanguageLiteral, repository.getContexts());
        connection.add(subject, predicate, objectTypedLiteral, repository.getContexts());

        assertEquals(5L, connection.size(repository.getContexts()));
        connection.close();
    }

    /**
     * Test executing SELECT query for predicate.
     */
    @Test
    public void TupleQueryResultTestForPredicate() throws RepositoryException {

        String selectQuery = "SELECT ?b ?c where {?a ?b ?c}";
        String[] expectedNames = { "b", "c" };

        String expectedVarName = "b";
        String[] expectedDataForVar = { "http://predicate", "http://p", "http://pred" };

        RepositoryConnection connection = repository.getConnection();
        ValueFactory factory = connection.getValueFactory();
        Resource subject = factory.createURI("http://subject");
        Resource subjectBlank = factory.createBNode("id");

        URI predicate = factory.createURI(expectedDataForVar[0]);
        URI p = factory.createURI(expectedDataForVar[1]);
        URI pred = factory.createURI(expectedDataForVar[2]);

        Value object = factory.createLiteral("object");
        Value objectBlank = factory.createBNode("blank");

        connection.add(subject, predicate, object, repository.getContexts());
        connection.add(subjectBlank, p, object, repository.getContexts());
        connection.add(subject, pred, objectBlank, repository.getContexts());
        assertEquals(3L, connection.size(repository.getContexts()));
        connection.close();

    }

    /**
     * Test executing SELECT query for subject.
     */
    @Test
    public void TupleQueryResultTestForSubject() throws RepositoryException {

        String selectQuery = "SELECT ?x ?y where {?x ?y ?z}";
        String[] expectedNames = { "x", "y" };

        String expectedVarName = "x";
        String[] expectedDataForVar = { "http://subject", "http://s" };

        RepositoryConnection connection = repository.getConnection();
        ValueFactory factory = connection.getValueFactory();
        Resource subject = factory.createURI(expectedDataForVar[0]);
        Resource s = factory.createURI(expectedDataForVar[1]);

        URI predicate = factory.createURI("http://predicate");
        Value object = factory.createLiteral("object");

        connection.add(subject, predicate, object, repository.getContexts());
        connection.add(s, predicate, object, repository.getContexts());

        assertEquals(2L, connection.size(repository.getContexts()));
        connection.close();

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
