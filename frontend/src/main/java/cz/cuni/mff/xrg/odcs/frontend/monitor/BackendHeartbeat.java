package cz.cuni.mff.xrg.odcs.frontend.monitor;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.RemoteAccessException;

import cz.cuni.mff.xrg.odcs.commons.app.communication.HeartbeatService;

/**
 * Periodically checks Backend status. As singleton component should prevent
 * multiple queries for backend status.
 * 
 * @author Škoda Petr
 */
public class BackendHeartbeat {

    private static final Logger log = LoggerFactory.getLogger(BackendHeartbeat.class);

    @Autowired
    private HeartbeatService heartbeatService;

    /**
     * True if backend is alive.
     */
    private Boolean alive = false;

    /**
     * Time of last check.
     */
    private long lastCheckTime = 0l;

    private void check() {
        final long now = (new Date()).getTime();
        if (now - lastCheckTime < 5000) {
            return;
        }
        lastCheckTime = now;
        try {
            alive = heartbeatService.isAlive();
        } catch (RemoteAccessException ex) {

            alive = false;

            log.warn("Problem when checking whether backend is online: {}", ex.getLocalizedMessage());
            log.info("Possible reason: Backend is offline");
        }
    }

    public Boolean checkIsAlive() {
        check();
        return alive;
    }

}
