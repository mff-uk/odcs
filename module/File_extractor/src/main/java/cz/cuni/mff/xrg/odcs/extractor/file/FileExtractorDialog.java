package cz.cuni.mff.xrg.odcs.extractor.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;

import cz.cuni.mff.xrg.odcs.commons.module.dialog.BaseConfigDialog;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;
import eu.unifiedviews.dpu.config.DPUConfigException;

/**
 * Configuration dialog for DPU RDF File Extractor.
 * 
 * @author Maria Kukhar
 * @author Jiri Tomes
 */
public class FileExtractorDialog extends BaseConfigDialog<FileExtractorConfig> {

    private GridLayout mainLayout;

    /**
     * ComboBox to set RDF format (Auto, RDF/XML, TTL, TriG, N3)
     */
    private ComboBox comboBoxFormat;

    private CheckBox useHandler; //Statistical handler

    private OptionGroup failsWhenErrors; // How to solve errors for Statistical handler

    /**
     * TextField for set file extension that will be processed in some
     * directory. Uses in case of FileExtractType.PATH_TO_DIRECTORY of {@link #pathType}
     */
    private TextField textFieldOnly;

    private OptionGroup directoryGroup;

    /**
     * TextField to set destination of the file
     */
    private TextField textFieldPath;

    private VerticalLayout verticalLayoutOnly;

    private HorizontalLayout horizontalLayoutFormat;

    /**
     * OptionGroup for path type definition
     */
    private OptionGroup pathType;

    private FileExtractType extractType;

    private InvalidValueException ex;

    private FileUploadReceiver fileUploadReceiver;

    private Upload fileUpload;

    private UploadInfoWindow uploadInfoWindow;

    static int fl = 0;

    /**
     * TabSheet of Configuration dialog. Contains two tabs: Core and Details
     */
    private TabSheet tabSheet;

    private GridLayout gridLayoutCore;

    private VerticalLayout verticalLayoutDetails;

    private static final String STOP = "Stop pipeline execution if extractor "
            + "extracted some triples with an error.";

    private static final String CONTINUE = "Extract only triples with no errors. "
            + "\nIf fatal error is discovered, pipeline is stopped.";

    private static final String FILE_SKIP = "When there is a problem parsing a file, it is skipped";

    private static final String FILE_ERROR = "When there is a program parsing a file, extraction ends with error";

    /**
     * Basic constructor.
     */
    public FileExtractorDialog() {
        super(FileExtractorConfig.class);
        initialize();
        buildMainLayout();
        setCompositionRoot(mainLayout);
        mapData();
    }

    /**
     * Initialization of Configuration dialog for DPU RDF File Extractor
     */
    private void initialize() {
        extractType = FileExtractType.UPLOAD_FILE;
        ex = new InvalidValueException("Valid");
    }

    /**
     * Set format data to {@link #comboBoxFormat} and type data to OptionGroup {@link #pathType}
     */
    private void mapData() {

        for (RDFFormatType next : RDFFormatType.getListOfRDFType()) {
            String value = RDFFormatType.getStringValue(next);
            comboBoxFormat.addItem(value);
        }

        comboBoxFormat.setValue(RDFFormatType.getStringValue(RDFFormatType.AUTO));

        pathType.addItem(FileExtractType.getDescriptionByType(
                FileExtractType.UPLOAD_FILE));
        pathType.addItem(FileExtractType.getDescriptionByType(
                FileExtractType.PATH_TO_FILE));
        pathType.addItem(FileExtractType.getDescriptionByType(
                FileExtractType.PATH_TO_DIRECTORY));
        pathType.addItem(FileExtractType.getDescriptionByType(
                FileExtractType.HTTP_URL));

        pathType.setValue(FileExtractType.getDescriptionByType(
                extractType));

    }

