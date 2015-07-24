package cz.cuni.mff.xrg.odcs.commons.app.execution.server;

import java.util.List;

import javax.persistence.TypedQuery;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;

public class DbExecutionServerImpl extends DbAccessBase<ExecutionServer> implements DbExecutionServer {

    public DbExecutionServerImpl() {
        super(ExecutionServer.class);
    }

    @Override
    public ExecutionServer getExecutionServerSingleActive() {
        return getInstance(1L);
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

}
