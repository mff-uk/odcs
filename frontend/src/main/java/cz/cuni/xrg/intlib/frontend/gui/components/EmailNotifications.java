package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;

/**
 * Builds layout with GUI components for settings nitifications
 * about scheduled events and their runs. Used in User Settings dialog
 * {@link UserSettings} and in Schedule a pipeline dialog {@link SchedulePipeline}
 *  
 * @author Maria Kukhar
 *
 */
public class EmailNotifications {

	private GridLayout shEmailLayout; 
	int noSuccessful =0;
	int noError=0;
	
	public VerticalLayout buildEmailNotificationsLayout(){
		
		VerticalLayout emailNotificationsLayout = new VerticalLayout();
		emailNotificationsLayout.setMargin(true);
		emailNotificationsLayout.setSpacing(true);
		emailNotificationsLayout.setWidth("370px");
		emailNotificationsLayout.setImmediate(true);
		emailNotificationsLayout.setStyleName("settings");
		
		emailNotificationsLayout.addComponent(new Label("Default form of report about scheduled pipeline execution (may be overriden in the particular schedulled event): "));
		
		GridLayout notifycationLayout = new GridLayout(2,2);
		notifycationLayout.setSpacing(true);
		
		notifycationLayout.addComponent(new Label("Successful execution:"),0,0);
		OptionGroup successfulExec = new OptionGroup();
		successfulExec.setImmediate(true);
		successfulExec.addItem("Instant");
		successfulExec.addItem("Daily bulk report (default)");
		successfulExec.addItem("no report");
		successfulExec.select("Daily bulk report (default)");
		successfulExec.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (event.getProperty().getValue().toString().equals("no report")){
					noSuccessful=1;
					if((noError==1) && ( noSuccessful==1)){
						
						shEmailLayout.setEnabled(false);
					}
				}
				else{
					noSuccessful=0;
					shEmailLayout.setEnabled(true);
				}
				
			}
		});
		notifycationLayout.addComponent(successfulExec,1,0);
		emailNotificationsLayout.addComponent(notifycationLayout);
		
		notifycationLayout.addComponent(new Label("Error in execution:"),0,1);
		OptionGroup errorExec = new OptionGroup();
		errorExec.setImmediate(true);
		errorExec.addItem("Instant (default)");
		errorExec.addItem("Daily bulk report");
		errorExec.addItem("no report");
		errorExec.select("Instant (default)");
		errorExec.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (event.getProperty().getValue().toString().equals("no report")){
					noError=1;
					if((noError==1) && ( noSuccessful==1)){
						
						shEmailLayout.setEnabled(false);
					}
				}
				else{
					 noError=0;
					 shEmailLayout.setEnabled(true);	
						}
				
			}
		});
		notifycationLayout.addComponent(errorExec,1,1);
		emailNotificationsLayout.addComponent(notifycationLayout);
		
		emailNotificationsLayout.addComponent(new Label("E-mail notifications to: "));
        
        EmailComponent shEmail = new EmailComponent();
        shEmailLayout = new GridLayout();
        
     
        shEmailLayout = shEmail.initializeEmailList();
        emailNotificationsLayout.addComponent(shEmailLayout);
  

		return emailNotificationsLayout;
	}
}
