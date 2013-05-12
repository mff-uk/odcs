package cz.cuni.xrg.intlib.commons.app.pipeline;

import cz.cuni.xrg.intlib.commons.app.pipeline.graph.PipelineGraph;
import cz.cuni.xrg.intlib.commons.app.util.IntlibEntityManagerFactory;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 * Facade providing actions with pipelines.
 * 
 * @author Jan Vojt
 * 
 * TODO	Refactor transactions to be able to perform multiple actions
 * 			per one transaction.
 */
public class PipelineFacade {
	
	/**
	 * Entity manager for accessing database with persisted objects
	 * TODO autowire through Spring and remove setter and constructor
	 */
	private EntityManager em;
	
	/**
	 * Constructs facade and its dependencies.
	 */
	public PipelineFacade() {
		this(IntlibEntityManagerFactory.getEm());
	}
	
	/**
	 * Construct with given Entity Manager
	 * @param em
	 */
	public PipelineFacade(EntityManager em) {
		this.em = em;
	}
	
	/* ******************* Methods for managing Pipeline ******************** */
	
	/**
	 * Pipeline factory.
	 * Created instance is not yet managed by {@link EntityManager}, thus needs
	 * to be saved with {@link #save(Pipeline)} method.
	 * 
	 * @return newly created pipeline
	 */
	public Pipeline createPipeline() {
		Pipeline pipe = new Pipeline();
		pipe.setGraph(new PipelineGraph());
		
		return pipe;
	}
	
	/**
	 * Returns list of all pipelines persisted in the database.
	 * 
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
	 * 
	 * @param id of Pipeline
	 * @return Pipeline
	 */
	public Pipeline getPipeline(int id) {
		
		return em.find(Pipeline.class, id);
	}
	
	/**
	 * Saves any modifications made to the pipeline into the database.
	 * 
	 * @param pipeline
	 */
	public void save(Pipeline pipeline) {
		
		EntityTransaction tx = em.getTransaction();
		if (!tx.isActive()) {
			tx.begin();
		}
		
		em.persist(pipeline);
		
		tx.commit();
	}
	
	/**
	 * Deletes pipeline from database.
	 * 
	 * @param pipeline
	 */
	public void delete(Pipeline pipeline) {
		
		EntityTransaction tx = em.getTransaction();
		if (!tx.isActive()) {
			tx.begin();
		}

		em.remove(pipeline);
		
		tx.commit();
	}
	
	/* ******************** Methods for managing PipelineExecutions ********* */
	
	/**
	 * Creates a new {@link PipelineExecution}, which represents a pipeline run.
	 * Created instance is not yet managed by {@link EntityManager}, thus needs
	 * to be saved with {@link #save(PipelineExecution)} method.
	 * 
	 * @param pipeline
	 * @return 
	 */
	public PipelineExecution createExecution(Pipeline pipeline) {
		PipelineExecution execution = new PipelineExecution(pipeline);
		return execution;
	}

	/**
	 * Fetches all {@link PipelineExecution}s from database.
	 * 
	 * @return list of executions
	 */
	public List<PipelineExecution> getAllExecutions() {
		
		@SuppressWarnings("unchecked")
		List<PipelineExecution> resultList = Collections.checkedList(
				em.createQuery("SELECT e FROM PipelineExecution e").getResultList(),
				PipelineExecution.class
		);
		
		return resultList;
	}

	/**
	 * Persists new {@link PipelineExecution} or updates it if it was already
	 * persisted before.
	 * 
	 * @param exec 
	 */
	public void save(PipelineExecution exec) {
		
		EntityTransaction tx = em.getTransaction();
		if (!tx.isActive()) {
			tx.begin();
		}
		
		em.persist(exec);
		
		tx.commit();
	}
	
	/**
	 * Deletes pipeline from database.
	 * 
	 * @param exec
	 */
	public void delete(PipelineExecution exec) {
		
		EntityTransaction tx = em.getTransaction();
		if (!tx.isActive()) {
			tx.begin();
		}

		em.remove(exec);
		
		tx.commit();
	}

}
