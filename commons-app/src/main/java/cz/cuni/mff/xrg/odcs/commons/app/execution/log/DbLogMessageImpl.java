package cz.cuni.mff.xrg.odcs.commons.app.execution.log;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;


public class DbLogMessageImpl extends DbAccessBase<LogMessage> implements DbLogMessage {
    
    protected DbLogMessageImpl() {
        super(LogMessage.class);
    }
    
}
