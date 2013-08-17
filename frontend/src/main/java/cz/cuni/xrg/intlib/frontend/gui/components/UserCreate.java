package cz.cuni.xrg.intlib.frontend.gui.components;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
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
	private TextField password;
	private TextField userName;
	private User user = null;
	private User selectUser = null;
	private InvalidValueException ex;
	private Set<Role> roles = null;
	private List<User> users;

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
		password.setValue(selectedUser.getPassword());
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

		users = App.getApp().getUsers().getAllUsers();

		userDetailsLayout = new GridLayout(2, 3);
		userDetailsLayout.setImmediate(false);
		userDetailsLayout.setSpacing(true);
		userDetailsLayout.setWidth("100%");

		userName = new TextField();
		userName.setWidth("100%");
		userName.addValidator(new Validator() {

			private static final long serialVersionUID = 1L;

			@Override
			public void validate(Object value) throws InvalidValueException {
				if (value.getClass() == String.class
						&& !((String) value).isEmpty()) {
					// if(!newUser)
					// users.remove(selectUser);

					String inputName = (String) value;
					for (User user : users) {
						if (user.getUsername().equals(inputName)) {
							if (selectUser==null || (selectUser.getId() != user.getId())) {
								ex = new InvalidValueException(
										"User with this user name is already exist");
								throw ex;
							}
						}
					}

					return;
				}
				ex = new InvalidValueException(
						"User name field must be filled!");
				throw ex;
			}
		});

		userDetailsLayout.addComponent(new Label("User name:"), 0, 0);
		userDetailsLayout.addComponent(userName, 1, 0);

		password = new TextField();
		password.setWidth("100%");
		password.addValidator(new Validator() {

			private static final long serialVersionUID = 1L;

			@Override
			public void validate(Object value) throws InvalidValueException {
				if (value.getClass() == String.class
						&& !((String) value).isEmpty()) {
					return;
				}
				ex = new InvalidValueException("Password field must be filled!");
				throw ex;
			}
		});

		Label passLabel = new Label("Password:");
		if (newUser) {
			password.setVisible(false);
			passLabel.setVisible(false);
		}

		userDetailsLayout.addComponent(passLabel, 0, 1);
		userDetailsLayout.addComponent(password, 1, 1);

		userEmail = new TextField();
		userEmail.setWidth("100%");
		userEmail.addValidator(new Validator() {
			private static final long serialVersionUID = 1L;

			@Override
			public void validate(Object value) throws InvalidValueException {

				if (value.getClass() == String.class
						&& !((String) value).isEmpty()) {
					String inputEmail = (String) value;
					if (!inputEmail
							.matches("[0-9a-zA-Z._-]+@[0-9a-zA-Z]+\\.[a-zA-Z]{2,5}")) {
						ex = new InvalidValueException("Wrong mail format");
						throw ex;
					}
					return;
				}
				ex = new InvalidValueException("E-mail field must be filled!");
				throw ex;

			}
		});

		userDetailsLayout.addComponent(new Label("E-mail:"), 0, 2);
		userDetailsLayout.addComponent(userEmail, 1, 2);

		userDetailsLayout.setColumnExpandRatio(0, 0.2f);
		userDetailsLayout.setColumnExpandRatio(1, 0.8f);

		roleSelector = new TwinColSelect();
		roleSelector.addItem(Role.ADMINISTRATOR);
		roleSelector.addItem(Role.USER);

		roleSelector.setNullSelectionAllowed(true);
		roleSelector.setMultiSelect(true);
		roleSelector.setImmediate(true);
		roleSelector.setWidth("350px");
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
				ex = new InvalidValueException("Set Roles must be filled!");
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

				// validation
				// User name, password,e-mail and roles should be filled
				// email should be in correct format
				try {
					userName.validate();
					if (password.isVisible())
						password.validate();
					userEmail.validate();
					roleSelector.validate();

				} catch (Validator.InvalidValueException e) {
					Notification.show("Failed to save settings. Reason:",
							e.getMessage(), Notification.Type.ERROR_MESSAGE);
					return;
				}

				// checking if the dialog was open from the User table
				// if no, create new user record
				if (selectUser == null) {
					user = new User();

				} else {
					user = selectUser;
					selectUser = null;
				}

				// setting user parameters
				user.setUsername(userName.getValue());
				user.setPassword(password.getValue());

				EmailAddress email = new EmailAddress(userEmail.getValue());
				user.setEmail(email);

				@SuppressWarnings("unchecked")
				Set<Object> selectedRoles = (Set<Object>) roleSelector
						.getValue();
				Iterator<Object> it = selectedRoles.iterator();
				roles = new HashSet<Role>();
				while (it.hasNext()) {
					Object selectRole = it.next();
					if (selectRole.equals(Role.ADMINISTRATOR)) {
						roles.add(Role.ADMINISTRATOR);
					} else
						roles.add(Role.USER);
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

}
