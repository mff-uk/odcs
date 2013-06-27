package cz.cuni.xrg.intlib.commons.app.scheduling;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.execution.Record;
import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;

/**
 * Facade providing actions with plan.
 *
 */
public class ScheduleFacade {

	private static final Logger logger = LoggerFactory.getLogger(ScheduleFacade.class);
	
	/**
	 * Entity manager for accessing database with persisted objects
	 */
	@PersistenceContext
	private EntityManager em;
	
	/**
	 * Returns list of all Plans currently persisted in database.
	 * @return Plans list
	 */
	public List<Schedule> getAllPlans() {
		
		@SuppressWarnings("unchecked")
		List<Schedule> resultList = Collections.checkedList(
				em.createQuery("SELECT e FROM schedule e").getResultList(),
				Schedule.class
		);

		return resultList;
	}
	
	/**
	 * Fetches all Schedule that should be activated after given pipeline execution.
	 *
	 * @param pipeline
	 * @return
	 */
	public List<Schedule> getFollowers(Pipeline pipeline) {
		@SuppressWarnings("unchecked")
		List<Schedule> resultList = Collections.checkedList(
			em.createQuery("SELECT r FROM Record r WHERE r.pred = :pipeline AND r.type = :type")
				.setParameter("pipeline", pipeline)
				.setParameter("type", ScheduleType.AfterPipeline)
				.getResultList(),
				Schedule.class
		);
		return resultList;
	}
	
	/**
	 * Find Schedule in database by ID and return it.
	 * @param id
	 * @return
	 */
	public Schedule getSchedule(long id) {
		return em.find(Schedule.class, id);
	}	
	
	/**
	 * Saves any modifications made to the Schedule into the database.
	 * @param schedule
	 */
	@Transactional
	public void save(Schedule schdule) {
		if (schdule.getId() == null) {
			em.persist(schdule);
		} else {
			em.merge(schdule);
		}
	}

	/**
	 * Deletes Schedule from the database.
	 * @param schedule
	 */
	@Transactional
	public void delete(Schedule schedule) {
		// we might be trying to remove detached entity
		// lets fetch it again and then try to remove
		// TODO Honza: this is just a workaround -> resolve in future release!
		Schedule s = schedule.getId() == null ? schedule : getSchedule(schedule.getId());
		if (s != null) {
			em.remove(s);
		} else {
			logger.warn("Schedule with ID " + s.getId() + " was not found and so cannot be deleted!");
		}
	}	
	
}
