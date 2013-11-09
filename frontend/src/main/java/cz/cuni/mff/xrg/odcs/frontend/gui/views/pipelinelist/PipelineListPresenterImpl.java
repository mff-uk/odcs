package cz.cuni.mff.xrg.odcs.frontend.gui.views.pipelinelist;

import com.vaadin.ui.Notification;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.DbPipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineFacade;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.App;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.IntlibHelper;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.MaxLengthValidator;
import cz.cuni.mff.xrg.odcs.frontend.container.ReadOnlyContainer;
import cz.cuni.mff.xrg.odcs.frontend.container.accessor.PipelineAccessor;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.SchedulePipeline;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.executionmonitor.ExecutionMonitor;
import cz.cuni.mff.xrg.odcs.frontend.navigation.Address;
import cz.cuni.mff.xrg.odcs.frontend.navigation.ClassNavigator;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;




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
		Pipeline nPipeline = pipelineFacade.copyPipeline(pipeline);
		String copiedPipelineName = "Copy of " + pipeline.getName();
		boolean isNameLengthOk = copiedPipelineName.length() <= MaxLengthValidator.NAME_LENGTH;
		if (isNameLengthOk) {
			nPipeline.setName(copiedPipelineName);
		}
		pipelineFacade.save(nPipeline);
		if (!isNameLengthOk) {
			Notification.show(String.format("Name of copied pipeline would exceed limit of %d characters, new pipeline has same name as original.", MaxLengthValidator.NAME_LENGTH), Notification.Type.WARNING_MESSAGE);
		}
		refreshEventHandler();
	}

	@Override
	public void deleteEventHandler(long id) {
		pipelineFacade.delete(getLightPipeline(id));
		refreshEventHandler();
	}

	@Override
	public void scheduleEventHandler(long id) {
		Pipeline pipeline = getLightPipeline(id);
		// open scheduler dialog
		SchedulePipeline sch = new SchedulePipeline();
		sch.setSelectePipeline(pipeline);
		App.getApp().addWindow(sch);
	}

	@Override
	public void runEventHandler(long id, boolean inDebugMode) {
		PipelineExecution exec = IntlibHelper.runPipeline(getLightPipeline(id), inDebugMode);
		if (inDebugMode && exec != null) {
			navigator.navigateTo(ExecutionMonitor.class, exec.getId().toString());
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