    /**
     * Set values from from dialog where the configuration object may be edited
     * to configuration object implementing {@link DPUConfigObject} interface
     * and configuring DPU
     * 
     * @throws ConfigException
     *             Exception which might be thrown when field {@link #textFieldPath} contains null value.
     * @return conf Object holding configuration which is used in {@link #setConfiguration} to initialize fields in the
     *         configuration dialog.
     */
    @Override
    public FileExtractorConfig getConfiguration() throws DPUConfigException {

        if (getContext().isTemplate()) {
        }

        if (!textFieldPath.isValid()) {
            throw new DPUConfigException(ex.getMessage(), ex);
        } else {

            String path;
            if (extractType == FileExtractType.UPLOAD_FILE) {
                path = fileUploadReceiver.getPath();

            } else {
                path = textFieldPath.getValue().trim();
            }

            String fileSuffix;
            boolean onlyThisSuffix;

            if (extractType == FileExtractType.PATH_TO_DIRECTORY
                    || extractType == FileExtractType.PATH_TO_DIRECTORY_SKIP_PROBLEM_FILES) {

                fileSuffix = textFieldOnly.getValue().trim();

                if (textFieldOnly.getValue().trim().isEmpty()) {
                    onlyThisSuffix = false;
                } else {
                    onlyThisSuffix = true;
                }

            } else {
                fileSuffix = "";
                onlyThisSuffix = false;
            }

            String formatValue = (String) comboBoxFormat.getValue();
            RDFFormatType RDFFormatValue = RDFFormatType.getTypeByString(
                    formatValue);

            boolean useStatisticalHandler = useHandler.getValue();

            String selectedValue = (String) failsWhenErrors.getValue();

            boolean failWhenErrors;
            if (selectedValue.equals(STOP)) {
                failWhenErrors = true;
            } else if (selectedValue.endsWith(CONTINUE)) {
                failWhenErrors = false;
            } else {
                throw new DPUConfigException(
                        "No value for case using statistical and error handler");
            }

            FileExtractorConfig config = new FileExtractorConfig(path,
                    fileSuffix,
                    RDFFormatValue, extractType, onlyThisSuffix,
                    useStatisticalHandler, failWhenErrors);

            return config;
        }
    }

    /**
     * Load values from configuration object implementing {@link DPUConfigObject} interface and configuring DPU into the dialog
     * where the configuration object may be edited.
     * 
     * @param conf
     *            Object holding configuration which is used to initialize
     *            fields in the configuration dialog.
     */
    @Override
    public void setConfiguration(FileExtractorConfig conf) {

        extractType = conf.getFileExtractType();
        pathType.setValue(FileExtractType.getDescriptionByType(
                extractType));

        String path = conf.getPath().trim();

        if (extractType == FileExtractType.UPLOAD_FILE) {

            File file = new File(path);
            String filename = file.getName();

            fileUploadReceiver.setFileName(filename);
            fileUploadReceiver.setPath(path);

            textFieldPath.setReadOnly(false); // allow value settings
            textFieldPath.setValue(filename.trim()); // set value
            textFieldPath.setReadOnly(true); // forbid

        } else {
            textFieldPath.setValue(path);
        }

        if (extractType == FileExtractType.PATH_TO_DIRECTORY
                || extractType == FileExtractType.PATH_TO_DIRECTORY_SKIP_PROBLEM_FILES) {

            textFieldOnly.setValue(conf.getFileSuffix().trim());

            if (extractType == FileExtractType.PATH_TO_DIRECTORY) {
                directoryGroup.setValue(FILE_ERROR);
            } else {
                directoryGroup.setValue(FILE_SKIP);
            }
        }

        String formatValue = RDFFormatType.getStringValue(conf
                .getRDFFormatValue());

        comboBoxFormat.setValue(formatValue);

        useHandler.setValue(conf.isUsedStatisticalHandler());

        if (conf.isFailWhenErrors()) {
            failsWhenErrors.setValue(STOP);
        } else {
            failsWhenErrors.setValue(CONTINUE);
        }

    }

    /**
     * @return description of file extractor as string.
     */
    @Override
    public String getDescription() {
        String path;
        if (extractType == FileExtractType.UPLOAD_FILE) {
            path = fileUploadReceiver.getPath();
        } else {
            path = textFieldPath.getValue().trim();
        }
        // create description
        StringBuilder description = new StringBuilder();
        description.append("Extract from: ");
        description.append(path);
        return description.toString();
    }

