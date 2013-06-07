package cz.cuni.xrg.intlib.commons.app.scheduling;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Facade providing actions with plan.
 *
 */
public class PlanFacade {

	private static final Logger LOG = LoggerFactory.getLogger(PlanFacade.class);
	
	/**
	 * Entity manager for accessing database with persisted objects
	 */
	@PersistenceContext
	private EntityManager em;
	
	
}
