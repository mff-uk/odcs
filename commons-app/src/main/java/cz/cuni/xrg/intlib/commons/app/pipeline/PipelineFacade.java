package cz.cuni.xrg.intlib.commons.app.pipeline;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import cz.cuni.xrg.intlib.commons.app.pipeline.graph.PipelineGraph;
import cz.cuni.xrg.intlib.commons.app.util.IntlibEntityManagerFactory;

/**
 * Facade providing actions with pipelines.
 * 
 * @author Jan Vojt <jan@vojt.net>
 *
 */
public class PipelineFacade {
	
	/**
	 * Entity manager for accessing database with persisted objects
	 * @todo autowire through Spring and remove setter and constructor
	 */
	private EntityManager em;
	
	/**
	 * Constructs facade and its dependencies.
	 */
	public PipelineFacade() {
		this(IntlibEntityManagerFactory.create());
	}
	
	/**
	 * Construct with given Entity Manager
	 * @param em
	 */
	public PipelineFacade(EntityManager em) {
		this.em = em;
	}
	
	/**
	 * Pipeline factory
	 * @return newly created pipeline
	 */
	public Pipeline createPipeline() {
		Pipeline pipe = new Pipeline();
		pipe.setGraph(new PipelineGraph());
		
		return pipe;
	}
	
	/**
	 * Returns list of all pipelines persisted in the database.
	 * @return list of pipelines
	 */
	public List<Pipeline> getAllPipelines() {
		
		@SuppressWarnings("unchecked")
		List<Pipeline> resultList = Collections.checkedList(
				em.createQuery("SELECT e FROM Pipeline e").getResultList(),
				Pipeline.class
		);
		
		return resultList;
	}

	/**
	 * Find pipeline in database by ID and return it.
	 * @param id of Pipeline
	 * @return Pipeline
	 */
	public Pipeline getPipeline(int id) {
		
		return em.find(Pipeline.class, id);
	}
	
	/**
	 * Saves any modifications made to the pipeline into the database.
	 * @param pipeline
	 */
	public void save(Pipeline pipeline) {
		
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		if (pipeline.getId() == 0) {
			em.persist(pipeline);
		} else {
			em.merge(pipeline);
		}
		
		tx.commit();
	}
	
	/**
	 * Deletes pipeline from database.
	 * @param pipeline
	 */
	public void delete(Pipeline pipeline) {
		
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		em.remove(pipeline);
		
		tx.commit();
	}

}
