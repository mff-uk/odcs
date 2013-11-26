package cz.cuni.mff.xrg.odcs.frontend.gui.views.executionlist;

import com.github.wolfie.refresher.Refresher;
import com.vaadin.ui.UI;
import cz.cuni.mff.xrg.odcs.commons.app.execution.log.DbLogMessage;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.DbMessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.DbExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineFacade;
import cz.cuni.mff.xrg.odcs.frontend.AppEntry;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.IntlibHelper;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.RefreshManager;
import cz.cuni.mff.xrg.odcs.frontend.container.ReadOnlyContainer;
import cz.cuni.mff.xrg.odcs.frontend.container.accessor.ExecutionAccessor;
import cz.cuni.mff.xrg.odcs.frontend.container.accessor.MessageRecordAccessor;
import cz.cuni.mff.xrg.odcs.frontend.doa.container.CachedSource;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.DebuggingView;
import cz.cuni.mff.xrg.odcs.frontend.navigation.Address;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private CachedSource<PipelineExecution> cachedSource;
	private RefreshManager refreshManager;
	private Date lastLoad = new Date(0L);
	private static final Logger LOG = LoggerFactory.getLogger(ExecutionListPresenterImpl.class);

	@Override
	public Object enter() {
		// prepare data object
		cachedSource = new CachedSource<>(dbExecution, new ExecutionAccessor());
		ReadOnlyContainer c = new ReadOnlyContainer<>(cachedSource);
		c.sort(new Object[]{"id"}, new boolean[]{false});
		dataObject = new ExecutionListData(c);
		// prepare view
		Object viewObject = view.enter(this);
		refreshManager = ((AppEntry) UI.getCurrent()).getRefreshManager();
		refreshManager.addListener(RefreshManager.EXECUTION_MONITOR, new Refresher.RefreshListener() {
			@Override
			public void refresh(Refresher source) {
				refreshEventHandler();
				LOG.debug("ExecutionMonitor refreshed.");
			}
		});

		// set data object
		view.setDisplay(dataObject);

		// return main component
		return viewObject;
	}

	@Override
	public void setParameters(Object configuration) {
		if (configuration != null && configuration.getClass() == String.class) {
			String strExecId = (String) configuration;
			try {
				Long execId = Long.parseLong(strExecId);
				view.setSelectedRow(execId);
				showDebugEventHandler(execId);
			} catch (NumberFormatException e) {
				//LOG.warn("Invalid parameter for execution monitor.", e);
			}
		}
	}		
	
	@Override
	public void refreshEventHandler() {
		boolean hasModifiedExecutions = pipelineFacade.hasModifiedExecutions(lastLoad);
		if (hasModifiedExecutions) {
			lastLoad = new Date();
			cachedSource.invalidate();
			dataObject.getContainer().refresh();
		}
		view.refresh(hasModifiedExecutions);
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
		view.showExecutionDetail(getLightExecution(executionId), new ExecutionDetailData(getMessageDataSource()));
	}

	@Override
	public void runEventHandler(long executionId) {
		IntlibHelper.runPipeline(getLightExecution(executionId).getPipeline(), false);
		//pipelineFacade.run(getLightExecution(executionId).getPipeline(), false);
		refreshEventHandler();
	}

	@Override
	public void debugEventHandler(long executionId) {
		PipelineExecution exec = IntlibHelper.runPipeline(getLightExecution(executionId).getPipeline(), true);
		//pipelineFacade.run(getLightExecution(executionId).getPipeline(), true);
		if (exec != null) {
			refreshEventHandler();
			view.setSelectedRow(exec.getId());
			view.showExecutionDetail(exec, new ExecutionDetailData(getMessageDataSource()));
		}
	}

	/**
	 * Get light copy of execution.
	 *
	 * @param executionId
	 * @return
	 */
	private PipelineExecution getLightExecution(long executionId) {
		return pipelineFacade.getExecution(executionId);
	}

	private ReadOnlyContainer<MessageRecord> getMessageDataSource() {
		return new ReadOnlyContainer<>(
				new CachedSource<>(dbMessageRecord, new MessageRecordAccessor()));
	}

	@Override
	public void stopRefreshEventHandler() {
		refreshManager.removeListener(RefreshManager.DEBUGGINGVIEW);
	}

	@Override
	public void startDebugRefreshEventHandler(DebuggingView debugView, PipelineExecution execution) {
		refreshManager.addListener(RefreshManager.DEBUGGINGVIEW,
				RefreshManager.getDebugRefresher(debugView, execution));
	}
}
