package cz.cuni.mff.xrg.odcs.commons.app.scheduling;

import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test suite for {@link DbSheduleImpl}
 * @author Petyr
 */
@ContextConfiguration(locations = {"classpath:commons-app-test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback=true)
public class DbSheduleImplTest {
	
	@Autowired
	private DbSchedule dbSchedule;
	
	/**
	 * Test of getAllSchedules method, of class ScheduleFacade.
	 */
	@Test
	@Transactional
	public void testGetLastExecForRunAfter() {
		Schedule schedule = dbSchedule.getInstance(2);

		Assert.assertNotNull(schedule);
		
		// just try to excute this
		List<Date> times = dbSchedule.getLastExecForRunAfter(schedule);
		
		Assert.assertNotNull(times);
		Assert.assertEquals(1, times.size());
		
		Assert.assertEquals(null, times.get(0));
	}
	
}
