package cz.cuni.mff.xrg.odcs.frontend.gui.components;

import java.io.File;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.Upload.SucceededEvent;

import cz.cuni.mff.xrg.odcs.commons.app.auth.ShareType;
import cz.cuni.mff.xrg.odcs.commons.app.constants.LenghtLimits;
import cz.cuni.mff.xrg.odcs.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.mff.xrg.odcs.commons.app.facade.DPUFacade;
import cz.cuni.mff.xrg.odcs.commons.app.module.DPUCreateException;
import cz.cuni.mff.xrg.odcs.commons.app.module.DPUModuleManipulator;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.MaxLengthValidator;
import cz.cuni.mff.xrg.odcs.frontend.dpu.wrap.DPUTemplateWrap;
import cz.cuni.mff.xrg.odcs.frontend.gui.AuthAwareButtonClickWrapper;

/**
 * Dialog for the DPU template creation. Allows to upload a JAR file and on base
 * of it create a new DPU template that will be stored to the DPU template tree.
 * 
 * @author Maria Kukhar
 */
@Component
@Scope("prototype")
public class DPUCreate extends Window {

    /**
     * @return the uploadInfoWindow
     */
    public static UploadInfoWindow getUploadInfoWindow() {
        return uploadInfoWindow;
    }

    /**
     * @param aUploadInfoWindow
     *            the uploadInfoWindow to set
     */
    public static void setUploadInfoWindow(UploadInfoWindow aUploadInfoWindow) {
        uploadInfoWindow = aUploadInfoWindow;
    }

    /**
     * @return the fl
     */
    public static int getFl() {
        return fl;
    }

    /**
     * @param aFl
     *            the fl to set
     */
    public static void setFl(int aFl) {
        fl = aFl;
    }

    private TextField dpuName;

    private TextArea dpuDescription;

    private OptionGroup groupVisibility;
    private OptionGroup groupVisibilityZip;

    private FileUploadReceiver fileUploadReceiver;
    private FileUploadReceiver fileUploadReceiverZip;

    private static UploadInfoWindow uploadInfoWindow;

    private GridLayout dpuGeneralSettingsLayout;
    private GridLayout dpuGeneralSettingsLayoutZip;

    private DPUTemplateRecord dpuTemplate;

    private TextField uploadFile;
    private TextField uploadFileZip;

    private static int fl = 0;

    @Autowired
    private DPUFacade dpuFacade;

    @Autowired
    private DPUModuleManipulator dpuManipulator;

    /**
     * Basic constructor.
     */
    public DPUCreate() {
    }

    @PostConstruct
    private void init() {

        this.setResizable(false);
        this.setModal(true);
        this.setCaption("DPU Template Creation");
        
        TabSheet tabs = new TabSheet();
        tabs.addTab(createJarTab(), "jar");
        tabs.addTab(createZipTab(), "zip");

        this.setContent(tabs);
        setSizeUndefined();
        setWidth("500px");
    }
    
    private com.vaadin.ui.Component createZipTab() {
    	VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setStyleName("dpuDetailMainLayout");
        mainLayout.setMargin(true);

        dpuGeneralSettingsLayoutZip = new GridLayout(2, 4);
        dpuGeneralSettingsLayoutZip.setSpacing(true);
        dpuGeneralSettingsLayoutZip.setWidth("400px");
        dpuGeneralSettingsLayoutZip.setHeight("200px");

        Label help = new Label("TODO select text"); // TODO add label for som info
        help.setWidth("310px");
        help.setHeight("60px"); // TODO ?
        dpuGeneralSettingsLayoutZip.addComponent(help, 1, 0);
        
        //Visibility of DPU Template: label & OptionGroup
        groupVisibilityZip = createVisibilityOption(dpuGeneralSettingsLayoutZip, 1);

        Label selectLabel = new Label("Select .zip file");
        selectLabel.setImmediate(false);
        selectLabel.setWidth("-1px");
        selectLabel.setHeight("-1px");
        dpuGeneralSettingsLayoutZip.addComponent(selectLabel, 0, 2);

        fileUploadReceiverZip = new FileUploadReceiver();

        uploadFileZip = new TextField();
        HorizontalLayout uploadFileLayout = buildUploadLayout(dpuGeneralSettingsLayoutZip,
        		fileUploadReceiverZip, uploadFileZip, "zip");

        dpuGeneralSettingsLayoutZip.addComponent(uploadFileLayout, 1, 2);

        dpuGeneralSettingsLayoutZip.setMargin(new MarginInfo(false, false, true,
                false));
        mainLayout.addComponent(dpuGeneralSettingsLayoutZip);

        //Layout with buttons Save and Cancel
        HorizontalLayout buttonBar = new HorizontalLayout();
        buttonBar.setStyleName("dpuDetailButtonBar");
        buttonBar.setMargin(new MarginInfo(true, false, false, false));

        buttonBar.addComponent(createSaveZipButton());

        buttonBar.addComponent(createCancelButton());

        mainLayout.addComponent(buttonBar);

        return mainLayout;
	}

