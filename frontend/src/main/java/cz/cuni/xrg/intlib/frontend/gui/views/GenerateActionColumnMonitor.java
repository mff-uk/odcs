package cz.cuni.xrg.intlib.frontend.gui.views;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;



public class GenerateActionColumnMonitor implements ColumnGenerator {

	private ClickListener clickListener = null;
	private Object execID;
	
	public GenerateActionColumnMonitor(ClickListener outclickListerner){
		super();
		this.clickListener = outclickListerner;
	}

	@Override
	public Object generateCell(Table source, Object itemId, Object columnId) {
		Property prop1 = source.getItem(itemId).getItemProperty("status");
		Property prop2 = source.getItem(itemId).getItemProperty("debug");
		String testStatus = "---";
		String testDebug = "---";
		//execID=itemId;
		execID=itemId;
		
		HorizontalLayout box = new HorizontalLayout();
		box.setSpacing(true);
		
		if ((prop1.getType().equals(String.class)) || (prop2.getType().equals(String.class)) )
		{
			testStatus = (String)prop1.getValue();
			testDebug = (String)prop2.getValue();
			if ((testStatus.contains("SCHEDULED"))&&(testDebug.contains("false")))
			{
				Button stopButton = new Button("Stop");
				stopButton.setData(new ActionButtonData("stop",itemId));
				stopButton.setWidth("90px");
				box.addComponent(stopButton);
				if(this.clickListener!=null)
					stopButton.addListener(this.clickListener);
				
				
				
			}
			if (((testStatus.contains("FAILED"))|| (testStatus.contains("FINISHED_SUCCESS")))&&(testDebug.contains("false")))
			{
				Button logButton = new Button("Show log");
				logButton.setData(new ActionButtonData("showlog",itemId));
				
				logButton.setWidth("90px");
				if(this.clickListener!=null)
					logButton.addListener(this.clickListener);
				
				
				box.addComponent(logButton);
				
				
			}

			if (testDebug.contains("true"))
			{
				Button debugButton = new Button("Debug data");
				
				debugButton.setData(new ActionButtonData("debug",itemId));
				debugButton.setWidth("90px");
				if(this.clickListener!=null)
					debugButton.addListener(this.clickListener);
								
				
				box.addComponent(debugButton);
				
			}
					
		}		
		
		
		return box;
	}
	
	

	
}
