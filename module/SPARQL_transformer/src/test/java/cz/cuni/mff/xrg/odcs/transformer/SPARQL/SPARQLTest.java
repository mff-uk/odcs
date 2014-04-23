package cz.cuni.mff.xrg.odcs.transformer.SPARQL;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;

public class SPARQLTest {

	@Test
	public void constructAllTest() throws Exception {
		// prepare dpu
		SPARQLTransformer trans = new SPARQLTransformer();

		String SPARQL_Update_Query = "CONSTRUCT {?s ?p ?o} where {?s ?p ?o }";
		boolean isConstructType = true;

		SPARQLTransformerConfig config = new SPARQLTransformerConfig(
				SPARQL_Update_Query, isConstructType);


		trans.configureDirectly(config);

		// prepare test environment
		TestEnvironment env =  new TestEnvironment();
		// prepare data units
		RDFDataUnit input = env.createRdfInputFromResource("input", false,
				"metadata.ttl", RDFFormat.TURTLE);
		RDFDataUnit output = env.createRdfOutput("output", false);

		// some triples has been loaded
        RepositoryConnection connection = input.getConnection();
		assertTrue(connection.size(input.getDataGraph()) > 0);
		// run
		try {
			env.run(trans);
            RepositoryConnection connection2 = output.getConnection();
			// verify result
			assertTrue(connection.size(input.getDataGraph()) == connection2.size(output.getDataGraph()));
		} finally {
			// release resources
			env.release();
		}
	}
}
