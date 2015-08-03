package cz.cuni.mff.xrg.odcs.commons.app.execution.server;

import java.util.List;

import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import cz.cuni.mff.xrg.odcs.commons.app.ScheduledJobsPriority;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;

public class DbExecutionServerImpl extends DbAccessBase<ExecutionServer>implements DbExecutionServer {

    private static final Logger LOG = LoggerFactory.getLogger(DbExecutionServerImpl.class);

    public DbExecutionServerImpl() {
        super(ExecutionServer.class);
    }

    @Override
    public ExecutionServer getExecutionServer(String backendId) {
        final String stringQuery = "SELECT e FROM ExecutionServer e WHERE e.backendId = :backendId";
        TypedQuery<ExecutionServer> query = createTypedQuery(stringQuery);
        query.setParameter("backendId", backendId);
        return execute(query);
    }

    @Override
    public List<ExecutionServer> getAllExecutionServers() {
        final String queryStr = "SELECT e FROM ExecutionServer e";
        return executeList(queryStr);
    }

    @Override
    @Transactional
    public int allocateQueuedExecutionsForBackendByPriority(String backendID, int limit) {
        final String queryStr = "UPDATE exec_pipeline SET backend_id = '%s'"
                + " WHERE id IN ((SELECT e.id from exec_pipeline e WHERE e.backend_id IS NULL AND e.status = %d"
                + " AND e.order_number > %d"
                + " ORDER BY e.order_number ASC, e.id ASC LIMIT %d FOR UPDATE) UNION"
                + " (SELECT e.id from exec_pipeline e WHERE e.backend_id IS NULL AND e.status = %d"
                + " AND e.order_number = %d FOR UPDATE))";
        String query = String.format(queryStr,
                backendID,
                0, // = QUEUED
                ScheduledJobsPriority.IGNORE.getValue(),
                limit,
                0, // = QUEUED
                ScheduledJobsPriority.IGNORE.getValue());
        LOG.debug(">>> allocate query: {}", query);
        return this.em.createNativeQuery(query).executeUpdate();
    }

}
