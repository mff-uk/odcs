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

	@Override
	public PipelineExecution create(Pipeline pipeline) {
        return new PipelineExecution(pipeline);
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
