package cz.cuni.mff.xrg.odcs.commons.app.pipeline;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.JPQLDbQuery;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link DbExecution}
 *
 * @author Petyr
 */
@Transactional(propagation = Propagation.MANDATORY)
class DbExecutionImpl extends DbAccessBase<PipelineExecution> implements DbExecution {

    protected DbExecutionImpl() {
        super(PipelineExecution.class);
    }

	@Override
	public List<PipelineExecution> getAllExecutions(Pipeline pipeline, PipelineExecutionStatus status) {
		
		JPQLDbQuery<PipelineExecution> jpql = new JPQLDbQuery<>();
		String query = "SELECT e FROM PipelineExecution e";
		
		if (pipeline != null && status != null) {
			query += " WHERE e.pipeline = :pipe"
                + " AND e.status = :status";
			jpql.setParameter("pipe", pipeline);
			jpql.setParameter("status", status);
		} else if (pipeline != null) {
			query += " WHERE e.pipeline = :pipe";
			jpql.setParameter("pipe", pipeline);
		} else if (status != null) {
			query += " WHERE e.status = :status";
			jpql.setParameter("status", status);
		}
	
		return executeList(jpql.setQuery(query));
	}

	@Override
	public PipelineExecution getLastExecution(Pipeline pipeline,
			Set<PipelineExecutionStatus> statuses) {

		JPQLDbQuery<PipelineExecution> jpql = new JPQLDbQuery<>(
				"SELECT e FROM PipelineExecution e"
                    + " WHERE e.pipeline = :pipe"
                    + " AND e.status IN :status"
                    + " AND e.end IS NOT NULL"
                    + " ORDER BY e.end DESC");
		
		jpql.setParameter("pipe", pipeline)
			.setParameter("status", statuses);
		
		return execute(jpql);
	}

	@Override
	public PipelineExecution getLastExecution(Schedule schedule,
			Set<PipelineExecutionStatus> statuses) {
		
		JPQLDbQuery<PipelineExecution> jpql = new JPQLDbQuery<>(
				"SELECT e FROM PipelineExecution e"
				+ " WHERE e.schedule = :schedule"
				+ " AND e.status IN :status"
				+ " AND e.end IS NOT NULL"
				+ " ORDER BY e.end DESC");
		
		jpql.setParameter("schedule", schedule)
			.setParameter("status", statuses);

        return execute(jpql);
	}

	@Override
	public boolean hasModifiedExecutions(Date since) {
		
		JPQLDbQuery<PipelineExecution> jpql = new JPQLDbQuery<>(
				"SELECT CASE"
				+ " WHEN MAX(e.lastChange) > :last THEN CAST(1 AS INTEGER)"
				+ " ELSE CAST(0 AS INTEGER)"
				+ " END "
				+ " FROM PipelineExecution e");
		jpql.setParameter("last", since);

		long size = executeSize(jpql);
		
        return size>0;
	}

}
