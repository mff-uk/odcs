package cz.cuni.mff.xrg.odcs.frontend.gui.views.pipelinelist;

import com.github.wolfie.refresher.Refresher;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.DbPipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecutionStatus;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineFacade;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.ScheduleFacade;
import cz.cuni.mff.xrg.odcs.frontend.AppEntry;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.IntlibHelper;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.RefreshManager;
import cz.cuni.mff.xrg.odcs.frontend.container.ReadOnlyContainer;
import cz.cuni.mff.xrg.odcs.frontend.container.accessor.PipelineAccessor;
import cz.cuni.mff.xrg.odcs.frontend.doa.container.CachedSource;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.SchedulePipeline;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.executionlist.ExecutionListPresenterImpl;
import cz.cuni.mff.xrg.odcs.frontend.navigation.Address;
import cz.cuni.mff.xrg.odcs.frontend.navigation.ClassNavigator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.vaadin.dialogs.ConfirmDialog;

@Component
@Scope("prototype")
@Address(url = "PipelineList")
public class PipelineListPresenterImpl implements PipelineListPresenter {

	private ClassNavigator navigator;
	@Autowired
	private PipelineFacade pipelineFacade;
	@Autowired
	private DbPipeline dbPipeline;
	@Autowired
	private PipelineAccessor pipelineAccessor;
	@Autowired
	private PipelineListView view;
	private PipelineListData dataObject;
	private CachedSource<Pipeline> cachedSource;
	private RefreshManager refreshManager;
	private static final Logger LOG = LoggerFactory.getLogger(PipelineListPresenterImpl.class);
	private Date lastLoad = new Date(0L);
	@Autowired
	private SchedulePipeline schedulePipeline;
	@Autowired
	private ScheduleFacade scheduleFacade;

	@Override
	public Object enter() {
		navigator = ((AppEntry) UI.getCurrent()).getNavigation();
		// prepare data object
		cachedSource = new CachedSource<>(dbPipeline, pipelineAccessor);
		dataObject = new PipelineListPresenter.PipelineListData(new ReadOnlyContainer<>(cachedSource));

		// prepare view
		Object viewObject = view.enter(this);

		refreshManager = ((AppEntry) UI.getCurrent()).getRefreshManager();
		refreshManager.addListener(RefreshManager.PIPELINE_LIST, new Refresher.RefreshListener() {
			@Override
			public void refresh(Refresher source) {
				boolean hasModifiedExecutions = pipelineFacade.hasModifiedExecutions(lastLoad);
				if (hasModifiedExecutions) {
					lastLoad = new Date();
					refreshEventHandler();
				}
				LOG.debug("Pipeline list refreshed.");
			}
		});

		// set data object
		view.setDisplay(dataObject);
		// return main component
		return viewObject;
	}

	@Override
	public void setParameters(Object configuration) {
		// we do not care about parameters, we always do the same job .. 
	}

	@Override
	public void refreshEventHandler() {
		pipelineAccessor.clearExecCache();
		cachedSource.invalidate();
		dataObject.getContainer().refresh();
	}

	@Override
	public void copyEventHandler(long id) {
		Pipeline pipeline = getLightPipeline(id);
		pipelineFacade.copyPipeline(pipeline);
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
		String message = "Would you really like to delete the " + pipeline.getName() + " pipeline and all associated records (DPU instances e.g.)?";
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
		PipelineExecution exec = IntlibHelper.runPipeline(getLightPipeline(id), inDebugMode);
		if (inDebugMode && exec != null) {
			navigator.navigateTo(ExecutionListPresenterImpl.class, exec.getId().toString());
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
}
