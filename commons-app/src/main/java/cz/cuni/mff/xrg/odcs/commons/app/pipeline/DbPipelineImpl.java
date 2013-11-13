package cz.cuni.mff.xrg.odcs.commons.app.pipeline;

import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;

/**
 * Implementation for accessing {@link Pipeline} data objects.
 *
 * @author Petyr
 */
class DbPipelineImpl extends DbAccessBase<Pipeline> implements DbPipeline {

    protected DbPipelineImpl() {
        super(Pipeline.class);
    }

    @Override
    public Pipeline copy(Pipeline object) {
        return new Pipeline(object);
    }

}
