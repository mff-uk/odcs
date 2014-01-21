package cz.cuni.mff.xrg.odcs.loader.file;

import cz.cuni.mff.xrg.odcs.dpu.test.TestEnvironment;
import cz.cuni.mff.xrg.odcs.rdf.exceptions.RDFException;
import cz.cuni.mff.xrg.odcs.rdf.handlers.StatisticalHandler;
import cz.cuni.mff.xrg.odcs.rdf.interfaces.RDFDataUnit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import org.junit.Test;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.util.RDFInserter;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;

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
	
	//@Test
	public void loadRDFParse() throws RDFException, RepositoryException, FileNotFoundException, IOException, RDFParseException, RDFHandlerException {
		TestEnvironment env = TestEnvironment.create();
		RDFDataUnit input = env.createRdfInput("input", false);		
		
		// create handler and bind it to the graph
		//StatisticalHandler handler = new StatisticalHandler(input.getConnection());
		
		RepositoryConnection conn = input.getConnection();
		
		// use RDFInserter (is extended by StatisticalHandler)
		RDFHandler handler = new RDFInserter(conn);
		
		RDFParser parser = Rio.createParser(RDFFormat.TURTLE);
		parser.setRDFHandler(handler);
		
		Date start = new Date();
		
		// comment this line
		conn.begin();
		
		// -> the problem probably was, that the RDF comits after every addition !!
		// which cause wirte on hdd, write on hdd ..  
		
		InputStreamReader is = new InputStreamReader(new FileInputStream(PATH));
		parser.parse(is, "");
		
		// and this line .. and the parsing will take 20 minutes .. 
		conn.commit();
		
		long durr = ((new Date()).getTime() - start.getTime()) / 1000;
		System.out.println("Connection.add in: " + durr);
		System.out.println("size: " + conn.size());	
	}
	
}
