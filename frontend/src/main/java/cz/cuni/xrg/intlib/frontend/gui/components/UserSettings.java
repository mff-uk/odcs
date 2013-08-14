package cz.cuni.xrg.intlib.frontend.gui.components;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

import cz.cuni.xrg.intlib.commons.app.scheduling.EmailAddress;
import cz.cuni.xrg.intlib.commons.app.scheduling.UserNotificationRecord;
import cz.cuni.xrg.intlib.commons.app.user.User;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;

/**
 * Dialog for the User settings creation which opens from the main menu. 
 * Allows to set e-mail for get notifications and set scheduler notifications.
 * 
 * @author Maria Kukhar
 *
 */


public class UserSettings extends Window {
	
	private HorizontalLayout mainLayout;
	private Panel mainPanel;
	private HorizontalSplitPanel hsplit;
	private VerticalLayout tabsLayout;
	private VerticalLayout settingsLayout;
	private VerticalLayout schedulerLayout;
	private VerticalLayout usersLayout;

	private Button shownTab = null;
	private Button schedulerButton;
	private Button myAccountButton;
	private HorizontalLayout buttonBar;
	private boolean validation=false;
	private EmailNotifications emailNotifications;
	
	private GridLayout emailLayout;
	private EmailComponent email;
	private UsersList usersList;
	private Button usersButton;
	
