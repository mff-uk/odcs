package cz.cuni.xrg.intlib.frontend.gui.components;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import cz.cuni.xrg.intlib.commons.app.execution.PipelineExecution;
import cz.cuni.xrg.intlib.commons.app.pipeline.Pipeline;
import cz.cuni.xrg.intlib.commons.app.scheduling.PeriodUnit;
import cz.cuni.xrg.intlib.commons.app.scheduling.Schedule;
import cz.cuni.xrg.intlib.commons.app.scheduling.ScheduleType;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;

public class PipelineStatus extends Window {
	
	private String lastRunTimeStr;
	private Label lastRunTime; 
	private Label runsNumber;
	private int i;
	
	public void setSelectedPipeline(Pipeline selectedPipeline){
		
		List<Schedule> schedulers = App.getApp().getSchedules().getAllSchedules();
		Date maxDate = null;  
		for (Schedule item : schedulers) {
			if ((item.getPipeline().getId().equals(selectedPipeline.getId()))&&(item.getLastExecution()!=null) ){
//				lastRunTimeStr = item.getLastExecution().toLocaleString();
				if (maxDate==null || maxDate.getTime()<item.getLastExecution().getTime()){
					maxDate=item.getLastExecution();
				}
//				break;
			}
		}
		if(maxDate!=null)
			lastRunTimeStr = maxDate.toLocaleString();
		else
			lastRunTimeStr="";
		lastRunTime.setCaption(lastRunTimeStr);	
		
		List<PipelineExecution> executions = App.getApp().getPipelines().getAllExecutions();
		i=0;
		for (PipelineExecution item : executions) {
			if (item.getPipeline().getId().equals(selectedPipeline.getId()) ){
				i++;
			}
		}
		runsNumber.setCaption(Integer.toString(i));	
	}
	
	public PipelineStatus(){
		
		this.setResizable(false);
		this.setDraggable(false);
		this.setModal(true);
		this.setCaption("Pipeline status");
		
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setMargin(true);
		mainLayout.setWidth("200px");
		mainLayout.setHeight("100px");
		
		HorizontalLayout lastRunLayout = new HorizontalLayout();
		lastRunLayout.setSpacing(true);
		lastRunLayout.addComponent(new Label("Last run:"));
		
		lastRunTime = new Label();
		
		if(lastRunTimeStr==null)
			lastRunTimeStr ="";
		
		lastRunTime.setCaption(lastRunTimeStr);		
		lastRunLayout.addComponent(lastRunTime);
		
		HorizontalLayout runsNumberLayout = new HorizontalLayout();
		runsNumberLayout.setSpacing(true);
		runsNumberLayout.addComponent(new Label("Number of runs:"));
		
		runsNumber = new Label();

		runsNumberLayout.addComponent(runsNumber);
		
		mainLayout.addComponent(lastRunLayout);
		mainLayout.addComponent(runsNumberLayout);
		
		this.setContent(mainLayout);
		setSizeUndefined();
		
		
	}

}
