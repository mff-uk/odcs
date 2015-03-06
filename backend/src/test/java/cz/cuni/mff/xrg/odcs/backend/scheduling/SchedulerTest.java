package cz.cuni.mff.xrg.odcs.backend.scheduling;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

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

import static org.junit.Assert.assertTrue;

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

        final Set<Long> history = new HashSet<>();
        history.add(schedule4.getId());
        history.add(schedule3.getId());

        for (int i = 0 ; i < engine.historyOfExecution.size(); ++i) {
            assertTrue(history.contains(engine.historyOfExecution.get(i).getSchedule().getId()));
        }

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
        Schedule schedule = createSchedule(0, ppl);
        Schedule schedule2 = createSchedule(0, ppl);
        Schedule schedule3 = createSchedule(0, ppl);
        Schedule schedule4 = createSchedule(3, ppl);
        scheduler.timeBasedCheck();

        EngineMock engine = new EngineMockWithLimit();
        engine.setPipelineFacade(pipelineFacade);
        engine.doCheck();

        assertEquals(3, engine.historyOfExecution.size());
        {
            final Set<Long> history = new HashSet<>();
            history.add(schedule.getId());
            history.add(schedule2.getId());
            history.add(schedule3.getId());

            for (int i = 0 ; i < engine.historyOfExecution.size(); ++i) {
                assertTrue(history.contains(engine.historyOfExecution.get(i).getSchedule().getId()));
            }
        }

        engine.numberOfRunningJobs--;
        engine.doCheck();

        assertEquals(engine.historyOfExecution.size(), 3);
        {
            final Set<Long> history = new HashSet<>();
            history.add(schedule.getId());
            history.add(schedule2.getId());
            history.add(schedule3.getId());

            for (int i = 0 ; i < engine.historyOfExecution.size(); ++i) {
                assertTrue(history.contains(engine.historyOfExecution.get(i).getSchedule().getId()));
            }
        }

        engine.numberOfRunningJobs--;
        engine.numberOfRunningJobs--;
        engine.doCheck();

        assertEquals(engine.historyOfExecution.size(), 4);
        {
            final Set<Long> history = new HashSet<>();
            history.add(schedule.getId());
            history.add(schedule2.getId());
            history.add(schedule3.getId());
            history.add(schedule4.getId());

            for (int i = 0 ; i < engine.historyOfExecution.size(); ++i) {
                assertTrue(history.contains(engine.historyOfExecution.get(i).getSchedule().getId()));
            }
        }

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
        {
            final Set<Long> history = new HashSet<>();
            history.add(schedule3.getId());
            history.add(schedule4.getId());

            for (int i = 0 ; i < engine.historyOfExecution.size(); ++i) {
                assertTrue(history.contains(engine.historyOfExecution.get(i).getSchedule().getId()));
            }
        }

        engine.numberOfRunningJobs--;
        engine.doCheck();

        assertEquals(engine.historyOfExecution.size(), 3);
        {
            final Set<Long> history = new HashSet<>();
            history.add(schedule2.getId());
            history.add(schedule3.getId());
            history.add(schedule4.getId());

            for (int i = 0 ; i < engine.historyOfExecution.size(); ++i) {
                assertTrue(history.contains(engine.historyOfExecution.get(i).getSchedule().getId()));
            }
        }


        engine.numberOfRunningJobs--;
        engine.doCheck();

        assertEquals(engine.historyOfExecution.size(), 4);
        {
            final Set<Long> history = new HashSet<>();
            history.add(schedule.getId());
            history.add(schedule2.getId());
            history.add(schedule3.getId());
            history.add(schedule4.getId());

            for (int i = 0 ; i < engine.historyOfExecution.size(); ++i) {
                assertTrue(history.contains(engine.historyOfExecution.get(i).getSchedule().getId()));
            }
        }

    }
}
