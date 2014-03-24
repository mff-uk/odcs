package cz.cuni.mff.xrg.odcs.frontend.gui.views.executionlist;

import com.github.wolfie.refresher.Refresher;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthAwarePermissionEvaluator;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.DbMessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.execution.message.MessageRecord;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.DbExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.commons.app.facade.PipelineFacade;
import cz.cuni.mff.xrg.odcs.frontend.AppEntry;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.PipelineHelper;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.RefreshManager;
import cz.cuni.mff.xrg.odcs.frontend.container.ReadOnlyContainer;
import cz.cuni.mff.xrg.odcs.frontend.container.accessor.ExecutionAccessor;
import cz.cuni.mff.xrg.odcs.frontend.container.accessor.MessageRecordAccessor;
import cz.cuni.mff.xrg.odcs.frontend.doa.container.db.DbCachedSource;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.DebuggingView;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Utils;
import cz.cuni.mff.xrg.odcs.frontend.navigation.Address;
import cz.cuni.mff.xrg.odcs.frontend.navigation.ClassNavigator;
import cz.cuni.mff.xrg.odcs.frontend.navigation.ParametersHandler;
import java.util.Date;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tepi.filtertable.datefilter.DateInterval;
import org.tepi.filtertable.numberfilter.NumberInterval;

/**
 * Presenter for {@link ExecutionListPresenter}.
 *
 * @author Petyr
 */
@Component
@Scope("prototype")
@Address(url = "ExecutionList")
public class ExecutionListPresenterImpl implements ExecutionListPresenter {

	private static final Logger LOG = LoggerFactory.getLogger(ExecutionListPresenterImpl.class);
	@Autowired
	private DbExecution dbExecution;
	@Autowired
	private DbMessageRecord dbMessageRecord;
	@Autowired
	private PipelineFacade pipelineFacade;
	@Autowired
	private PipelineHelper pipelineHelper;
	@Autowired
	private ExecutionListView view;
	@Autowired
	private Utils utils;

	@Autowired
	private AuthAwarePermissionEvaluator permissionEvaluator;

	private ExecutionListData dataObject;
	private DbCachedSource<PipelineExecution> cachedSource;
	private RefreshManager refreshManager;
	private Date lastLoad = new Date(0L);
	private ClassNavigator navigator;

	@Override
	public Object enter() {
		navigator = ((AppEntry) UI.getCurrent()).getNavigation();
		// prepare data object
		cachedSource = new DbCachedSource<>(dbExecution, new ExecutionAccessor(),
				utils.getPageLength());
		ReadOnlyContainer c = new ReadOnlyContainer<>(cachedSource);
		c.sort(new Object[]{"id"}, new boolean[]{false});
		dataObject = new ExecutionListData(c);
		// prepare view
		Object viewObject = view.enter(this);
		refreshManager = ((AppEntry) UI.getCurrent()).getRefreshManager();
		refreshManager.addListener(RefreshManager.EXECUTION_MONITOR, new Refresher.RefreshListener() {
			private long lastRefreshFinished = 0;

			@Override
			public void refresh(Refresher source) {
				if (new Date().getTime() - lastRefreshFinished > RefreshManager.MIN_REFRESH_INTERVAL) {
					refreshEventHandler();
					LOG.debug("ExecutionMonitor refreshed.");
					lastRefreshFinished = new Date().getTime();
				}
			}
		});

		// set data object
		view.setDisplay(dataObject);

		// add initial name filter
		view.setFilter("owner.username", utils.getUserName());

		// return main component
		return viewObject;
	}

	@Override
	public void setParameters(Object configuration) {
		if (configuration != null && Map.class.isAssignableFrom(configuration.getClass())) {
			int pageNumber = 0;
			Map<String, String> config = (Map<String, String>) configuration;
			for (Map.Entry<String, String> entry : config.entrySet()) {
				switch (entry.getKey()) {
					case "exec":
						Long execId = Long.parseLong(entry.getValue());
						view.setSelectedRow(execId);
						showDebugEventHandler(execId);
						break;
					case "page":
						pageNumber = Integer.parseInt(entry.getValue());
						break;
					case "id":
						view.setFilter(entry.getKey(), ParametersHandler.getInterval(entry.getValue()));
						break;
					case "status":
						view.setFilter(entry.getKey(), PipelineExecutionStatus.valueOf(entry.getValue()));
						break;
					case "isDebugging":
					case "schedule":
						view.setFilter(entry.getKey(), Boolean.parseBoolean(entry.getValue()));
						break;
					case "start":
						view.setFilter(entry.getKey(), ParametersHandler.getDateInterval(entry.getValue()));
						break;
					default:
						view.setFilter(entry.getKey(), entry.getValue());
						break;
				}
			}
			if (pageNumber != 0) {
				//Page number is set as last, because filtering automatically moves table to first page.
				view.setPage(pageNumber);
			}
		}
//		if (configuration != null && configuration.getClass() == String.class) {
//			String strExecId = (String) configuration;
//			try {
//				Long execId = Long.parseLong(strExecId);
//				view.setSelectedRow(execId);
//				showDebugEventHandler(execId);
//			} catch (NumberFormatException e) {
//				//LOG.warn("Invalid parameter for execution monitor.", e);
//			}
//		}
	}