    /**
     * Builds main layout contains {@link #tabSheet} with all dialog components.
     * 
     * @return mainLayout GridLayout with all components of configuration
     *         dialog.
     */
    private GridLayout buildMainLayout() {

        // common part: create layout
        mainLayout = new GridLayout(1, 1);
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");
        mainLayout.setMargin(false);
        mainLayout.setSpacing(true);

        // top-level component properties
        setWidth("100%");
        setHeight("100%");

        // create tabSheet
        tabSheet = new TabSheet();
        tabSheet.setImmediate(true);
        tabSheet.setWidth("100%");
        tabSheet.setHeight("100%");

        // Core tab
        gridLayoutCore = buildGridLayoutCore();
        tabSheet.addTab(gridLayoutCore, "Core", null);

        // Details tab
        verticalLayoutDetails = buildVerticalLayoutDetails();
        tabSheet.addTab(verticalLayoutDetails, "Details", null);

        mainLayout.addComponent(tabSheet, 0, 0);

        return mainLayout;
    }

    /**
     * Return message to {@link #pathType} in accordance with file extract type
     * of the item.
     * 
     * @param type
     *            FileExtractType of {@link #pathType} item
     * @return message. String that assign to the {@link #pathType} item
     */
    private String getValidMessageByFileExtractType(FileExtractType type) {

        String message = "";

        switch (type) {
            case HTTP_URL:
                message = "URL path must start with prefix http://";
                break;
            case PATH_TO_DIRECTORY:
            case PATH_TO_DIRECTORY_SKIP_PROBLEM_FILES:
                message = "Path to directory must be filled, not empty.";
                break;
            case PATH_TO_FILE:
                message = "Path to file must be filled, not empty.";
                break;
            case UPLOAD_FILE:
                message = "Path to upload file must not be empty.";
                break;
        }

        return message;

    }

