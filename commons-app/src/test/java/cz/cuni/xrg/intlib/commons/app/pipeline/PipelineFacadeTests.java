package cz.cuni.xrg.intlib.commons.app.pipeline;

import org.junit.Before;
import org.junit.Test;

import cz.cuni.xrg.intlib.commons.app.InMemoryEntityManager;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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

		assertNotNull(facade.getPipeline(pipes[0].getId()));
		assertNull(facade.getPipeline(pipes[1].getId()));
		assertNotNull(facade.getPipeline(pipes[2].getId()));
	}

}