	private TextArea createDpuDescription(GridLayout layout, int row) {
		Label descriptionLabel = new Label("Description");
        descriptionLabel.setImmediate(false);
        descriptionLabel.setWidth("-1px");
        descriptionLabel.setHeight("-1px");
        layout.addComponent(descriptionLabel, 0, row);

        TextArea dpuDescription = new TextArea();
        dpuDescription.setImmediate(false);
        dpuDescription.setWidth("310px");
        dpuDescription.setHeight("60px");
        layout.addComponent(dpuDescription, 1, row);
		return dpuDescription;
	}

	private TextField createDpuName() {
		TextField dpuName = new TextField();
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
        dpuName.addValidator(new MaxLengthValidator(LenghtLimits.DPU_NAME));
		return dpuName;
	}

	public com.vaadin.ui.Component createJarTab() {
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

        dpuName = createDpuName();
        dpuName.setInputPrompt("<If not selected, one from jar will be used.>");
        dpuGeneralSettingsLayout.addComponent(dpuName, 1, 0);

        //Description of DPU Template: label & TextArea
        dpuDescription = createDpuDescription(dpuGeneralSettingsLayout, 1);
        dpuDescription.setInputPrompt("<If not selected, one from jar will be used.>");

        //Visibility of DPU Template: label & OptionGroup
        groupVisibility = createVisibilityOption(dpuGeneralSettingsLayout, 2);

        Label selectLabel = new Label("Select .jar file");
        selectLabel.setImmediate(false);
        selectLabel.setWidth("-1px");
        selectLabel.setHeight("-1px");
        dpuGeneralSettingsLayout.addComponent(selectLabel, 0, 3);

        fileUploadReceiver = new FileUploadReceiver();

        uploadFile = new TextField();
        HorizontalLayout uploadFileLayout = buildUploadLayout(dpuGeneralSettingsLayout, fileUploadReceiver, uploadFile, "jar");

        dpuGeneralSettingsLayout.addComponent(uploadFileLayout, 1, 3);

        dpuGeneralSettingsLayout.setMargin(new MarginInfo(false, false, true,
                false));
        mainLayout.addComponent(dpuGeneralSettingsLayout);

        //Layout with buttons Save and Cancel
        HorizontalLayout buttonBar = new HorizontalLayout();
        buttonBar.setStyleName("dpuDetailButtonBar");
        buttonBar.setMargin(new MarginInfo(true, false, false, false));

        buttonBar.addComponent(createSaveJarButton());

        buttonBar.addComponent(createCancelButton());

        mainLayout.addComponent(buttonBar);

        return mainLayout;
    }
	
	private OptionGroup createVisibilityOption(GridLayout layout, int row) {
		Label visibilityLabel = new Label("Visibility");
		visibilityLabel.setImmediate(false);
		visibilityLabel.setWidth("-1px");
		visibilityLabel.setHeight("-1px");
        layout.addComponent(visibilityLabel, 0, row);

        OptionGroup grVis = new OptionGroup();
        grVis.addStyleName("horizontalgroup");
        grVis.addItem(ShareType.PRIVATE);
        grVis.setItemCaption(ShareType.PRIVATE, ShareType.PRIVATE.getName());
        grVis.addItem(ShareType.PUBLIC_RO);
        grVis.setItemCaption(ShareType.PUBLIC_RO, ShareType.PUBLIC_RO.getName());
        grVis.setValue(ShareType.PUBLIC_RO);

        layout.addComponent(grVis, 1, row);
		return grVis;
	}
	
