package cz.cuni.mff.xrg.odcs.commons.app.facade;

import cz.cuni.mff.xrg.odcs.commons.app.execution.server.ExecutionServer;

public interface ExecutionFacade extends Facade {

    /**
     * @return
     */
    ExecutionServer getExecutionServerSingleActiveForLock();

    /**
     * @return
     */
    ExecutionServer getExecutionServerSingleActive();

    /**
     * Try to obtain lock in database - this means entry with backend server is updated to new backend_id and new IP address
     * If server which already has lock is trying to acquire it, only timestamp is updated.
     * Other server can obtain lock only if timestamp is older than some configured time
     * 
     * @param backendId
     * @param ipAddress
     * @return
     */
    boolean obtainLockAndUpdateTimestamp(String backendId);

    /**
     * Get execution server for given backend_id
     * 
     * @param backendId
     * @return
     */
    ExecutionServer getExecutionServer(String backendId);

    boolean checkAnyBackendActive();

}
