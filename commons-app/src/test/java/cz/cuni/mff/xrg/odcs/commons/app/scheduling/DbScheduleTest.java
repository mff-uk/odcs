package cz.cuni.mff.xrg.odcs.commons.app.scheduling;

import cz.cuni.mff.xrg.odcs.commons.app.facade.ScheduleFacade;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.DbPipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.Assert.*;

/**
 * Test suite for DAO providing access to pipeline schedules.
 *
 * @author Jan Vojt
 */
@ContextConfiguration(locations = {"classpath:commons-app-test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback=true)
public class DbScheduleTest {
	
	@Autowired
	private DbSchedule scheduleDao;
	
	@Autowired
	private DbPipeline pipelineDao;
	
	@Test @Transactional
	public void testGetFollowers() {
		
		Pipeline pipe = pipelineDao.getInstance(1L);
		List<Schedule> followers = scheduleDao.getFollowers(pipe, true);
		
		assertNotNull(followers);
		assertEquals(1, followers.size());
		assertEquals(2L, (long) followers.get(0).getId());
	}
	
}
