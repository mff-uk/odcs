package cz.cuni.xrg.intlib.frontend.data;

import javax.persistence.EntityManager;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;

import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;

/**
 * Class contains access to database.
 * @author Petyr
 *
 */
@Deprecated
public class DataAccess {

	/**
	 * Entity manager for entityIdIntlib entity connection.
	 */
	private EntityManager entityManagerIntlib = null;	
	
	/**
	 * Name of entity in persistence.xml
	 */
	private final String entityIdIntlib = "intlib";	

	/**
	 * Contains function used to access pipeline related tables.
	 */
	private Pipelines pipelines = null;

	/**
	 * Return class for manipulation with pipeline related data.
	 * @return
	 */
	public Pipelines pipelines() {
		return this.pipelines;
	}
	
	/**
	 * Create entity managers. Should be called only once at application entry.  
	 */
	public DataAccess() {
		// connect to database
		this.entityManagerIntlib = 
				JPAContainerFactory.createEntityManagerForPersistenceUnit(entityIdIntlib);
		// ... 
		this.pipelines = new Pipelines(this.entityManagerIntlib);
	}
	
}
