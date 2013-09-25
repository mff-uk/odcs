package cz.cuni.xrg.intlib.frontend.gui.components;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.data.Validator;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.StartedEvent;

import cz.cuni.xrg.intlib.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.xrg.intlib.commons.app.module.DPUCreateException;
import cz.cuni.xrg.intlib.commons.app.module.DPUValidator;
import cz.cuni.xrg.intlib.commons.app.auth.VisibilityType;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.auxiliaries.dpu.DPUTemplateWrap;
import cz.cuni.xrg.intlib.frontend.dpu.validator.DPUDialogValidator;
import cz.cuni.xrg.intlib.frontend.gui.AuthAwareButtonClickWrapper;

/**
 * Dialog for the DPU template creation. Called from the {@link #DPU}.  Allows to upload a JAR file
 * and on base of it create a new DPU template that will be stored to the DPU template tree.
 * 
 * @author Maria Kukhar
 *
 */
public class DPUCreate extends Window {

	private TextField dpuName;

	private TextArea dpuDescription;
	private OptionGroup groupVisibility;
	private Upload selectFile;
	private FileUploadReceiver fileUploadReceiver;
	public static UploadInfoWindow uploadInfoWindow;
	private GridLayout dpuGeneralSettingsLayout;
	private DPUTemplateRecord dpuTemplate;
	private TextField uploadFile;
	public static int fl=0;

	/**
	 *  Basic constructor.
	 */

