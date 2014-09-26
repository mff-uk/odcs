package cz.cuni.mff.xrg.odcs.backend.scheduling;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import cz.cuni.mff.xrg.odcs.backend.execution.EngineMock;
import cz.cuni.mff.xrg.odcs.commons.app.facade.PipelineFacade;
import cz.cuni.mff.xrg.odcs.commons.app.facade.ScheduleFacade;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.ScheduleType;

@ContextConfiguration(locations = { "classpath:backend-test-context.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback = true)
public class SchedulerTest {

    public static final Integer RUNNIG_PPL_LIMIT = 2;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private PipelineFacade pipelineFacade;

    @Autowired
    private ScheduleFacade scheduleFacade;
    
    private class EngineMockWithLimit extends EngineMock {
        @Override
        protected Integer getLimitOfScheduledPipelines() {
            return RUNNIG_PPL_LIMIT;
        }
    }
    
    @Test
    @Transactional
    public void test() {
        Pipeline ppl = pipelineFacade.createPipeline();
        pipelineFacade.save(ppl);
        Schedule schedule = scheduleFacade.createSchedule();
        schedule.setType(ScheduleType.PERIODICALLY);
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.MINUTE, -1);
        schedule.setFirstExecution(cal.getTime());
        schedule.setEnabled(true);
        schedule.setPipeline(ppl);
        schedule.setPriority((long) 1);
        scheduleFacade.save(schedule);
        scheduler.timeBasedCheck();
        EngineMock engine = new EngineMockWithLimit();
        engine.setPipelineFacade(pipelineFacade);
        engine.doCheck();

    }

    @Test
    @Transactional
    public void test2() {
        Pipeline ppl = pipelineFacade.createPipeline();
        pipelineFacade.save(ppl);

        Schedule schedule = createSchedule(2, ppl);
        Schedule schedule2 = createSchedule(3, ppl);
        Schedule schedule3 = createSchedule(4, ppl);
        Schedule schedule4 = createSchedule(5, ppl);

        scheduler.timeBasedCheck();

        EngineMock engine = new EngineMockWithLimit();
        engine.setPipelineFacade(pipelineFacade);

        engine.doCheck();
        assertEquals(engine.historyOfExecution.size(), 2);
        assertEquals(schedule4.getId(), engine.historyOfExecution.get(0).getSchedule().getId());
        assertEquals(schedule3.getId(), engine.historyOfExecution.get(1).getSchedule().getId());

    }

    public Schedule createSchedule(int priority, Pipeline ppl) {
        Calendar cal = Calendar.getInstance();
        Schedule schedule = scheduleFacade.createSchedule();
        schedule.setType(ScheduleType.PERIODICALLY);
        cal.add(Calendar.MINUTE, -1);
        schedule.setFirstExecution(cal.getTime());
        schedule.setEnabled(true);
        schedule.setPipeline(ppl);
        schedule.setPriority((long) priority);
        scheduleFacade.save(schedule);
        return schedule;

    }

    @Test
    @Transactional
    public void test3() {
        Pipeline ppl = pipelineFacade.createPipeline();
        pipelineFacade.save(ppl);

        Schedule schedule = createSchedule(1, ppl);
        Schedule schedule2 = createSchedule(1, ppl);
        Schedule schedule3 = createSchedule(1, ppl);
        Schedule schedule4 = createSchedule(2, ppl);
        scheduler.timeBasedCheck();

        EngineMock engine = new EngineMockWithLimit();
        engine.setPipelineFacade(pipelineFacade);

        System.out.println("id: {} " + schedule.getId());
        System.out.println("id: {} " + schedule2.getId());
        System.out.println("id: {} " + schedule3.getId());
        System.out.println("id: {} " + schedule4.getId());

        engine.doCheck();

        for (PipelineExecution sch : engine.historyOfExecution) {
            System.out.println("id: " + sch.getId().toString() + " position: " + sch.getOrderNumber());
        }
        assertEquals(3, engine.historyOfExecution.size());
        assertEquals(schedule.getId(), engine.historyOfExecution.get(0).getSchedule().getId());
        assertEquals(schedule2.getId(), engine.historyOfExecution.get(1).getSchedule().getId());
        assertEquals(schedule3.getId(), engine.historyOfExecution.get(2).getSchedule().getId());

        engine.numberOfRunningJobs--;
        engine.doCheck();

        assertEquals(engine.historyOfExecution.size(), 3);
        assertEquals(schedule.getId(), engine.historyOfExecution.get(0).getSchedule().getId());
        assertEquals(schedule2.getId(), engine.historyOfExecution.get(1).getSchedule().getId());
        assertEquals(schedule3.getId(), engine.historyOfExecution.get(2).getSchedule().getId());

        engine.numberOfRunningJobs--;
        engine.numberOfRunningJobs--;
        engine.doCheck();

        assertEquals(engine.historyOfExecution.size(), 4);
        assertEquals(schedule.getId(), engine.historyOfExecution.get(0).getSchedule().getId());
        assertEquals(schedule2.getId(), engine.historyOfExecution.get(1).getSchedule().getId());
        assertEquals(schedule3.getId(), engine.historyOfExecution.get(2).getSchedule().getId());
        assertEquals(schedule4.getId(), engine.historyOfExecution.get(3).getSchedule().getId());

    }

    @Test
    @Transactional
    public void test4() {
        Pipeline ppl = pipelineFacade.createPipeline();
        pipelineFacade.save(ppl);

        Schedule schedule = createSchedule(2, ppl);
        Schedule schedule2 = createSchedule(3, ppl);
        Schedule schedule3 = createSchedule(4, ppl);
        Schedule schedule4 = createSchedule(5, ppl);

        scheduler.timeBasedCheck();

        EngineMock engine = new EngineMockWithLimit();
        engine.setPipelineFacade(pipelineFacade);

        engine.doCheck();
        assertEquals(engine.historyOfExecution.size(), 2);
        assertEquals(schedule4.getId(), engine.historyOfExecution.get(0).getSchedule().getId());
        assertEquals(schedule3.getId(), engine.historyOfExecution.get(1).getSchedule().getId());

        engine.numberOfRunningJobs--;
        engine.doCheck();

        assertEquals(engine.historyOfExecution.size(), 3);
        assertEquals(schedule4.getId(), engine.historyOfExecution.get(0).getSchedule().getId());
        assertEquals(schedule3.getId(), engine.historyOfExecution.get(1).getSchedule().getId());
        assertEquals(schedule2.getId(), engine.historyOfExecution.get(2).getSchedule().getId());

        engine.numberOfRunningJobs--;
        engine.doCheck();

        assertEquals(engine.historyOfExecution.size(), 4);
        assertEquals(schedule4.getId(), engine.historyOfExecution.get(0).getSchedule().getId());
        assertEquals(schedule3.getId(), engine.historyOfExecution.get(1).getSchedule().getId());
        assertEquals(schedule2.getId(), engine.historyOfExecution.get(2).getSchedule().getId());
        assertEquals(schedule.getId(), engine.historyOfExecution.get(3).getSchedule().getId());

    }
}
