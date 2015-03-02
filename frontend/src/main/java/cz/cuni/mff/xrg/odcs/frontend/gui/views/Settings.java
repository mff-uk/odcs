package cz.cuni.mff.xrg.odcs.frontend.gui.views;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import cz.cuni.mff.xrg.odcs.commons.app.ScheduledJobsPriority;
import cz.cuni.mff.xrg.odcs.commons.app.auth.AuthenticationContext;
import cz.cuni.mff.xrg.odcs.commons.app.conf.ConfigProperty;
import cz.cuni.mff.xrg.odcs.commons.app.constants.LenghtLimits;
import cz.cuni.mff.xrg.odcs.commons.app.facade.RuntimePropertiesFacade;
import cz.cuni.mff.xrg.odcs.commons.app.facade.UserFacade;
import cz.cuni.mff.xrg.odcs.commons.app.properties.RuntimeProperty;
import cz.cuni.mff.xrg.odcs.commons.app.user.EmailAddress;
import cz.cuni.mff.xrg.odcs.commons.app.user.NotificationRecordType;
import cz.cuni.mff.xrg.odcs.commons.app.user.Role;
import cz.cuni.mff.xrg.odcs.commons.app.user.User;
import cz.cuni.mff.xrg.odcs.commons.app.user.UserNotificationRecord;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.MaxLengthValidator;
import cz.cuni.mff.xrg.odcs.frontend.gui.ViewComponent;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.EmailComponent;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.EmailNotifications;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.ManipulableListComponentProvider;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.ManipulableListManager;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.UsersList;
import cz.cuni.mff.xrg.odcs.frontend.i18n.Messages;
import cz.cuni.mff.xrg.odcs.frontend.navigation.Address;

/**
 * GUI for Settings page which opens from the main menu. For User role it
 * contains Email notifications form. For Administrator role it contains extra
 * functionality: Users list, Prune execution records, Release locked pipelines
 * 
 * @author Maria Kukhar
 */
@org.springframework.stereotype.Component
@Scope("session")
@Address(url = "Administrator")
public class Settings extends ViewComponent implements PostLogoutCleaner {

    private static final long serialVersionUID = 1L;

    private GridLayout mainLayout;

    private VerticalLayout accountLayout;

    private VerticalLayout notificationsLayout;

    private VerticalLayout usersLayout;

    private VerticalLayout pipelinesLayout;

    private VerticalLayout tabsLayout;

    private VerticalLayout runtimePropsLayout;

    private Button notificationsButton;

    private Button accountButton;

    private Button usersButton;

    private Button pipelinesButton;

    private Button runtimePropsButton;

    private Button shownTab = null;

    @Autowired
    private UsersList usersList;

    /**
     * Buttons layout for my account.
     */
    public HorizontalLayout buttonMyAccountBar;

    /**
     * Buttons layout for notifications.
     */
    public HorizontalLayout buttonNotificationBar;

    private EmailComponent email;

    private EmailNotifications emailNotifications;

    private GridLayout emailLayout;

    private TextField rows;

    @Autowired
    private AuthenticationContext authCtx;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private RuntimePropertiesFacade runtimePropertiesFacade;

    /**
     * Currently logged in user.
     */
    private User loggedUser;

    private boolean isMainLayoutInitialized = false;

    private ManipulableListManager runtimePropsManager;

    private List<RuntimeProperty> runtimeProperties;

    /**
     * The constructor should first build the main layout, set the composition
     * root and then do any custom initialisation.
     * The constructor will not be automatically regenerated by the visual
     * editor.
     */
    public Settings() {
    }

    @Override
    public boolean isModified() {

        if (shownTab.equals(notificationsButton)) {
            return areNotificationsModified();
        } else if (shownTab.equals(accountButton)) {
            return isMyAccountModified();
        }
        return false;
    }

    @Override
    public boolean saveChanges() {
        if (shownTab.equals(notificationsButton) || shownTab.equals(
                accountButton)) {
            return saveEmailNotifications();
        }
        return true;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        loggedUser = authCtx.getUser();
        if (!isMainLayoutInitialized) {
            buildMainLayout();
            isMainLayoutInitialized = true;
        }
        setCompositionRoot(mainLayout);
    }

