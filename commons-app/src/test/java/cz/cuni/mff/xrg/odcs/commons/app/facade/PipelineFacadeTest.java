package cz.cuni.mff.xrg.odcs.commons.app.facade;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import cz.cuni.mff.xrg.odcs.commons.app.auth.ShareType;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUType;
import cz.cuni.mff.xrg.odcs.commons.app.execution.context.ExecutionContextInfo;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.PipelineGraph;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;

/**
 * Test suite for pipeline facade interface. Each test is run in own
 * transaction, which is rolled back in the end.
 * 
 * @author Jan Vojt
 * @author michal.klempa@eea.sk
 */
@ContextConfiguration(locations = { "classpath:commons-app-test-context.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = true)
public class PipelineFacadeTest {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private DPUFacade dpuFacade;

	@Autowired
	private UserFacade userFacade;

	@Autowired
	private PipelineFacade pipelineFacade;

	@Autowired
	private ScheduleFacade schedulerFacade;

	public PipelineFacadeTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test of createPipeline method, of class PipelineFacade.
	 */
	@Test
	@Transactional
	public void testCreatePipeline() {
		System.out.println("createPipeline");
		Pipeline pipeline = pipelineFacade.createPipeline();

		assertNotNull(pipeline);
		assertNotNull(pipeline.getGraph());
		assertNotNull(pipeline.getConflicts());
		assertNull(pipeline.getName());
	}

	/**
	 * Test of copyPipeline method, of class PipelineFacade.
	 */
	@Test
	@Transactional
	public void testCopyPipeline() {
		System.out.println("copyPipeline");

		Pipeline pipeline = pipelineFacade.createPipeline();
		pipeline.setDescription("testDescription");
		pipeline.setGraph(new PipelineGraph());
		pipeline.setLastChange(new Date());
		pipeline.setName("testName");
		pipeline.setUser(userFacade.getUserByUsername("pdoe"));
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
		assertNotSame(pipeline.getOwner(), pipeline2.getOwner());
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
		assertNotSame(pipeline.getOwner(), pipeline3.getOwner());
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
		assertNotSame(pipeline.getOwner(), pipeline4.getOwner());
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
		assertNotSame(pipeline.getOwner(), pipeline5.getOwner());
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
		assertNotSame(pipeline.getOwner(), pipeline6.getOwner());
		assertEquals(ShareType.PRIVATE, pipeline6.getShareType());
		// assertEquals(pipeline.getConflicts(), pipeline3.getConflicts());
	}

	/**
	 * Test of getAllPipelines method, of class PipelineFacade.
	 */
	@Test
	@Transactional
	public void testGetAllPipelines() {
		System.out.println("getAllPipelines");

		Pipeline pipeline = pipelineFacade.createPipeline();
		pipeline.setDescription("testDescription");
		pipeline.setGraph(new PipelineGraph());
		pipeline.setLastChange(new Date());
		pipeline.setName("testName");
		pipeline.setUser(userFacade.getUserByUsername("jdoe"));
		pipeline.setVisibility(ShareType.PUBLIC_RO);
		pipeline.getConflicts().add(pipeline);

		Pipeline pipeline2 = pipelineFacade.createPipeline();
		pipeline2.setDescription("testDescription2");
		pipeline2.setGraph(new PipelineGraph());
		pipeline2.setLastChange(new Date());
		pipeline2.setName("testName2");
		pipeline2.setUser(userFacade.getUserByUsername("jdoe"));
		pipeline2.setVisibility(ShareType.PUBLIC_RO);
		pipeline2.getConflicts().add(pipeline2);

		pipelineFacade.save(pipeline);
		pipelineFacade.save(pipeline2);

		List<Pipeline> pipelines = pipelineFacade.getAllPipelines();
		assertNotNull(pipelines);
		assertTrue(pipelines.size() == 4);
		assertEquals(pipeline, pipelines.get(2));
		assertEquals(pipeline2, pipelines.get(3));
	}

	/**
	 * Test of getPipeline method, of class PipelineFacade.
	 */
	@Test
	@Transactional
	public void testGetPipeline() {
		System.out.println("getPipeline");

		Pipeline pipeline = pipelineFacade.createPipeline();
		pipeline.setDescription("testDescription");
		pipeline.setGraph(new PipelineGraph());
		pipeline.setLastChange(new Date());
		pipeline.setName("testName");
		pipeline.setUser(userFacade.getUserByUsername("jdoe"));
		pipeline.setVisibility(ShareType.PUBLIC_RO);
		pipeline.getConflicts().add(pipeline);

		Pipeline pipeline2 = pipelineFacade.createPipeline();
		pipeline2.setDescription("testDescription2");
		pipeline2.setGraph(new PipelineGraph());
		pipeline2.setLastChange(new Date());
		pipeline2.setName("testName2");
		pipeline2.setUser(userFacade.getUserByUsername("jdoe"));
		pipeline2.setVisibility(ShareType.PUBLIC_RO);
		pipeline2.getConflicts().add(pipeline2);

		pipelineFacade.save(pipeline);
		assertNotNull(pipeline.getId());
		Long id = pipeline.getId();
		pipelineFacade.save(pipeline2);
		assertNotNull(pipeline2.getId());
		Long id2 = pipeline.getId();

		Pipeline pipeline3 = pipelineFacade.getPipeline(id);
		assertNotNull(pipeline3);
		assertEquals(pipeline, pipeline3);

		Pipeline pipeline4 = pipelineFacade.getPipeline(id2);
		assertNotNull(pipeline4);
		assertEquals(pipeline, pipeline4);
	}

	/**
	 * Test of save method, of class PipelineFacade.
	 */
	@Test
	@Transactional
	public void testSave_Pipeline() {
		System.out.println("save");

		Pipeline pipeline = pipelineFacade.createPipeline();
		pipeline.setDescription("testDescription");
		pipeline.setGraph(new PipelineGraph());
		pipeline.setLastChange(new Date());
		pipeline.setName("testName");
		pipeline.setUser(userFacade.getUserByUsername("jdoe"));
		pipeline.setVisibility(ShareType.PUBLIC_RO);
		pipeline.getConflicts().add(pipeline);

		pipelineFacade.save(pipeline);
		assertNotNull(pipeline.getId());
		Long id = pipeline.getId();

		em.flush();
		em.clear();

		Pipeline pipeline3 = pipelineFacade.getPipeline(id);
		assertNotNull(pipeline3);
		assertEquals(pipeline, pipeline3);
	}

	/**
	 * Test of delete method, of class PipelineFacade.
	 */
	@Test
	@Transactional
	public void testDelete_Pipeline() {
		System.out.println("delete");

		Pipeline pipeline = pipelineFacade.createPipeline();
		pipeline.setDescription("testDescription");
		pipeline.setGraph(new PipelineGraph());
		pipeline.setLastChange(new Date());
		pipeline.setName("testName");
		pipeline.setUser(userFacade.getUserByUsername("jdoe"));
		pipeline.setVisibility(ShareType.PUBLIC_RO);
		pipeline.getConflicts().add(pipeline);

		pipelineFacade.save(pipeline);
		assertNotNull(pipeline.getId());
		Long id = pipeline.getId();

		em.flush();
		em.clear();

		pipelineFacade.delete(pipeline);
		Pipeline pipeline3 = pipelineFacade.getPipeline(id);
		assertNull(pipeline3);
	}

	/**
	 * Test of getPipelinesUsingDPU method, of class PipelineFacade.
	 */
	@Test
	@Transactional
	public void testGetPipelinesUsingDPU() {
		System.out.println("getPipelinesUsingDPU");

		DPUTemplateRecord parentTemplateRecord = dpuFacade.createTemplate(
				"testParent", DPUType.EXTRACTOR);
        parentTemplateRecord.setDescription("parentTestDescription");
        parentTemplateRecord.setId(-52L);
        parentTemplateRecord.setJarDescription("parenttestJarDescription");
        parentTemplateRecord.setJarDirectory("parenttestJarDirectory");
        parentTemplateRecord.setJarName("parenttestJarName");
		
		DPUTemplateRecord templateRecord = dpuFacade.createTemplate("testName",
				DPUType.EXTRACTOR);
		templateRecord.setDescription("testDescription");
		templateRecord.setId(-54L);
		templateRecord.setJarDescription("testJarDescription");
		templateRecord.setJarDirectory("testJarDirectory");
		templateRecord.setJarName("testJarName");
		templateRecord.setParent(parentTemplateRecord);
		dpuFacade.save(parentTemplateRecord);
		dpuFacade.save(templateRecord);
		
		Pipeline pipeline = pipelineFacade.createPipeline();
		pipeline.setDescription("testDescription");
		PipelineGraph pipelineGraph = new PipelineGraph();
		DPUInstanceRecord dpuInstanceRecord = dpuFacade
				.createInstanceFromTemplate(templateRecord);
		pipelineGraph.addDpuInstance(dpuInstanceRecord);
		pipeline.setGraph(pipelineGraph);
		pipeline.setLastChange(new Date());
		pipeline.setName("testName");
		pipeline.setUser(userFacade.getUserByUsername("jdoe"));
		pipeline.setVisibility(ShareType.PUBLIC_RO);
		pipeline.getConflicts().add(pipeline);
		dpuFacade.save(dpuInstanceRecord);
		pipelineFacade.save(pipeline);
		
		List<Pipeline> pipelines = pipelineFacade
				.getPipelinesUsingDPU(templateRecord);
		assertNotNull(pipelines);
		assertTrue(pipelines.size() == 1);
		assertEquals(pipeline, pipelines.get(0));
	}

	/**
	 * Test of hasPipelineWithName method, of class PipelineFacade.
	 */
	@Test
	@Transactional
	public void testHasPipelineWithName() {
		System.out.println("hasPipelineWithName");
		
		Pipeline pipeline = pipelineFacade.createPipeline();
		pipeline.setDescription("testDescription");
		pipeline.setGraph(new PipelineGraph());
		pipeline.setLastChange(new Date());
		pipeline.setName("testName");
		pipeline.setUser(userFacade.getUserByUsername("jdoe"));
		pipeline.setVisibility(ShareType.PUBLIC_RO);
		pipeline.getConflicts().add(pipeline);

		Pipeline pipeline2 = pipelineFacade.createPipeline();
		pipeline2.setDescription("testDescription2");
		pipeline2.setGraph(new PipelineGraph());
		pipeline2.setLastChange(new Date());
		pipeline2.setName("testName2");
		pipeline2.setUser(userFacade.getUserByUsername("jdoe"));
		pipeline2.setVisibility(ShareType.PUBLIC_RO);
		pipeline2.getConflicts().add(pipeline2);
		
		
		assertFalse(pipelineFacade.hasPipelineWithName("testDescription", pipeline));
	}

	/**
	 * Test of getPrivateDPUs method, of class PipelineFacade.
	 */
	@Test
	@Transactional
	public void testGetPrivateDPUs() {
		System.out.println("getPrivateDPUs");
	}

	/**
	 * Test of createOpenEvent method, of class PipelineFacade.
	 */
	@Test
	@Transactional
	public void testCreateOpenEvent() {
		System.out.println("createOpenEvent");
	}

	/**
	 * Test of getOpenPipelineEvents method, of class PipelineFacade.
	 */
	@Test
	@Transactional
	public void testGetOpenPipelineEvents() {
		System.out.println("getOpenPipelineEvents");
	}

	/**
	 * Test of isUpToDate method, of class PipelineFacade.
	 */
	@Test
	@Transactional
	public void testIsUpToDate() {
		System.out.println("isUpToDate");
	}

	/**
	 * Test of createExecution method, of class PipelineFacade.
	 */
	@Test
	@Transactional
	public void testCreateExecution() {
		System.out.println("createExecution");
	}

	/**
	 * Test of getAllExecutions method, of class PipelineFacade.
	 */
	@Test
	@Transactional
	public void testGetAllExecutions_0args() {
		System.out.println("getAllExecutions");
	}

	/**
	 * Test of getAllExecutions method, of class PipelineFacade.
	 */
	@Test
	@Transactional
	public void testGetAllExecutions_PipelineExecutionStatus() {
		System.out.println("getAllExecutions");
	}

	/**
	 * Test of getExecution method, of class PipelineFacade.
	 */
	@Test
	@Transactional
	public void testGetExecution() {
		System.out.println("getExecution");
	}

	/**
	 * Test of getExecutions method, of class PipelineFacade.
	 */
	@Test
	@Transactional
	public void testGetExecutions_Pipeline() {
		System.out.println("getExecutions");
	}

	/**
	 * Test of getExecutions method, of class PipelineFacade.
	 */
	@Test
	@Transactional
	public void testGetExecutions_Pipeline_PipelineExecutionStatus() {
		System.out.println("getExecutions");
	}

	/**
	 * Test of getLastExecTime method, of class PipelineFacade.
	 */
	@Test
	@Transactional
	public void testGetLastExecTime() {
		System.out.println("getLastExecTime");
	}

	/**
	 * Test of getLastExec method, of class PipelineFacade.
	 */
	@Test
	@Transactional
	public void testGetLastExec_Pipeline_Set() {
		System.out.println("getLastExec");
	}

	/**
	 * Test of getLastExec method, of class PipelineFacade.
	 */
	@Test
	@Transactional
	public void testGetLastExec_Pipeline() {
		System.out.println("getLastExec");
	}

	/**
	 * Test of getLastExec method, of class PipelineFacade.
	 */
	@Test
	@Transactional
	public void testGetLastExec_Schedule_Set() {
		System.out.println("getLastExec");
	}

	/**
	 * Test of hasModifiedExecutions method, of class PipelineFacade.
	 */
	@Test
	@Transactional
	public void testHasModifiedExecutions() {
		System.out.println("hasModifiedExecutions");
	}

	/**
	 * Test of save method, of class PipelineFacade.
	 */
	@Test
	@Transactional
	public void testSave_PipelineExecution() {
		System.out.println("save");
	}

	/**
	 * Test of delete method, of class PipelineFacade.
	 */
	@Test
	@Transactional
	public void testDelete_PipelineExecution() {
		System.out.println("delete");
	}

	/**
	 * Test of stopExecution method, of class PipelineFacade.
	 */
	@Test
	@Transactional
	public void testStopExecution() {
		System.out.println("stopExecution");
	}

	@Test
	@Transactional
	public void testCreatePipeline2() {

		Pipeline pipe = pipelineFacade.createPipeline();

		assertNotNull(pipe);
		assertNotNull(pipe.getGraph());
		assertNotNull(pipe.getConflicts());
		assertNull(pipe.getName());
	}

	@Test
	@Transactional
	public void testPersistPipeline() {

		Pipeline[] pipes = new Pipeline[3];
		for (int i = 0; i < 3; i++) {
			pipes[i] = pipelineFacade.createPipeline();
			pipelineFacade.save(pipes[i]);
		}

		em.flush();

		for (int i = 0; i < 3; i++) {
			assertNotNull(pipelineFacade.getPipeline(pipes[i].getId()));
		}
	}

	@Test
	@Transactional
	public void testGetAllExecutions() {
		List<PipelineExecution> execsPrev = pipelineFacade
				.getAllExecutions(PipelineExecutionStatus.CANCELLING);
		assertNotNull(execsPrev);

		Pipeline pipe = pipelineFacade.createPipeline();
		PipelineExecution exec = pipelineFacade.createExecution(pipe);
		exec.setStatus(PipelineExecutionStatus.CANCELLING);

		pipelineFacade.save(pipe);
		pipelineFacade.save(exec);

		List<PipelineExecution> execs = pipelineFacade
				.getAllExecutions(PipelineExecutionStatus.CANCELLING);
		assertNotNull(execs);
		assertEquals(execsPrev.size() + 1, execs.size());
	}

	@Test
	@Transactional
	public void testExecutionsOfPipeline() {
		Pipeline pipe = pipelineFacade.createPipeline();
		PipelineExecution exec = pipelineFacade.createExecution(pipe);

		pipelineFacade.save(pipe);
		pipelineFacade.save(exec);

		List<PipelineExecution> execs = pipelineFacade.getExecutions(pipe);

		assertNotNull(execs);
		assertEquals(1, execs.size());
		assertEquals(exec, execs.get(0));
	}

	@Test
	@Transactional
	public void testDeletePipeline() {

		Pipeline[] pipes = new Pipeline[3];
		for (int i = 0; i < 3; i++) {
			pipes[i] = pipelineFacade.createPipeline();
			pipelineFacade.save(pipes[i]);
		}

		pipelineFacade.delete(pipes[1]);

		em.flush();

		assertEquals(pipes[0], pipelineFacade.getPipeline(pipes[0].getId()));
		assertNull(pipelineFacade.getPipeline(pipes[1].getId()));
		assertEquals(pipes[2], pipelineFacade.getPipeline(pipes[2].getId()));
	}

	@Test
	@Transactional
	public void testDeepDeletePipeline() {

		long pid = 1;
		Pipeline pipe = pipelineFacade.getPipeline(pid);
		assertNotNull(pipe);
		List<PipelineExecution> execs = pipelineFacade.getExecutions(pipe);
		List<Schedule> jobs = schedulerFacade.getSchedulesFor(pipe);

		pipelineFacade.delete(pipe);

		// Cascading of deletes is happenning on DB level, so we need to flush
		// changes to DB and clear netityManager to reread from DB.
		em.flush();
		em.clear();

		// make sure pipeline was deleted
		assertNull(pipelineFacade.getPipeline(pid));

		// check that all pipeline executions were deleted
		for (PipelineExecution exec : execs) {
			assertNull(pipelineFacade.getExecution(exec.getId()));
		}

		// check that all scheduled jobs were deleted
		for (Schedule job : jobs) {
			assertNull(schedulerFacade.getSchedule(job.getId()));
		}
	}

	@Test
	@Transactional
	public void testPipelineList() {

		List<Pipeline> pipes = pipelineFacade.getAllPipelines();

		for (int i = 0; i < 3; i++) {
			Pipeline newPpl = pipelineFacade.createPipeline();
			pipes.add(newPpl);
			pipelineFacade.save(newPpl);
		}

		// refetch entities
		List<Pipeline> resPipes = pipelineFacade.getAllPipelines();

		// test
		assertEquals(pipes.size(), resPipes.size());
		for (Pipeline pipe : pipes) {
			assertTrue(resPipes.contains(pipe));
		}
	}

	@Test
	@Transactional
	public void testExecutionsContext() {
		Pipeline pipe = pipelineFacade.createPipeline();
		PipelineExecution exec = pipelineFacade.createExecution(pipe);
		pipelineFacade.save(pipe);
		pipelineFacade.save(exec);
		em.flush();

		// create context
		ExecutionContextInfo context = exec.getContext();

		assertNotNull(exec.getContext());
		assertNotNull(context.getId());
	}

	@Test
	@Transactional
	public void testGetPipelinesUsingDPU2() {
		DPUTemplateRecord dpu = new DPUTemplateRecord();
		dpu.setId(1L);

		List<Pipeline> pipes = pipelineFacade.getPipelinesUsingDPU(dpu);

		assertNotNull(pipes);
		assertEquals(1, pipes.size());
		assertEquals("Test 1", pipes.get(0).getName());
	}

	@Test
	@Transactional
	public void testGetPipelinesUsingUnusedDPU() {
		DPUTemplateRecord dpu = new DPUTemplateRecord();
		dpu.setId(2L);

		List<Pipeline> pipes = pipelineFacade.getPipelinesUsingDPU(dpu);

		assertNotNull(pipes);
		assertEquals(0, pipes.size());
	}

	@Test
	@Transactional
	public void testCopyPipeline2() {

		Pipeline ppl = new Pipeline();
		ppl.setName("pplName");
		ppl.setDescription("pplDesc");

		Pipeline nPpl = pipelineFacade.copyPipeline(ppl);

		String newName = "Copy of " + ppl.getName();
		assertNotSame(ppl, nPpl);
		assertEquals(newName, nPpl.getName());
		assertEquals(ppl.getDescription(), nPpl.getDescription());
	}

	@Test
	@Transactional
	public void testDuplicateCopyPipeline() {

		Pipeline ppl = new Pipeline();
		ppl.setName("pplName");
		ppl.setDescription("pplDesc");

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

	// @Test
	// @Transactional
	// public void testOpenPipelineEvent() {
	// // we use this to access the "test" functions
	// PipelineFacadeImpl facadeImpl = (PipelineFacadeImpl)facade;
	//
	// // mock authentication context for 2 different users
	// AuthenticationContext authCtx1 = mock(AuthenticationContext.class);
	// when(authCtx1.getUser()).thenReturn(em.find(User.class, 1L));
	//
	// AuthenticationContext authCtx2 = mock(AuthenticationContext.class);
	// when(authCtx2.getUser()).thenReturn(em.find(User.class, 2L));
	//
	// // fetch a pipeline we will use
	// Pipeline pipe1 = facade.getPipeline(1L);
	// Pipeline pipe2 = facade.getPipeline(2L);
	//
	// // check we have no events so far
	// assertFalse(facade.getOpenPipelineEvents(pipe1).size() > 0);
	// assertFalse(facade.getOpenPipelineEvents(pipe2).size() > 0);
	//
	// // first user opens only the first pipeline
	// facadeImpl.setAuthCtx(authCtx1);
	// facade.createOpenEvent(pipe1);
	//
	// // second user opens both pipelines
	// facadeImpl.setAuthCtx(authCtx2);
	// facade.createOpenEvent(pipe1);
	// facade.createOpenEvent(pipe2);
	//
	// em.flush();
	//
	// // check for first user
	// facadeImpl.setAuthCtx(authCtx1);
	// assertTrue(facade.getOpenPipelineEvents(pipe1).size() == 1);
	// assertTrue(facade.getOpenPipelineEvents(pipe2).size() == 1);
	//
	// // check for second user
	// facadeImpl.setAuthCtx(authCtx2);
	// assertTrue(facade.getOpenPipelineEvents(pipe1).size() == 1);
	// assertTrue(facade.getOpenPipelineEvents(pipe2).isEmpty());
	// }
}
