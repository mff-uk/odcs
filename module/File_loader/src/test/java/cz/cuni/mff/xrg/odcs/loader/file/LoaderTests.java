package cz.cuni.mff.xrg.odcs.loader.file;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import org.junit.Test;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

/**
 *
 * @author Petyr
 */
public class LoaderTests {
	
	// put path to the "addresses.ttl" here 
	private static final String PATH = "";
	
	//@Test
	public void loadRDFDataUnit_Extract() throws RDFException {
		// extractFromFile in: 1245 - 20 min
		// size: 408364
		TestEnvironment env = TestEnvironment.create();
		RDFDataUnit input = env.createRdfInput("input", false);

		Date start = new Date();		
		input.extractFromFile(new File(PATH), RDFFormat.TURTLE, "http://skodape.cz/");
		long durr = ((new Date()).getTime() - start.getTime()) / 1000;
		System.out.println("extractFromFile in: " + durr);
		System.out.println("size: " + input.getTripleCount());
	}
	
	//@Test
	public void loadRDFDataUnit_Add() throws RDFException {
		// addFromFile in: 1116 = 20 min
		// size: 408364
		TestEnvironment env = TestEnvironment.create();
		RDFDataUnit input = env.createRdfInput("input", false);

		Date start = new Date();		
		input.addFromFile(new File(PATH), RDFFormat.TURTLE);
		long durr = ((new Date()).getTime() - start.getTime()) / 1000;
		System.out.println("addFromFile in: " + durr);
		System.out.println("size: " + input.getTripleCount());
	}
	
	//@Test
	public void loadConnection() throws RDFException, IOException, RDFParseException, RepositoryException {
		// Connection.add in: 22 s !!
		// size: 408364
		TestEnvironment env = TestEnvironment.create();
		RDFDataUnit input = env.createRdfInput("input", false);

		Date start = new Date();		
		RepositoryConnection conn = input.getConnection();
		conn.add(new File(PATH), "http://skodape.cz/", RDFFormat.TURTLE);
		
		long durr = ((new Date()).getTime() - start.getTime()) / 1000;
		System.out.println("Connection.add in: " + durr);
		System.out.println("size: " + conn.size());
		
		// ?? if we ask for size on repository we get 0, why ?
	}	
	
	
}
