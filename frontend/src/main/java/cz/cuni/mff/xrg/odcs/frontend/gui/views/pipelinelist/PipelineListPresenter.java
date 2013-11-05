package cz.cuni.mff.xrg.odcs.frontend.gui.views.pipelinelist;

import com.vaadin.data.Container;
import com.vaadin.ui.Notification;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineExecution;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.PipelineFacade;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.App;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.ContainerFactory;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.IntlibHelper;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.MaxLengthValidator;
import cz.cuni.mff.xrg.odcs.frontend.gui.ViewNames;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.SchedulePipeline;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.Utils;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.pipelinelist.PipelineListView.PipelineListViewListener;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Presenter class for PipelineList. Handles events from PipelineListView.
 *
 * @author Bogo
 */
public class PipelineListPresenter implements PipelineListViewListener {

	PipelineListView view;

	
    //TODO pipelineFacade, containerFactory should be defined only on the model
	@Autowired
	private PipelineFacade pipelineFacade;
	
        @Autowired
	private ContainerFactory containerFactory;
		
	public PipelineListPresenter() {
	}
	
        /**
         * Prepares the view - data source for the view and listener for the event of the view
         * @param view 
         */
	public void setView(PipelineListView view) {
		this.view = view;

		view.setListener(this);
		view.setDataSource(getDataSource(Utils.PAGE_LENGTH));
	}

	@Override
	public void navigation(String where) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void pipelineEvent(long id, String event) {
		switch (event) {
			case "copy":
				copyPipeline(id);
				break;
			case "delete":
				deletePipeline(id);
				break;
			case "run":
				runPipeline(id);
				break;
			case "debug":
				debugPipeline(id);
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

        //TODO not doing anything? 
	void refresh() {
	}

        //TODO move to the Model
	void copyPipeline(long id) {
		Pipeline pipeline = pipelineFacade.getPipeline(id);
		Pipeline nPipeline = pipelineFacade.copyPipeline(pipeline);
		String copiedPipelineName = "Copy of " + pipeline.getName();
		if (copiedPipelineName.length() > MaxLengthValidator.NAME_LENGTH) {
			Notification.show(String.format("Name of copied pipeline would exceed limit of %d characters, new pipeline has same name as original.", MaxLengthValidator.NAME_LENGTH), Notification.Type.WARNING_MESSAGE);
		} else {
			nPipeline.setName(copiedPipelineName);
		}
		pipelineFacade.save(nPipeline);
	}

	void deletePipeline(long id) {
		final Pipeline pipeline = pipelineFacade.getPipeline(id);
		pipelineFacade.delete(pipeline);
	}

	void runPipeline(long id) {
		Pipeline pipeline = pipelineFacade.getPipeline(id);
		IntlibHelper.runPipeline(pipeline, false);
	}

	void debugPipeline(long id) {
		Pipeline pipeline = pipelineFacade.getPipeline(id);
		PipelineExecution exec = IntlibHelper.runPipeline(pipeline, true);
		if (exec != null) {
			App.getApp().getNavigator().navigateTo(ViewNames.EXECUTION_MONITOR.getUrl() + "/" + exec.getId());
		}
	}

	void schedulePipeline(long id) {
		Pipeline pipeline = pipelineFacade.getPipeline(id);
		// open scheduler dialog
		SchedulePipeline sch = new SchedulePipeline();
		sch.setSelectePipeline(pipeline);
		App.getApp().addWindow(sch);
	}

        //TODO should be defined as public method on the Model class
	private Container getDataSource(int pageLength) {
		return containerFactory.createPipelines(pageLength);
	}
}
