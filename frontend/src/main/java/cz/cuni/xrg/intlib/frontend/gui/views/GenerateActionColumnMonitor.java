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
		Property prop = source.getItem(itemId).getItemProperty("status");
		String test = "---";
		//execID=itemId;
		execID=itemId;
		
		HorizontalLayout box = new HorizontalLayout();
		box.setSpacing(true);
		
		if (prop.getType().equals(String.class))
		{
			test = (String)prop.getValue();
			if (test.contains("SCHEDULED"))
			{
				Button stopButton = new Button("Stop");
				stopButton.setData(new ActionButtonData("stop",itemId));
				stopButton.setWidth("120px");
				box.addComponent(stopButton);
				if(this.clickListener!=null)
					stopButton.addListener(this.clickListener);
				
				
			}
			if ((test.contains("FAILED"))|| (test.contains("FINISHED_SUCCESS")))
			{
				Button logButton = new Button("Show log");
				logButton.setData(new ActionButtonData("showlog",itemId));
				
				logButton.setWidth("120px");
				if(this.clickListener!=null)
					logButton.addListener(this.clickListener);
				
				
				box.addComponent(logButton);
				
			}

		/*	if (test.contains("FINISHED_SUCCESS"))
			{
				Button debugButton = new Button("Debug data");
				
				debugButton.setData(new ActionButtonData("debug",itemId));
				debugButton.setWidth("120px");
				if(this.clickListener!=null)
					debugButton.addListener(this.clickListener);
				
				
				box.addComponent(debugButton);
				
			}*/
					
		}		
		
		
		return box;
	}
	
	

	
}
