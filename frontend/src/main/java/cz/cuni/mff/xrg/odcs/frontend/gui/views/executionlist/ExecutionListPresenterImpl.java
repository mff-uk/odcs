package cz.cuni.mff.xrg.odcs.frontend.gui.views.executionlist;

import cz.cuni.mff.xrg.odcs.commons.app.execution.log.DbLogMessage;
import cz.cuni.mff.xrg.odcs.commons.app.execution.log.LogMessage;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.DbMessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.DbExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineFacade;
import cz.cuni.mff.xrg.odcs.frontend.container.ReadOnlyContainer;
import cz.cuni.mff.xrg.odcs.frontend.container.accessor.ExecutionAccessor;
import cz.cuni.mff.xrg.odcs.frontend.container.accessor.LogAccessor;
import cz.cuni.mff.xrg.odcs.frontend.container.accessor.MessageRecordAccessor;
import cz.cuni.mff.xrg.odcs.frontend.navigation.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Presenter for {@link ExecutionListPresenter}.
 *
 * @author Petyr
 */
@Component
@Scope("prototype")
@Address(url = "ExecutionList")
public class ExecutionListPresenterImpl implements ExecutionListPresenter {

    @Autowired
    private DbExecution dbExecution;
	
	@Autowired
	private DbLogMessage dbLogMessage;
	
	@Autowired
	private DbMessageRecord dbMessageRecord;

    @Autowired
    private PipelineFacade pipelineFacade;
    
    @Autowired
    private ExecutionListView view;
        
    private ExecutionListData dataObject;
    
    @Override
    public Object enter(Object configuration) {
        // prepare data object
        dataObject = new ExecutionListData(new ReadOnlyContainer<>(dbExecution, 
            new ExecutionAccessor()));
        // prepare view
        Object viewObject = view.enter(this);
        // set data object
        view.setDisplay(dataObject);
        // return main component
        return viewObject;
    }
        
    @Override
    public void refreshEventHandler() {
        // TODO check for database change
        dataObject.getContainer().refresh();
    }

    @Override
    public void stopEventHandler(long executionId) {
        pipelineFacade.stopExecution(getLightExecution(executionId));
        refreshEventHandler();
    }

    @Override
    public void showLogEventHandler(long executionId) {
        //TODO: Show log for selected DPU?
    }

    @Override
    public void showDebugEventHandler(long executionId) {
        view.showExecutionDetail(getLightExecution(executionId), new ExecutionDetailData(getLogDataSource(), getMessageDataSource()));
    }

    @Override
    public void runEventHandler(long executionId) {
        pipelineFacade.run(getLightExecution(executionId).getPipeline(), false);
        refreshEventHandler();
    }

    @Override
    public void debugEventHandler(long executionId) {
        pipelineFacade.run(getLightExecution(executionId).getPipeline(), true);
        refreshEventHandler();
    }

    /**
     * Get light copy of execution.
     * @param executionId
     * @return 
     */
    private PipelineExecution getLightExecution(long executionId) {
        return pipelineFacade.getExecution(executionId);
    }
	
	private ReadOnlyContainer<LogMessage> getLogDataSource() {
		return new ReadOnlyContainer<>(dbLogMessage, new LogAccessor());
	}
	
	private ReadOnlyContainer<MessageRecord> getMessageDataSource() {
		return new ReadOnlyContainer<>(dbMessageRecord, new MessageRecordAccessor());
	}
    
}
