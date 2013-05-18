package cz.cuni.xrg.intlib.frontend.gui.views;

import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;


public class GenerateActionColumnMonitor implements ColumnGenerator {

	private ClickListener clickListener = null;
	public GenerateActionColumnMonitor(ClickListener outclickListerner){
		super();
		this.clickListener = outclickListerner;
	}

	@Override
	public Object generateCell(Table source, Object itemId, Object columnId) {
		Property prop = source.getItem(itemId).getItemProperty("status");
		String test = "---";
		
		HorizontalLayout box = new HorizontalLayout();
		box.setSpacing(true);
		
		if (prop.getType().equals(String.class))
		{
			test = (String)prop.getValue();
			if (test.contains("SCHEDULED"))
			{
				Button stopButton = new Button("Stop");
				stopButton.setData("stop");
				stopButton.setWidth("120px");
				box.addComponent(stopButton);
			}
			if (test.contains("FAILED"))
			{
				Button logButton = new Button("Show log");
				logButton.setData("showlog");
				logButton.setWidth("120px");
				if(this.clickListener!=null)
					logButton.addListener(this.clickListener);
				box.addComponent(logButton);
				
			}

			if (test.contains("FINISHED_SUCCESS"))
			{
				Button debugButton = new Button("Debug data");
				debugButton.setData("debug");
				debugButton.setWidth("120px");
				if(this.clickListener!=null)
					debugButton.addListener(this.clickListener);
				box.addComponent(debugButton);
			}
		
		}		
		
		
		return box;
	}
	
	

	
}