	public DPUCreate() {

		this.setResizable(false);
		this.setModal(true);
		this.setCaption("DPU Template Creation");
		

		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setStyleName("dpuDetailMainLayout");
		mainLayout.setMargin(true);

		dpuGeneralSettingsLayout = new GridLayout(2, 4);
		dpuGeneralSettingsLayout.setSpacing(true);
		dpuGeneralSettingsLayout.setWidth("400px");
		dpuGeneralSettingsLayout.setHeight("200px");

		//Name of DPU Template: label & TextField
		Label nameLabel = new Label("Name");
		nameLabel.setImmediate(false);
		nameLabel.setWidth("-1px");
		nameLabel.setHeight("-1px");
		dpuGeneralSettingsLayout.addComponent(nameLabel, 0, 0);

		dpuName = new TextField();
		dpuName.setImmediate(true);
		dpuName.setWidth("310px");
		dpuName.setHeight("-1px");
		//settings of mandatory
		dpuName.addValidator(new Validator() {

			@Override
			public void validate(Object value) throws InvalidValueException {
				if (value.getClass() == String.class
						&& !((String) value).isEmpty()) {
					return;
				}
				throw new InvalidValueException("Name must be filled!");

			}
		});
		dpuGeneralSettingsLayout.addComponent(dpuName, 1, 0);
		
		//Description of DPU Template: label & TextArea
		Label descriptionLabel = new Label("Description");
		descriptionLabel.setImmediate(false);
		descriptionLabel.setWidth("-1px");
		descriptionLabel.setHeight("-1px");
		dpuGeneralSettingsLayout.addComponent(descriptionLabel, 0, 1);

		dpuDescription = new TextArea();
		dpuDescription.setImmediate(false);
		dpuDescription.setWidth("310px");
		dpuDescription.setHeight("60px");
		dpuGeneralSettingsLayout.addComponent(dpuDescription, 1, 1);
		
		//Visibility of DPU Template: label & OptionGroup
		Label visibilityLabel = new Label("Visibility");
		descriptionLabel.setImmediate(false);
		descriptionLabel.setWidth("-1px");
		descriptionLabel.setHeight("-1px");
		dpuGeneralSettingsLayout.addComponent(visibilityLabel, 0, 2);

		groupVisibility = new OptionGroup();
		groupVisibility.addStyleName("horizontalgroup");
		groupVisibility.addItem(VisibilityType.PRIVATE);
		groupVisibility.addItem(VisibilityType.PUBLIC);
		groupVisibility.setValue(VisibilityType.PUBLIC);

		dpuGeneralSettingsLayout.addComponent(groupVisibility, 1, 2);

		Label selectLabel = new Label("Select .jar file");
		selectLabel.setImmediate(false);
		selectLabel.setWidth("-1px");
		selectLabel.setHeight("-1px");
		dpuGeneralSettingsLayout.addComponent(selectLabel, 0, 3);

		fileUploadReceiver = new FileUploadReceiver();
		
		HorizontalLayout uploadFileLayout = new HorizontalLayout();
		uploadFileLayout.setSpacing(true);

		//JAR file uploader
		selectFile = new Upload(null, fileUploadReceiver);
		selectFile.setImmediate(true);
		selectFile.setButtonCaption("Choose file");
		selectFile.addStyleName("horizontalgroup");
		selectFile.setHeight("40px");

		selectFile.addStartedListener(new StartedListener() {

			/**
			 * Upload start listener. If selected file has JAR extension then 
			 * an upload status window with upload progress bar will be shown. 
			 * If selected file has other extension, then upload will be interrupted and 
			 * error notification will be shown.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void uploadStarted(final StartedEvent event) {
				String filename = event.getFilename();
				String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
				String jar = "jar";
				
				if(!jar.equals(extension)){
					selectFile.interruptUpload();
					fl=1;
					Notification.show(
							"Selected file is not .jar file", Notification.Type.ERROR_MESSAGE);
					return;
				}
				if (uploadInfoWindow.getParent() == null) {
					UI.getCurrent().addWindow(uploadInfoWindow);
				}
				uploadInfoWindow.setClosable(false);
			}
		});

		selectFile.addFinishedListener(new Upload.FinishedListener() {
			/**
			 * Upload finished listener. Upload window will be closed after upload finished.
			 * If an upload process wasn't interrupted then will be
			 * show the name of an uploaded file on the DPU template creation dialogue. 
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void uploadFinished(final FinishedEvent event) {
				
				uploadInfoWindow.setClosable(true);
				uploadInfoWindow.close();
				if(fl==0){
					uploadFile.setReadOnly(false);
					uploadFile.setValue(event.getFilename());
					uploadFile.setReadOnly(true);
				}
				else{
					uploadFile.setReadOnly(false);
					uploadFile.setValue("");
					uploadFile.setReadOnly(true);
					fl=0;
				}
			}
		});
		// Upload status window
		uploadInfoWindow = new UploadInfoWindow(selectFile);
		
		uploadFileLayout.addComponent(selectFile);

		uploadFile = new TextField();
		uploadFile.setWidth("210px");
		uploadFile.setReadOnly(true);
		//set mandatory to uploadFile text field.
		uploadFile.addValidator(new Validator() {

			@Override
			public void validate(Object value) throws InvalidValueException {
				if (value.getClass() == String.class
						&& !((String) value).isEmpty()) {
					return;
				}
				throw new InvalidValueException("Upload file must be filled!");

			}
		});
		
		uploadFileLayout.addComponent(uploadFile);
		
		dpuGeneralSettingsLayout.addComponent(uploadFileLayout, 1, 3);

		dpuGeneralSettingsLayout.setMargin(new MarginInfo(false, false, true,
				false));
		mainLayout.addComponent(dpuGeneralSettingsLayout);

		//Layout with buttons Save and Cancel
		HorizontalLayout buttonBar = new HorizontalLayout();
		buttonBar.setStyleName("dpuDetailButtonBar");
		buttonBar.setMargin(new MarginInfo(true, false, false, false));

		Button saveButton = new Button("Save");
		saveButton.setWidth("90px");
		
		saveButton.addClickListener(new AuthAwareButtonClickWrapper(new ClickListener() {

			/**
			 * After pushing the button Save will be checked validation of the mandatory fields:
			 * Name, Description and uploadFile. 
			 * JAR file will be copied from template folder to  the /target/dpu/ folder 
			 * if there no conflicts.
			 * After getting all information from JAR file needed to store new DPUTemplateRecord,
			 * the record in Database will be created
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				// checking validation of the mandatory fields
				if (!dpuName.isValid() || (!uploadFile.isValid())) {
					Notification.show("Failed to save DPURecord",
							"Mandatory fields should be filled",
							Notification.Type.ERROR_MESSAGE);
					return;
				}
				
				// prepare dpu validators
				List<DPUValidator> validators = new LinkedList<>();
				validators.add(new DPUDialogValidator());
				
				final File sourceFile = fileUploadReceiver.file;
				// create new representation
				DPUTemplateWrap dpuWrap = null;				
				try {
					dpuWrap = new DPUTemplateWrap(
							App.getApp().getDPUManipulator().create(sourceFile, dpuName.getValue(), validators));
				} catch(DPUCreateException e) {
					uploadFile.setReadOnly(false);
					uploadFile.setValue("");
					uploadFile.setReadOnly(true);
					Notification.show("Failed to create DPU",
							e.getMessage(), 
							Notification.Type.ERROR_MESSAGE);
					return;
				}
				// set additional variables
				dpuTemplate = dpuWrap.getDPUTemplateRecord();
				// now we know all, we can update the DPU template
				dpuTemplate.setDescription(dpuDescription.getValue());
				dpuTemplate.setVisibility((VisibilityType) groupVisibility.getValue());
				App.getDPUs().save(dpuTemplate);
				// and at the end we can close the dialog .. 
				close();				
			}

		}));
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
		cancelButton.setWidth("90px");
		buttonBar.addComponent(cancelButton);

		mainLayout.addComponent(buttonBar);

		this.setContent(mainLayout);
		setSizeUndefined();
	}

}
