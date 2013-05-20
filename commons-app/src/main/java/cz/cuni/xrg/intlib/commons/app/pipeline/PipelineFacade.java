package cz.cuni.xrg.intlib.commons.app.pipeline;

import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

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
	 */
	@PersistenceContext
	private EntityManager em;

	/* ******************* Methods for managing Pipeline ******************** */

	/**
	 * Pipeline factory.
	 * Created instance is not yet managed by {@link EntityManager}, thus needs
	 * to be saved with {@link #save(Pipeline)} method.
	 *
	 * @return newly created pipeline
	 */
	public Pipeline createPipeline() {
		return new Pipeline();
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
	public Pipeline getPipeline(long id) {

		return em.find(Pipeline.class, id);
	}

	/**
	 * Saves any modifications made to the pipeline into the database.
	 *
	 * @param pipeline
	 */
	@Transactional
	public void save(Pipeline pipeline) {
		em.persist(pipeline);
	}

	/**
	 * Deletes pipeline from database.
	 *
	 * @param pipeline
	 */
	@Transactional
	public void delete(Pipeline pipeline) {
		em.remove(pipeline);
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
	 * Find pipeline execution in database by ID and return it.
	 *
	 * @param id of PipelineExecution
	 * @return PipelineExecution
	 */
	public PipelineExecution getExecution(int id) {
		return em.find(PipelineExecution.class, id);
	}

	/**
	 * Persists new {@link PipelineExecution} or updates it if it was already
	 * persisted before.
	 *
	 * @param exec
	 */
	@Transactional
	public void save(PipelineExecution exec) {
		em.persist(exec);
	}

	/**
	 * Deletes pipeline from database.
	 *
	 * @param exec
	 */
	@Transactional
	public void delete(PipelineExecution exec) {
		em.remove(exec);
	}

}
