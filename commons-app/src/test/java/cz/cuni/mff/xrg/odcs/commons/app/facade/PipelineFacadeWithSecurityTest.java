package cz.cuni.mff.xrg.odcs.commons.app.facade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthenticationContext;
import cz.cuni.mff.xrg.odcs.commons.app.auth.ShareType;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.DbOpenEvent;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.PipelineGraph;
import cz.cuni.mff.xrg.odcs.commons.app.user.User;

/**
 * Test suite for pipeline facade interface.
 * Each test is run in own transaction, which is rolled back in the end.
 * 
 * @author Jan Vojt
 */
@ContextConfiguration(locations = {"classpath:commons-app-test-context.xml","classpath:commons-app-test-context-security.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback=true)
public class PipelineFacadeWithSecurityTest extends PipelineFacadeTest {
	@Autowired
	@Qualifier("authenticationManager")
	private AuthenticationManager authManager;
	
	@Autowired(required = false)
    private AuthenticationContext authCtx;

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private UserFacade userFacade;

	@Autowired
	private DPUFacade dpuFacade;

	@Autowired
	private PipelineFacade pipelineFacade;

	@Autowired
	private ScheduleFacade schedulerFacade;
	
    @Autowired
    private ScheduleFacade scheduleFacade;
    
	@Autowired
	private DbOpenEvent openEventDao;
	
	@Before
	public void before() {
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			User user = userFacade.getUserByUsername("jdoe");
			TestingAuthenticationToken token = new TestingAuthenticationToken(user,user.getPassword());
			SecurityContextHolder.getContext().setAuthentication(authManager.authenticate(token));
		}
	}
	
	@Override
	@Test(expected=AccessDeniedException.class)
	@Transactional
	public void testGetPipelinesUsingDPU2() {
		super.testGetPipelinesUsingDPU2();
	}
	
	@Test
	@Transactional
	public void testGetPipelinesUsingDPU3() {
		DPUTemplateRecord dpu = new DPUTemplateRecord();
		dpu.setId(1L);
		dpu.setOwner(authCtx.getUser());

		List<Pipeline> pipes = pipelineFacade.getPipelinesUsingDPU(dpu);

		assertNotNull(pipes);
		assertEquals(1, pipes.size());
		assertEquals("Test 1", pipes.get(0).getName());
	}
	
	@Override
	@Test(expected=AccessDeniedException.class)
	@Transactional
	public void testDelete_Pipeline() {
		super.testDelete_Pipeline();
	}
	
	@Test
	@Transactional
	public void testDelete_Pipeline2() {
		System.out.println("delete");

		Pipeline pipeline = pipelineFacade.createPipeline();
		pipeline.setDescription("testDescription");
		pipeline.setGraph(new PipelineGraph());
		pipeline.setLastChange(new Date());
		pipeline.setName("testName");
		pipeline.setUser(userFacade.getUserByUsername("jdoe"));
		pipeline.setVisibility(ShareType.PUBLIC_RO);
		pipeline.getConflicts().add(pipeline);
		pipeline.setUser(authCtx.getUser());

		pipelineFacade.save(pipeline);
		assertNotNull(pipeline.getId());
		Long id = pipeline.getId();

		em.flush();
		em.clear();

		pipelineFacade.delete(pipeline);
		Pipeline pipeline3 = pipelineFacade.getPipeline(id);
		assertNull(pipeline3);
	}	
	
	@Override
	@Test(expected=AccessDeniedException.class)
	@Transactional
	public void testCopyPipeline() {
		super.testCopyPipeline();
	}
	
	@Test
	@Transactional
	public void testCopyPipeline2() {
		System.out.println("copyPipeline");

		Pipeline pipeline = pipelineFacade.createPipeline();
		pipeline.setDescription("testDescription");
		pipeline.setGraph(new PipelineGraph());
		pipeline.setLastChange(new Date());
		pipeline.setName("testName");
		pipeline.setUser(authCtx.getUser());
		pipeline.setVisibility(ShareType.PUBLIC_RO);
		pipeline.getConflicts().add(pipeline);
		pipelineFacade.save(pipeline);

		Pipeline pipeline2 = pipelineFacade.copyPipeline(pipeline);
		assertNotNull(pipeline2);
		assertNotSame(pipeline.getId(), pipeline2.getId());
		assertEquals(pipeline.getDescription(), pipeline2.getDescription());
		// assertEquals(pipeline.getGraph(), pipeline2.getGraph());
		// assertEquals(pipeline.getLastChange(), pipeline2.getLastChange());
		assertEquals("Copy of " + pipeline.getName(), pipeline2.getName());
		assertEquals(pipeline.getOwner(), pipeline2.getOwner());
		assertEquals(ShareType.PRIVATE, pipeline2.getShareType());
		// assertEquals(pipeline.getConflicts(), pipeline2.getConflicts());

		Pipeline pipeline3 = pipelineFacade.copyPipeline(pipeline);
		assertNotNull(pipeline3);
		assertNotSame(pipeline.getId(), pipeline3.getId());
		assertEquals(pipeline.getDescription(), pipeline3.getDescription());
		// assertEquals(pipeline.getGraph(), pipeline3.getGraph());
		// assertEquals(pipeline.getLastChange(), pipeline3.getLastChange());
		assertEquals("Copy of " + pipeline.getName() + " #1",
				pipeline3.getName());
		assertEquals(pipeline.getOwner(), pipeline3.getOwner());
		assertEquals(ShareType.PRIVATE, pipeline3.getShareType());
		// assertEquals(pipeline.getConflicts(), pipeline3.getConflicts());

		Pipeline pipeline4 = pipelineFacade.copyPipeline(pipeline);
		assertNotNull(pipeline4);
		assertNotSame(pipeline.getId(), pipeline4.getId());
		assertEquals(pipeline.getDescription(), pipeline4.getDescription());
		// assertEquals(pipeline.getGraph(), pipeline3.getGraph());
		// assertEquals(pipeline.getLastChange(), pipeline3.getLastChange());
		assertEquals("Copy of " + pipeline.getName() + " #2",
				pipeline4.getName());
		assertEquals(pipeline.getOwner(), pipeline4.getOwner());
		assertEquals(ShareType.PRIVATE, pipeline4.getShareType());
		// assertEquals(pipeline.getConflicts(), pipeline3.getConflicts());

		Pipeline pipeline5 = pipelineFacade.copyPipeline(pipeline4);
		assertNotNull(pipeline5);
		assertNotSame(pipeline.getId(), pipeline5.getId());
		assertEquals(pipeline.getDescription(), pipeline5.getDescription());
		// assertEquals(pipeline.getGraph(), pipeline3.getGraph());
		// assertEquals(pipeline.getLastChange(), pipeline3.getLastChange());
		assertEquals("Copy of Copy of " + pipeline.getName() + " #2",
				pipeline5.getName());
		assertEquals(pipeline.getOwner(), pipeline5.getOwner());
		assertEquals(ShareType.PRIVATE, pipeline5.getShareType());
		// assertEquals(pipeline.getConflicts(), pipeline3.getConflicts());

		Pipeline pipeline6 = pipelineFacade.copyPipeline(pipeline4);
		assertNotNull(pipeline6);
		assertNotSame(pipeline.getId(), pipeline6.getId());
		assertEquals(pipeline.getDescription(), pipeline6.getDescription());
		// assertEquals(pipeline.getGraph(), pipeline3.getGraph());
		// assertEquals(pipeline.getLastChange(), pipeline3.getLastChange());
		assertEquals("Copy of Copy of " + pipeline.getName() + " #2 #1",
				pipeline6.getName());
		assertEquals(pipeline.getOwner(), pipeline6.getOwner());
		assertEquals(ShareType.PRIVATE, pipeline6.getShareType());
		// assertEquals(pipeline.getConflicts(), pipeline3.getConflicts());
	}
	
	@Override
	@Test(expected=AccessDeniedException.class)
	@Transactional	
	public void testGetPipelinesUsingUnusedDPU() {
		super.testGetPipelinesUsingUnusedDPU();
	}
	
	@Test
	@Transactional
	public void testGetPipelinesUsingUnusedDPU2() {
		DPUTemplateRecord dpu = new DPUTemplateRecord();
		dpu.setId(2L);
		dpu.setOwner(authCtx.getUser());

		List<Pipeline> pipes = pipelineFacade.getPipelinesUsingDPU(dpu);

		assertNotNull(pipes);
		assertEquals(0, pipes.size());
	}	
	
	@Override
	@Test(expected=AccessDeniedException.class)
	@Transactional	
	public void testDeepDeletePipeline() {
		super.testDeepDeletePipeline();
	}
	
	@Override
	@Test(expected=AccessDeniedException.class)
	@Transactional	
	public void testDuplicateCopyPipeline() {
		super.testDuplicateCopyPipeline();
	}
	
	@Test
	@Transactional
	public void testDuplicateCopyPipeline2() {

		Pipeline ppl = new Pipeline();
		ppl.setName("pplName");
		ppl.setDescription("pplDesc");
		ppl.setUser(authCtx.getUser());

		Pipeline nPpl = pipelineFacade.copyPipeline(ppl);

		// test copying for the first time
		String newName = "Copy of " + ppl.getName();
		assertNotSame(ppl, nPpl);
		assertEquals(newName, nPpl.getName());
		assertEquals(ppl.getDescription(), nPpl.getDescription());

		Pipeline nPpl1 = pipelineFacade.copyPipeline(ppl);

		// test copying for the second time
		String newName1 = "Copy of " + ppl.getName() + " #1";
		assertNotSame(ppl, nPpl1);
		assertEquals(newName1, nPpl1.getName());
		assertEquals(ppl.getDescription(), nPpl1.getDescription());
	}	
	
	@Override
	@Test(expected=AccessDeniedException.class)
	@Transactional	
	public void testDeletePipeline() {
		super.testDeletePipeline();
	}
	
	
	@Test
	@Transactional
	public void testDeletePipeline2() {

		Pipeline[] pipes = new Pipeline[3];
		for (int i = 0; i < 3; i++) {
			pipes[i] = pipelineFacade.createPipeline();
			pipes[i].setUser(authCtx.getUser());
			pipelineFacade.save(pipes[i]);
		}

		pipelineFacade.delete(pipes[1]);

		em.flush();

		assertEquals(pipes[0], pipelineFacade.getPipeline(pipes[0].getId()));
		assertNull(pipelineFacade.getPipeline(pipes[1].getId()));
		assertEquals(pipes[2], pipelineFacade.getPipeline(pipes[2].getId()));
	}	
}
