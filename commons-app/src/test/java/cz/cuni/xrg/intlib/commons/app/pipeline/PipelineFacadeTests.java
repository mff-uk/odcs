package cz.cuni.xrg.intlib.commons.app.pipeline;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import cz.cuni.xrg.intlib.commons.app.util.InMemoryEntityManager;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertArrayEquals;

/**
 * Test suite for pipeline facade interface.
 * @author Jan Vojt <jan@vojt.net>
 *
 */
public class PipelineFacadeTests {
	
	private PipelineFacade facade;
	
	@Before
	public void setUp() {
		facade = new PipelineFacade(new InMemoryEntityManager());
	}
	
	@Test
	public void testCreatePipeline() {
		
		Pipeline pipe = facade.createPipeline();
		
		assertNotNull(pipe);
		assertNotNull(pipe.getGraph());
		assertNull(pipe.getName());
	}
	
	@Test
	public void testPersistPipeline() {
		
		Pipeline[] pipes = new Pipeline[3];
		for (int i = 0; i<3; i++) {
			pipes[i] = facade.createPipeline();
			facade.save(pipes[i]);
		}

		for (int i = 0; i<3; i++) {
			assertNotNull(facade.getPipeline(pipes[i].getId()));
		}
	}
	
	@Test
	public void testDeletePipeline() {
		
		Pipeline[] pipes = new Pipeline[3];
		for (int i = 0; i<3; i++) {
			pipes[i] = facade.createPipeline();
			facade.save(pipes[i]);
		}
		
		facade.delete(pipes[1]);

		assertEquals(pipes[0], facade.getPipeline(pipes[0].getId()));
		assertNull(facade.getPipeline(pipes[1].getId()));
		assertEquals(pipes[2], facade.getPipeline(pipes[2].getId()));
	}
	
	@Test
	public void testPipelineList() {
		
		Pipeline[] pipes = new Pipeline[3];
		for (int i = 0; i<3; i++) {
			pipes[i] = facade.createPipeline();
			facade.save(pipes[i]);
		}
		
		List<Pipeline> resPipes = facade.getAllPipelines();
		
		assertEquals(pipes.length, resPipes.size());
		assertArrayEquals(pipes, resPipes.toArray());
	}

}
