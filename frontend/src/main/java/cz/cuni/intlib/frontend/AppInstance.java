package cz.cuni.intlib.frontend;

import javax.persistence.EntityManager;

import com.vaadin.addon.jpacontainer.JPAContainerFactory;

/**
 * Contains application static data.
 * 
 * @author Petyr
 *
 */
public class AppInstance{

	private static ThreadLocal<AppInstance> instance = 
            new ThreadLocal<AppInstance>();

	private EntityManager entityManager;
	
	/**
	 * Name of entity in persistence.xml
	 */
	private final String PUNIT_INTLIB = "intlib";
	
	private AppInstance() {
		// create SQL connection
		this.entityManager = 
				JPAContainerFactory.createEntityManagerForPersistenceUnit(PUNIT_INTLIB);
	}
	
	/**
	 * If there is not a instance of AppInstance
	 * then create it. 
	 */
	public static void createInstance() {
		if (instance.get() == null) {
			// create new instance
			instance.set( new AppInstance() );
		}
	}
	
	public static EntityManager getEntityManager() {
		// TODO: remove 
		createInstance();
		
		
		return instance.get().entityManager;
	}
	
	// TODO provazat zivotnost tridy s zivotnosti session
}
