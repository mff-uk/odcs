package cz.cuni.xrg.intlib.rdf;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test suite for {@link GraphUrl} class.
 * 
 * @author Petyr
 *
 */
public class GraphUrlTest {

	@Test
	public void translateTest() {
		final String input = "exec_3_dpu_4_du_0";
		final String expectedOutput = 
				"http://linked.opendata.cz/resource/odcs/internal/pipeline/exec/3/dpu/4/du/0";
		
		assertEquals(expectedOutput, GraphUrl.translateDataUnitId(input));
	}	
	
}
