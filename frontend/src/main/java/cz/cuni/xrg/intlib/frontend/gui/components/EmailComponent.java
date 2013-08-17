package cz.cuni.xrg.intlib.frontend.gui.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;

import cz.cuni.xrg.intlib.commons.app.scheduling.EmailAddress;
import cz.cuni.xrg.intlib.commons.app.scheduling.NotificationRecordType;
import cz.cuni.xrg.intlib.commons.app.scheduling.Schedule;
import cz.cuni.xrg.intlib.commons.app.scheduling.ScheduleNotificationRecord;
import cz.cuni.xrg.intlib.commons.app.scheduling.UserNotificationRecord;
import cz.cuni.xrg.intlib.commons.app.user.User;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;

/**
 * Builds E-mail notification component which consists of textfields for e-mail
 * and buttons for add and remove this textfields.
 * Used in {@link UserSettings} and {@link EmailNotifications} 
 * 
 * @author Maria Kukhar
 *
 */
public class EmailComponent {

//	public EmailNotifications parentComponent; 
	private Button buttonEmailhRem;
	private Button buttonEmailAdd;
	private InvalidValueException ex;
	private GridLayout gridLayoutEmail;
	public TextField textFieldEmail;
	private InvalidValueException mailEx;
	  
	
		/**
		 * List<String> that contains e-mails.
		 */
		public List<String> griddata = initializeEmailData();

		/**
		 * Initializes data of the E-mail notification component
		 */
		private static List<String> initializeEmailData() {
			List<String> result = new LinkedList<>();
			result.add("");

			return result;

		}

		/**
		 * Add new data to E-mail notification component.
		 *
		 * @param newData. String that will be added
		 */
		private void addDataToEmailData(String newData) {
			griddata.add(newData.trim());
		}

		/**
		 * Remove data from E-mail notification component. Only if component contain more
		 * then 1 row.
		 *
		 * @param row Data that will be removed.
		 */
		private void removeDataEmailData(Integer row) {
			int index = row;
			if (griddata.size() > 1) {
				griddata.remove(index);
			}
		}

		private List<TextField> listedEditText = null;

		/**
		 * Save edited texts in the E-mail notification component
		 */
		public void saveEditedTexts() {
			griddata = new LinkedList<>();
			for (TextField editText : listedEditText) {
				griddata.add(editText.getValue().trim());
			}
		}


