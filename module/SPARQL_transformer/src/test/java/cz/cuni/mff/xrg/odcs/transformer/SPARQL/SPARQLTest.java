package cz.cuni.mff.xrg.odcs.transformer.SPARQL;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Test;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;

public class SPARQLTest {

	@Test
	public void constructAllTest() throws Exception  {
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
		RDFDataUnit input = env.createRdfInput("input", false);
		RDFDataUnit output = env.createRdfOutput("output", false);
		
		InputStream inputStream= Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("metadata.ttl");
		
		RepositoryConnection connection = null;
		RepositoryConnection connection2 = null;
		try {
	        connection = input.getConnection();
	        String baseURI = "";
	        connection.add(inputStream, baseURI, RDFFormat.TURTLE, input.getDataGraph());
	        
			// some triples has been loaded
			assertTrue(connection.size(input.getDataGraph()) > 0);
			// run
			env.run(trans);
            connection2 = output.getConnection();
			// verify result
			assertTrue(connection.size(input.getDataGraph()) == connection2.size(output.getDataGraph()));
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (RepositoryException ex) {
					// eat close exception, we cannot do anything clever here
				}
			}
			if (connection2 != null) {
				try {
					connection2.close();
				} catch (RepositoryException ex) {
					// eat close exception, we cannot do anything clever here
				}
			}			
			// release resources
			env.release();
		}
	}
}