    private GridLayout buildMainLayout() {
        // common part: create layout
        mainLayout = new GridLayout(2, 1);
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);

        // top-level component properties
        setWidth("100%");

        //layout with tabs
        tabsLayout = new VerticalLayout();
        tabsLayout.setWidth("250px");
        tabsLayout.setImmediate(true);

        //layout with my account components
        accountLayout = buildMyAccountLayout();

        emailNotifications = new EmailNotifications();
        emailNotifications.parentComponentUs = this;

        //layout with schedule notifications components
        notificationsLayout = buildNotificationsLayout();

        //layout with user list and user creations components
        usersLayout = new VerticalLayout();
        usersLayout.setImmediate(true);
        usersLayout.setWidth("100%");
        usersLayout.setHeight("100%");
        usersLayout = usersList.buildUsersListLayout();
        usersLayout.setStyleName("settings");

        //layout for Delete debug resources
        pipelinesLayout = new VerticalLayout();
        pipelinesLayout.setMargin(true);
        pipelinesLayout.setSpacing(true);
        pipelinesLayout.setImmediate(true);
        pipelinesLayout.setStyleName("settings");
        pipelinesLayout.setWidth("100%");

        //layout for Namespace Prefixes
//		prefixesLayout = new VerticalLayout();
//		prefixesLayout.setImmediate(true);
//		prefixesLayout.setWidth("100%");
//		prefixesLayout.setHeight("100%");
//		prefixesLayout = prefixesList.buildNamespacePrefixesLayout();
//		prefixesLayout.setStyleName("settings");

        //My account tab
        accountButton = new NativeButton(Messages.getString("Settings.my.account"));
        accountButton.setHeight("40px");
        accountButton.setWidth("250px");
        accountButton.setStyleName("selectedtab");
        accountButton.addClickListener(new ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                //if before click was pushed Schedule notification tab
                if (shownTab.equals(notificationsButton)) {
                    notificationSaveConfirmation(accountButton, accountLayout);
                } else {
                    buttonPush(accountButton, accountLayout);
                }

            }
        });
        tabsLayout.addComponent(accountButton);
        tabsLayout.setComponentAlignment(accountButton, Alignment.TOP_RIGHT);

        //Scheduler notifications tab
        notificationsButton = new NativeButton(Messages.getString("Settings.scheduler.notifications"));
        notificationsButton.setHeight("40px");
        notificationsButton.setWidth("250px");
        notificationsButton.setStyleName("multiline");
        notificationsButton.addClickListener(new ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                //if before click was pushed My account tab
                if (shownTab.equals(accountButton)) {
                    myAccountSaveConfirmation(notificationsButton,
                            notificationsLayout);
                } else {
                    buttonPush(notificationsButton, notificationsLayout);
                }

            }
        });
        tabsLayout.addComponent(notificationsButton);
        tabsLayout.setComponentAlignment(notificationsButton,
                Alignment.TOP_RIGHT);

        //Manage users tab
        usersButton = new NativeButton(Messages.getString("Settings.manage.users"));
        usersButton.setHeight("40px");
        usersButton.setWidth("250px");
        usersButton.setStyleName("multiline");
        usersButton.setVisible(loggedUser.getRoles().contains(Role.ROLE_ADMIN));
        usersButton.addClickListener(new ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                //if before click was pushed My account tab
                if (shownTab.equals(accountButton)) {
                    myAccountSaveConfirmation(usersButton, usersLayout);
                } else {
                    //if before click was pushed Schedule notification tab
                    if (shownTab.equals(notificationsButton)) {
                        notificationSaveConfirmation(usersButton, usersLayout);
                    } else {
                        buttonPush(usersButton, usersLayout);
                    }
                }
            }
        });
        tabsLayout.addComponent(usersButton);
        tabsLayout.setComponentAlignment(usersButton, Alignment.TOP_RIGHT);

        //Delete debug resources tab
        pipelinesButton = new NativeButton(Messages.getString("Settings.delete.resources"));
        pipelinesButton.setHeight("40px");
        pipelinesButton.setWidth("250px");
        pipelinesButton.setStyleName("multiline");
        pipelinesButton.setVisible(loggedUser.getRoles().contains(Role.ROLE_ADMIN));
        pipelinesButton.addClickListener(new ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                //if before click was pushed My account tab
                if (shownTab.equals(accountButton)) {
                    myAccountSaveConfirmation(pipelinesButton, pipelinesLayout);
                } else {
                    //if before click was pushed Schedule notification tab
                    if (shownTab.equals(notificationsButton)) {
                        notificationSaveConfirmation(pipelinesButton,
                                pipelinesLayout);
                    } else {
                        buttonPush(pipelinesButton, pipelinesLayout);
                    }

                }
            }
        });
        tabsLayout.addComponent(pipelinesButton);
        tabsLayout.setComponentAlignment(pipelinesButton, Alignment.TOP_RIGHT);

        //Namespace prefixes tab