		/**
		 * Builds E-mail notification component which consists of textfields for e-mail
		 * and buttons for add and remove this textfields. Used in
//		 * {@link #initializeEmailList} and also in adding and removing fields
		 * for component refresh
		 */
		public void refreshEmailData() {
			gridLayoutEmail.removeAllComponents();
			int row = 0;
			listedEditText = new ArrayList<>();
			if (griddata.size() < 1) {
				griddata.add("");
			}
			gridLayoutEmail.setRows(griddata.size() + 1);
			for (String item : griddata) {
				textFieldEmail = new TextField();
				textFieldEmail.setImmediate(true);
				listedEditText.add(textFieldEmail);

				//text field for the graph
				textFieldEmail.setWidth("100%");
				textFieldEmail.setData(row);
				textFieldEmail.setValue(item.trim());
				textFieldEmail.addTextChangeListener(new TextChangeListener() {
					
					@Override
					public void textChange(TextChangeEvent event) {
						// TODO Auto-generated method stub
						saveEditedTexts();
					}
				});
				textFieldEmail.setInputPrompt("franta@test.cz");

					textFieldEmail.addValidator(new Validator() {
						@Override
						public void validate(Object value) throws InvalidValueException {
				//			if((parentComponent!=null) && (parentComponent.shEmailLayout.isEnabled())){
								if (value.getClass() == String.class
										&& !((String) value).isEmpty()) {
									String inputEmail = (String) value;
									if(!inputEmail.matches("[0-9a-zA-Z._-]+@[0-9a-zA-Z]+\\.[a-zA-Z]{2,5}")){
										mailEx = new InvalidValueException("Wrong mail format");
										throw mailEx;
									}
									return;
								}
								
								mailEx = new InvalidValueException("Email must be filled!");
								throw mailEx;
								
				//			}
							
						}
					});
				


				//remove button
				buttonEmailhRem = new Button();
				buttonEmailhRem.setWidth("55px");
				buttonEmailhRem.setCaption("-");
				buttonEmailhRem.setData(row);
				buttonEmailhRem.addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(Button.ClickEvent event) {
						saveEditedTexts();
						Button senderButton = event.getButton();
						Integer row = (Integer) senderButton.getData();
						removeDataEmailData(row);
						refreshEmailData();
					}
				});
				gridLayoutEmail.addComponent(textFieldEmail, 0, row);
				gridLayoutEmail.addComponent(buttonEmailhRem, 1, row);
				gridLayoutEmail.setComponentAlignment(buttonEmailhRem,
						Alignment.TOP_RIGHT);
				row++;
			}
			//add button
			buttonEmailAdd = new Button();
			buttonEmailAdd.setCaption("+");
			buttonEmailAdd.setImmediate(true);
			buttonEmailAdd.setWidth("55px");
			buttonEmailAdd.setHeight("-1px");
			buttonEmailAdd.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(Button.ClickEvent event) {
					saveEditedTexts();
					addDataToEmailData(" ");
					refreshEmailData();
				}
			});
			gridLayoutEmail.addComponent(buttonEmailAdd, 0, row);

		}

		/**
		 * Initializes E-mail notification component.
		 * @return 
		 */
		public GridLayout initializeEmailList() {

			gridLayoutEmail = new GridLayout();
			gridLayoutEmail.setImmediate(false);
			gridLayoutEmail.setWidth("380px");
			gridLayoutEmail.setHeight("100%");
			gridLayoutEmail.setMargin(false);
			gridLayoutEmail.setColumns(2);
			gridLayoutEmail.setColumnExpandRatio(0, 0.95f);
			gridLayoutEmail.setColumnExpandRatio(1, 0.05f);

			refreshEmailData();
			return gridLayoutEmail;

		}
		
		public void setUserEmailNotification(UserNotificationRecord notofication){
			
			Set<EmailAddress> emails = new HashSet<>();
			List<String> emailStr = griddata;
			
			for (String mail:emailStr){
				if(!mail.equals("")){
				EmailAddress e = new EmailAddress(mail);
				emails.add(e);
				}
			}
			notofication.setEmails(emails);
		}
		
		public void setScheduleEmailNotification(ScheduleNotificationRecord notofication, Schedule schedule){
			
			Set<EmailAddress> emails = new HashSet<>();
			List<String> emailStr = griddata;
			
			for (String mail:emailStr){
				if(!mail.equals("")){
				EmailAddress e = new EmailAddress(mail);
				emails.add(e);
				}
			}
			notofication.setEmails(emails);
		}
		
		public void getUserEmailNotification(User user){
			
			UserNotificationRecord notification = user.getNotification();
			
			if(notification!=null){
		
			Set<EmailAddress> emails = notification.getEmails();
			List<String> emailStr = new LinkedList<>();
			
			for (EmailAddress mail:emails)
					emailStr.add(mail.getName()+"@"+ mail.getDomain());

			griddata.clear();
			griddata = emailStr;
			refreshEmailData();
			
			}
			else{
				EmailAddress email = user.getEmail();
				String emailStr = email.getName()+"@"+ email.getDomain();

				griddata.clear();
				griddata.add(0,emailStr);
				refreshEmailData();
			}
		}
		
		
		public void getScheduleEmailNotification(Schedule schedule){
			
			ScheduleNotificationRecord notification = schedule.getNotification();
			
			if(notification!=null){
				Set<EmailAddress> emails = notification.getEmails();
				List<String> emailStr = new LinkedList<>();
				
				for (EmailAddress mail:emails)
						emailStr.add(mail.getName()+"@"+ mail.getDomain());
	
				griddata.clear();
				griddata = emailStr;
				refreshEmailData();
				

			}
			else{
				User user = App.getApp().getUsers().getUser(1L);
				getUserEmailNotification(user);
			}
			
		}
	

}