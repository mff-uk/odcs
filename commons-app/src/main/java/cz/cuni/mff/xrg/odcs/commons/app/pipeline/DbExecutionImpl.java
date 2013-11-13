package cz.cuni.mff.xrg.odcs.commons.app.pipeline;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;

/**
 * Implementation of {@link DbExecution}
 *
 * @author Petyr
 */
class DbExecutionImpl extends DbAccessBase<PipelineExecution> implements DbExecution {

    protected DbExecutionImpl() {
        super(PipelineExecution.class);
    }

}
