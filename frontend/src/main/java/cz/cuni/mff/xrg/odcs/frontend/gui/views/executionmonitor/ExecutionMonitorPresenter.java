package cz.cuni.mff.xrg.odcs.frontend.gui.views.executionmonitor;

import com.vaadin.ui.CustomComponent;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.DbExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineFacade;
import cz.cuni.mff.xrg.odcs.frontend.container.ReadOnlyContainer;
import cz.cuni.mff.xrg.odcs.frontend.container.accessor.ExecutionAccessor;
import cz.cuni.mff.xrg.odcs.frontend.container.exp.ContainerAuthorizator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Presenter for {@link ExecutionView}.
 *
 * @author Petyr
 */
@Component
@Scope("prototype")
public class ExecutionMonitorPresenter implements ExecutionView.ExecutionPresenter {

    @Autowired
    private DbExecution dbExecution;

    @Autowired
    private PipelineFacade pipelineFacade;
    
    @Autowired
    private ExecutionView view;
    
     @Autowired
    private ContainerAuthorizator containerAuth;
    
    private ReadOnlyContainer<PipelineExecution> pipelineContainer;
    
    @Override
    public CustomComponent enter() {
        // create main container
        final ExecutionAccessor acessor = new ExecutionAccessor();
        pipelineContainer = new ReadOnlyContainer<>(dbExecution, acessor);
        containerAuth.authorize(pipelineContainer, acessor.getEntityClass());
        // prepare view
        CustomComponent viewComponent = view.enter(this);
        view.setDisplay(pipelineContainer);
        // return main component
        return viewComponent;
    }
    
    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void save() {
        // nothing to save here
    }
    
    @Override
    public void refresh() {
        // TODO check for database change
        //pipelineContainer.refresh();
    }

    @Override
    public void stop(long executionId) {
        pipelineFacade.stopExecution(getLightExecution(executionId));
        refresh();
    }

    @Override
    public void showLog(long executionId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void showDebug(long executionId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void run(long executionId) {
        pipelineFacade.run(getLightExecution(executionId).getPipeline(), false);
        refresh();
    }

    @Override
    public void debug(long executionId) {
        pipelineFacade.run(getLightExecution(executionId).getPipeline(), true);
        refresh();
    }

    /**
     * Get light copy of execution.
     * @param executionId
     * @return 
     */
    private PipelineExecution getLightExecution(long executionId) {
        return pipelineFacade.getExecution(executionId);
    }
    
}