    /**
     * Builds layout contains Core tab components of {@link #tabSheet}. Calls
     * from {@link #buildMainLayout}
     * 
     * @return gridLayoutCore. GridLayout with components located at the Core
     *         tab.
     */
    private GridLayout buildGridLayoutCore() {
        // common part: create layout
        gridLayoutCore = new GridLayout(1, 4);
        gridLayoutCore.setImmediate(false);
        gridLayoutCore.setWidth("100%");
        gridLayoutCore.setHeight("-1px");
        gridLayoutCore.setMargin(true);
        gridLayoutCore.setSpacing(true);

        //create fileUploadReceiver
        fileUploadReceiver = new FileUploadReceiver();

        // OptionGroup for path type definition
        pathType = new OptionGroup();
        pathType.setImmediate(true);
        pathType.setSizeUndefined();
        pathType.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {

                gridLayoutCore.removeComponent(0, 1);
                gridLayoutCore.removeComponent(0, 2);

                // text field for path to file/directory, HTTP URL or path to upload file
                textFieldPath = new TextField();
                textFieldPath.setNullRepresentation("");
                textFieldPath.setImmediate(true);
                textFieldPath.setWidth("100%");
                textFieldPath.setHeight("-1px");

                textFieldPath.addValidator(new Validator() {
                    @Override
                    public void validate(Object value) throws InvalidValueException {
                        Class<?> myClass = value.getClass();

                        if (myClass.equals(String.class)) {
                            String stringValue = (String) value;

                            if (extractType
                            == FileExtractType.HTTP_URL) {
                                if (!stringValue.toLowerCase().startsWith(
                                        "http://")) {

                                    String message = getValidMessageByFileExtractType(
                                            extractType);
                                    ex = new InvalidValueException(message);
                                    throw ex;
                                }
                            } else {

                                if (!getContext().isTemplate() && stringValue
                                        .isEmpty()) {
                                    String message = getValidMessageByFileExtractType(
                                            extractType);
                                    ex = new EmptyValueException(message);
                                    throw ex;

                                }
                            }
                        } else {
                            ex = new InvalidValueException(
                                    "Value is not string type");
                            throw ex;
                        }
                    }
                });

                //If selected "Extract uploaded file" option
                if (event.getProperty().getValue().equals(
                        FileExtractType.getDescriptionByType(
                                FileExtractType.UPLOAD_FILE))) {

                    extractType = FileExtractType.UPLOAD_FILE;

                    //Upload component
                    fileUpload = new Upload("", fileUploadReceiver);
                    fileUpload.setImmediate(true);
                    fileUpload.setButtonCaption("Choose file");
                    //Upload started event listener
                    fileUpload.addStartedListener(new StartedListener() {
                        @Override
                        public void uploadStarted(final StartedEvent event) {

                            if (uploadInfoWindow.getParent() == null) {
                                UI.getCurrent().addWindow(uploadInfoWindow);
                            }
                            uploadInfoWindow.setClosable(false);

                        }
                    });
                    //Upload received event listener. 
                    fileUpload.addFinishedListener(
                            new Upload.FinishedListener() {
                                @Override
                                public void uploadFinished(final FinishedEvent event) {

                                    uploadInfoWindow.setClosable(true);
                                    uploadInfoWindow.close();
                                    //If upload wasn't interrupt by user
                                    if (fl == 0) {
                                        textFieldPath.setReadOnly(false);
                                        //File was upload to the temp folder. 
                                        //Path to this file is setting to the textFieldPath field
                                        textFieldPath.setValue(
                                                fileUploadReceiver.getFileName());
                                        textFieldPath.setReadOnly(true);
                                    } //If upload was interrupt by user
                                    else {
                                        textFieldPath.setReadOnly(false);
                                        textFieldPath.setValue("");
                                        textFieldPath.setReadOnly(true);
                                        fl = 0;
                                    }
                                }
                            });

                    // The window with upload information
                    uploadInfoWindow = new UploadInfoWindow(fileUpload);

                    HorizontalLayout uploadFileLayout = new HorizontalLayout();
                    uploadFileLayout.setWidth("100%");
                    uploadFileLayout.setSpacing(true);

                    textFieldPath.setReadOnly(true);
                    uploadFileLayout.addComponent(fileUpload);
                    uploadFileLayout.addComponent(textFieldPath);
                    uploadFileLayout.setExpandRatio(fileUpload, 0.2f);
                    uploadFileLayout.setExpandRatio(textFieldPath, 0.8f);

                    //Adding uploading component
                    gridLayoutCore.addComponent(uploadFileLayout, 0, 1);

                    //If selected "Extract file based on the path to file" option
                } else if (event.getProperty().getValue().equals(
                        FileExtractType.getDescriptionByType(
                                FileExtractType.PATH_TO_FILE))) {

                    extractType = FileExtractType.PATH_TO_FILE;

                    textFieldPath.setInputPrompt("C:\\ted\\test.ttl");

                    //Adding component for specify path to file
                    gridLayoutCore.addComponent(textFieldPath, 0, 1);

                    //If selected "Extract file based on the path to the directory" option
                } else if (event.getProperty().getValue().equals(FileExtractType
                        .getDescriptionByType(FileExtractType.PATH_TO_DIRECTORY))) {

                    extractType = FileExtractType.PATH_TO_DIRECTORY;
                    prepareDirectoryForm();

                } else if (event.getProperty().getValue().equals(
                        FileExtractType.getDescriptionByType(
                                FileExtractType.HTTP_URL))) {

                    extractType = FileExtractType.HTTP_URL;

                    textFieldPath.setInputPrompt("http://");

                    //Adding component for specify HTTP URL
                    gridLayoutCore.addComponent(textFieldPath, 0, 1);
                }
            }
        });
        gridLayoutCore.addComponent(pathType, 0, 0);

        // horizontalLayoutFormat
        horizontalLayoutFormat = new HorizontalLayout();
        horizontalLayoutFormat.setImmediate(false);
        horizontalLayoutFormat.setSpacing(true);

        horizontalLayoutFormat.addComponent(new Label("RDF Format:"));

        // comboBoxFormat
        comboBoxFormat = new ComboBox();
        comboBoxFormat.setImmediate(true);
        comboBoxFormat.setNewItemsAllowed(false);
        comboBoxFormat.setNullSelectionAllowed(false);
        horizontalLayoutFormat.addComponent(comboBoxFormat);

        gridLayoutCore.addComponent(horizontalLayoutFormat, 0, 3);

        return gridLayoutCore;
    }

    private void prepareDirectoryForm() {
        textFieldPath.setInputPrompt("C:\\ted\\");

        //Adding component for specify path to directory
        gridLayoutCore.addComponent(textFieldPath, 0, 1);

        // layoutOnly

        verticalLayoutOnly = new VerticalLayout();
        verticalLayoutOnly.setImmediate(false);
        verticalLayoutOnly.setSpacing(true);

        // textFieldOnly
        textFieldOnly = new TextField(
                "If directory, process only files with extension:");
        textFieldOnly.setImmediate(false);
        textFieldOnly.setWidth("50px");
        textFieldOnly.setInputPrompt(".ttl");
        verticalLayoutOnly.addComponent(textFieldOnly);

        directoryGroup = new OptionGroup();
        directoryGroup.setImmediate(false);
        directoryGroup.setWidth("-1px");
        directoryGroup.setHeight("-1px");
        directoryGroup.setMultiSelect(false);

        directoryGroup.addItem(FILE_SKIP);
        directoryGroup.addItem(FILE_ERROR);

        switch (extractType) {
            case PATH_TO_DIRECTORY:
                directoryGroup.setValue(FILE_ERROR);
                break;

            case PATH_TO_DIRECTORY_SKIP_PROBLEM_FILES:
            default:
                directoryGroup.setValue(FILE_SKIP);
                extractType = FileExtractType.PATH_TO_DIRECTORY_SKIP_PROBLEM_FILES;
                break;
        }

        directoryGroup.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                String selectedItem = directoryGroup.getValue().toString();

                switch (selectedItem) {
                    case FILE_SKIP:
                        extractType = FileExtractType.PATH_TO_DIRECTORY_SKIP_PROBLEM_FILES;
                        break;
                    case FILE_ERROR:
                        extractType = FileExtractType.PATH_TO_DIRECTORY;
                        break;
                }
            }
        });

        verticalLayoutOnly.addComponent(directoryGroup);

        //Adding component for specify file extension
        gridLayoutCore.addComponent(verticalLayoutOnly, 0, 2);

    }

    /**
     * Builds layout contains Details tab components of {@link #tabSheet}. Calls
     * from {@link #buildMainLayout}
     * 
     * @return verticalLayoutDetails. VerticalLayout with components located at
     *         the Details tab.
     */
    private VerticalLayout buildVerticalLayoutDetails() {
        // common part: create layout
        verticalLayoutDetails = new VerticalLayout();
        verticalLayoutDetails.setImmediate(false);
        verticalLayoutDetails.setWidth("100%");
        verticalLayoutDetails.setHeight("-1px");
        verticalLayoutDetails.setMargin(true);
        verticalLayoutDetails.setSpacing(true);

        //Statistical handler
        useHandler = new CheckBox("Use statistical and error handler");
        useHandler.setValue(true);
        useHandler.setWidth("-1px");
        useHandler.setHeight("-1px");
        useHandler.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {

                if (failsWhenErrors != null) {
                    failsWhenErrors.setEnabled(useHandler.getValue());
                }
            }
        });

        verticalLayoutDetails.addComponent(useHandler);

        //How to solve errors for Statistical handler
        failsWhenErrors = new OptionGroup();
        failsWhenErrors.setImmediate(false);
        failsWhenErrors.setWidth("-1px");
        failsWhenErrors.setHeight("-1px");
        failsWhenErrors.setMultiSelect(false);

        //extract only triples with no errors.
        failsWhenErrors.addItem(CONTINUE);
        //stop pipeline execution if extractor extracted some triples with an error.
        failsWhenErrors.addItem(STOP);

        failsWhenErrors.setValue(CONTINUE);
        failsWhenErrors.setEnabled(useHandler.getValue());

        verticalLayoutDetails.addComponent(failsWhenErrors);

        return verticalLayoutDetails;

    }
}

