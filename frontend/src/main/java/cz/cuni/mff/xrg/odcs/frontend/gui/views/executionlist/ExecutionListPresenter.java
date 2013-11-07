package cz.cuni.mff.xrg.odcs.frontend.gui.views.executionlist;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.frontend.container.ReadOnlyContainer;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Presenter;

/**
 * Interface for presenter that take care about presenting information
 * about executions.
 * 
 * @author Petyr
 */
public interface ExecutionListPresenter extends Presenter {

    /**
     * Refresh data from data sources.
     */
    public void refresh();

    /**
     * Stop given execution.
     *
     * @param executionId
     */
    public void stop(long executionId);

    /**
     * Show log for given execution.
     *
     * @param executionId
     */
    public void showLog(long executionId);

    /**
     * Show debug data for given execution.
     *
     * @param executionId
     */
    public void showDebug(long executionId);

    /**
     * Re-run given execution.
     *
     * @param executionId
     */
    public void run(long executionId);

    /**
     * Re-run given execution in debug mode.
     *
     * @param executionId
     */
    public void debug(long executionId);

    /**
     * View that can be used with the presenter.
     */
    public interface ExecutionListView {

        /**
         * Generate view, that interact with given presenter.
         *
         * @param presenter
         * @return
         */
        public Object enter(final ExecutionListPresenter presenter);

        /**
         * Set data for view.
         *
         * @param dataObject
         */
        public void setDisplay(ExecutionListData dataObject);

        /**
         * Show detail for given execution.
         *
         * @param execution
         */
        public void showExecutionDetail(PipelineExecution execution);

    }

    /**
     * Data object for handling informations between view and presenter.
     */
    public class ExecutionListData {
    
        public final ReadOnlyContainer<PipelineExecution> container;
        
        public ExecutionListData(ReadOnlyContainer<PipelineExecution> container) {
            this.container = container;
        }
        
    }
    
}
