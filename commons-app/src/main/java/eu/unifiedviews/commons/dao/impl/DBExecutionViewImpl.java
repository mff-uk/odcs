package eu.unifiedviews.commons.dao.impl;

import org.springframework.stereotype.Component;
import cz.cuni.mff.xrg.odcs.commons.app.dao.db.DbAccessBase;
import eu.unifiedviews.commons.dao.DBExecutionView;
import eu.unifiedviews.commons.dao.view.ExecutionView;

/**
 * Implementation of {@link DBPipelineView}.
 *
 * @author Škoda Petr
 */
@Component
class DBExecutionViewImpl extends DbAccessBase<ExecutionView> implements DBExecutionView {

    public DBExecutionViewImpl() {
        super(ExecutionView.class);
    }

}
