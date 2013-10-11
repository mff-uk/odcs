package cz.cuni.mff.xrg.odcs.backend.facade;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;

import java.util.Date;
import java.util.List;
import javax.persistence.PersistenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Facade for managing pipelines, which tolerates database crashes. This facade
 * is specially altered for servicing backend, where we do not want to trash
 * all progress of unfinished pipeline runs just because of a short database
 * outage.
 *
 * @author Jan Vojt
 */
public class PipelineFacade extends cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineFacade {
	
	private static final Logger LOG = LoggerFactory.getLogger(PipelineFacade.class);
	
	/**
	 * Handler taking care of DB outages.
	 */
	@Autowired
	private ErrorHandler handler;

	@Override
	public Pipeline getPipeline(long id) {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getPipeline(id);
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	@Transactional
	public void save(Pipeline pipeline) {
		int attempts = 0;
		while (true) try {
			attempts++;
			super.save(pipeline);
			return;
		} catch (IllegalArgumentException ex) {
			// given pipeline is a removed entity
			throw ex;
		} catch (RuntimeException ex) {
			// presume DB error
			handler.handle(attempts, ex);
		}
	}

	@Override
	public PipelineExecution getExecution(long id) {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getExecution(id);
		} catch (RuntimeException ex) {
			handler.handle(attempts, ex);
		}
	}

	@Override
	public List<PipelineExecution> getExecutions(Pipeline pipeline) {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getExecutions(pipeline);
		} catch (RuntimeException ex) {
			handler.handle(attempts, ex);
		}
	}

	@Override
	@Transactional
	public void save(PipelineExecution exec) {
		int attempts = 0;
		while (true) try {
			attempts++;
			super.save(exec);
			return;
		} catch (IllegalArgumentException ex) {
			// given execution is a removed entity
			throw ex;
		} catch (RuntimeException ex) {
			handler.handle(attempts, ex);
		}
	}

	@Override
	@Transactional
	public void delete(PipelineExecution exec) {
		int attempts = 0;
		while (true) try {
			attempts++;
			super.delete(exec);
			return;
		} catch (IllegalArgumentException ex) {
			// given execution is not persisted
			throw ex;
		} catch (PersistenceException ex) {
			handler.handle(attempts, ex);
		}
	}
	
	@Override
	public List<PipelineExecution> getAllExecutions() {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getAllExecutions();
		} catch (IllegalArgumentException ex) {
			// invalid SQL query was called
			throw ex;
		} catch (PersistenceException ex) {
			handler.handle(attempts, ex);
		}
	}

	@Override
	public Date getLastExecTime(Pipeline pipeline, PipelineExecutionStatus status) {
		int attempts = 0;
		while (true) try {
			attempts++;
			return super.getLastExecTime(pipeline, status);
		} catch (IllegalArgumentException ex) {
			// invalid SQL query was called
			throw ex;
		} catch (RuntimeException ex) {
			handler.handle(attempts, ex);
		}
	}
}
