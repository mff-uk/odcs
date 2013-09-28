package cz.cuni.mff.xrg.odcs.commons.app.pipeline;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.PipelineGraph;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 * Test suite for {@link Pipeline} entity.
 *
 * @author Jan Vojt
 */
public class PipelineTest {
	
	/**
	 * Tested instance.
	 */
	private Pipeline instance;
	
	@Before
	public void setUp() {
		instance = new Pipeline();
	}
	
	@Test
	public void testCopy() {
		String name = "pplname";
		String description = "ppldescription";
		
		instance.setName(name);
		instance.setDescription(description);
		instance.setGraph(new PipelineGraph());
		
		Pipeline copy = new Pipeline(instance);
		assertNotSame(instance, copy);
		
		assertEquals(name, instance.getName());
		assertEquals(description, instance.getDescription());
		assertSame(instance, instance.getGraph().getPipeline());
		
		assertEquals(name, copy.getName());
		assertEquals(description, copy.getDescription());
		assertSame(copy, copy.getGraph().getPipeline());
		
		assertNotSame(instance.getGraph(), copy.getGraph());
	}
	
}