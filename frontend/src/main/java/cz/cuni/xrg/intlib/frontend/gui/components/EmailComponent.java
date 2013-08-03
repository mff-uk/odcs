package cz.cuni.xrg.intlib.frontend.gui.components;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;

/**
 * Builds E-mail notification component which consists of textfields for e-mail
 * and buttons for add and remove this textfields.
 * Used in {@link UserSettings} and {@link EmailNotifications} 
 * 
 * @author Maria Kukhar
 *
 */
public class EmailComponent {


	private Button buttonEmailhRem;
	private Button buttonEmailAdd;
	private InvalidValueException ex;
	private GridLayout gridLayoutEmail;
	private TextField textFieldEmail;
	  
	
		/**
		 * List<String> that contains e-mails.
		 */
		private List<String> griddata = initializeEmailData();

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
		private void saveEditedTexts() {
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
		private void refreshEmailData() {
			gridLayoutEmail.removeAllComponents();
			int row = 0;
			listedEditText = new ArrayList<>();
			if (griddata.size() < 1) {
				griddata.add("");
			}
			gridLayoutEmail.setRows(griddata.size() + 1);
			for (String item : griddata) {
				textFieldEmail = new TextField();
				listedEditText.add(textFieldEmail);

				//text field for the graph
				textFieldEmail.setWidth("100%");
				textFieldEmail.setData(row);
				textFieldEmail.setValue(item.trim());
				textFieldEmail.setInputPrompt("franta@test.cz");
				textFieldEmail.addValidator(new Validator() {
					@Override
					public void validate(Object value) throws InvalidValueException {
						if (value != null) {

							String email = value.toString().toLowerCase()
									.trim();

							if (email.isEmpty()) {
								return;
							}

							if (email.contains(" ")) {
								ex = new InvalidValueException(
										"E-mail(s) must contain no white spaces");
								throw ex;
							} 

						}

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
		GridLayout initializeEmailList() {

			gridLayoutEmail = new GridLayout();
			gridLayoutEmail.setImmediate(false);
			gridLayoutEmail.setWidth("100%");
			gridLayoutEmail.setHeight("100%");
			gridLayoutEmail.setMargin(false);
			gridLayoutEmail.setColumns(2);
			gridLayoutEmail.setColumnExpandRatio(0, 0.95f);
			gridLayoutEmail.setColumnExpandRatio(1, 0.05f);

			refreshEmailData();
			return gridLayoutEmail;

		}
	

}