	@Override
	public void refreshEventHandler() {
		boolean hasModifiedExecutions = pipelineFacade.hasModifiedExecutions(lastLoad);
		view.refresh(hasModifiedExecutions);
		if (hasModifiedExecutions) {
			lastLoad = new Date();
			cachedSource.invalidate();
			dataObject.getContainer().refresh();
		}
	}

	@Override
	public boolean canStopExecution(long executionId) {
		PipelineExecution exec = cachedSource.getObject(executionId);
		return permissionEvaluator.hasPermission(exec, "save");
	}

	@Override
	public void stopEventHandler(long executionId) {
		pipelineFacade.stopExecution(getLightExecution(executionId));
		refreshEventHandler();
	}

	@Override
	public void showDebugEventHandler(long executionId) {
		PipelineExecution exec = getLightExecution(executionId);
		if (exec == null) {
			Notification.show(String.format("Execution with ID=%d doesn't exist!", executionId), Notification.Type.ERROR_MESSAGE);
			return;
		}
		view.showExecutionDetail(exec, new ExecutionDetailData(getMessageDataSource()));
	}

	@Override
	public void runEventHandler(long executionId) {
		pipelineHelper.runPipeline(getLightExecution(executionId).getPipeline(), false);
		refreshEventHandler();
	}

	@Override
	public void debugEventHandler(long executionId) {
		PipelineExecution exec = pipelineHelper.runPipeline(getLightExecution(executionId).getPipeline(), true);
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
	 * @return light copy of execution
	 */
	private PipelineExecution getLightExecution(long executionId) {
		return pipelineFacade.getExecution(executionId);
	}

	private ReadOnlyContainer<MessageRecord> getMessageDataSource() {
		return new ReadOnlyContainer<>(
				new DbCachedSource<>(dbMessageRecord,
						new MessageRecordAccessor(), utils.getPageLength()));
	}

	@Override
	public void stopRefreshEventHandler() {
		refreshManager.removeListener(RefreshManager.DEBUGGINGVIEW);
	}

	@Override
	public void startDebugRefreshEventHandler(DebuggingView debugView, PipelineExecution execution) {
		refreshManager.addListener(RefreshManager.DEBUGGINGVIEW,
				RefreshManager.getDebugRefresher(debugView, execution, pipelineFacade));
	}

	@Override
	public void pageChangedHandler(Integer newPageNumber) {
		String uriFragment = Page.getCurrent().getUriFragment();
		ParametersHandler handler = new ParametersHandler(uriFragment);
		handler.addParameter("page", newPageNumber.toString());
		((AppEntry) UI.getCurrent()).setUriFragment(handler.getUriFragment(), false);
	}

	@Override
	public void filterParameterEventHander(String propertyId, Object filterValue) {
		String uriFragment = Page.getCurrent().getUriFragment();
		ParametersHandler handler = new ParametersHandler(uriFragment);
		if (filterValue == null || (filterValue.getClass() == String.class && ((String) filterValue).isEmpty())) {
			//Remove from URI
			handler.removeParameter(propertyId);
		} else {
			String value;
			switch (propertyId) {
				case "id":
					value = ParametersHandler.getStringForInterval((NumberInterval) filterValue);
					break;
				case "start":
					value = ParametersHandler.getStringForInterval((DateInterval) filterValue);
					break;
				default:
					value = filterValue.toString();
					break;
			}
			handler.addParameter(propertyId, value);
		}
		((AppEntry) UI.getCurrent()).setUriFragment(handler.getUriFragment(), false);
	}

	@Override
	public void navigateToEventHandler(Class where, Object param) {
		if (param == null) {
			navigator.navigateTo(where);
		} else {
			navigator.navigateTo(where, param.toString());
		}
	}
}
