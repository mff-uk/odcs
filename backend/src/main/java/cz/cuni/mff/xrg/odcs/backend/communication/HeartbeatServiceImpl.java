package cz.cuni.mff.xrg.odcs.backend.communication;

import cz.cuni.mff.xrg.odcs.commons.app.communication.HeartbeatService;

public class HeartbeatServiceImpl implements HeartbeatService {

    @Override
    public boolean isAlive() {
        return true;
    }

}