	private Button createSaveZipButton() {
		Button saveButton = new Button("Save");
        saveButton.setWidth("90px");

        saveButton.addClickListener(new AuthAwareButtonClickWrapper(new ClickListener() {
            /**
             * After pushing the button Save will be checked validation of the
             * mandatory fields: Name, Description and uploadFile. JAR file will
             * be copied from template folder to the /target/dpu/ folder if
             * there no conflicts. After getting all information from JAR file
             * needed to store new DPUTemplateRecord, the record in Database
             * will be created
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                // checking validation of the mandatory fields
                if ((!uploadFileZip.isValid())) {
                    Notification.show("Failed to save DPURecord",
                            "Mandatory fields should be filled",
                            Notification.Type.ERROR_MESSAGE);
                    return;
                }

                final File sourceFile = fileUploadReceiverZip.getFile();
                
                // TODO [jano] create new representation
            }
        }));
        return saveButton;
	}
	
	private Button createSaveJarButton() {
		Button saveButton = new Button("Save");
        saveButton.setWidth("90px");

        saveButton.addClickListener(new AuthAwareButtonClickWrapper(new ClickListener() {
            /**
             * After pushing the button Save will be checked validation of the
             * mandatory fields: Name, Description and uploadFile. JAR file will
             * be copied from template folder to the /target/dpu/ folder if
             * there no conflicts. After getting all information from JAR file
             * needed to store new DPUTemplateRecord, the record in Database
             * will be created
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(ClickEvent event) {
                // checking validation of the mandatory fields
                if (!uploadFile.isValid()) {
                    Notification.show("Failed to save DPURecord",
                            "Mandatory fields should be filled",
                            Notification.Type.ERROR_MESSAGE);
                    return;
                }

                final File sourceFile = fileUploadReceiver.getFile();
                // create new representation
                DPUTemplateWrap dpuWrap;
                try {
                	String name = dpuName.isValid() ? dpuName.getValue() : null;
                    dpuWrap = new DPUTemplateWrap(
                            dpuManipulator.create(sourceFile, name));
                } catch (DPUCreateException e) {

                    dpuGeneralSettingsLayout.removeComponent(1, 3);
                    uploadFile = new TextField();
                    dpuGeneralSettingsLayout.addComponent(buildUploadLayout(dpuGeneralSettingsLayout, fileUploadReceiver, uploadFile, "jar"), 1, 3);
                    Notification.show("Failed to create DPU",
                            e.getMessage(),
                            Notification.Type.ERROR_MESSAGE);
                    return;
                }
                // set additional variables
                dpuTemplate = dpuWrap.getDPUTemplateRecord();
                // now we know all, we can update the DPU template
                dpuTemplate.setDescription(dpuDescription.getValue());
                dpuTemplate.setShareType((ShareType) groupVisibility.getValue());
                dpuFacade.save(dpuTemplate);
                // and at the end we can close the dialog .. 
                close();
            }
        }));
        return saveButton;
	}
	
	private Button createCancelButton() {
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
        return cancelButton;
	}

    private HorizontalLayout buildUploadLayout(final GridLayout layout,
    		final FileUploadReceiver fileUploadReceiver,
    		final TextField uploadFile,
    		final String fileExtension) {

        HorizontalLayout uploadFileLayout = new HorizontalLayout();
        uploadFileLayout.setSpacing(true);

        //JAR file uploader
        final Upload selectFile = new Upload(null, fileUploadReceiver);
        selectFile.setImmediate(true);
        selectFile.setButtonCaption("Choose file");
        selectFile.addStyleName("horizontalgroup");
        selectFile.setHeight("40px");

        selectFile.addStartedListener(new StartedListener() {
            /**
             * Upload start listener. If selected file has JAR extension then an
             * upload status window with upload progress bar will be shown. If
             * selected file has other extension, then upload will be
             * interrupted and error notification will be shown.
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void uploadStarted(final StartedEvent event) {
                String filename = event.getFilename();
                String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());

                if (!fileExtension.equals(extension)) {
                    selectFile.interruptUpload();
                    Notification.show(
                            "Selected file is not ." + fileExtension + " file",
                            Notification.Type.ERROR_MESSAGE);
                    return;
                }
                if (getUploadInfoWindow().getParent() == null) {
                    UI.getCurrent().addWindow(getUploadInfoWindow());
                }
                getUploadInfoWindow().setClosable(false);
            }
        });

        //If upload failed, upload window will be closed 
        selectFile.addFailedListener(new Upload.FailedListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void uploadFailed(FailedEvent event) {

                getUploadInfoWindow().setClosable(true);
                getUploadInfoWindow().close();
                layout.removeComponent(1, 3);
                layout.addComponent(buildUploadLayout(layout, fileUploadReceiver, uploadFile, fileExtension), 1, 3);

            }
        });

        //If upload finish successful, upload window will be closed and the name 
        //of the uploaded file will be shown
        selectFile.addSucceededListener(new Upload.SucceededListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void uploadSucceeded(final SucceededEvent event) {

                getUploadInfoWindow().setClosable(true);
                getUploadInfoWindow().close();
                uploadFile.setReadOnly(false);
                uploadFile.setValue(event.getFilename());
                uploadFile.setReadOnly(true);

            }
        });
        // Upload status window
        setUploadInfoWindow(new UploadInfoWindow(selectFile));

        uploadFileLayout.addComponent(selectFile);

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

        return uploadFileLayout;

    }

    /**
     * Reset the component to empty values.
     */
    public void initClean() {
        dpuName.setValue("");
        dpuDescription.setValue("");
        groupVisibility.setValue(ShareType.PUBLIC_RO);
        uploadFile.setReadOnly(false);
        uploadFile.setValue("");
        uploadFile.setReadOnly(true);
        // clean zip version
        groupVisibilityZip.setValue(ShareType.PUBLIC_RO);
        uploadFileZip.setReadOnly(false);
        uploadFileZip.setValue("");
        uploadFileZip.setReadOnly(true);
    }
}
