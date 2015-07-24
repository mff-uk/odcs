package cz.cuni.mff.xrg.odcs.commons.app.execution.server;

import java.util.List;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccess;

public interface DbExecutionServer extends DbAccess<ExecutionServer> {

    /**
     * @return
     */
    ExecutionServer getExecutionServerSingleActive();

    ExecutionServer getExecutionServer(String backendId);

    List<ExecutionServer> getAllExecutionServers();

}
