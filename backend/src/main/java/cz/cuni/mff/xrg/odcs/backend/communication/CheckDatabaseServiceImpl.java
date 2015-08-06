package cz.cuni.mff.xrg.odcs.backend.communication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import cz.cuni.mff.xrg.odcs.backend.execution.event.CheckDatabaseEvent;
import cz.cuni.mff.xrg.odcs.commons.app.communication.CheckDatabaseService;

public class CheckDatabaseServiceImpl implements CheckDatabaseService {
    /**
     * Event publisher used to publicize events.
     */
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void checkDatabase() {
        eventPublisher.publishEvent(new CheckDatabaseEvent(this));
    }
}
