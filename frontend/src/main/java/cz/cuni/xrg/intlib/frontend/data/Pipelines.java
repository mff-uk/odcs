package cz.cuni.xrg.intlib.frontend.data;

import javax.persistence.EntityManager;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;

import cz.cuni.intlib.xrg.commons.app.data.pipeline.Pipeline;

/**
 * Class provide function necessary to work with pipelines related
 * records in database.
 * @author Petyr
 *
 */
public class Pipelines {

	/**
	 * Entity manager for database access.
	 */
	private EntityManager entityManager = null;		
	
	/**
	 * 
	 * @param entityManager entity manager for database connection
	 */
	public Pipelines(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * Create a JPAContainer for pipelines. Container
	 * filters has been set according to user rights.
	 * @return
	 */
	public JPAContainer<Pipeline> getPipelines() {
		JPAContainer<Pipeline> pipelines = 
				JPAContainerFactory.make(Pipeline.class, this.entityManager);
		// TODO set filters ...
		return pipelines;
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
		JPAContainer<Pipeline> pipelineContainer = getPipelines();
		// return instance
		return pipelineContainer.getItem(id);
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
		if (entity == null ){
			// new record
			JPAContainer<Pipeline> pipelineContainer = this.getPipelines();
			// retrieve id of new data
			Object itemId = pipelineContainer.addEntity(pipeline);
			// return new entity
			return pipelineContainer.getItem(itemId);
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
}









