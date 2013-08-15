package cz.cuni.xrg.intlib.frontend.gui.views;
import com.vaadin.data.Validator;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import cz.cuni.xrg.intlib.commons.app.scheduling.UserNotificationRecord;
import cz.cuni.xrg.intlib.commons.app.user.User;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.gui.ViewComponent;
import cz.cuni.xrg.intlib.frontend.gui.components.EmailComponent;
import cz.cuni.xrg.intlib.frontend.gui.components.EmailNotifications;
import cz.cuni.xrg.intlib.frontend.gui.components.UsersList;

/**
 * GUI for Settings page which opens from the main menu. 
 * For User role it contains Email notifications form.
 * For Administrator role it contains extra functionality: Users list,
 * Prune execution records, Release locked pipelines
 * 
 * 
 * @author Maria Kukhar
 *
 */
public class Settings extends ViewComponent {

	private static final long serialVersionUID = 1L;
	private GridLayout mainLayout;
	private VerticalLayout accountLayout;
	private VerticalLayout notificationsLayout;
	private VerticalLayout usersLayout;
	private VerticalLayout recordsLayout;
	private VerticalLayout pipelinesLayout;
	
	private VerticalLayout tabsLayout;
	private Button notificationsButton;
	private Button accountButton;
	private Button usersButton;
	private Button recordsButton;
	private Button pipelinesButton;
	private Button shownTab = null;
	private UsersList usersList;
	private HorizontalLayout buttonBar;
	private EmailComponent email;
	private EmailNotifications emailNotifications;
	public GridLayout emailLayout;


	public Settings() { }

