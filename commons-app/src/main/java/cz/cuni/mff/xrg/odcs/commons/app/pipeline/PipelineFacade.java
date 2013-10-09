package cz.cuni.mff.xrg.odcs.commons.app.pipeline;

import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthenticationContext;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.graph.PipelineGraph;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import cz.cuni.mff.xrg.odcs.commons.app.user.User;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;


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
	
	@Autowired(required = false)
	private AuthenticationContext authCtx;

	/* ******************* Methods for managing Pipeline ******************** */

	/**
	 * Pipeline factory with preset currently logged-in {@link User} as owner.
	 * Created instance is not yet managed by {@link EntityManager}, thus needs
	 * to be saved with {@link #save(Pipeline)} method.
	 *
	 * @return newly created pipeline
	 */
	public Pipeline createPipeline() {
		Pipeline pipeline = new Pipeline();
		pipeline.setGraph(new PipelineGraph());
		if (authCtx != null) {
			pipeline.setUser(authCtx.getUser());
		}
		return pipeline;
	}
	
	/**
	 * Creates a clone of given pipeline and returns it as a new instance.
	 * Original owner is not preserved, rather currently logged in user is set
	 * as an owner of the newly created pipeline.
	 * 
	 * @param pipeline original pipeline to copy
	 * @return newly copied pipeline
	 */
	@PreAuthorize("hasPermission(#pipeline, 'copy')")
	public Pipeline copyPipeline(Pipeline pipeline) {
		Pipeline nPipeline = new Pipeline(pipeline);
		if (authCtx != null) {
			nPipeline.setUser(authCtx.getUser());
		}
		return nPipeline;
	}

	/**
	 * Returns list of all pipelines persisted in the database.
	 *
	 * @return list of pipelines
	 */
	@PostFilter("hasPermission(filterObject,'view')")
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
	@PostAuthorize("hasPermission(returnObject,'view')")
	public Pipeline getPipeline(long id) {

		return em.find(Pipeline.class, id);
	}

	/**
	 * Saves any modifications made to the pipeline into the database.
	 *
	 * @param pipeline
	 */
	@Transactional
	@PreAuthorize("hasPermission(#pipeline,'save')")
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
	@PreAuthorize("hasPermission(#pipeline, 'delete')")
	public void delete(Pipeline pipeline) {
		// we might be trying to remove detached entity
		if (!em.contains(pipeline) && pipeline.getId() != null) {
			pipeline = getPipeline(pipeline.getId());
		}
		em.remove(pipeline);
	}
	
	/**
	 * Fetches all pipelines using give DPU template.
	 * 
	 * @param dpu template
	 * @return pipelines using DPU template
	 */
	@PreAuthorize("hasPermission(#dpu, 'view')")
	public List<Pipeline> getPipelinesUsingDPU(DPUTemplateRecord dpu) {

		@SuppressWarnings("unchecked")
		List<Pipeline> resultList = Collections.checkedList(
				em.createQuery("SELECT e FROM Pipeline e"
				+ " LEFT JOIN e.graph g"
				+ " LEFT JOIN g.nodes n"
				+ " LEFT JOIN n.dpuInstance i"
				+ " LEFT JOIN i.template t"
				+ " WHERE t = :dpu"
				).setParameter("dpu", dpu)
				.getResultList(),
				Pipeline.class
		);

		return resultList;
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
		if (authCtx != null) {
			execution.setOwner(authCtx.getUser());
		}
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
	 * Fetch executions for given pipeline in given status.
	 * @param pipeline Pipeline which executions should be fetched.
	 * @param status Execution status, in which execution should be.
	 * @return PipelineExecutions
	 * 
	 */
	public List<PipelineExecution> getExecutions(Pipeline pipeline, PipelineExecutionStatus status) {
		@SuppressWarnings("unchecked")
		List<PipelineExecution> resultList = Collections.checkedList(
				em.createQuery(
				"SELECT e FROM PipelineExecution e" +
				" WHERE e.pipeline = :pipe" +
				" AND e.status = :status")
				.setParameter("pipe", pipeline)
				.setParameter("status", status)
				.getResultList(),
				PipelineExecution.class
		);
		return resultList;
	}

	/**
	 * Return end time of latest execution of given status for given pipeline.
	 * Ignore null values.
	 * @param pipeline
	 * @param status Execution status, used to filter pipelines.
	 * @return
	 */
	public Date getLastExecTime(Pipeline pipeline, PipelineExecutionStatus status) {
            HashSet statuses = new HashSet(1);
            statuses.add(status);
            PipelineExecution exec = getLastExec(pipeline, statuses);
            if (exec == null) {
                return null;
            } else {
                return exec.getEnd();
            }
	}
        
        /**
	 * Return latest execution of given statuses for given pipeline.
	 * Ignore null values.
	 * @param pipeline
	 * @param statuses Set of execution statuses, used to filter pipelines.
	 * @return
	 */
        public PipelineExecution getLastExec(Pipeline pipeline, Set<PipelineExecutionStatus> statuses) {
            @SuppressWarnings("unchecked")
		List<PipelineExecution> resultList = Collections.checkedList(
				em.createQuery(
				"SELECT e FROM PipelineExecution e" +
				" WHERE e.pipeline = :pipe" +
				" AND e.status IN :status" +
				" AND e.end IS NOT NULL" +
				" ORDER BY e.end DESC")
				.setParameter("pipe", pipeline)
				.setParameter("status", statuses)
				.getResultList(),
				PipelineExecution.class
		);
		if (resultList.isEmpty()) {
			return null;
		} else {
			return resultList.get(0);
		}
        }
	
        /**
	 * Return latest execution of given pipeline.
	 * Ignore null values.
	 * @param pipeline
	 * @return
	 */
        public PipelineExecution getLastExec(Pipeline pipeline) {
            @SuppressWarnings("unchecked")
		List<PipelineExecution> resultList = Collections.checkedList(
				em.createQuery(
				"SELECT e FROM PipelineExecution e" +
				" WHERE e.pipeline = :pipe" +
				" AND e.start IS NOT NULL" +
				" ORDER BY e.start DESC")
				.setParameter("pipe", pipeline)
				.getResultList(),
				PipelineExecution.class
		);
		if (resultList.isEmpty()) {
			return null;
		} else {
			return resultList.get(0);
		}
        }
	/**
	 * Return latest execution of given statuses for given schedule.
	 * Ignore null values.
	 * @param schedule
	 * @param statuses Set of execution statuses, used to filter pipelines.
	 * @return
	 */
        public PipelineExecution getLastExec(Schedule schedule, Set<PipelineExecutionStatus> statuses) {
            @SuppressWarnings("unchecked")
            List<PipelineExecution> resultList = Collections.checkedList(
                    em.createQuery(
                    "SELECT e FROM PipelineExecution e"
                    + " WHERE e.schedule = :schedule"
                    + " AND e.status IN :status"
                    + " AND e.end IS NOT NULL"
                    + " ORDER BY e.end DESC")
                    .setParameter("schedule", schedule)
                    .setParameter("status", statuses)
                    .getResultList(),
                    PipelineExecution.class);
            if (resultList.isEmpty()) {
                return null;
            } else {
                return resultList.get(0);
            }
        }
        
        public boolean hasModifiedExecutions(Date lastLoad) {
            @SuppressWarnings("unchecked")
            
                    Object r = em.createQuery(
                    "SELECT CASE\n" +
                    "    WHEN MAX(e.lastChange) > :last THEN 1\n" +
                    "    ELSE 0\n" +
                    "END " +
                    "FROM PipelineExecution e")
                    .setParameter("last", lastLoad)
                    .getSingleResult();

			return r.equals("1");
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
		if (!em.contains(exec) && exec.getId() != null) {
			exec = getExecution(exec.getId());
		}
		em.remove(exec);
	}

}
