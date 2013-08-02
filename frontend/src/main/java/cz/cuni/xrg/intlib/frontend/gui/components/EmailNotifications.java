package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;

import cz.cuni.xrg.intlib.commons.app.scheduling.NotificationRecordType;

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

		
		GridLayout notifycationLayout = new GridLayout(2,2);
		notifycationLayout.setSpacing(true);
		
		notifycationLayout.addComponent(new Label("Successful execution:"),0,0);
		OptionGroup successfulExec = new OptionGroup();
		successfulExec.setImmediate(true);
		successfulExec.addItem(NotificationRecordType.INSTANT);
		successfulExec.addItem(NotificationRecordType.DAILY);
		successfulExec.addItem(NotificationRecordType.NO_REPORT);
		successfulExec.select(NotificationRecordType.DAILY);
		successfulExec.setItemCaption(NotificationRecordType.INSTANT, "Instant");
		successfulExec.setItemCaption(NotificationRecordType.DAILY, "Daily bulk report (default)");
		successfulExec.setItemCaption(NotificationRecordType.NO_REPORT, "No report");

		successfulExec.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (event.getProperty().getValue().equals(NotificationRecordType.NO_REPORT)){
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
		errorExec.setImmediate(true);
		errorExec.addItem(NotificationRecordType.INSTANT);
		errorExec.addItem(NotificationRecordType.DAILY);
		errorExec.addItem(NotificationRecordType.NO_REPORT);
		errorExec.select(NotificationRecordType.INSTANT);
		errorExec.setItemCaption(NotificationRecordType.INSTANT,"Instant (default)");
		errorExec.setItemCaption(NotificationRecordType.DAILY, "Daily bulk report");
		errorExec.setItemCaption(NotificationRecordType.NO_REPORT, "No report");
		errorExec.addValueChangeListener(new ValueChangeListener() {
			
			@Override
			public void valueChange(ValueChangeEvent event) {
				if (event.getProperty().getValue().equals(NotificationRecordType.NO_REPORT)){
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