	private GridLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new GridLayout(2,1);
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);

		
		// top-level component properties
		setWidth("100%");
		setHeight("100%");
		
        tabsLayout = new VerticalLayout();

        tabsLayout.setWidth("100%");
        tabsLayout.setImmediate(true);


        accountLayout = buildMyAccountLayout();
        
        notificationsLayout = new VerticalLayout();
        notificationsLayout.setWidth("100%");
        notificationsLayout.setHeight("100%");
        emailNotifications = new EmailNotifications();
        emailNotifications.parentComponentUs=this; 
        notificationsLayout= emailNotifications.buildEmailNotificationsLayout();
        emailNotifications.getUserNotificationRecord(App.getApp().getUsers().getUser(1L));
        
        notificationsLayout.setStyleName("settings");
        HorizontalLayout buttonBarNotify= new HorizontalLayout();
        buttonBarNotify = buildButtonBar();
                notificationsLayout.addComponent(buttonBarNotify);


        
        usersLayout = new VerticalLayout();
        usersLayout.setImmediate(true);
        usersLayout.setWidth("100%");
        usersLayout.setHeight("100%");
        usersList = new UsersList();
        usersLayout = usersList.buildUsersListLayout();
        usersLayout.setStyleName("settings");
        
        
        
        recordsLayout = new VerticalLayout();
        recordsLayout.setMargin(true);
        recordsLayout.setSpacing(true);
        recordsLayout.setImmediate(true);
        recordsLayout.setStyleName("settings");
        recordsLayout.setWidth("100%");
        recordsLayout.addComponent(new Label("Records"));
        
        
        pipelinesLayout = new VerticalLayout();
        pipelinesLayout.setMargin(true);
        pipelinesLayout.setSpacing(true);
        pipelinesLayout.setImmediate(true);
        pipelinesLayout.setStyleName("settings");
        pipelinesLayout.setWidth("100%");
        pipelinesLayout.addComponent(new Label("Pipelines"));
        
        
        accountButton = new NativeButton("My account");
        accountButton.setHeight("40px");
        accountButton.setWidth("170px");
        accountButton.setStyleName("selectedtab");
        accountButton.addClickListener(new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				shownTab=accountButton;
				shownTab.setStyleName("selectedtab");
				notificationsButton.setStyleName("multiline");
				usersButton.setStyleName("multiline");
				recordsButton.setStyleName("multiline");
				pipelinesButton.setStyleName("multiline");

				mainLayout.removeComponent(1,0);
				mainLayout.addComponent(accountLayout,1,0);
				mainLayout.setColumnExpandRatio(1, 0.85f);
				
			}
		});
        tabsLayout.addComponent(accountButton);
        tabsLayout.setComponentAlignment(accountButton, Alignment.TOP_RIGHT);
        
    
        notificationsButton = new NativeButton("Scheduler notifications");
        notificationsButton.setHeight("40px");
        notificationsButton.setWidth("170px");
        notificationsButton.setStyleName("multiline");
        notificationsButton.addClickListener(new ClickListener() {
	
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				shownTab=notificationsButton;
				shownTab.setStyleName("selectedtab");
				accountButton.setStyleName("multiline");
				usersButton.setStyleName("multiline");
				recordsButton.setStyleName("multiline");
				pipelinesButton.setStyleName("multiline");
				
				mainLayout.removeComponent(1,0);
				mainLayout.addComponent(notificationsLayout,1,0);
				mainLayout.setColumnExpandRatio(1, 0.85f);

				
			}
		});
        tabsLayout.addComponent(notificationsButton);
        tabsLayout.setComponentAlignment(notificationsButton, Alignment.TOP_RIGHT);
   
        
        usersButton = new NativeButton("Users");
        usersButton.setHeight("40px");
        usersButton.setWidth("170px");
        usersButton.setStyleName("multiline");
        usersButton.addClickListener(new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				shownTab=usersButton;
				shownTab.setStyleName("selectedtab");
				accountButton.setStyleName("multiline");
				recordsButton.setStyleName("multiline");
				pipelinesButton.setStyleName("multiline");
				notificationsButton.setStyleName("multiline");

				mainLayout.removeComponent(1,0);
				mainLayout.addComponent(usersLayout,1,0);
				mainLayout.setColumnExpandRatio(1, 0.85f);

				
			}
		});
        tabsLayout.addComponent(usersButton);
        tabsLayout.setComponentAlignment(usersButton, Alignment.TOP_RIGHT);
        
        recordsButton = new NativeButton("Prune execution records");
        recordsButton.setHeight("40px");
        recordsButton.setWidth("170px");
        recordsButton.setStyleName("multiline");
        recordsButton.addClickListener(new ClickListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				shownTab=recordsButton;
				shownTab.setStyleName("selectedtab");
				usersButton.setStyleName("multiline");
				pipelinesButton.setStyleName("multiline");
				accountButton.setStyleName("multiline");
				notificationsButton.setStyleName("multiline");
				

				mainLayout.removeComponent(1,0);
				mainLayout.addComponent(recordsLayout,1,0);
				mainLayout.setColumnExpandRatio(1, 0.85f);

				
			}
		});
        tabsLayout.addComponent(recordsButton);
        tabsLayout.setComponentAlignment(recordsButton, Alignment.TOP_RIGHT);
        
        pipelinesButton = new NativeButton("Release locked pipelines");
        pipelinesButton.setHeight("40px");
        pipelinesButton.setWidth("170px");
        pipelinesButton.setStyleName("multiline");
        pipelinesButton.addClickListener(new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				shownTab=pipelinesButton;
				shownTab.setStyleName("selectedtab");
				usersButton.setStyleName("multiline");
				recordsButton.setStyleName("multiline");
				accountButton.setStyleName("multiline");
				notificationsButton.setStyleName("multiline");
				
				mainLayout.removeComponent(1,0);
				mainLayout.addComponent(pipelinesLayout,1,0);
				mainLayout.setColumnExpandRatio(1, 0.85f);

				
			}
		});
        tabsLayout.addComponent(pipelinesButton);
        tabsLayout.setComponentAlignment(pipelinesButton, Alignment.TOP_RIGHT);


		
		
		mainLayout.addComponent(tabsLayout,0,0);
		mainLayout.addComponent(accountLayout,1,0);
		mainLayout.setColumnExpandRatio(0, 0.15f);
		mainLayout.setColumnExpandRatio(1, 0.85f);
		
		return mainLayout;
	}
	
	private VerticalLayout buildMyAccountLayout() {
		
        accountLayout = new VerticalLayout();
        accountLayout.setMargin(true);
        accountLayout.setSpacing(true);
//        accountLayout.setWidth("100%");
        accountLayout.setHeight("100%");
        accountLayout.setImmediate(true);
        accountLayout.setStyleName("settings");

        email = new EmailComponent();
        emailLayout = new GridLayout();
        
        emailLayout = email.initializeEmailList();
    
        
        User user = App.getApp().getUsers().getUser(1L);
        email.getUserEmailNotification(user);
        
        HorizontalLayout buttonBarMyAcc= new HorizontalLayout();
        buttonBarMyAcc = buildButtonBar();
        
        accountLayout.addComponent(emailLayout);
        accountLayout.addComponent(buttonBarMyAcc);
//       accountLayout.setComponentAlignment(buttonBarMyAcc, Alignment.BOTTOM_RIGHT);
		
		return accountLayout;
	}

	private HorizontalLayout buildButtonBar(){
		
		//Layout with buttons Save and Cancel
		buttonBar = new HorizontalLayout();
		buttonBar.setWidth("380px");
		buttonBar.setStyleName("dpuDetailButtonBar");
		buttonBar.setMargin(new MarginInfo(true, false, false, false));

		Button saveButton = new Button("Save");
		saveButton.addClickListener(new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				
//				emailNotifications.shEmail.saveEditedTexts();    
				email.saveEditedTexts();
				
					try {
						
						email.textFieldEmail.validate();

					} catch (Validator.InvalidValueException e) {
						Notification.show("Failed to save settings. Reason:", e.getMessage(), Notification.Type.ERROR_MESSAGE);
						return;
					}
						
				User user = App.getApp().getUsers().getUser(1L);
				UserNotificationRecord notification = user.getNotification();
				if(notification!=null){
					
					email.setUserEmailNotification(notification);
					emailNotifications.setUserNotificatonRecord(notification);
					user.setNotification(notification);
				}
				else{
		
					UserNotificationRecord userNotificationRecord = new UserNotificationRecord();
					userNotificationRecord.setUser(App.getApp().getUsers().getUser(1L));
					emailNotifications.setUserNotificatonRecord(userNotificationRecord);
					email.setUserEmailNotification(userNotificationRecord);
					user.setNotification(userNotificationRecord);
				}


				App.getApp().getUsers().save(user);
				
			}
		});
		buttonBar.addComponent(saveButton);
		buttonBar.setComponentAlignment(saveButton, Alignment.BOTTOM_RIGHT);



		return buttonBar;
		
	}
	

	@Override
	public void enter(ViewChangeEvent event) {
		buildMainLayout();
		setCompositionRoot(mainLayout);		
	}

}
