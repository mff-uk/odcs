package cz.cuni.xrg.intlib.commons.app.pipeline;

import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import cz.cuni.xrg.intlib.commons.app.execution.PipelineExecution;

/**
 * Facade providing actions with pipelines.
 *
 * @author Jan Vojt
 */
public class PipelineFacade {

	private static final Logger LOG = LoggerFactory.getLogger(PipelineFacade.class);
	
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
		if (pipeline.getId() == null) {
			em.persist(pipeline);
		} else {
			em.merge(pipeline);
		}
	}

	/**
	 * Deletes pipeline from database.
	 *
	 * @param pipeline
	 */
	@Transactional
	public void delete(Pipeline pipeline) {
		// we might be trying to remove detached entity
		// lets fetch it again and then try to remove
		// TODO this is just a workaround -> resolve in future release!
		Pipeline p = pipeline.getId() == null
			? pipeline : getPipeline(pipeline.getId());
		if (p != null) {
			em.remove(p);
		} else {
			LOG.warn("Pipeline with ID " + pipeline.getId() + " was not found and so cannot be deleted!");
		}
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
	public PipelineExecution getExecution(long id) {
		return em.find(PipelineExecution.class, id);
	}

	/**
	 * Fetch all executions for given pipeline.
	 * 
	 * @param pipeline
	 * @return pipeline executions
	 */
	public List<PipelineExecution> getExecutions(Pipeline pipeline) {

		@SuppressWarnings("unchecked")
		List<PipelineExecution> resultList = Collections.checkedList(
			em.createQuery("SELECT e FROM PipelineExecution e"
				+ " WHERE e.pipeline = :pipe")
			.setParameter("pipe", pipeline)
			.getResultList(),
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
	@Transactional
	public void save(PipelineExecution exec) {
		if (exec.getId() == null) {
			em.persist(exec);
		} else {
			em.merge(exec);
		}
	}

	/**
	 * Deletes pipeline from database.
	 *
	 * @param exec
	 */
	@Transactional
	public void delete(PipelineExecution exec) {
		// we might be trying to remove detached entity
		// lets fetch it again and then try to remove
		// TODO this is just a workaround -> resolve in future release!
		PipelineExecution e = exec.getId() == null
				? exec : getExecution(exec.getId());
		if (e != null) {
			em.remove(e);
		} else {
			LOG.warn("Pipeline execution with ID " + exec.getId() + " was not found and so cannot be deleted!");
		}
	}

}
