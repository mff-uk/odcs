package cz.cuni.xrg.intlib.commons.app.scheduling;

import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Test suite for facade providing persistence of scheduled pipeline runs.
 *
 * @author Jan Vojt
 */
@ContextConfiguration(locations = {"classpath:commons-app-test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback=true)
public class ScheduleFacadeTest {
	
	@Autowired
	private ScheduleFacade scheduler;

	/**
	 * Test of getAllSchedules method, of class ScheduleFacade.
	 */
	@Test
	public void testGetAllSchedules() {
		List<Schedule> result = scheduler.getAllSchedules();
		assertEquals(2, result.size());
	}

	/**
	 * Test of getSchedulesFor method, of class ScheduleFacade.
	 */
	@Test
	public void testGetSchedulesFor() {
		Schedule sch = scheduler.getSchedule(1L);
		Pipeline pipeline = sch.getPipeline();
		
		List<Schedule> result = scheduler.getSchedulesFor(pipeline);
		
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(sch.getId(), result.get(0).getId());
	}

	/**
	 * Test of getFollowers method, of class ScheduleFacade.
	 */
	@Test
	public void testGetFollowers() {
		long pplId = 1L;
		Pipeline pipeline = mock(Pipeline.class);
		when(pipeline.getId()).thenReturn(pplId);
		
		List<Schedule> result = scheduler.getFollowers(pipeline);
		assertEquals(1, result.size());
		assertEquals(2L, (long) result.get(0).getId());
	}

	/**
	 * Test of getSchedule method, of class ScheduleFacade.
	 */
	@Test
	public void testGetSchedule() {
		Schedule result = scheduler.getSchedule(1L);
		assertNotNull(result);
	}
	
	/**
	 * Test of fetching email notification settings for schedule.
	 */
	@Test
	public void testGetNotification() {
		Schedule sch = scheduler.getSchedule(1L);
		
		assertNotNull(sch);
		assertNotNull(sch.getNotification());
		assertNotNull(sch.getNotification().getEmails());
		assertEquals(1, sch.getNotification().getEmails().size());
		
		EmailAddress email = sch.getNotification().getEmails().iterator().next();
		assertEquals("scheduler", email.getName());
		assertEquals("example.com", email.getDomain());
	}
}