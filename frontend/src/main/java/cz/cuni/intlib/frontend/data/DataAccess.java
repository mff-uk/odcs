package cz.cuni.intlib.frontend.data;

import javax.persistence.EntityManager;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;

import cz.cuni.intlib.commons.app.data.Pipeline;

/**
 * Class contains access to database.
 * @author Petyr
 *
 */
public class DataAccess {

	/**
	 * Entity manager for PUNIT_INTLIB entity connection.
	 */
	private EntityManager entityManagerIntlib = null;	
	
	/**
	 * Name of entity in persistence.xml
	 */
	private final String entityIdIntlib = "intlib";	

	/**
	 * Create a JPAContainer for pipelines. Container
	 * filters has been set according to user rights.
	 * @return
	 */
	public JPAContainer<Pipeline> getPipelines() {
		JPAContainer<Pipeline> pipelines = 
				JPAContainerFactory.make(Pipeline.class, this.entityManagerIntlib);
		// TODO set filters ...
		return pipelines;
	}
	
	public EntityManager getEntityManager() {
		return this.entityManagerIntlib;
	}
	
	/**
	 * Create entity managers.
	 */
	public DataAccess() {
		// connect to database
		this.entityManagerIntlib = 
				JPAContainerFactory.createEntityManagerForPersistenceUnit(entityIdIntlib);
	}
	
}
