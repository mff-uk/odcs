package cz.cuni.xrg.intlib.frontend.gui.components;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

import cz.cuni.xrg.intlib.commons.app.scheduling.EmailAddress;
import cz.cuni.xrg.intlib.commons.app.user.Role;
import cz.cuni.xrg.intlib.commons.app.user.User;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;

/**
 * Dialog for new user creation. Called from the {@link #UsersList}.
 * 
 * @author Maria Kukhar
 * 
 */
public class UserCreate extends Window {

	private static final long serialVersionUID = 1L;
	@AutoGenerated
	private GridLayout userDetailsLayout;
	@AutoGenerated
	private Label label;
	private TwinColSelect roleSelector;
	private TextField userEmail;
	private VerticalLayout mainLayout;
	private PasswordField password;
	private PasswordField passwordConfim;
	private TextField userName;
	private User user = null;
	private User selectUser = null;
	private InvalidValueException ex;
	private Set<Role> roles = null;
	private List<User> users;
//	private TextField passText = null;
	private boolean passChanged=false;

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/**
	 * The constructor should first build the main layout, set the composition
	 * root and then do any custom initialization.
	 * 
	 * The constructor will not be automatically regenerated by the visual
	 * editor.
	 */
	public UserCreate(boolean newUser) {
		this.setResizable(false);
		this.setModal(true);
		this.setCaption("Create new user");

		buildMainLayout(newUser);
		this.setContent(mainLayout);
		setSizeUndefined();

	}

	/**
	 * The method calls from {@link #Administrator} and sets the corresponding
	 * values of specific user to the dialog.
	 * 
	 * @param selectedUser
	 *            . User that locate in the row of User table in which has been
	 *            pressed the button Change settings.
	 */
	public void setSelectedUser(User selectedUser) {
		userName.setValue(selectedUser.getUsername());
		password.setValue("*****");
		passwordConfim.setValue("*****");
		userEmail.setValue(selectedUser.getEmail().toString());
		roleSelector.setValue(selectedUser.getRoles());

		selectUser = selectedUser;

	}

	/**
	 * Builds main layout
	 * 
	 * @return mainLayout VerticalLayout with all dialog components
	 */
	@AutoGenerated
	private VerticalLayout buildMainLayout(final boolean newUser) {

		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(false);
		mainLayout.setMargin(true);
		mainLayout.setSpacing(true);
		mainLayout.setWidth("370px");

		users = App.getApp().getUsers().getAllUsers();

		userDetailsLayout = new GridLayout(2, 4);
		userDetailsLayout.setImmediate(false);
		userDetailsLayout.setSpacing(true);


		userName = new TextField();
		userName.setImmediate(true);
		userName.setWidth("250px");
		userName.addValidator(new Validator() {

			private static final long serialVersionUID = 1L;

			@Override
			public void validate(Object value) throws InvalidValueException {
				if (value.getClass() == String.class
						&& !((String) value).isEmpty()) {

					String inputName = (String) value;
					for (User user : users) {
						if (user.getUsername().equals(inputName)) {
							if (selectUser==null || (selectUser.getId() != user.getId())) {
								ex = new InvalidValueException(
										"user with this user name is already exist");
								throw ex;
							}
						}
					}

					return;
				}
				ex = new InvalidValueException(
						"user name field must be filled");
				throw ex;
			}
		});

		userDetailsLayout.addComponent(new Label("User name:"), 0, 0);
		userDetailsLayout.addComponent(userName, 1, 0);

		password = new PasswordField();
		password.setImmediate(true);
		password.setWidth("250px");
		password.addFocusListener(new FocusListener() {
				
				private static final long serialVersionUID = 1L;
	
				@Override
				public void focus(FocusEvent event) {
					password.setValue("");
					passwordConfim.setValue("");
					passChanged=true;
				}
			});
		
		Label passLabel = new Label("Password:");


		userDetailsLayout.addComponent(passLabel, 0, 1);
		userDetailsLayout.addComponent(password, 1, 1);
		
		passwordConfim = new PasswordField();
		passwordConfim.setImmediate(true);
		passwordConfim.setWidth("250px");
		Label confirmLabel = new Label("Password<br>confirmation:");
		passwordConfim.addFocusListener(new FocusListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void focus(FocusEvent event) {
				passwordConfim.setValue("");
				passChanged=true;
			}
		});
		
		confirmLabel.setContentMode(ContentMode.HTML);
		
		userDetailsLayout.addComponent(confirmLabel, 0, 2);
		userDetailsLayout.addComponent(passwordConfim, 1, 2);

		userEmail = new TextField();
		userEmail.setImmediate(true);
		userEmail.setWidth("250px");
		userEmail.addValidator(new Validator() {
			private static final long serialVersionUID = 1L;

			@Override
			public void validate(Object value) throws InvalidValueException {

				if (value.getClass() == String.class
						&& !((String) value).isEmpty()) {
					String inputEmail = (String) value;
					if (!inputEmail
							.matches("[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})")) {
						ex = new InvalidValueException("wrong mail format");
						throw ex;
					}
					return;
				}
				ex = new InvalidValueException("e-mail field must be filled");
				throw ex;

			}
		});

		userDetailsLayout.addComponent(new Label("E-mail:"), 0, 3);
		userDetailsLayout.addComponent(userEmail, 1, 3);

