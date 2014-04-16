package cz.cuni.mff.xrg.odcs.frontend.monitor;

import cz.cuni.mff.xrg.odcs.commons.app.communication.HeartbeatService;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.remoting.RemoteAccessException;
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
			// backend is offline
			alive = false;
		}
	}

	public Boolean checkIsAlive() {
		check();
		return alive;
	}

}
