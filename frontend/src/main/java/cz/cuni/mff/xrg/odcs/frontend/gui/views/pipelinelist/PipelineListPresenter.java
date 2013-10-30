/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cuni.mff.xrg.odcs.frontend.gui.views.pipelinelist;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.frontend.gui.views.pipelinelist.PipelineListView.PipelineListViewListener;

/**
 *
 * @author Bogo
 */
public class PipelineListPresenter implements PipelineListViewListener {
	
	PipelineListModel model;
	PipelineListView view;
	
	private static final int PAGE_LENGTH = 20;
	
	public PipelineListPresenter(PipelineListModel model, PipelineListView view) {
		this.model = model;
		this.view = view;
		
		view.setListener(this);
		view.setDataSource(model.getDataSource(PAGE_LENGTH));
	}

	@Override
	public void navigation(String where) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void pipelineEvent(long id, String event) {
		switch(event) {
			case "copy": 
				model.copyPipeline(id);
				break;
			case "delete":
				model.deletePipeline(id);
				break;
			case "run":
				model.runPipeline(id);
				break;
			case "debug":
				model.debugPipeline(id);
				break;
			case "schedule":
				model.schedulePipeline(id);
				break;
		}
	}

	@Override
	public void event(String name) {
		switch(name) {
			case "refresh": 
				model.refresh();
				break;
		}
	}

	@Override
	public Object getLastExecDetail(Pipeline ppl, String detail) {
		switch(detail) {
			case "duration":
				return model.getLastExecutionDuration(ppl);
			case "status":
				return model.getLastExecutionStatus(ppl);
			case "time":
				return model.getLastExecutionTime(ppl);
			default: 
				return null;
				
		}
	}
	
}