	public UserSettings(){
		
		this.setResizable(false);
		this.setModal(true);
		this.setCaption("User Settings");
		
		mainLayout = new HorizontalLayout();
//		mainLayout.setMargin(true);

        
        tabsLayout = new VerticalLayout();
        tabsLayout.setMargin(true);
        tabsLayout.setWidth("105px");
        tabsLayout.setImmediate(true);

        
        settingsLayout = new VerticalLayout();
        settingsLayout.setMargin(true);
        settingsLayout.setSpacing(true);
        settingsLayout.setWidth("370px");
        settingsLayout.setImmediate(true);
        settingsLayout.setStyleName("settings");
       
        schedulerLayout = new VerticalLayout();
        schedulerLayout.setMargin(true);
        schedulerLayout.setSpacing(true);
        schedulerLayout.setWidth("370px");
        schedulerLayout.setImmediate(true);
        
        usersLayout = new VerticalLayout();
        usersLayout.setImmediate(true);
        usersLayout.setWidth("100%");
      
        usersList = new UsersList();
        usersLayout = usersList.buildUsersListLayout();
        usersLayout.setStyleName("settings");
        usersLayout.setWidth("600px");

        myAccountButton = new NativeButton("My account");
        myAccountButton.setHeight("40px");
        myAccountButton.setWidth("90px");
        myAccountButton.setStyleName("selectedtab");
        myAccountButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				shownTab=myAccountButton;
				shownTab.setStyleName("selectedtab");
				schedulerButton.setStyleName("multiline");
				usersButton.setStyleName("multiline");
				
				mainLayout.removeComponent(usersLayout);
				mainLayout.removeComponent(settingsLayout);
				mainLayout.removeComponent(schedulerLayout);
				mainLayout.addComponent(settingsLayout);

				
			}
		});
        tabsLayout.addComponent(myAccountButton);
        tabsLayout.setComponentAlignment(myAccountButton, Alignment.TOP_RIGHT);
        
    
        schedulerButton = new NativeButton("Scheduler\r\n notifications");
        schedulerButton.setHeight("40px");
        schedulerButton.setWidth("90px");
        schedulerButton.setStyleName("multiline");
        schedulerButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				shownTab=schedulerButton;
				shownTab.setStyleName("selectedtab");
				myAccountButton.setStyleName("multiline");
				usersButton.setStyleName("multiline");
				
				mainLayout.removeComponent(usersLayout);
				mainLayout.removeComponent(settingsLayout);
				mainLayout.removeComponent(schedulerLayout);
				mainLayout.addComponent(schedulerLayout);


				
			}
		});
        tabsLayout.addComponent(schedulerButton);
        tabsLayout.setComponentAlignment(schedulerButton, Alignment.TOP_RIGHT);
        
        
        usersButton = new NativeButton("Users");
        usersButton.setHeight("40px");
        usersButton.setWidth("90px");
        usersButton.setStyleName("multiline");
        usersButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				shownTab=usersButton;
				shownTab.setStyleName("selectedtab");
				schedulerButton.setStyleName("multiline");
				myAccountButton.setStyleName("multiline");
				
				mainLayout.removeComponent(usersLayout);
				mainLayout.removeComponent(settingsLayout);
				mainLayout.removeComponent(schedulerLayout);
				mainLayout.addComponent(usersLayout);
				mainLayout.setExpandRatio(usersLayout, 0.85f);

				
			}
		});
        tabsLayout.addComponent(usersButton);
        tabsLayout.setComponentAlignment(usersButton, Alignment.TOP_RIGHT);

        
		
        settingsLayout.addComponent(new Label("E-mail notifications to: "));
        
        email = new EmailComponent();
        emailLayout = new GridLayout();
        
        emailLayout = email.initializeEmailList(validation);
        
        EmailAddress userEemail = App.getApp().getUsers().getUser(1L).getEmail();
        if(userEemail!=null){
			String emailStr = userEemail.getName()+"@"+ userEemail.getDomain();
			email.griddata.clear();
			email.griddata.add(0,emailStr);
			email.refreshEmailData(true);
        }
        
        HorizontalLayout buttonBarMyAcc= new HorizontalLayout();
        buttonBarMyAcc =buildButtonBar();
        
		settingsLayout.addComponent(emailLayout);
		settingsLayout.addComponent(buttonBarMyAcc);
		settingsLayout.setComponentAlignment(buttonBarMyAcc, Alignment.BOTTOM_RIGHT);
		
		emailNotifications = new EmailNotifications();
		

		
		schedulerLayout = emailNotifications.buildEmailNotificationsLayout();
		emailNotifications.getUserNotificationRecord(App.getApp().getUsers().getUser(1L));

		schedulerLayout.addComponent(new Label("Default form of report about scheduled pipeline execution (may be overriden in the particular schedulled event): "),0);

        HorizontalLayout buttonBarSch= new HorizontalLayout();
        buttonBarSch =buildButtonBar();
        schedulerLayout.addComponent(buttonBarSch);
        schedulerLayout.setComponentAlignment(buttonBarSch, Alignment.BOTTOM_RIGHT);
        schedulerLayout.setStyleName("settings");
		
		
		mainLayout.addComponent(tabsLayout);
		mainLayout.addComponent(settingsLayout);

		this.setContent(mainLayout);
		setSizeUndefined();
		
	}
	
	private HorizontalLayout buildButtonBar(){
		
		//Layout with buttons Save and Cancel
		buttonBar = new HorizontalLayout();
		buttonBar.setStyleName("dpuDetailButtonBar");
		buttonBar.setMargin(new MarginInfo(true, false, false, false));

		Button saveButton = new Button("Save");
		saveButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				emailNotifications.shEmail.saveEditedTexts();    
				email.saveEditedTexts();
				
					try {
						email.textFieldEmail.validate();
						if(emailNotifications.shEmailLayout.isEnabled()){
							emailNotifications.shEmail.textFieldEmail.validate();
						}
					} catch (Validator.InvalidValueException e) {
						Notification.show("Failed to save settings. Reason:", e.getMessage(), Notification.Type.ERROR_MESSAGE);
						return;
					}
						
				User user = App.getApp().getUsers().getUser(1L);
				

				List<String> emailStr = email.griddata;
				int fl=0;
				for (String mail:emailStr){
					if(mail!="" && fl==0){
					EmailAddress e = new EmailAddress(mail);
					user.setEmail(e);
					fl++;
					}
				}
				
				
				
				
				UserNotificationRecord notification = user.getNotification();
				if(notification!=null){
					
					emailNotifications.setUserNotificatonRecord(notification);
					user.setNotification(notification);
				}
				else{
					
					UserNotificationRecord userNotifcationRecord = emailNotifications.setUserNotificatonRecord();
					user.setNotification(userNotifcationRecord);
				}

				App.getApp().getUsers().save(user);
				
			}
		});
		buttonBar.addComponent(saveButton);

		Button cancelButton = new Button("Cancel", new Button.ClickListener() {

			/**
			 * Closes DPU Template creation window
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(Button.ClickEvent event) {
			close();

			}
		});
		buttonBar.addComponent(cancelButton);
		return buttonBar;
		
	}
	

}



