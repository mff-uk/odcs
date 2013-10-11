package cz.cuni.mff.xrg.odcs.transformer.SPARQL;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openrdf.rio.RDFFormat;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;

public class SPARQLTest {

	@Test
	public void constructAllTest() throws Exception {
		// prepare dpu
		SPARQLTransformer trans = new SPARQLTransformer();
		SPARQLTransformerConfig config = new SPARQLTransformerConfig();
		config.isConstructType = true;
		config.SPARQL_Update_Query = "CONSTRUCT {?s ?p ?o} where {?s ?p ?o }";
		trans.configureDirectly(config);

		// prepare test environment
		TestEnvironment env = TestEnvironment.create();
		// prepare data units
		RDFDataUnit input = env.createRdfInputFromResource("input", false,
				"metadata.ttl", RDFFormat.TURTLE);
		RDFDataUnit output = env.createRdfOutput("output", false);
		
		// some triples has been loaded 
		assertTrue(input.getTripleCount() > 0);
		// run
		try {
			env.run(trans);

			// verify result
			assertTrue(input.getTripleCount() == output.getTripleCount());
		} finally {
			// release resources
			env.release();
		}
	}

}
