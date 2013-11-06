package cz.cuni.mff.xrg.odcs.frontend.gui.views.executionmonitor;

import com.vaadin.ui.CustomComponent;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.frontend.container.ReadOnlyContainer;

/**
 *
 * @author Petyr
 */
public interface ExecutionView {
    
    /**
     * Generate page, that interact with given presenter.
     * @param presenter 
     * @return  
     */
    public CustomComponent enter(final ExecutionPresenter presenter);
    
    /**
     * Set data for view.
     * @param executions 
     */
    public void setDisplay(ReadOnlyContainer<PipelineExecution> executions);
    
    /**
     * Show detail for given execution.
     * @param execution 
     */
    public void showExecutionDetail(PipelineExecution execution);
    
    /**
     * Execution for respective presenter.
     */
    public interface ExecutionPresenter {
        
        /**
         * 
         * @return 
         */
        public CustomComponent enter();
        
        /**
         * Return true if there are some unsaved modifications.
         * @return 
         */
        public boolean isModified();
        
        /**
         * Save the unsaved changes.
         */
        public void save();
  
        /**
         * Refresh data from data sources.
         */
        public void refresh();
        
        /**
         * Stop given execution. 
         * @param executionId
         */
        public void stop(long executionId);
        
        /**
         * Show log for given execution.
         * @param executionId 
         */
        public void showLog(long executionId);
        
        /**
         * Show debug data for given execution.
         * @param executionId 
         */
        public void showDebug(long executionId);
        
        /**
         * Re-run given execution.
         * @param executionId 
         */
        public void run(long executionId);
        
        /**
         * Re-run given execution in debug mode.
         * @param executionId 
         */
        public void debug(long executionId);
    }
    
}
