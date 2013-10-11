package cz.cuni.mff.xrg.odcs.backend.facade;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.ScheduleNotificationRecord;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Facade for managing schedules, which tolerates database crashes. This facade
 * is specially altered for servicing backend, where we do not want to trash
 * all progress of unfinished pipeline runs just because of a short database
 * outage.
 *
 * <p>
 * TODO The concept of crash-proof facades could be solved nicer and with less
 *		code using AOP.
 * 
 * @author Jan Vojt
 */
public class ScheduleFacade extends cz.cuni.mff.xrg.odcs.commons.app.scheduling.ScheduleFacade {
	
	/**
	 * Handler taking care of DB outages.
	 */
	@Autowired
	private ErrorHandler handler;

	@Override
	public List<Schedule> getAllSchedules() {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getAllSchedules();
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public List<Schedule> getSchedulesFor(Pipeline pipeline) {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getSchedulesFor(pipeline);
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public List<Schedule> getFollowers(Pipeline pipeline) {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getFollowers(pipeline);
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public List<Schedule> getAllTimeBased() {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getAllTimeBased();
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public Schedule getSchedule(long id) {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getSchedule(id);
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public void save(Schedule schedule) {
		int attempts = 0;
		while (true) try {
			attempts++;
			super.save(schedule);
			return;
		} catch (IllegalArgumentException ex) {
			// given schedule is a removed entity
			throw ex;
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public void delete(Schedule schedule) {
		int attempts = 0;
		while (true) try {
			attempts++;
			super.delete(schedule);
			return;
		} catch (IllegalArgumentException ex) {
			// given schedule is not persisted
			throw ex;
		} catch (RuntimeException ex) {
			handler.handle(attempts, ex);
		}
	}

	@Override
	public void deleteNotification(ScheduleNotificationRecord notify) {
		int attempts = 0;
		while (true) try {
			attempts++;
			super.deleteNotification(notify);
			return;
		} catch (IllegalArgumentException ex) {
			// given notification is not persisted
			throw ex;
		} catch (RuntimeException ex) {
			handler.handle(attempts, ex);
		}
	}
}