//		prefixesButton = new NativeButton("Namespace Prefixes");
//		prefixesButton.setHeight("40px");
//		prefixesButton.setWidth("170px");
//		prefixesButton.setStyleName("multiline");
//		prefixesButton.setVisible(loggedUser.getRoles().contains(Role.ROLE_ADMIN));
//		prefixesButton.addClickListener(new ClickListener() {
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public void buttonClick(ClickEvent event) {
//				if (shownTab.equals(accountButton)) {
//					myAccountSaveConfirmation(prefixesButton, prefixesLayout);
//				} else {
//					if (shownTab.equals(notificationsButton)) {
//						notificationSaveConfirmation(prefixesButton,
//								prefixesLayout);
//					} else {
//						buttonPush(prefixesButton, prefixesLayout);
//					}
//				}
//			}
//		});

        //tabsLayout.addComponent(prefixesButton);
        //tabsLayout.setComponentAlignment(prefixesButton, Alignment.TOP_RIGHT);

        // runtime limits
        runtimePropsLayout = createRuntimePropsLayout();

        runtimePropsButton = new NativeButton(Messages.getString("Settings.runtime.properties"));
        runtimePropsButton.setHeight("40px");
        runtimePropsButton.setWidth("250px");
        runtimePropsButton.setStyleName("multiline");
        runtimePropsButton.setVisible(loggedUser.getRoles().contains(Role.ROLE_ADMIN));
        runtimePropsButton.addClickListener(new ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                if (shownTab.equals(accountButton)) {
                    myAccountSaveConfirmation(runtimePropsButton, runtimePropsLayout);
                } else {
                    //if before click was pushed Schedule notification tab
                    if (shownTab.equals(notificationsButton)) {
                        notificationSaveConfirmation(runtimePropsButton,
                                runtimePropsLayout);
                    } else {
                        buttonPush(runtimePropsButton, runtimePropsLayout);
                    }
                    refreshRuntimeProperties();
                }
            }
        });
        tabsLayout.addComponent(runtimePropsButton);
        tabsLayout.setComponentAlignment(runtimePropsButton, Alignment.TOP_RIGHT);

        shownTab = accountButton;
        mainLayout.addComponent(tabsLayout, 0, 0);
        mainLayout.addComponent(accountLayout, 1, 0);
        //mainLayout.setColumnExpandRatio(0, 0.15f);
        mainLayout.setColumnExpandRatio(1, 1f);

        return mainLayout;
    }

    private VerticalLayout createRuntimePropsLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setSpacing(true);
        layout.setMargin(false);

        HorizontalLayout buttonBar = new HorizontalLayout();
        buttonBar.setWidth(380, Unit.PIXELS);
        buttonBar.setMargin(new MarginInfo(true, false, false, false));
        final Button saveButton = new Button(Messages.getString("Settings.runtime.properties.save"));
        saveButton.setEnabled(false);
        saveButton.addClickListener(new ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                List<Component> componentList = runtimePropsManager.getComponentList();

                // prepare props toSave
                Map<String, RuntimeProperty> toSave = new HashMap<String, RuntimeProperty>();
                Set<String> notChanged = new HashSet<String>();

                try {
                    for (Component property : componentList) {
                        String name = validateAndGetValue(property, 0);
                        String value = validateAndGetValue(property, 1);

                        if (toSave.containsKey(name) || notChanged.contains(name)) {
                            Notification.show(Messages.getString("Settings.runtime.properties.save.failed"), Messages.getString("Settings.runtime.properties.save.failed.description") + name, Notification.Type.ERROR_MESSAGE);
                            return;
                        }

                        RuntimeProperty prop = getRuntimePropertyByName(name);

                        if (prop == null) { // doesnt exist, have to make new one
                            prop = new RuntimeProperty();
                            prop.setName(name);
                            prop.setValue(value);
                            toSave.put(name, prop);
                        } else if (!prop.getValue().equals(value)) { // value changed 
                            prop.setValue(value);
                            toSave.put(name, prop);
                        } else { // else value didnt change
                            notChanged.add(name);
                        }
                    }
                } catch (InvalidValueException e) {
                    Notification.show(Messages.getString("Settings.runtime.properties.invalid.value"), e.getMessage(), Notification.Type.ERROR_MESSAGE);
                    return;
                }

                // remove missing, user removed props
                for (RuntimeProperty prop : runtimeProperties) {
                    if (!notChanged.contains(prop.getName())
                            && !toSave.containsKey(prop.getName())) {
                        runtimePropertiesFacade.delete(prop);
                    }
                }

                for (RuntimeProperty runtimeProperty : toSave.values()) {
                    runtimePropertiesFacade.save(runtimeProperty);
                }

                Notification.show(Messages.getString("Settings.runtime.properties.save.successfull"), Messages.getString("Settings.runtime.properties.save.successfull.description"), Notification.Type.HUMANIZED_MESSAGE);
                saveButton.setEnabled(false);
                refreshRuntimeProperties();
            }
        });
        buttonBar.addComponent(saveButton);
        buttonBar.setComponentAlignment(saveButton, Alignment.BOTTOM_RIGHT);

        runtimePropsManager = new ManipulableListManager(new ManipulableListComponentProvider() {

            @Override
            public Component createNewComponent(String[] values) {
                String name = values[0] == null ? "" : values[0].trim();
                String value = values[1] == null ? "" : values[1].trim();

                GridLayout oneLine = new GridLayout(2, 1);

                TextChangeListener changeListener = new TextChangeListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void textChange(TextChangeEvent event) {
                        saveButton.setEnabled(true);
                    }
                };

                TextField text = new TextField();
                text.addTextChangeListener(changeListener);
                text.setRequired(true);
                text.setRequiredError(Messages.getString("Settings.name.required"));
                text.setValue(name);
                text.setInputPrompt(Messages.getString("Settings.prompt.name"));
                text.setWidth(250, Unit.PIXELS);
                text.addValidator(new MaxLengthValidator(LenghtLimits.RUNTIME_PROPERTY_NAME_AND_VALUE));
                oneLine.addComponent(text, 0, 0);

                if (name.equals(ConfigProperty.FRONTEND_RUN_NOW_PIPELINE_PRIORITY.toString())) {
                    ComboBox priorityComboBox = new ComboBox();
                    priorityComboBox.setNullSelectionAllowed(false);
                    priorityComboBox.setTextInputAllowed(false);
                    priorityComboBox.setImmediate(true);
                    priorityComboBox.setWidth(250, Unit.PIXELS);
                    // Add some items
                    for (ScheduledJobsPriority job : ScheduledJobsPriority.values()) {
                        priorityComboBox.addItem(job);
                    }

                    long val = ScheduledJobsPriority.HIGHEST.getValue();
                    try {
                        val = Long.parseLong(value);
                    } catch (NumberFormatException e) {
                        // leaving the default value
                    }

                    priorityComboBox.setValue(ScheduledJobsPriority.getForValue(val));

                    priorityComboBox.addValueChangeListener(new ValueChangeListener() {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public void valueChange(ValueChangeEvent event) {
                            saveButton.setEnabled(true);
                        }
                    });

                    oneLine.addComponent(priorityComboBox, 1, 0);
                } else {
                    text = new TextField();
                    text.addTextChangeListener(changeListener);
                    text.setValue(value);
                    text.setInputPrompt(Messages.getString("Settings.prompt.value"));
                    text.setWidth(250, Unit.PIXELS);
                    text.addValidator(new MaxLengthValidator(LenghtLimits.RUNTIME_PROPERTY_NAME_AND_VALUE));
                    oneLine.addComponent(text, 1, 0);
                }

                return oneLine;
            }

            @Override
            public Component createNewComponent() {
                return createNewComponent(new String[] { "", "" });
            }
        });
        runtimePropsManager.setChangeListener(new Listener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void componentEvent(Event event) {
                saveButton.setEnabled(true);
            }
        });
        Layout runtimePropsList = runtimePropsManager.initList(null);

        // fill data
        refreshRuntimeProperties();
        layout.addComponent(runtimePropsList);
        layout.setExpandRatio(runtimePropsList, 1);

        layout.addComponent(buttonBar);

        return layout;
    }

    private RuntimeProperty getRuntimePropertyByName(String name) {
        for (RuntimeProperty prop : runtimeProperties) {
            if (prop.getName().equals(name)) {
                return prop;
            }
        }
        return null;
    }

    /**
     * Sets data for runtime properties, at the same time brings changes
     * made by other users with admin role
     */
    private void refreshRuntimeProperties() {
        runtimeProperties = runtimePropertiesFacade.getAllRuntimeProperties();
        runtimePropsManager.clearComponents();
        for (RuntimeProperty runtimeProperty : runtimeProperties) {
            runtimePropsManager.addComponent(new String[] {
                    runtimeProperty.getName(),
                    runtimeProperty.getValue() });
        }
        runtimePropsManager.refreshData();
    }

    /**
     * Validates and return the TextField.value
     * 
     * @param layout
     *            GridLayout
     * @param column
     *            column number of TextField in grid layout to return value
     * @return
     * @throws InvalidValueException
     */
    private String validateAndGetValue(Component layout, int column) throws InvalidValueException {

        Component field = ((GridLayout) layout).getComponent(column, 0);
        if (field instanceof TextField) {
            TextField textField = (TextField) field;
            textField.validate();
            return textField.getValue().trim();
        } else if (field instanceof ComboBox) {
            ComboBox runNowProperty = (ComboBox) field;
            ScheduledJobsPriority job = (ScheduledJobsPriority) runNowProperty.getValue();
            // doesnt need to be validated
            return String.valueOf(job.getValue());
        }
        return "";
    }

    /**
     * Building Schedule notifications layout. Appear after pushing Schedule
     * notifications tab
     * 
     * @return notificationsLayout Layout with components of Schedule
     *         notifications.
     */
    private VerticalLayout buildNotificationsLayout() {

        notificationsLayout = new VerticalLayout();
        notificationsLayout.setWidth("100%");
        notificationsLayout.setHeight("100%");

        notificationsLayout = emailNotifications.buildEmailNotificationsLayout();
        emailNotifications.getUserNotificationRecord(loggedUser);
        notificationsLayout.setStyleName("settings");

        HorizontalLayout buttonBarNotify = buildButtonNotificationBar();
        notificationsLayout.addComponent(buttonBarNotify);

        notificationsLayout.addComponent(new Label(
                Messages.getString("Settings.default.form")), 0);
        notificationsLayout.addComponent(new Label(
                Messages.getString("Settings.default.form.detail")), 1);

        return notificationsLayout;
    }

    /**
     * Building My account layout. Appear after pushing My account tab
     * 
     * @return accountLayout Layout with components of My account.
     */
    private VerticalLayout buildMyAccountLayout() {

        accountLayout = new VerticalLayout();
        accountLayout.setMargin(true);
        accountLayout.setSpacing(true);
        accountLayout.setHeight("100%");
        accountLayout.setImmediate(true);
        accountLayout.setStyleName("settings");

        email = new EmailComponent();
        emailLayout = new GridLayout();
        emailLayout.setImmediate(true);

        emailLayout = email.initializeEmailList();

        email.getUserEmailNotification(loggedUser);

        email.parentComponentAccount = this;

        HorizontalLayout buttonBarMyAcc = buildButtonMyAccountBar();

        accountLayout.addComponent(emailLayout);

        Label rowsLabel = new Label(Messages.getString("Settings.table.row.count"));
        rows = new TextField();

        Integer tableRows = loggedUser.getTableRows() != null ? loggedUser
                .getTableRows() : 20;
        rows.setPropertyDataSource(new ObjectProperty<>(tableRows));
        rows.addValidator(new IntegerRangeValidator(
                Messages.getString("Settings.range.validation"),
                5, 100));
        rows.setBuffered(true);
        rows.setImmediate(true);
        rows.addTextChangeListener(new FieldEvents.TextChangeListener() {
            /**
			 *
			 */
            private static final long serialVersionUID = 1L;

            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
                buttonMyAccountBar.setEnabled(true);
            }
        });
        accountLayout.addComponent(rowsLabel);
        accountLayout.addComponent(rows);

        accountLayout.addComponent(buttonBarMyAcc);
        accountLayout.addComponent(new Label(Messages.getString("Settings.email.notifications")), 0);

        return accountLayout;
    }

    /**
     * Building layout with button Save for saving My account tab
     * 
     * @return buttonBar Layout with button
     */
    private HorizontalLayout buildButtonMyAccountBar() {

        //Layout with buttons Save and Cancel
        buttonMyAccountBar = new HorizontalLayout();
        buttonMyAccountBar.setWidth("380px");
        buttonMyAccountBar.setStyleName("dpuDetailButtonBar");
        buttonMyAccountBar.setMargin(new MarginInfo(true, false, false, false));
        buttonMyAccountBar.setEnabled(false);

        Button saveButton = new Button(Messages.getString("Settings.myAccount.save"));
        saveButton.addClickListener(new ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {

                email.saveEditedTexts();
                saveEmailNotifications();

            }
        });
        buttonMyAccountBar.addComponent(saveButton);
        buttonMyAccountBar.setComponentAlignment(saveButton,
                Alignment.BOTTOM_RIGHT);

        return buttonMyAccountBar;

    }

    /**
     * Building layout with button Save for saving notifications
     * 
     * @return buttonBar Layout with button
     */
    private HorizontalLayout buildButtonNotificationBar() {

        //Layout with buttons Save and Cancel
        buttonNotificationBar = new HorizontalLayout();
        buttonNotificationBar.setWidth("380px");
        buttonNotificationBar.setStyleName("dpuDetailButtonBar");
        buttonNotificationBar.setMargin(
                new MarginInfo(true, false, false, false));
        buttonNotificationBar.setEnabled(false);

        Button saveButton = new Button(Messages.getString("Settings.notifications.save"));
        saveButton.addClickListener(new ClickListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {

                email.saveEditedTexts();
                saveEmailNotifications();

            }
        });
        buttonNotificationBar.addComponent(saveButton);
        buttonNotificationBar.setComponentAlignment(saveButton,
                Alignment.BOTTOM_RIGHT);

        return buttonNotificationBar;

    }

    /**
     * Showing active tab.
     * 
     * @param pressedButton
     *            Tab that was pressed.
     * @param layoutShow
     *            Layaut will be shown.
     */
    private void buttonPush(Button pressedButton, VerticalLayout layoutShow) {

        accountButton.setStyleName("multiline");
        usersButton.setStyleName("multiline");
        pipelinesButton.setStyleName("multiline");
        //prefixesButton.setStyleName("multiline");
        notificationsButton.setStyleName("multiline");
        runtimePropsButton.setStyleName("multiline");

        shownTab = pressedButton;
        shownTab.setStyleName("selectedtab");

        mainLayout.removeComponent(1, 0);
        mainLayout.addComponent(layoutShow, 1, 0);
        mainLayout.setColumnExpandRatio(1, 0.85f);
    }

    /**
     * Saving changes that relating to Schedule Notification.
     */
    private boolean saveEmailNotifications() {

        if (!emailValidationText().equals("")) {

            try {
                rows.validate();
            } catch (Validator.InvalidValueException ex) {
                Notification.show(Messages.getString("Settings.schedule.fail"), emailValidationText() + Messages.getString("Settings.schedule.fail.description", rows.getValue()), Notification.Type.ERROR_MESSAGE);
                return false;
            }

            Notification.show(Messages.getString("Settings.failed.to.save"),
                    emailValidationText(), Notification.Type.ERROR_MESSAGE);
            return false;
        }

        try {
            rows.validate();
        } catch (Validator.InvalidValueException ex) {
            Notification.show(Messages.getString("Settings.failed.to.save.reason"), Messages.getString("Settings.failed.to.save.description", rows.getValue()), Notification.Type.ERROR_MESSAGE);
            return false;
        }

        UserNotificationRecord notification = loggedUser.getNotification();
        if (notification != null) {

            email.setUserEmailNotification(notification);
            emailNotifications.setUserNotificatonRecord(notification);
            loggedUser.setNotification(notification);
        } else {

            UserNotificationRecord userNotificationRecord = new UserNotificationRecord();
            userNotificationRecord.setUser(loggedUser);
            emailNotifications.setUserNotificatonRecord(userNotificationRecord);
            email.setUserEmailNotification(userNotificationRecord);
            loggedUser.setNotification(userNotificationRecord);
        }
        loggedUser.setTableRows(Integer.parseInt(rows.getValue()));
        rows.commit();
        userFacade.save(loggedUser);
        Notification.show(Messages.getString("Settings.myAccout.successfull"),
                Notification.Type.HUMANIZED_MESSAGE);
        if (buttonNotificationBar != null)
            buttonNotificationBar.setEnabled(false);
        if (buttonMyAccountBar != null)
            buttonMyAccountBar.setEnabled(false);

        if (shownTab.equals(accountButton)) {
            accountLayout = buildMyAccountLayout();
            mainLayout.removeComponent(1, 0);
            mainLayout.addComponent(accountLayout, 1, 0);
        }
        return true;
    }

    /**
     * Show confirmation window in case if user make some changes in My account
     * tab and push anoter tab. User can save changes or discard. After that
     * will be shown another selected tab. If there was no changes, a
     * confirmation window will not be shown.
     * 
     * @param pressedButton
     *            New tab that was push.
     * @param layoutShow
     *            Layout will be shown after save/discard changes.
     */
    private void myAccountSaveConfirmation(final Button pressedButton,
            final VerticalLayout layoutShow) {
        if (isMyAccountModified()) {

            //open confirmation dialog
            ConfirmDialog.show(UI.getCurrent(), Messages.getString("Settings.unsaved.changes"),
                    Messages.getString("Settings.unsaved.changes.dialog"),
                    Messages.getString("Settings.unsaved.changes.save"), Messages.getString("Settings.unsaved.changes.discard"),
                    new ConfirmDialog.Listener() {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public void onClose(ConfirmDialog cd) {
                            if (cd.isConfirmed()) {
                                saveEmailNotifications();
                                accountLayout = buildMyAccountLayout();
                                buttonPush(pressedButton, layoutShow);
                            } else {
                                accountLayout = buildMyAccountLayout();
                                buttonPush(pressedButton, layoutShow);
                            }
                        }
                    });
        } else {
            accountLayout = buildMyAccountLayout();
            buttonPush(pressedButton, layoutShow);
        }

    }

    /**
     * Show confirmation window in case if user make some changes in Schedule
     * notifications tab and push anoter tab. User can save changes or discard.
     * After that will be shown another selected tab. If there was no changes, a
     * confirmation window will not be shown.
     * 
     * @param pressedButton
     *            New tab that was push.
     * @param layoutShow
     *            Layout will be shown after save/discard changes.
     */
    private void notificationSaveConfirmation(final Button pressedButton,
            final VerticalLayout layoutShow) {
        if (areNotificationsModified()) {
            //open confirmation dialog
            ConfirmDialog.show(UI.getCurrent(), Messages.getString("Settings.notifications.unsaved"),
                    Messages.getString("Settings.notifications.unsaved.dialog"),
                    Messages.getString("Settings.notifications.unsaved.save"), Messages.getString("Settings.notifications.unsaved.discard"),
                    new ConfirmDialog.Listener() {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public void onClose(ConfirmDialog cd) {
                            if (cd.isConfirmed()) {
                                saveEmailNotifications();
                                buttonPush(pressedButton, layoutShow);
                            } else {
                                notificationsLayout = buildNotificationsLayout();
                                buttonPush(pressedButton, layoutShow);
                            }
                        }
                    });
        } else {
            buttonPush(pressedButton, layoutShow);
        }

    }

    private boolean areNotificationsModified() {
        if (loggedUser.getNotification() == null) {
            return true;
        }
        NotificationRecordType aldSuccessEx = loggedUser.getNotification()
                .getTypeSuccess();
        NotificationRecordType aldErrorEx = loggedUser.getNotification()
                .getTypeError();
        UserNotificationRecord newNotification = new UserNotificationRecord();
        emailNotifications.setUserNotificatonRecord(newNotification);
        NotificationRecordType newSuccessEx = newNotification.getTypeSuccess();
        NotificationRecordType newErrorEx = newNotification.getTypeError();
        return !aldSuccessEx.equals(newSuccessEx) || !aldErrorEx.equals(
                newErrorEx);
    }

    private boolean isMyAccountModified() {
        email.saveEditedTexts();

        if (!emailValidationText().equals("")) {
            Notification.show("", emailValidationText(),
                    Notification.Type.ERROR_MESSAGE);
            return true;
        }

        UserNotificationRecord record = loggedUser.getNotification();
        if (record == null) {
            return true;
        }
        Set<EmailAddress> oldEmails = record.getEmails();
        UserNotificationRecord newNotification = new UserNotificationRecord();
        email.setUserEmailNotification(newNotification);
        Set<EmailAddress> newEmails = newNotification.getEmails();
        return !hasTheSameEmails(oldEmails, newEmails) || rows.isModified();
    }

    private boolean hasTheSameEmails(Set<EmailAddress> oldEmails, Set<EmailAddress> newEmails) {
        // i have to do it this way because the new mails dont have id set and the old have it set
        // so the oldEmails.equals(newEmails) method will always return false (dont want to change
        // hashCode and equals method of EmailAdress because they are correct but here it cant be used)
        if (oldEmails.size() != newEmails.size()) {
            return false;
        }
        for (EmailAddress emailAddress : oldEmails) {
            // both email dont have id set, so i can compare them with equals
            if (!newEmails.contains(new EmailAddress(emailAddress.getEmail()))) {
                return false;
            }
        }
        return true;
    }

    private String emailValidationText() {
        String errorText = "";
        String wrongFormat = "";
        boolean notEmpty = false;
        int errorNumber = 0;
        int fieldNumber = 0;
        for (TextField emailField : email.listedEditText) {
            if (!emailField.getValue().trim().isEmpty()) {
                notEmpty = true;
                break;
            }
        }

        if (notEmpty) {
            for (TextField emailField : email.listedEditText) {
                fieldNumber++;
                try {
                    emailField.validate();

                } catch (Validator.InvalidValueException e) {

                    if (e.getMessage().equals("wrong е-mail format")) {
                        if (fieldNumber == 1) {
                            wrongFormat = "\"" + emailField.getValue() + "\"";
                        } else {
                            wrongFormat = wrongFormat + ", " + "\"" + emailField
                                    .getValue() + "\"";
                        }
                        errorNumber++;
                    }
                }
            }
            if (errorNumber == 1) {
                errorText = Messages.getString("Settings.validation.email") + wrongFormat + Messages.getString("Settings.validation.wrong.format");
            }
            if (errorNumber > 1) {
                errorText = Messages.getString("Settings.validation.emails") + wrongFormat + Messages.getString("Settings.validation.emails.wrong.format");
            }
        } else {
            errorText = Messages.getString("Settings.validation.email.minimum");
        }

        return errorText;

    }

    @Override
    public void doAfterLogout() {
        isMainLayoutInitialized = false;
    }
}
