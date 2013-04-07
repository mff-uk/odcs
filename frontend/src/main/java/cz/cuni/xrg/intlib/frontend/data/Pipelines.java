package cz.cuni.xrg.intlib.frontend.data;

import javax.persistence.EntityManager;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;

import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;

/**
 * Class provide function necessary to work with pipelines related
 * records in database.
 * 
 * Class hold single internal representation of JPAContainer, that
 * is used to work with database.
 * 
 * @author Petyr
 *
 */
public class Pipelines {

	/**
	 * Entity manager for database access.
	 */
	private EntityManager entityManager = null;		
	
	/**
	 * Actual version of access container.
	 */
	private JPAContainer<Pipeline> pipelinesContainer = null;
		
	/**
	 * 
	 * @param entityManager entity manager for database connection
	 */
	public Pipelines(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * Check existence of this.pipelinesContainer. If
	 * the variable is not initialised then initialise it
	 * otherwise do nothing.
	 */
	protected void createPipelineConteiner() {
		if (this.pipelinesContainer == null) {
			this.pipelinesContainer = 
				JPAContainerFactory.make(Pipeline.class, this.entityManager);
		}
	}
	
	/**
	 * Create a JPAContainer for pipelines. Container
	 * filters has been set according to user rights.
	 * @return
	 */
	public JPAContainer<Pipeline> getPipelines() {		
		createPipelineConteiner();
		// TODO set filters ...
		return this.pipelinesContainer;
	}
	
	/**
	 * Return entity for pipeline.
	 * 
	 * Please note, that this method will create a new instance of EntityItem upon every execution.
	 * See JPAContainer<T>.getItem for more details.
	 * 
	 * @param id pipeline id
	 * @return pipeline or null in case of invalid id
	 */
	public EntityItem<Pipeline> getPipeline(String id) {
		createPipelineConteiner();
		// return instance
		return this.pipelinesContainer.getItem(id);
	}
	
	/**
	 * Set given pipeline class to the database. If no entity is given pipeline 
	 * is consider to be new and is saved under new unique id.
	 *  
	 * @param pipeline pipeline class to save
	 * @param entity entity related to pipeline or null
	 * @return updated/created pipeline entity
	 */
	public EntityItem<Pipeline> set(Pipeline pipeline, EntityItem<Pipeline> entity) {
		createPipelineConteiner();
		
		if (entity == null ){
			// new record
			// retrieve id of new data
			Object itemId = this.pipelinesContainer.addEntity(pipeline);
			// return new entity
			return this.pipelinesContainer.getItem(itemId);
		} else {
			// update existing record
			javax.persistence.EntityTransaction transaction = this.entityManager.getTransaction();
			// save entity 
			transaction.begin();			
			this.entityManager.merge(pipeline);
			transaction.commit();
			// refresh entity end return it
			entity.refresh();
			return entity;
		}		
	}
	
	/**
	 * Try to delete pipeline of given id.
	 * @param objectId pipeline id
	 * @return true if pipeline has been deleted
	 */
	public boolean remove(Object objectId) {
		createPipelineConteiner();
		// try to remove object
		boolean result = this.pipelinesContainer.removeItem(objectId);

		return result;
	}
}









