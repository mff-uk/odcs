package cz.cuni.mff.xrg.odcs.frontend.gui.views.pipelinelist;

import com.vaadin.ui.UI;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.DbPipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineFacade;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.IntlibHelper;
import cz.cuni.mff.xrg.odcs.frontend.container.ReadOnlyContainer;
import cz.cuni.mff.xrg.odcs.frontend.container.accessor.PipelineAccessor;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.SchedulePipeline;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.executionlist.ExecutionListPresenterImpl;
import cz.cuni.mff.xrg.odcs.frontend.navigation.Address;
import cz.cuni.mff.xrg.odcs.frontend.navigation.ClassNavigator;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.vaadin.dialogs.ConfirmDialog;

@Component
@Scope("prototype")
@Address(url = "PipelineList")
public class PipelineListPresenterImpl implements PipelineListPresenter {

	//TODO do we need this?
	public static final String NAME = "PipelineList";
	@Autowired
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
	
	@Autowired
	private SchedulePipeline schedulePipeline;

	@Override
	public Object enter(Object configuration) {
		// prepare data object
		dataObject = new PipelineListPresenter.PipelineListData(new ReadOnlyContainer<>(dbPipeline, pipelineAccessor));
		// prepare view
		Object viewObject = view.enter(this);
		// set data object
		view.setDisplay(dataObject);
		// return main component
		return viewObject;
	}

	@Override
	public void refreshEventHandler() {
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
		String message = "Would you really like to delete the " + pipeline.getName() + " pipeline and all associated records (DPU instances e.g.)?";
		//String message = "Would you really like to delete this pipeline and all associated records (DPU instances e.g.)?";
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
		if(!schedulePipeline.isInitialized()) {
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
