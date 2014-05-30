package cz.cuni.mff.xrg.odcs.frontend.gui.components;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.VerticalLayout;

import cz.cuni.mff.xrg.odcs.commons.app.scheduling.Schedule;
import cz.cuni.mff.xrg.odcs.commons.app.scheduling.ScheduleNotificationRecord;
import cz.cuni.mff.xrg.odcs.commons.app.user.NotificationRecordType;
import cz.cuni.mff.xrg.odcs.commons.app.user.User;
import cz.cuni.mff.xrg.odcs.commons.app.user.UserNotificationRecord;

/**
 * Builds layout with GUI components for settings notifications about scheduled
 * events and their runs. Used in User Settings dialog and in Schedule a
 * pipeline dialog.
 * 
 * @author Maria Kukhar
 */
public class EmailNotifications {

    private int noSuccessful = 0;

    private int noError = 0;

//	public EmailComponent shEmail;
    private OptionGroup errorExec;

    private OptionGroup successfulExec;

    /**
     * Parent component.
     */
    public SchedulePipeline parentComponentSh;

    /**
     * Alternative parent component.
     */
    public cz.cuni.mff.xrg.odcs.frontend.gui.views.Settings parentComponentUs;

    /**
     * Build layout.
     * 
     * @return built layout
     */
    public VerticalLayout buildEmailNotificationsLayout() {

        VerticalLayout emailNotificationsLayout = new VerticalLayout();
        emailNotificationsLayout.setMargin(true);
        emailNotificationsLayout.setSpacing(true);
        emailNotificationsLayout.setImmediate(true);

        GridLayout notifycationLayout = new GridLayout(2, 2);
        notifycationLayout.setSpacing(true);

        notifycationLayout.addComponent(new Label("Successful execution:"), 0, 0);
        successfulExec = new OptionGroup();
        successfulExec.setImmediate(true);
        successfulExec.addItem(NotificationRecordType.INSTANT);
        successfulExec.addItem(NotificationRecordType.DAILY);
        successfulExec.addItem(NotificationRecordType.NO_REPORT);
        successfulExec.setValue(NotificationRecordType.DAILY);
        successfulExec.setItemCaption(NotificationRecordType.INSTANT, "Instant");
        successfulExec.setItemCaption(NotificationRecordType.DAILY, "Daily bulk report (default)");
        successfulExec.setItemCaption(NotificationRecordType.NO_REPORT, "No report");

        successfulExec.addValueChangeListener(new ValueChangeListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (parentComponentUs != null && parentComponentUs.buttonNotificationBar != null) {
                    parentComponentUs.buttonNotificationBar.setEnabled(true);
                }

                if (event.getProperty().getValue().equals(NotificationRecordType.NO_REPORT)) {
                    noSuccessful = 1;
                    if ((noError == 1) && (noSuccessful == 1)) {
                        if (parentComponentSh != null) {
                            parentComponentSh.getEmailLayout().setEnabled(false);
                        }
//						if(parentComponentUs!=null)	
//							parentComponentUs.emailLayout.setEnabled(false);
                    }

                } else {
                    noSuccessful = 0;
                    if (parentComponentSh != null) {
                        parentComponentSh.getEmailLayout().setEnabled(true);
                    }
//					if(parentComponentUs!=null)	
//						parentComponentUs.emailLayout.setEnabled(true);

                }

            }
        });

        notifycationLayout.addComponent(successfulExec, 1, 0);
        emailNotificationsLayout.addComponent(notifycationLayout);

        notifycationLayout.addComponent(new Label("Error in execution:"), 0, 1);
        errorExec = new OptionGroup();
        errorExec.setImmediate(true);
        errorExec.setImmediate(true);
        errorExec.addItem(NotificationRecordType.INSTANT);
        errorExec.addItem(NotificationRecordType.DAILY);
        errorExec.addItem(NotificationRecordType.NO_REPORT);
        errorExec.setValue(NotificationRecordType.INSTANT);
        errorExec.setItemCaption(NotificationRecordType.INSTANT, "Instant (default)");
        errorExec.setItemCaption(NotificationRecordType.DAILY, "Daily bulk report");
        errorExec.setItemCaption(NotificationRecordType.NO_REPORT, "No report");

        errorExec.addValueChangeListener(new ValueChangeListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (parentComponentUs != null && parentComponentUs.buttonNotificationBar != null) {
                    parentComponentUs.buttonNotificationBar.setEnabled(true);
                }

                if (event.getProperty().getValue().equals(NotificationRecordType.NO_REPORT)) {
                    noError = 1;

                    if ((noError == 1) && (noSuccessful == 1)) {
                        if (parentComponentSh != null) {
                            parentComponentSh.getEmailLayout().setEnabled(false);
                        }
//						if(parentComponentUs!=null)	
//							parentComponentUs.emailLayout.setEnabled(false);
                    }
                } else {
                    noError = 0;
                    if (parentComponentSh != null) {
                        parentComponentSh.getEmailLayout().setEnabled(true);
                    }
//					if(parentComponentUs!=null)	
//						parentComponentUs.emailLayout.setEnabled(true);

                }

            }
        });

        notifycationLayout.addComponent(errorExec, 1, 1);
        emailNotificationsLayout.addComponent(notifycationLayout);

        return emailNotificationsLayout;
    }

    /**
     * Set notification record.
     * 
     * @param notification
     */
    public void setUserNotificatonRecord(UserNotificationRecord notification) {
        notification.setTypeError((NotificationRecordType) errorExec.getValue());
        notification.setTypeSuccess((NotificationRecordType) successfulExec.getValue());
    }

    /**
     * Set notification record for schedule.
     * 
     * @param notofication
     * @param schedule
     */
    public void setScheduleNotificationRecord(ScheduleNotificationRecord notofication, Schedule schedule) {

        notofication.setSchedule(schedule);
        notofication.setTypeError((NotificationRecordType) errorExec.getValue());
        notofication.setTypeSuccess((NotificationRecordType) successfulExec.getValue());

    }

    /**
     * Gets {@link ScheduleNotificationRecord} for given schedule.
     * 
     * @param schedule
     *            Schedule which record to retrieve.
     */
    public void getScheduleNotificationRecord(Schedule schedule) {

        ScheduleNotificationRecord notification = schedule.getNotification();

        if (notification != null) {
            errorExec.setValue(notification.getTypeError());
            successfulExec.setValue(notification.getTypeSuccess());
        }

    }

    /**
     * Get notification record for user.
     * 
     * @param user
     */
    public void getUserNotificationRecord(User user) {

        UserNotificationRecord notification = user.getNotification();

        if (notification != null) {
            errorExec.setValue(notification.getTypeError());
            successfulExec.setValue(notification.getTypeSuccess());

        }

    }

    /**
     * Get default notification record for schedules.
     */
    public void getDefaultScheduleNotificationRecord() {

        errorExec.setValue(NotificationRecordType.INSTANT);
        successfulExec.setValue(NotificationRecordType.DAILY);

    }

    /**
     * Disable components.
     */
    public void setDisableComponents() {

        successfulExec.setEnabled(false);
        errorExec.setEnabled(false);
        parentComponentSh.getEmailLayout().setEnabled(false);
//			shEmailLayout.setEnabled(false);

    }

    /**
     * Enables components in email notifications.
     */
    public void setEnableComponents() {

        successfulExec.setEnabled(true);
        errorExec.setEnabled(true);
        parentComponentSh.getEmailLayout().setEnabled(true);
//			shEmailLayout.setEnabled(true);

    }
}
