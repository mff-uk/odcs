package cz.cuni.mff.xrg.odcs.commons.app.data;

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

	private final EdgeCompiler edgeCompiler = new EdgeCompiler(); 
	
	@Test
	public void simpleTest() {
		List<DataUnitDescription> sources = new LinkedList<>();
		sources.add(DataUnitDescription.createOutput("out_A", "", ""));
		sources.add(DataUnitDescription.createOutput("out_B", "", ""));
		sources.add(DataUnitDescription.createOutput("out_C", "", ""));
		sources.add(DataUnitDescription.createOutput("out_D", "", ""));
		List<DataUnitDescription> targets = new LinkedList<>();
		targets.add(DataUnitDescription.createInput("in_A", "", "", false));
		targets.add(DataUnitDescription.createInput("in_B", "", "", false));
		targets.add(DataUnitDescription.createInput("in_C", "", "", false));
		targets.add(DataUnitDescription.createInput("in_D", "", "", false));
		// create mapping 
		// out_A -> in_B
		MutablePair<List<Integer>, Integer> mapping_A = new MutablePair<>();
		mapping_A.left = new LinkedList<>();
		mapping_A.left.add(0);
		mapping_A.right = 1;
		// out_B -> in_A
		MutablePair<List<Integer>, Integer> mapping_B = new MutablePair<>();
		mapping_B.left = new LinkedList<>();
		mapping_B.left.add(2);
		mapping_B.right = 0;
		// out_C -> in_C
		MutablePair<List<Integer>, Integer> mapping_C = new MutablePair<>();
		mapping_C.left = new LinkedList<>();
		mapping_C.left.add(3);
		mapping_C.right = 3;		
		// add to mapping
		List<MutablePair<List<Integer>, Integer>> mappings = new LinkedList<>();
		mappings.add(mapping_A);
		mappings.add(mapping_B);
		mappings.add(mapping_C);
		// convert into script ..
		String script = edgeCompiler.compile(mappings, sources, targets);
		// convert back from script
		List<MutablePair<List<Integer>, Integer>> outputMappings =
				edgeCompiler.decompile(script, sources, targets);
		// and should be the same
		assertArrayEquals(mappings.toArray(), outputMappings.toArray());
	}
	
	// TODO Enable after the EdgeCompiler update !!
	//@Test
	public void manyToOneTest() {
		List<DataUnitDescription> sources = new LinkedList<>();
		sources.add(DataUnitDescription.createOutput("out_A", "", ""));
		sources.add(DataUnitDescription.createOutput("out_B", "", ""));
		sources.add(DataUnitDescription.createOutput("out_C", "", ""));
		List<DataUnitDescription> targets = new LinkedList<>();
		targets.add(DataUnitDescription.createInput("in_A", "", "", false));
		targets.add(DataUnitDescription.createInput("in_B", "", "", false));
		// create mapping 
		// out_A -> in_A
		// out_B -> in_A
		// out_C -> in_A
		MutablePair<List<Integer>, Integer> mapping_A = new MutablePair<>();
		mapping_A.left = new LinkedList<>();
		mapping_A.left.add(0);
		mapping_A.left.add(1);
		mapping_A.left.add(2);
		mapping_A.right = 0;
		// add to mapping
		List<MutablePair<List<Integer>, Integer>> mappings = new LinkedList<>();
		mappings.add(mapping_A);
		// convert into script ..
		String script = edgeCompiler.compile(mappings, sources, targets);
		// convert back from script
		List<MutablePair<List<Integer>, Integer>> outputMappings =
				edgeCompiler.decompile(script, sources, targets);
		// and should be the same
		assertArrayEquals(mappings.toArray(), outputMappings.toArray());
	}	
}
