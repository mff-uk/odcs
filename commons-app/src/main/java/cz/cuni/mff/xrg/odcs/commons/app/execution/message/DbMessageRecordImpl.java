package cz.cuni.mff.xrg.odcs.commons.app.execution.message;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;



public class DbMessageRecordImpl extends DbAccessBase<MessageRecord> implements DbMessageRecord {

    protected DbMessageRecordImpl() {
        super(MessageRecord.class);
    }
    
}
