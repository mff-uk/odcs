package cz.cuni.mff.xrg.odcs.commons.app.scheduling;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.TypedQuery;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;

/**
 * Implementation providing access to {@link Schedule} data objects.
 * 
 * @author Jan Vojt
 * @author Petyr
 */
@Transactional(propagation = Propagation.MANDATORY)
public class DbScheduleImpl extends DbAccessBase<Schedule>
        implements DbSchedule {

    public DbScheduleImpl() {
        super(Schedule.class);
    }

    @Override
    public List<Schedule> getAllSchedules() {
        final String queryStr = "SELECT e FROM Schedule e";
        return executeList(queryStr);
    }

    @Override
    public List<Schedule> getSchedulesFor(Pipeline pipeline) {
        final String stringQuery = "SELECT e FROM Schedule e WHERE e.pipeline = :pipeline";
        TypedQuery<Schedule> query = createTypedQuery(stringQuery);
        query.setParameter("pipeline", pipeline);
        return executeList(query);
    }

    @Override
    public List<Schedule> getFollowers(Pipeline pipeline, boolean enabled) {
        final String stringQuery = "SELECT s FROM Schedule s JOIN s.afterPipelines p"
                + " WHERE p = :pipeline"
                + " AND s.type = :type"
                + " AND s.enabled = :enabled";
        TypedQuery<Schedule> query = createTypedQuery(stringQuery);
        query.setParameter("pipeline", pipeline);
        query.setParameter("type", ScheduleType.AFTER_PIPELINE);
        query.setParameter("enabled", enabled);
        return executeList(query);
    }

    @Override
    public List<Schedule> getAllTimeBased() {
        final String stringQuery = "SELECT s FROM Schedule s"
                + " WHERE s.type = :type order by s.id Asc";
        TypedQuery<Schedule> query = createTypedQuery(stringQuery);
        query.setParameter("type", ScheduleType.PERIODICALLY);
        return executeList(query);
    }

    @Override
    public List<Schedule> getActiveRunAfterBased() {
        final String stringQuery = "SELECT s FROM Schedule s"
                + " WHERE s.type = :type"
                + " AND s.enabled = 1";
        TypedQuery<Schedule> query = createTypedQuery(stringQuery);
        query.setParameter("type", ScheduleType.AFTER_PIPELINE);
        return executeList(query);
    }

    @Override
    public List<Date> getLastExecForRunAfter(Schedule schedule) {
        final String stringQuery = "SELECT max(exec.end)"
                + " FROM Schedule schedule"
                + " JOIN schedule.afterPipelines pipeline"
                + " JOIN PipelineExecution exec ON exec.pipeline = pipeline"
                + " WHERE schedule.id = :schedule AND exec.status IN :status"
                + " GROUP BY pipeline.id";

        Set<PipelineExecutionStatus> statuses = new HashSet<>();
        statuses.add(PipelineExecutionStatus.FINISHED_SUCCESS);
        statuses.add(PipelineExecutionStatus.FINISHED_WARNING);

        TypedQuery<Date> query = em.createQuery(stringQuery, Date.class);
        query.setParameter("schedule", schedule.getId());
        query.setParameter("status", statuses);

        return Collections.checkedList(query.getResultList(), Date.class);
    }

}
