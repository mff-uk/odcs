package cz.cuni.xrg.intlib.commons.app.data;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.MutablePair;
import org.junit.Test;

/**
 * Test suite for {@link EdgeCompiler} class.
 * 
 * @author Petyr
 *
 */
public class EdgeCompilerTest {

	private EdgeCompiler edgeCompiler = new EdgeCompiler(null); 
	
	@Test
	public void simpleTest() {
		List<String> sourceNames = new LinkedList<>();
		sourceNames.add("out_A");
		sourceNames.add("out_B");
		sourceNames.add("out_C");
		sourceNames.add("out_D");
		List<String> targetNames = new LinkedList<>();
		targetNames.add("in_A");
		targetNames.add("in_B");
		targetNames.add("in_C");
		targetNames.add("in_D");
		// create mapping 
		// out_A -> in_B
		MutablePair<List<Integer>, Integer> mapping_A = new MutablePair<>();
		mapping_A.left = new LinkedList<>();
		mapping_A.left.add(sourceNames.indexOf("out_A"));
		mapping_A.right = targetNames.indexOf("in_B");
		// out_B -> in_A
		MutablePair<List<Integer>, Integer> mapping_B = new MutablePair<>();
		mapping_B.left = new LinkedList<>();
		mapping_B.left.add(sourceNames.indexOf("out_B"));
		mapping_B.right = targetNames.indexOf("in_A");
		// out_C -> in_C
		MutablePair<List<Integer>, Integer> mapping_C = new MutablePair<>();
		mapping_C.left = new LinkedList<>();
		mapping_C.left.add(sourceNames.indexOf("out_C"));
		mapping_C.right = targetNames.indexOf("in_C");		
		// add to mapping
		List<MutablePair<List<Integer>, Integer>> mappings = new LinkedList<>();
		mappings.add(mapping_A);
		mappings.add(mapping_B);
		mappings.add(mapping_C);
		// convert into script ..
		String script = edgeCompiler.compileScript(mappings, sourceNames, targetNames);
		// convert back from script
		List<MutablePair<List<Integer>, Integer>> outputMappings =
				edgeCompiler.decompileMapping(script, sourceNames, targetNames);
		// and should be the same
		assertArrayEquals(mappings.toArray(), outputMappings.toArray());
	}
	
	@Test
	public void manyToOneTest() {
		List<String> sourceNames = new LinkedList<>();
		sourceNames.add("out_A");
		sourceNames.add("out_B");
		sourceNames.add("out_C");
		List<String> targetNames = new LinkedList<>();
		targetNames.add("in_A");
		targetNames.add("in_B");
		// create mapping 
		// out_A -> in_A
		// out_B -> in_A
		// out_C -> in_A
		MutablePair<List<Integer>, Integer> mapping_A = new MutablePair<>();
		mapping_A.left = new LinkedList<>();
		mapping_A.left.add(sourceNames.indexOf("out_A"));
		mapping_A.left.add(sourceNames.indexOf("out_B"));
		mapping_A.left.add(sourceNames.indexOf("out_C"));
		mapping_A.right = targetNames.indexOf("in_A");
		// add to mapping
		List<MutablePair<List<Integer>, Integer>> mappings = new LinkedList<>();
		mappings.add(mapping_A);
		// convert into script ..
		String script = edgeCompiler.compileScript(mappings, sourceNames, targetNames);
		// convert back from script
		List<MutablePair<List<Integer>, Integer>> outputMappings =
				edgeCompiler.decompileMapping(script, sourceNames, targetNames);
		// and should be the same
		assertArrayEquals(mappings.toArray(), outputMappings.toArray());
	}	
}