		userDetailsLayout.setColumnExpandRatio(0, 0.3f);
		userDetailsLayout.setColumnExpandRatio(1, 0.7f);

		roleSelector = new TwinColSelect();
		roleSelector.addItem(Role.ROLE_ADMIN);
		roleSelector.addItem(Role.ROLE_USER);

		roleSelector.setNullSelectionAllowed(true);
		roleSelector.setMultiSelect(true);
		roleSelector.setImmediate(true);
		roleSelector.setWidth("325px");
		roleSelector.setHeight("200px");
		roleSelector.setLeftColumnCaption("Defined Roles:");
		roleSelector.setRightColumnCaption("Set Roles:");
		// roleSelector is mandatory component
		roleSelector.addValidator(new Validator() {

			private static final long serialVersionUID = 1L;

			@Override
			public void validate(Object value) throws InvalidValueException {

				if (value.toString() != "[]") {
					return;
				}
				ex = new InvalidValueException("at least one role must be set");
				throw ex;

			}
		});

		// Layout with buttons Save and Cancel
		HorizontalLayout buttonBar = new HorizontalLayout();
		buttonBar.setMargin(true);
		

		// Save button
		Button createRule = new Button();
		createRule.setCaption("Save user");
		createRule.setImmediate(true);
		createRule.addClickListener(new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				String errorText="";
				// validation
				// User name, password,e-mail and roles should be filled
				// email should be in correct format
				try {
					userName.validate();

				} catch (Validator.InvalidValueException e) {
					errorText=errorText + e.getMessage();
				}
				
				try {
					userEmail.validate();

				} catch (Validator.InvalidValueException e) {
					if(!errorText.equals(""))
						errorText=errorText + ", " + e.getMessage();
					else
						errorText=errorText + e.getMessage();
				}
				
				try {
					roleSelector.validate();

				} catch (Validator.InvalidValueException e) {
					if(!errorText.equals(""))
						errorText=errorText + ", " + e.getMessage();
					else
						errorText=errorText + e.getMessage();
				}
				
				if(!errorText.equals("")){
					errorText=errorText + ".";
					Notification.show("Failed to save settings. Reason:",
							errorText, Notification.Type.ERROR_MESSAGE);
					return;
				}
				

				// checking if the dialog was open from the User table
				// if no, create new user record

				if(newUser){
					String userPassword="";
					
					if(passwordConfim.getValue().equals(password.getValue()) ){
						if(!passwordConfim.getValue().isEmpty())
							userPassword =  password.getValue();
						else
							userPassword = createPassword();
					}
					else{
						Notification.show("Password confirmation is wrong", "The typed pasword is different than the retyped password", Notification.Type.ERROR_MESSAGE);
						return;
					}
							

					EmailAddress email = new EmailAddress(userEmail.getValue().trim());	
					user = App.getApp().getUsers().createUser(userName.getValue().trim(), userPassword, email);
				}
				else{
					user = selectUser;
					user.setUsername(userName.getValue().trim());
					if(passChanged){
						if(passwordConfim.getValue().equals(password.getValue())){
							if(!passwordConfim.getValue().isEmpty())
								user.setPassword(password.getValue());
							else
								user.setPassword(createPassword());
						}
						else{
							Notification.show("Password confirmation is wrong","The typed pasword is different than the retyped password", Notification.Type.ERROR_MESSAGE);
							return;
						}
					}

					EmailAddress email = new EmailAddress(userEmail.getValue().trim());
					user.setEmail(email);
				}

				@SuppressWarnings("unchecked")
				Set<Object> selectedRoles = (Set<Object>) roleSelector
						.getValue();
				Iterator<Object> it = selectedRoles.iterator();
				roles = new HashSet<Role>();
				while (it.hasNext()) {
					Object selectRole = it.next();
					if (selectRole.equals(Role.ROLE_ADMIN)) {
						roles.add(Role.ROLE_ADMIN);
					} else
						roles.add(Role.ROLE_USER);
				}

				user.setRoles(roles);

				// store user record to DB
				App.getApp().getUsers().save(user);

				close();

			}
		});

		buttonBar.addComponent(createRule);

		Button cancelButton = new Button("Cancel", new Button.ClickListener() {

			/**
			 * Closes Scheduling pipeline window
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(Button.ClickEvent event) {
				close();

			}
		});
		buttonBar.addComponent(cancelButton);

		mainLayout.addComponent(userDetailsLayout);
		mainLayout.addComponent(roleSelector);
		mainLayout.addComponent(buttonBar);
		mainLayout.setComponentAlignment(buttonBar, Alignment.MIDDLE_RIGHT);

		return mainLayout;
	}
	
	private static final String allowedcharacters= "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWZXY0123456789";
	/**
	 * Generate random password of 6 symbols a-zA-z0-9
	 * 
	 * @return password in string format
	 */
	private String createPassword(){
		int passwordSize = 6;
		Random rnd = new Random();
		
		StringBuilder result = new StringBuilder();
		int randomIndex;
		while (passwordSize>0)
		{
			randomIndex = rnd.nextInt(allowedcharacters.length());
			result.append(allowedcharacters.charAt(randomIndex));
			passwordSize--;
		}
		return result.toString();
	}

}
