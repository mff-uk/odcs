package cz.cuni.mff.xrg.odcs.commons.app.pipeline;

import org.springframework.beans.factory.annotation.Autowired;

import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthenticationContext;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;

/**
 * Implementation of {@link DbPipeline}
 *
 * @author Petyr
 */
class DbPipelineImpl extends DbAccessBase<Pipeline> implements DbPipeline {

    /**
     * Authentication context.
     */
    @Autowired(required = false)
    protected AuthenticationContext authCtx;

    protected DbPipelineImpl() {
        super(Pipeline.class);
    }

    @Override
    public Pipeline create() {
        Pipeline newPipeline = new Pipeline();
        if (authCtx != null) {
            newPipeline.setUser(authCtx.getUser());
        }
        return newPipeline;
    }

    @Override
    public Pipeline copy(Pipeline object) {
        Pipeline newPipeline = new Pipeline(object);
        if (authCtx != null) {
            newPipeline.setUser(authCtx.getUser());
        }
        return newPipeline;
    }

}
