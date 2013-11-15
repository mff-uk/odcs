package cz.cuni.mff.xrg.odcs.commons.app.execution.message;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;

/**
 * Implementation of {@link DbMessageRecord} can not be used
 * to create the {@LInk MessageRecord}.
 * 
 * @author 
 */
class DbMessageRecordImpl extends DbAccessBase<MessageRecord> implements DbMessageRecord {

    protected DbMessageRecordImpl() {
        super(MessageRecord.class);
    }
    
}
