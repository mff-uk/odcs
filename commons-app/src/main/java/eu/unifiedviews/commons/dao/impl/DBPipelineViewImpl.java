package eu.unifiedviews.commons.dao.impl;

import org.springframework.stereotype.Component;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;
import eu.unifiedviews.commons.dao.DBPipelineView;
import eu.unifiedviews.commons.dao.view.PipelineView;

/**
 * Implementation of {@link DBPipelineView}.
 *
 * @author Škoda Petr
 */
@Component
class DBPipelineViewImpl extends DbAccessBase<PipelineView> implements DBPipelineView {

    public DBPipelineViewImpl() {
        super(PipelineView.class);
    }

}
