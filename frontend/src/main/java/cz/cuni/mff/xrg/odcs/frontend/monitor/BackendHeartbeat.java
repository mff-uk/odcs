package cz.cuni.mff.xrg.odcs.frontend.monitor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.scheduling.annotation.Scheduled;

import cz.cuni.mff.xrg.odcs.commons.app.communication.HeartbeatService;

/**
 * Periodically checks Backend status. As singleton component should prevent
 * multiple queries for backend status.
 * 
 * @author Škoda Petr
 */
public class BackendHeartbeat {

    @Autowired
    private HeartbeatService heartbeatService;

    /**
     * True if backend is alive.
     */
    private boolean alive = false;

    @Scheduled(fixedDelay = 6 * 1000)
    private void check() {
        try {
            alive = heartbeatService.isAlive();
        } catch (RemoteAccessException ex) {
            alive = false;
        }
    }

    public boolean checkIsAlive() {
        return alive;
    }

}
