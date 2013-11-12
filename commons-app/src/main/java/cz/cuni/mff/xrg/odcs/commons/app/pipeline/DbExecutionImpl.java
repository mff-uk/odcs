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
	public PipelineExecution create(Pipeline pipeline) {
        PipelineExecution newExecution = new PipelineExecution(pipeline);
        if (authCtx != null) {
            newExecution.setOwner(authCtx.getUser());
        }
        return newExecution;
	}
	
	/**
	 * Unsupported, as we need information about pipeline for constructing valid
	 * execution.
	 * Use {@link #create(cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline)
	 * instead.
	 * 
	 * @return 
	 * @see #create(cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline) 
	 */
    @Override
    public PipelineExecution create() {
		throw new UnsupportedOperationException("PipelineExecution must be built through a factory with pipeline argument.");
    }

    @Override
    public PipelineExecution copy(PipelineExecution object) {
        throw new UnsupportedOperationException();
    }

}
