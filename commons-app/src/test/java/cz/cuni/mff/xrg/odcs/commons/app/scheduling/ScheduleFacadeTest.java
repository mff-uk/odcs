package cz.cuni.mff.xrg.odcs.commons.app.scheduling;

import cz.cuni.mff.xrg.odcs.commons.app.facade.ScheduleFacade;
import cz.cuni.mff.xrg.odcs.commons.app.user.EmailAddress;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import java.util.Date;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.Assert.*;

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
	
	@PersistenceContext
	private EntityManager em;

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
		assertEquals("scheduler@example.com", email.getEmail());
	}
	
	/**
	 * Test deleting schedule notification.
	 */
	@Test @Transactional
	public void testDeleteScheduleNotification() {
		
		Schedule sch = scheduler.getSchedule(1L);
		assertNotNull(sch);
		assertNotNull(sch.getNotification());
		
		scheduler.deleteNotification(sch.getNotification());
		em.flush();
		em.clear();
		
		Schedule ret = scheduler.getSchedule(1L);
		assertNotNull(ret);
		assertNull(ret.getNotification());
	}
	
	@Test @Transactional
	public void testChangeLastExecution() {
		Date now = new Date();
		Schedule sch = scheduler.getSchedule(1L);
		sch.setLastExecution(now);
		scheduler.save(sch);
		
		em.flush();
		em.clear();
		
		Schedule ret = scheduler.getSchedule(1L);
		assertEquals(now, ret.getLastExecution());
	}
}