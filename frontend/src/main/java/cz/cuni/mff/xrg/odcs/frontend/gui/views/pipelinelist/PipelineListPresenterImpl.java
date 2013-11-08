package cz.cuni.mff.xrg.odcs.frontend.gui.views.pipelinelist;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Notification;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.frontend.ViewNavigator;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.App;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.MaxLengthValidator;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.SchedulePipeline;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Utils;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.executionmonitor.ExecutionMonitor;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.pipelinelist.PipelineListView.PipelineListViewListener;
import cz.cuni.mff.xrg.odcs.frontend.mvp.BasePresenter;
import cz.cuni.mff.xrg.odcs.frontend.mvp.MVPModel;
import cz.cuni.mff.xrg.odcs.frontend.mvp.MVPView;
import cz.cuni.mff.xrg.odcs.frontend.mvp.Model;
import cz.cuni.mff.xrg.odcs.frontend.mvp.View;
import cz.cuni.mff.xrg.odcs.frontend.navigation.Address;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ru.xpoft.vaadin.VaadinView;

/**
 * Presenter class for PipelineList. Handles events from PipelineListView.
 *
 * @author Bogo
 */
@Component
@Scope("prototype")
@VaadinView(PipelineListPresenterImpl.NAME)
@Model(PipelineListModel.class)
@View(PipelineListViewImpl.class)
@Address(url = "PipelineList")
public class PipelineListPresenterImpl extends BasePresenter implements PipelineListViewListener {

	/**
	 * View name.
	 */
	//TODO do we need this?
	public static final String NAME = "PipelineList";
	private PipelineListView view;
	private PipelineListModel pipelineModel;
	@Autowired
	private ViewNavigator navigator;

	public PipelineListPresenterImpl() {
            
        }

	@Override
	public void setModel(MVPModel model) {
		if (!PipelineListModel.class.isInstance(model)) {
			return;
		}
		this.pipelineModel = (PipelineListModel) model;
	}

	/**
	 * Prepares the view - data source for the view and listener for the event
	 * of the view
	 *
	 * @param view
	 */
	@Override
	public void setView(MVPView view) {
		if (!PipelineListView.class.isInstance(view)) {
			return;
		}
		this.view = (PipelineListView) view;
		this.view.setListener(this);
		this.view.setDataSource(pipelineModel.getDataSource(Utils.PAGE_LENGTH));
	}

	@Override
	public void navigation(String where) {
		navigator.navigateTo(where);
	}

	@Override
	public void navigation(String where, Object parameter) {
		navigator.navigateTo(where, parameter);
	}

	@Override
	public void pipelineEvent(long id, String event) {
		switch (event) {
			case "copy":
				if (!pipelineModel.copyPipeline(id)) {
					Notification.show(String.format("Name of copied pipeline would exceed limit of %d characters, new pipeline has same name as original.", MaxLengthValidator.NAME_LENGTH), Notification.Type.WARNING_MESSAGE);
				}
				view.refresh();
				break;
			case "delete":
				pipelineModel.deletePipeline(id);
				view.refresh();
				break;
			case "run":
				pipelineModel.runPipeline(id, false);
				break;
			case "debug":
				PipelineExecution exec = pipelineModel.runPipeline(id, true);
				if (exec != null) {
					navigator.navigateTo(ExecutionMonitor.NAME, exec.getId());
				}
				break;
			case "schedule":
				schedulePipeline(id);
				break;
		}
	}

	@Override
	public void event(String name) {
		switch (name) {
			case "refresh":
				refresh();
				break;
		}
	}

	void refresh() {
		view.refresh();
	}

	void schedulePipeline(long id) {
		Pipeline pipeline = pipelineModel.getPipeline(id);
		// open scheduler dialog
		SchedulePipeline sch = new SchedulePipeline();
		sch.setSelectePipeline(pipeline);
		App.getApp().addWindow(sch);
	}
}
