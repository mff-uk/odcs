package cz.cuni.xrg.intlib.backend.scheduling;

import java.util.Calendar;
import java.util.Date;

import cz.cuni.xrg.intlib.commons.app.scheduling.PeriodUnit;

/**
 * Class help decide if run pipeline which is scheduled based on 
 * time.
 * 
 * @author Petyr
 *
 */
class TimeScheduleHelper {

	/**
	 * Size of max minutes differences for two times
	 * that should be considered to be the same by
	 * method {@link #sameForHour(Calendar, Calendar)} 
	 */
	private static final int MINUTE_TOLERANCE = 10;
	
	/**
	 * Private constructor.
	 */
	private TimeScheduleHelper() {}
	
	/**
	 * Return true if given times are same in terms of hour and similar 
	 * in minutes.
	 * @param first
	 * @param second
	 * @return
	 */
	private static Boolean sameForHour(Calendar first, Calendar second) {
		return first.get(Calendar.HOUR_OF_DAY) == second.get(Calendar.HOUR_OF_DAY) &&
				Math.abs(first.get(Calendar.MINUTE) - second.get(Calendar.MINUTE)) < MINUTE_TOLERANCE; 
	}
	
	/**
	 * Check it run execution based on given information about time.
	 * @param firstExecution Time of first scheduled execution.
	 * @param lastExecution Time of last execution.
	 * @param now Current time.
	 * @param period Determine required time between execution in {@link PeriodUnit}s.
	 * @param unit Used time units in period.
	 * @return True if it's time to execute pipeline.
	 */
	public static Boolean runExecution(Date firstExecution, Date lastExecution, Date now, Long period, PeriodUnit unit) {
		Long timeDiffMinutes = (now.getTime() - lastExecution.getTime()) / (1000 * 60);
		switch(unit) {
			case MINUTE:
				// in case of minutes just use time from last execution
				return timeDiffMinutes > period;
			case HOUR:
				// in case of hours just use time from last execution
				return (timeDiffMinutes / 60) > period;
		}
		// in the following we will need calendar support
		Calendar firstExecCalendar = Calendar.getInstance();
		firstExecCalendar.setTime(firstExecution);
		Calendar lastExecCalendar = Calendar.getInstance();
		lastExecCalendar.setTime(lastExecution);
		Calendar nowCalendar = Calendar.getInstance();
		nowCalendar.setTime(now);		
		
		switch(unit) {
			case DAY:
				// for days we require same time in a day and different day from the last execution
				int dayDiff = Math.abs(lastExecCalendar.get(Calendar.DAY_OF_YEAR) - nowCalendar.get(Calendar.DAY_OF_YEAR));
				return 	dayDiff > period &&						
						sameForHour(firstExecCalendar, nowCalendar);
			case WEEK:
				// we require same day in week, same forHour and next week
				int weekDiff = Math.abs(lastExecCalendar.get(Calendar.DAY_OF_YEAR) - nowCalendar.get(Calendar.DAY_OF_YEAR));
				return  weekDiff > period && 
						firstExecCalendar.get(Calendar.DAY_OF_WEEK) == nowCalendar.get(Calendar.DAY_OF_WEEK) &&
						sameForHour(firstExecCalendar, nowCalendar);
			case MONTH:
				int monthDiff = Math.abs(lastExecCalendar.get(Calendar.MONTH) - nowCalendar.get(Calendar.MONTH));				
				if (monthDiff < period) {
					return false;
				}
				// the difference in month is sufficient
				if (firstExecCalendar.get(Calendar.DAY_OF_MONTH) > nowCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
					// we should run the pipeline in the 30, 31 .. day of the month, but the current month does 
					// not have so many days 
					
					// TODO: Petyr: how to deal with this?
					
				} else {
					// check for day
					if (lastExecCalendar.get(Calendar.DAY_OF_MONTH) == nowCalendar.get(Calendar.DAY_OF_MONTH)) {
						// continue be next checking .. 
					} else {
						// bad day .. 
						return false;
					}
				}
				// at the very end .. forHour decide if run .. 
				return sameForHour(firstExecCalendar, nowCalendar);
			default:
				// other PeridUnits are not supported
				return false;
		}
	}
}
