package cz.cuni.mff.xrg.odcs.commons.app.facade;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.execution.server.DbExecutionServer;
import cz.cuni.mff.xrg.odcs.commons.app.execution.server.ExecutionServer;

public class ExecutionFacadeImpl implements ExecutionFacade {

    @Autowired
    private DbExecutionServer dbExecutionServer;

    @Autowired
    private AppConfig appConfig;

    @Override
    public ExecutionServer getExecutionServerSingleActive() {
        return this.dbExecutionServer.getExecutionServerSingleActive();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Override
    public boolean obtainLockAndUpdateTimestamp(String backendId) {
        boolean hasLock = false;
        ExecutionServer server = getExecutionServerSingleActive();
        if (!backendId.equals(server.getBackendId())) { // asking server does not own the lock
            Long limitDateTime = System.currentTimeMillis() - (this.appConfig.getInteger(ConfigProperty.BACKEND_TAKEOVER_TIME_LIMIT) * 1000);
            Date limitDate = new Date(limitDateTime);
            if (server.getLastUpdate().before(limitDate)) { // owning server has not notified itself longer than a limit, taking over the lock
                server.setBackendId(backendId);
                server.setLastUpdate(new Date(System.currentTimeMillis()));
                hasLock = true;
            }
        } else { // asking server owns the lock, just update timestamp 
            server.setLastUpdate(new Date(System.currentTimeMillis()));
            hasLock = true;
        }
        this.dbExecutionServer.save(server);

        return hasLock;
    }

    @Override
    public ExecutionServer getExecutionServer(String backendId) {
        return this.dbExecutionServer.getExecutionServer(backendId);
    }

}
