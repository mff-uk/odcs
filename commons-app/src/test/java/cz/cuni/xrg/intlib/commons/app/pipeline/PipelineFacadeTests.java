package cz.cuni.xrg.intlib.commons.app.pipeline;

import java.util.List;

import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertArrayEquals;

/**
 * Test suite for pipeline facade interface.
 * Each test is run in own transaction, which is rolled back in the end.
 * 
 * @author Jan Vojt
 */
@ContextConfiguration(locations = {"classpath:commons-app-test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback=true)
public class PipelineFacadeTests {
	
	@Autowired
	private PipelineFacade facade;
	
	@Test
	@Transactional
	public void testCreatePipeline() {
		
		Pipeline pipe = facade.createPipeline();
		
		assertNotNull(pipe);
		assertNotNull(pipe.getGraph());
		assertNull(pipe.getName());
	}
	
	@Test
	@Transactional
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
	@Transactional
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
	@Transactional
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
