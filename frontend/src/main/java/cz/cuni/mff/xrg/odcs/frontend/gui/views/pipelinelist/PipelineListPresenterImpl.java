package cz.cuni.mff.xrg.odcs.frontend.gui.views.pipelinelist;

import com.github.wolfie.refresher.Refresher;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthAwarePermissionEvaluator;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.DbPipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.commons.app.facade.PipelineFacade;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import cz.cuni.mff.xrg.odcs.commons.app.facade.ScheduleFacade;
import cz.cuni.mff.xrg.odcs.frontend.AppEntry;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.PipelineHelper;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.RefreshManager;
import cz.cuni.mff.xrg.odcs.frontend.container.ReadOnlyContainer;
import cz.cuni.mff.xrg.odcs.frontend.container.accessor.PipelineAccessor;
import cz.cuni.mff.xrg.odcs.frontend.doa.container.db.DbCachedSource;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.SchedulePipeline;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Utils;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.executionlist.ExecutionListPresenterImpl;
import cz.cuni.mff.xrg.odcs.frontend.navigation.Address;
import cz.cuni.mff.xrg.odcs.frontend.navigation.ClassNavigator;
import cz.cuni.mff.xrg.odcs.frontend.navigation.ParametersHandler;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tepi.filtertable.numberfilter.NumberInterval;
import org.vaadin.dialogs.ConfirmDialog;

/**
 * Implementation of {@link PipelineListPresenter}.
 *
 * @author Bogo
 */
@Component
@Scope("prototype")
@Address(url = "PipelineList")
public class PipelineListPresenterImpl implements PipelineListPresenter {

	private static final Logger LOG = LoggerFactory.getLogger(PipelineListPresenterImpl.class);
	@Autowired
	private PipelineFacade pipelineFacade;
	@Autowired
	private DbPipeline dbPipeline;
	@Autowired
	private PipelineAccessor pipelineAccessor;
	@Autowired
	private PipelineListView view;
	@Autowired
	private SchedulePipeline schedulePipeline;
	@Autowired
	private ScheduleFacade scheduleFacade;
	@Autowired
	private PipelineHelper pipelineHelper;
	@Autowired
	private Utils utils;
	private ClassNavigator navigator;
	private PipelineListData dataObject;
	private DbCachedSource<Pipeline> cachedSource;
	private RefreshManager refreshManager;
	private Date lastLoad = new Date(0L);
	/**
	 * Evaluates permissions of currently logged in user.
	 */
	@Autowired
	private AuthAwarePermissionEvaluator permissions;

	@Override
	public Object enter() {
		navigator = ((AppEntry) UI.getCurrent()).getNavigation();
		// prepare data object
		cachedSource = new DbCachedSource<>(dbPipeline, pipelineAccessor, utils.getPageLength());
		dataObject = new PipelineListPresenter.PipelineListData(new ReadOnlyContainer<>(cachedSource));

		// prepare view
		Object viewObject = view.enter(this);

		refreshManager = ((AppEntry) UI.getCurrent()).getRefreshManager();
		refreshManager.addListener(RefreshManager.PIPELINE_LIST, new Refresher.RefreshListener() {
			private long lastRefreshFinished = 0;

			@Override
			public void refresh(Refresher source) {
				if (new Date().getTime() - lastRefreshFinished > RefreshManager.MIN_REFRESH_INTERVAL) {
					boolean hasModifiedExecutions = pipelineFacade.hasModifiedExecutions(lastLoad);
					if (hasModifiedExecutions) {
						lastLoad = new Date();
						refreshEventHandler();
					}
					LOG.debug("Pipeline list refreshed.");
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
			for (Entry<String, String> entry : config.entrySet()) {
				switch (entry.getKey()) {
					case "page":
						pageNumber = Integer.parseInt(entry.getValue());
						break;
					case "id":
						view.setFilter(entry.getKey(), ParametersHandler.getInterval(entry.getValue()));
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
	}

	@Override
	public void refreshEventHandler() {
		pipelineAccessor.clearExecCache();
		cachedSource.invalidate();
		dataObject.getContainer().refresh();
		view.refreshTableControls();
	}

	@Override
	public void copyEventHandler(long id) {
		Pipeline pipeline = getLightPipeline(id);
		pipelineFacade.copyPipeline(pipeline);
		Notification.show("Pipeline \"" + pipeline.getName() + "\" was successfully copied",
				Notification.Type.HUMANIZED_MESSAGE);
		refreshEventHandler();
	}

	@Override
	public void deleteEventHandler(long id) {
		final Pipeline pipeline = getLightPipeline(id);
		List<PipelineExecution> executions = pipelineFacade.getExecutions(pipeline, PipelineExecutionStatus.QUEUED);
		if (executions.isEmpty()) {
			executions = pipelineFacade.getExecutions(pipeline, PipelineExecutionStatus.RUNNING);
		}
		if (!executions.isEmpty()) {
			Notification.show("Pipeline " + pipeline.getName() + " has current(QUEUED or RUNNING) execution(s) and cannot be deleted now!", Notification.Type.WARNING_MESSAGE);
			return;
		}
		String message = "Would you really like to delete the \"" + pipeline.getName() + "\" pipeline and all associated records (DPU instances e.g.)?";
		List<Schedule> schedules = scheduleFacade.getSchedulesFor(pipeline);
		if (!schedules.isEmpty()) {
			HashSet<String> usersWithSchedules = new HashSet<>();
			for (Schedule schedule : schedules) {
				usersWithSchedules.add(schedule.getOwner().getUsername());
			}
			Iterator<String> it = usersWithSchedules.iterator();
			String users = it.next();
			while (it.hasNext()) {
				users = users + ", " + it.next();
			}
			String scheduleMessage = String.format(" This pipeline is schedulled by user(s) %s. Delete anyway?", users);
			message = message + scheduleMessage;
		}
		ConfirmDialog.show(UI.getCurrent(), "Confirmation of deleting pipeline", message, "Delete pipeline", "Cancel", new ConfirmDialog.Listener() {
			@Override
			public void onClose(ConfirmDialog cd) {
				if (cd.isConfirmed()) {
					pipelineFacade.delete(pipeline);
					refreshEventHandler();
				}
			}
		});
	}

	@Override
	public boolean canDeletePipeline(long pipelineId) {
		Pipeline pipeline = cachedSource.getObject(pipelineId);
		return permissions.hasPermission(pipeline, "delete");
	}

	@Override
	public void scheduleEventHandler(long id) {
		Pipeline pipeline = getLightPipeline(id);
		// open scheduler dialog
		if (!schedulePipeline.isInitialized()) {
			schedulePipeline.init();
		}
		schedulePipeline.setSelectePipeline(pipeline);
		UI.getCurrent().addWindow(schedulePipeline);
	}

	@Override
	public void runEventHandler(long id, boolean inDebugMode) {
		PipelineExecution exec = pipelineHelper.runPipeline(getLightPipeline(id), inDebugMode);
		if (inDebugMode && exec != null) {
			navigator.navigateTo(ExecutionListPresenterImpl.class, String.format("exec=%s", exec.getId()));
		}
	}

	@Override
	public void navigateToEventHandler(Class where, Object param) {
		if (param == null) {
			navigator.navigateTo(where);
		} else {
			navigator.navigateTo(where, param.toString());
		}
	}

	private Pipeline getLightPipeline(long pipelineId) {
		return pipelineFacade.getPipeline(pipelineId);
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
				default:
					value = filterValue.toString();
					break;
			}
			handler.addParameter(propertyId, value);
		}
		((AppEntry) UI.getCurrent()).setUriFragment(handler.getUriFragment(), false);
	}
}
