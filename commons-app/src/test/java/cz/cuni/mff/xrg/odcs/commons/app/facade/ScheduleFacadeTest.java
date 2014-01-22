package cz.cuni.mff.xrg.odcs.commons.app.facade;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.ScheduleNotificationRecord;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.ScheduleType;
import cz.cuni.mff.xrg.odcs.commons.app.user.EmailAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test suite for schedule facade interface. Each test is run in own
 * transaction, which is rolled back in the end.
 *
 * @author michal.klempa@eea.sk
 */
@ContextConfiguration(locations = {"classpath:commons-app-test-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = true)
public class ScheduleFacadeTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private PipelineFacade pipelineFacade;

    @Autowired
    private ScheduleFacade scheduleFacade;

    /**
     * Test of createSchedule method, of class ScheduleFacade.
     */
    @Test
    @Transactional
    public void testCreateSchedule() {
        System.out.println("createSchedule");
        Schedule schedule = scheduleFacade.createSchedule();
        assertNotNull(schedule);
        assertNotNull(schedule.getAfterPipelines());
    }

    /**
     * Test of getSchedulesFor method, of class ScheduleFacade.
     */
    @Test
    @Transactional
    public void testGetSchedulesFor() {
        System.out.println("getAllSchedules");
        Pipeline pipeline = pipelineFacade.createPipeline();
        pipelineFacade.save(pipeline);

        List<Schedule> schedules = scheduleFacade.getSchedulesFor(pipeline);
        assertNotNull(schedules);
        assertTrue(schedules.isEmpty());

        Schedule schedule = scheduleFacade.createSchedule();
        schedule.setPipeline(pipeline);
        scheduleFacade.save(schedule);
        
        List<Schedule> schedules1 = scheduleFacade.getSchedulesFor(pipeline);
        assertNotNull(schedules1);
        assertTrue(schedules1.size() == 1);
        assertEquals(schedule, schedules1.get(0));
    }

    /**
     * Test of getAllTimeBased method, of class ScheduleFacade.
     */
    @Test
    @Transactional
    public void testGetAllTimeBased() {
        System.out.println("getAllTimeBased");
        Pipeline pipeline = pipelineFacade.createPipeline();
        pipelineFacade.save(pipeline);

        List<Schedule> schedules = scheduleFacade.getAllTimeBased();
        assertNotNull(schedules);

        Schedule schedule = scheduleFacade.createSchedule();
        schedule.setType(ScheduleType.PERIODICALLY);
        schedule.setPipeline(pipeline);
        scheduleFacade.save(schedule);

        List<Schedule> schedules1 = scheduleFacade.getAllTimeBased();
        assertNotNull(schedules1);
        assertTrue(schedules.size() + 1 == schedules1.size());

        assertTrue(schedules1.contains(schedule));
    }

    /**
     * Test of getSchedule method, of class ScheduleFacade.
     */
    @Test
    @Transactional
    public void testGetSchedule() {
        System.out.println("getSchedule");
        Pipeline pipeline = pipelineFacade.createPipeline();
        pipelineFacade.save(pipeline);

        Schedule schedule = scheduleFacade.createSchedule();
        assertNull(schedule.getId());
        schedule.setType(ScheduleType.PERIODICALLY);
        schedule.setPipeline(pipeline);
        scheduleFacade.save(schedule);
        assertNotNull(schedule.getId());
        Long id = schedule.getId();

        Schedule schedule1 = scheduleFacade.getSchedule(id);
        assertNotNull(schedule1);
        assertEquals(schedule, schedule1);
    }

    /**
     * Test of save method, of class ScheduleFacade.
     */
    @Test
    @Transactional
    public void testSave() {
        System.out.println("save");
        Pipeline pipeline = pipelineFacade.createPipeline();
        pipelineFacade.save(pipeline);

        Schedule schedule = scheduleFacade.createSchedule();
        assertNull(schedule.getId());
        schedule.setType(ScheduleType.PERIODICALLY);
        schedule.setPipeline(pipeline);
        scheduleFacade.save(schedule);
        assertNotNull(schedule.getId());
    }

    /**
     * Test of delete method, of class ScheduleFacade.
     */
    @Test
    @Transactional
    public void testDelete() {
        System.out.println("delete");
        Pipeline pipeline = pipelineFacade.createPipeline();
        pipelineFacade.save(pipeline);

        Schedule schedule = scheduleFacade.createSchedule();
        assertNull(schedule.getId());
        schedule.setType(ScheduleType.PERIODICALLY);
        schedule.setPipeline(pipeline);
        scheduleFacade.save(schedule);
        assertNotNull(schedule.getId());
        Long id = schedule.getId();
		em.flush();

        scheduleFacade.delete(schedule);
        Schedule schedule1 = scheduleFacade.getSchedule(id);
        assertNull(schedule1);
    }

    /**
     * Test of deleteNotification method, of class ScheduleFacade.
     */
    @Test
    @Transactional
    public void testDeleteNotification() {
        System.out.println("deleteNotification");
        Pipeline pipeline = pipelineFacade.createPipeline();
        pipelineFacade.save(pipeline);

        Schedule schedule = scheduleFacade.createSchedule();
        assertNull(schedule.getId());
        schedule.setType(ScheduleType.PERIODICALLY);
        schedule.setPipeline(pipeline);
        
        ScheduleNotificationRecord scheduleNotificationRecord = new ScheduleNotificationRecord();
        EmailAddress emailAddress = new  EmailAddress("user@example.com");
        Set<EmailAddress> emailAddresses = new HashSet<>();
        emailAddresses.add(emailAddress);
        
        scheduleNotificationRecord.setSchedule(schedule);
        scheduleNotificationRecord.setEmails(emailAddresses);
        schedule.setNotification(scheduleNotificationRecord);
        scheduleFacade.save(schedule);
        
        scheduleFacade.deleteNotification(scheduleNotificationRecord);
        em.flush();
        
        Schedule schedule1 = scheduleFacade.getSchedule(schedule.getId());
        assertNotNull(schedule1);
        assertEquals(schedule, schedule1);
        
        ScheduleNotificationRecord scheduleNotificationRecord1 = schedule1.getNotification();
        assertNull(scheduleNotificationRecord1);
    }

    /**
     * Test of execute method, of class ScheduleFacade.
     */
    @Test
    public void testExecute() {
        System.out.println("execute");
        // TODO review the generated test code and remove the default call to fail.
        fail("Have no idea what to test here, if anything.");
    }

    /**
     * Test of executeFollowers method, of class ScheduleFacade.
     */
    @Test
    public void testExecuteFollowers_0args() {
        System.out.println("executeFollowers");
        // TODO review the generated test code and remove the default call to fail.
        fail("Have no idea what to test here, if anything.");
    }

    /**
     * Test of executeFollowers method, of class ScheduleFacade.
     */
    @Test
    public void testExecuteFollowers_Pipeline() {
        System.out.println("executeFollowers");
        // TODO review the generated test code and remove the default call to fail.
        fail("Have no idea what to test here, if anything.");
    }
}
