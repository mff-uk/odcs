package cz.cuni.xrg.intlib.commons.app.scheduling;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test suite for{@link ScheduleNextRun}
 * 
 * @author Petyr
 *
 */
public class ScheduleNextRunTest {
	
	/**
	 * If pipeline is disabled it should always return false.
	 */
	@Test
	public void disabled() {
		Schedule schedule = new Schedule();		
		schedule.setEnabled(false);
		schedule.setType(ScheduleType.PERIODICALLY);
		schedule.setStrictlyTimed(false);
		// prepare times -> run one hour in future
		Calendar calendarFirst = Calendar.getInstance();
		calendarFirst.setTime(new Date());
		calendarFirst.add(Calendar.HOUR, -1);
		// set schedule
		schedule.setFirstExecution(calendarFirst.getTime());
		schedule.setPeriod(2);
		schedule.setPeriodUnit(PeriodUnit.HOUR);
		
		Date nextRunScheduled = ScheduleNextRun.calculateNextRun(schedule);
		
		assertNull(nextRunScheduled);
	}
	
	/**
	 * If in {@link ScheduleType#AFTER_PIPELINE} state then null 
	 * should be returned.
	 */
	@Test
	public void runAfter() {
		Schedule schedule = new Schedule();
		schedule.setEnabled(true);
		schedule.setType(ScheduleType.AFTER_PIPELINE);
		
		Date nextRunScheduled = ScheduleNextRun.calculateNextRun(schedule);
		assertNull(nextRunScheduled);
	}
	
	/**
	 * As first run it should return the value of first.
	 */
	@Test
	public void firstInFutureNonStrict() {
		Schedule schedule = new Schedule();		
		schedule.setEnabled(true);
		schedule.setType(ScheduleType.PERIODICALLY);
		schedule.setStrictlyTimed(false);
		// prepare times -> run one hour in future
		Calendar calendarFirst = Calendar.getInstance();
		calendarFirst.setTime(new Date());
		calendarFirst.add(Calendar.HOUR, -1);
				
		// set schedule
		schedule.setFirstExecution(calendarFirst.getTime());
		schedule.setPeriod(2);
		schedule.setPeriodUnit(PeriodUnit.HOUR);
		
		Date nextRunScheduled = ScheduleNextRun.calculateNextRun(schedule);
		
		assertEquals(calendarFirst.getTime(), nextRunScheduled);
	}

	/**
	 * First execution is in past, strict mode, just once, 
	 * then the pipeline should not be executed.
	 */
	@Test
	public void firstInFutureStrictJustOnce() {
		Schedule schedule = new Schedule();		
		schedule.setEnabled(true);
		schedule.setType(ScheduleType.PERIODICALLY);
		schedule.setStrictlyTimed(true);
		schedule.setStrictToleranceMinutes(10);
		schedule.setJustOnce(true);
		// prepare times -> run one hour in future
		Calendar calendarFirst = Calendar.getInstance();
		calendarFirst.setTime(new Date());
		calendarFirst.add(Calendar.HOUR, -1);
				
		// set schedule
		schedule.setFirstExecution(calendarFirst.getTime());
		schedule.setPeriod(2);
		schedule.setPeriodUnit(PeriodUnit.HOUR);
		
		Date nextRunScheduled = ScheduleNextRun.calculateNextRun(schedule);
		
		assertNull(nextRunScheduled);
	}		
	
