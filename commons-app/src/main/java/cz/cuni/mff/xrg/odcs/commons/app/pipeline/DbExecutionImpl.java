package cz.cuni.mff.xrg.odcs.commons.app.pipeline;

import org.springframework.beans.factory.annotation.Autowired;

import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthenticationContext;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;

/**
 * Implementation of {@link DbExecution}
 *
 * @author Petyr
 */
class DbExecutionImpl extends DbAccessBase<PipelineExecution> implements DbExecution {

    /**
     * Authentication context.
     */
    @Autowired(required = false)
    protected AuthenticationContext authCtx;

    protected DbExecutionImpl() {
        super(PipelineExecution.class);
    }

    @Override
    public PipelineExecution create() {
        PipelineExecution newExecution = new PipelineExecution();
        if (authCtx != null) {
            newExecution.setOwner(authCtx.getUser());
        }
        return newExecution;
    }

    @Override
    public PipelineExecution copy(PipelineExecution object) {
        throw new UnsupportedOperationException();
    }

}
