package cz.cuni.xrg.intlib.commons.app.pipeline;

import cz.cuni.xrg.intlib.commons.app.execution.PipelineExecution;
import java.util.List;

import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.Assert.*;

/**
 * Test suite for pipeline facade interface.
 * Each test is run in own transaction, which is rolled back in the end.
 * 
 * @author Jan Vojt
 */
@ContextConfiguration(locations = {"classpath:commons-app-test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback=true)
public class PipelineFacadeTest {
	
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
	public void testExecutionsOfPipeline() {
		Pipeline pipe = facade.createPipeline();
		PipelineExecution exec = new PipelineExecution(pipe);
		
		facade.save(pipe);
		facade.save(exec);
		
		List<PipelineExecution> execs = facade.getExecutions(pipe);
		
		assertNotNull(execs);
		assertEquals(1, execs.size());
		assertEquals(exec, execs.get(0));
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
	public void testDeepDeletePipeline() {
		
		long pid = 1;
		Pipeline pipe = facade.getPipeline(pid);
		assertNotNull(pipe);
		List<PipelineExecution> execs = facade.getExecutions(pipe);
		
		facade.delete(pipe);
		assertNull(facade.getPipeline(pid));
		for (PipelineExecution exec : execs) {
			assertNull(facade.getExecution(exec.getId()));
		}
	}
	
	@Test
	@Transactional
	public void testPipelineList() {
		
		List<Pipeline> pipes = facade.getAllPipelines();
		
		for (int i = 0; i<3; i++) {
			Pipeline newPpl = facade.createPipeline();
			pipes.add(newPpl);
			facade.save(newPpl);
		}
		
		// refetch entities
		List<Pipeline> resPipes = facade.getAllPipelines();
		
		// test
		assertEquals(pipes.size(), resPipes.size());
		for (Pipeline pipe : pipes) {
			assertTrue(resPipes.contains(pipe));
		}
	}

}