	/**
	 * First execution is in past, strict mode, no just once. In this
	 * case we should run in the next period.
	 */
	@Test
	public void firstInFutureStrict() {
		Schedule schedule = new Schedule();		
		schedule.setEnabled(true);
		schedule.setType(ScheduleType.PERIODICALLY);
		schedule.setStrictlyTimed(true);
		schedule.setStrictToleranceMinutes(10);
		schedule.setJustOnce(false);
		// prepare times -> run one hour in future
		Calendar calendarFirst = Calendar.getInstance();
		calendarFirst.setTime(new Date());
		calendarFirst.add(Calendar.HOUR, -1);
				
		// set schedule
		schedule.setFirstExecution(calendarFirst.getTime());
		schedule.setPeriod(2);
		schedule.setPeriodUnit(PeriodUnit.HOUR);
		
		Date nextRunScheduled = ScheduleNextRun.calculateNextRun(schedule);
		
		// calculate nextTime
		Calendar calendarNext = Calendar.getInstance();
		calendarNext.setTime(calendarFirst.getTime());
		calendarNext.add(Calendar.HOUR, 2);
		
		assertEquals(calendarNext.getTime(), nextRunScheduled);
	}	
	
	/**
	 * There were already some execution. But the next execution is in past.
	 * For non strict mode it should be in past.
	 */
	@Test
	public void nextInPast() {
		Schedule schedule = new Schedule();		
		schedule.setEnabled(true);
		schedule.setType(ScheduleType.PERIODICALLY);
		schedule.setStrictlyTimed(false);
		schedule.setJustOnce(false);
		// prepare times -> run one hour in future
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.HOUR, -5);
				
		// set schedule
		schedule.setFirstExecution(calendar.getTime()); // -5
		calendar.add(Calendar.HOUR, 2);
		schedule.setLastExecution(calendar.getTime()); // - 3
		
		schedule.setPeriod(2);
		schedule.setPeriodUnit(PeriodUnit.HOUR); 
		
		Date nextRunScheduled = ScheduleNextRun.calculateNextRun(schedule);
		
		// calculate nextTime
		calendar.add(Calendar.HOUR, 2); // -1
		
		assertEquals(calendar.getTime(), nextRunScheduled);		
	}

	/**
	 * There were already some execution. But the next execution is in past.
	 * For strict mode the execution should be moved into the feature.
	 */
	@Test
	public void nextInPastStrict() {
		Schedule schedule = new Schedule();		
		schedule.setEnabled(true);
		schedule.setType(ScheduleType.PERIODICALLY);
		schedule.setStrictlyTimed(true);
		schedule.setStrictToleranceMinutes(10);
		schedule.setJustOnce(false);
		// prepare times -> run one hour in future
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.HOUR, -5);
				
		// set schedule
		schedule.setFirstExecution(calendar.getTime()); // -5
		calendar.add(Calendar.HOUR, 2);
		schedule.setLastExecution(calendar.getTime()); // - 3
		
		schedule.setPeriod(2);
		schedule.setPeriodUnit(PeriodUnit.HOUR); 
		
		Date nextRunScheduled = ScheduleNextRun.calculateNextRun(schedule);
		
		// calculate nextTime
		calendar.add(Calendar.HOUR, 2); // -1
		calendar.add(Calendar.HOUR, 2); // +1
		
		assertEquals(calendar.getTime(), nextRunScheduled);
	}

	/**
	 * There were already some execution. But the next execution is in past, 
	 * but in tolerance. So it should return the value in past.
	 */
	@Test
	public void nextInPastStrictInTolerance() {
		Schedule schedule = new Schedule();		
		schedule.setEnabled(true);
		schedule.setType(ScheduleType.PERIODICALLY);
		schedule.setStrictlyTimed(true);
		schedule.setStrictToleranceMinutes(70);
		schedule.setJustOnce(false);
		// prepare times -> run one hour in future
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.HOUR, -5);
				
		// set schedule
		schedule.setFirstExecution(calendar.getTime()); // -5
		calendar.add(Calendar.HOUR, 2);
		schedule.setLastExecution(calendar.getTime()); // - 3
		
		schedule.setPeriod(2);
		schedule.setPeriodUnit(PeriodUnit.HOUR); 
		
		Date nextRunScheduled = ScheduleNextRun.calculateNextRun(schedule);
		
		// calculate nextTime
		calendar.add(Calendar.HOUR, 2); // -1
		// tolerance is 70 minute .. so 1+ hour
		
		assertEquals(calendar.getTime(), nextRunScheduled);
	}	
	
}