//TODO: Petyr move the next two classes: UploadInfoWindow and FileUploadReceiver to 
//commons-web for enable to use it also from fronted
/**
 * Dialog for uploading status. Appear automatically after file upload start.
 * 
 * @author Maria Kukhar
 */
class UploadInfoWindow extends Window implements Upload.StartedListener,
        Upload.ProgressListener, Upload.FinishedListener {

    private Label state;

    private Label fileName;

    private Label textualProgress;

    private ProgressBar progress;

    private Button cancelButton;

    private Upload upload;

    /**
     * Basic constructor
     * 
     * @param nextUpload
     *            Upload component
     */
    public UploadInfoWindow(Upload nextUpload) {

        super("Status");
        this.upload = nextUpload;
        this.state = new Label();
        this.fileName = new Label();
        this.textualProgress = new Label();
        this.progress = new ProgressBar();
        cancelButton = new Button("Cancel");

        setComponent();

    }

    private void setComponent() {
        addStyleName("upload-info");

        setResizable(false);
        setDraggable(false);

        final FormLayout formLayout = new FormLayout();
        setContent(formLayout);
        formLayout.setMargin(true);

        final HorizontalLayout stateLayout = new HorizontalLayout();
        stateLayout.setSpacing(true);
        stateLayout.addComponent(state);

        cancelButton.addClickListener(new Button.ClickListener() {
            /**
             * Upload interruption
             */
            @Override
            public void buttonClick(final ClickEvent event) {
                upload.interruptUpload();
                FileExtractorDialog.fl = 1;
            }
        });
        cancelButton.setVisible(false);
        cancelButton.setStyleName("small");
        stateLayout.addComponent(cancelButton);

        stateLayout.setCaption("Current state");
        state.setValue("Idle");
        formLayout.addComponent(stateLayout);

        fileName.setCaption("File name");
        formLayout.addComponent(fileName);

        //progress indicator
        progress.setCaption("Progress");
        progress.setVisible(false);
        formLayout.addComponent(progress);

        textualProgress.setVisible(false);
        formLayout.addComponent(textualProgress);

        upload.addStartedListener(this);
        upload.addProgressListener(this);
        upload.addFinishedListener(this);
    }

    /**
     * this method gets called immediately after upload is finished
     */
    @Override
    public void uploadFinished(final FinishedEvent event) {
        state.setValue("Idle");
        progress.setVisible(false);
        textualProgress.setVisible(false);
        cancelButton.setVisible(false);

    }

    /**
     * this method gets called immediately after upload is started
     */
    @Override
    public void uploadStarted(final StartedEvent event) {

        progress.setValue(0f);
        progress.setVisible(true);
        progress.getUI().setPollInterval(500); // hit server frequantly to get
        textualProgress.setVisible(true);
        // updates to client
        state.setValue("Uploading");
        fileName.setValue(event.getFilename());

        cancelButton.setVisible(true);
    }

    /**
     * this method shows update progress
     */
    @Override
    public void updateProgress(final long readBytes, final long contentLength) {
        // this method gets called several times during the update
        progress.setValue(new Float(readBytes / (float) contentLength));
        textualProgress.setValue(
                "Processed " + (readBytes / 1024) + " k bytes of "
                        + (contentLength / 1024) + " k");
    }
}

/**
 * Upload selected file to template directory
 * 
 * @author Maria Kukhar
 */
class FileUploadReceiver implements Receiver {

    private String path;

    private String fileName;

    public FileUploadReceiver() {
        this.path = "";
        this.fileName = "";
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public String getFileName() {
        return fileName;
    }

    private Path createDirectoryTempPath() {
        try {
            //create template directory
            Path newPath = Files.createTempDirectory("Upload");
            return newPath;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    /**
     * return an OutputStream
     */
    @Override
    public OutputStream receiveUpload(final String fileName,
            final String MIMEType) {

        // path for upload file in temp directory
        String dirPath = createDirectoryTempPath().toString();
        File file = new File(dirPath + "/" + fileName);

        setFileName(fileName);
        setPath(file.getAbsolutePath());

        try {
            FileOutputStream fstream = new FileOutputStream(file);
            return fstream;

        } catch (FileNotFoundException e) {
            new Notification("Could not open file<br/>", e.getMessage(),
                    Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
            return null;
        }

    }
}
