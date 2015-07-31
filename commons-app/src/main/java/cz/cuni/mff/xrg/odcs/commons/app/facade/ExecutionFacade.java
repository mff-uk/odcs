package cz.cuni.mff.xrg.odcs.commons.app.facade;

import java.util.List;

import cz.cuni.mff.xrg.odcs.commons.app.execution.server.ExecutionServer;

public interface ExecutionFacade extends Facade {

    /**
     * @return
     */
    List<ExecutionServer> getAllExecutionServers();

    /**
     * Get execution server for given backend_id
     * 
     * @param backendId
     * @return
     */
    ExecutionServer getExecutionServer(String backendId);

    /**
     * @return
     */
    boolean checkAnyBackendActive();

    /**
     * @param backendId
     */
    void updateBackendTimestamp(String backendId);

}
