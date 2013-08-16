package cz.cuni.xrg.intlib.frontend.gui.components;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.hibernate.cfg.Settings;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;
import cz.cuni.xrg.intlib.commons.app.scheduling.EmailAddress;
import cz.cuni.xrg.intlib.commons.app.scheduling.NotificationRecordType;
import cz.cuni.xrg.intlib.commons.app.scheduling.Schedule;
import cz.cuni.xrg.intlib.commons.app.scheduling.ScheduleNotificationRecord;
import cz.cuni.xrg.intlib.commons.app.scheduling.UserNotificationRecord;
import cz.cuni.xrg.intlib.commons.app.user.User;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;

/**
 * Builds layout with GUI components for settings nitifications
 * about scheduled events and their runs. Used in User Settings dialog
 * {@link UserSettings} and in Schedule a pipeline dialog {@link SchedulePipeline}
 *  
 * @author Maria Kukhar
 *
 */
public class EmailNotifications {


	private int noSuccessful =0;
	private int noError=0;
	private boolean validation=true;
//	public EmailComponent shEmail;
	private OptionGroup errorExec;
	private OptionGroup successfulExec;
	private UserNotificationRecord userNotifcationRecord;
	private ScheduleNotificationRecord schNotifcationRecord;
	public SchedulePipeline parentComponentSh;
	public cz.cuni.xrg.intlib.frontend.gui.views.Settings parentComponentUs;


	
	public void setUserNotificatonRecord(UserNotificationRecord notofication){
		
		notofication.setTypeError((NotificationRecordType)errorExec.getValue());
		notofication.setTypeSuccess((NotificationRecordType)successfulExec.getValue());


	}
	
/*	public ScheduleNotificationRecord setScheduleNotificationRecord(Schedule schedule){
		
		schNotifcationRecord = new ScheduleNotificationRecord();
		schNotifcationRecord.setSchedule(schedule);
		schNotifcationRecord.setTypeError((NotificationRecordType)errorExec.getValue());
		schNotifcationRecord.setTypeSuccess((NotificationRecordType)successfulExec.getValue());


		return schNotifcationRecord;

	}*/
	
	public void setScheduleNotificationRecord(ScheduleNotificationRecord notofication, Schedule schedule){
		
		notofication.setSchedule(schedule);
		notofication.setTypeError((NotificationRecordType)errorExec.getValue());
		notofication.setTypeSuccess((NotificationRecordType)successfulExec.getValue());

	}
	
	
	public void getScheduleNotificationRecord(Schedule schedule){
		
		ScheduleNotificationRecord notification = schedule.getNotification();
		
		if(notification!=null){
			errorExec.setValue(notification.getTypeError());
			successfulExec.setValue(notification.getTypeSuccess());
		}
		
	}
	
	
	public void getUserNotificationRecord(User user){
		
		UserNotificationRecord notification = user.getNotification();
		
		if(notification!=null){
			errorExec.setValue(notification.getTypeError());
			successfulExec.setValue(notification.getTypeSuccess());
		
		}

	}
	
	
		public VerticalLayout buildEmailNotificationsLayout(){
		
		VerticalLayout emailNotificationsLayout = new VerticalLayout();
		emailNotificationsLayout.setMargin(true);
		emailNotificationsLayout.setSpacing(true);
		emailNotificationsLayout.setImmediate(true);

		
		GridLayout notifycationLayout = new GridLayout(2,2);
		notifycationLayout.setSpacing(true);
		
		notifycationLayout.addComponent(new Label("Successful execution:"),0,0);
		successfulExec = new OptionGroup();
		successfulExec.setImmediate(true);
		successfulExec.addItem(NotificationRecordType.INSTANT);
		successfulExec.addItem(NotificationRecordType.DAILY);
		successfulExec.addItem(NotificationRecordType.NO_REPORT);
		successfulExec.select(NotificationRecordType.DAILY);
		successfulExec.setItemCaption(NotificationRecordType.INSTANT, "Instant");
		successfulExec.setItemCaption(NotificationRecordType.DAILY, "Daily bulk report (default)");
		successfulExec.setItemCaption(NotificationRecordType.NO_REPORT, "No report");


		successfulExec.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (event.getProperty().getValue().equals(NotificationRecordType.NO_REPORT)){
					noSuccessful=1;
					if((noError==1) && ( noSuccessful==1)){
						if(parentComponentSh!=null)	
							parentComponentSh.emailLayout.setEnabled(false);
//						if(parentComponentUs!=null)	
//							parentComponentUs.emailLayout.setEnabled(false);
					}

				}
				else{
					noSuccessful=0;
					if(parentComponentSh!=null)	
						parentComponentSh.emailLayout.setEnabled(true);
//					if(parentComponentUs!=null)	
//						parentComponentUs.emailLayout.setEnabled(true);
					
				}
				
			}
		});
		
		notifycationLayout.addComponent(successfulExec,1,0);
		emailNotificationsLayout.addComponent(notifycationLayout);
		
		notifycationLayout.addComponent(new Label("Error in execution:"),0,1);
		errorExec = new OptionGroup();
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

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (event.getProperty().getValue().equals(NotificationRecordType.NO_REPORT)){
					noError=1;
					
					if((noError==1) && ( noSuccessful==1)){
						if(parentComponentSh!=null)
							parentComponentSh.emailLayout.setEnabled(false);
//						if(parentComponentUs!=null)	
//							parentComponentUs.emailLayout.setEnabled(false);
					}
				}
				else{
					noError=0;
					if(parentComponentSh!=null)
						parentComponentSh.emailLayout.setEnabled(true);
//					if(parentComponentUs!=null)	
//						parentComponentUs.emailLayout.setEnabled(true);

						}
				
			}
		});

		notifycationLayout.addComponent(errorExec,1,1);
		emailNotificationsLayout.addComponent(notifycationLayout);
		

		return emailNotificationsLayout;
	}
		
		public void setDisableComponents(){
			
			successfulExec.setEnabled(false);
			errorExec.setEnabled(false);
			parentComponentSh.emailLayout.setEnabled(false);
//			shEmailLayout.setEnabled(false);
			
		}
		
		public void setEnableComponents(){
			
			successfulExec.setEnabled(true);
			errorExec.setEnabled(true);
			parentComponentSh.emailLayout.setEnabled(true);
//			shEmailLayout.setEnabled(true);
			
		}
		
}
