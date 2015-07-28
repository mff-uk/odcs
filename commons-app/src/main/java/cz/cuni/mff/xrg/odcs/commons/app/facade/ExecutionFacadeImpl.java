package cz.cuni.mff.xrg.odcs.commons.app.facade;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import cz.cuni.mff.xrg.odcs.commons.app.conf.AppConfig;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.conf.MissingConfigPropertyException;
import cz.cuni.mff.xrg.odcs.commons.app.execution.server.DbExecutionServer;
import cz.cuni.mff.xrg.odcs.commons.app.execution.server.ExecutionServer;

public class ExecutionFacadeImpl implements ExecutionFacade {

    private static Logger LOG = LoggerFactory.getLogger(ExecutionFacadeImpl.class);

    @Autowired
    private DbExecutionServer dbExecutionServer;

    @Autowired
    private AppConfig appConfig;

    private int backendTakoverLimit;

    private static final int BACKEND_TAKOVER_DEFAULT_LIMIT = 60;

    @Override
    public ExecutionServer getExecutionServerSingleActive() {
        return this.dbExecutionServer.getExecutionServerSingleActive();
    }

    @PostConstruct
    public void init() {
        try {
            this.backendTakoverLimit = this.appConfig.getInteger(ConfigProperty.BACKEND_TAKEOVER_TIME_LIMIT);
        } catch (MissingConfigPropertyException e) {
            this.backendTakoverLimit = BACKEND_TAKOVER_DEFAULT_LIMIT;
        }
    }

    @Transactional
    @Override
    public boolean obtainLockAndUpdateTimestamp(String backendId) {
        boolean hasLock = false;
        ExecutionServer server = getExecutionServerSingleActive();
        if (server != null) {
            LOG.debug("Backend server entry already in database, going to check lock");
            if (!backendId.equals(server.getBackendId())) { // this server does not own the lock
                LOG.debug("This backend ({}) does not own the lock, going to check timestamp", backendId);
                Long limitDateTime = System.currentTimeMillis() - (this.backendTakoverLimit * 1000);
                Date limitDate = new Date(limitDateTime);
                if (server.getLastUpdate().before(limitDate)) { // owning server has not notified itself longer than a limit, taking over the lock
                    LOG.info("Lock owning backend ({}) has not notified itself longer than a limit, taking over the lock", server.getBackendId());
                    server.setBackendId(backendId);
                    server.setLastUpdate(new Date());
                    hasLock = true;
                }
            } else { // this server already owns the lock, just update timestamp 
                LOG.debug("This backend ({}) already owns the lock, going to update timestamp", backendId);
                server.setLastUpdate(new Date());
                hasLock = true;
            }
        } else { // no backend has run yet
            LOG.info("No backend has ever run with this database, acquiring lock for {}", backendId);
            server = new ExecutionServer();
            server.setBackendId(backendId);
            server.setLastUpdate(new Date());
            server.setId(1L);
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
