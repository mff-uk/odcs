package cz.cuni.mff.xrg.odcs.politicalDonationExtractor.core;

import java.io.*;
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

import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.configuration.DPUConfigObject;
import cz.cuni.mff.xrg.odcs.commons.module.dialog.BaseConfigDialog;
import cz.cuni.mff.xrg.odcs.rdf.enums.FileExtractType;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;

/**
 * Configuration dialog for DPU RDF File Extractor.
 * 
 * @author Maria Kukhar
 * @author Jiri Tomes
 * 
 * 
 */
public class CsvPoliticalExtractorDialog extends BaseConfigDialog<CsvPoliticalExtractorConfig> {
    private GridLayout mainLayout;

    /**
     * ComboBox to set RDF format (Auto, RDF/XML, TTL, TriG, N3)
     */
    private ComboBox comboBoxFormat;

    private CheckBox useHandler; // Statistical handler

    private CheckBox failWhenErrors; // How to solve errors for Statistical handler

    /**
     * TextField for set file extension that will be processed in some directory. Uses in case of FileExtractType.PATH_TO_DIRECTORY of {@link #pathType}
     */
    private TextField textFieldOnly;

    /**
     * TextField to set destination of the file
     */

    private HorizontalLayout horizontalLayoutOnly;

    private HorizontalLayout horizontalLayoutFormat;

    private TextField textFieldPath;

    private TextField textFieldTargetPath;

    private TextField textFieldDebugProcessOnlyNItems;

    private TextField textFieldBatchSize;

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

    /**
     * Basic constructor.
     */
    public CsvPoliticalExtractorDialog() {
        super(CsvPoliticalExtractorConfig.class);
        inicialize();
        buildMainLayout();
        setCompositionRoot(mainLayout);
        mapData();
    }

    /**
     * Initialization of Configuration dialog for DPU RDF File Extractor
     */
    private void inicialize() {
        extractType = FileExtractType.UPLOAD_FILE;
        ex = new InvalidValueException("Valid");
    }

    /**
     * Set format data to {@link #comboBoxFormat} and type data to OptionGroup {@link #pathType}
     */
    private void mapData() {

        for (RDFFormatType next : RDFFormatType.getListOfRDFType()) {
            comboBoxFormat.addItem(next);
        }

        comboBoxFormat.setValue(RDFFormatType.AUTO);

        pathType.addItem(FileExtractType.getDescriptionByType(FileExtractType.UPLOAD_FILE));
        pathType.addItem(FileExtractType.getDescriptionByType(FileExtractType.PATH_TO_FILE));
        // TODO to extend this functionality
        // pathType.addItem(FileExtractType.getDescriptionByType(FileExtractType.PATH_TO_DIRECTORY));
        // pathType.addItem(FileExtractType.getDescriptionByType(FileExtractType.PATH_TO_DIRECTORY_SKIP_PROBLEM_FILES));
        pathType.addItem(FileExtractType.getDescriptionByType(FileExtractType.HTTP_URL));

        pathType.setValue(FileExtractType.getDescriptionByType(extractType));

    }

    /**
     * Set values from from dialog where the configuration object may be edited to configuration object implementing {@link DPUConfigObject} interface and
     * configuring DPU
     * 
     * @throws ConfigException
     *             Exception which might be thrown when field {@link #textFieldPath} contains null value. // * @return conf Object holding configuration which
     *             is used in {@link #setConfiguration} to initialize fields in the configuration dialog.
     */
    @Override
    public CsvPoliticalExtractorConfig getConfiguration() throws ConfigException {

        if (!textFieldPath.isValid()) {
            throw new ConfigException(ex.getMessage(), ex);
        } else {
            CsvPoliticalExtractorConfig conf = new CsvPoliticalExtractorConfig();

            if (extractType == FileExtractType.UPLOAD_FILE) {
                conf.Path = FileUploadReceiver.path + "/" + textFieldPath.getValue().trim();

            } else {
                conf.Path = textFieldPath.getValue().trim();
            }

            if (extractType == FileExtractType.PATH_TO_DIRECTORY || extractType == FileExtractType.PATH_TO_DIRECTORY_SKIP_PROBLEM_FILES) {

                conf.FileSuffix = textFieldOnly.getValue().trim();

                if (textFieldOnly.getValue().trim().isEmpty()) {
                    conf.OnlyThisSuffix = false;
                } else {
                    conf.OnlyThisSuffix = true;
                }

            } else {
                conf.FileSuffix = "";
                conf.OnlyThisSuffix = false;
            }

            conf.RDFFormatValue = (RDFFormatType) comboBoxFormat.getValue();
            conf.UseStatisticalHandler = useHandler.getValue();
            conf.failWhenErrors = failWhenErrors.getValue();

            conf.fileExtractType = extractType;

            conf.TargetRDF = textFieldTargetPath.getValue().trim();

            conf.DebugProcessOnlyNItems = Integer.parseInt(textFieldDebugProcessOnlyNItems.getValue().trim());

            conf.BatchSize = Integer.parseInt(textFieldBatchSize.getValue().trim());
            return conf;
        }
    }

    /**
     * Load values from configuration object implementing {@link DPUConfigObject} interface and configuring DPU into the dialog where the configuration object
     * may be edited.
     * 
     * @throws ConfigException
     *             Exception not used in current implementation of this method.
     * @param conf
     *            Object holding configuration which is used to initialize fields in the configuration dialog.
     */
    @Override
    public void setConfiguration(CsvPoliticalExtractorConfig conf) {

        extractType = conf.fileExtractType;
        pathType.setValue(FileExtractType.getDescriptionByType(extractType));

        if (extractType == FileExtractType.UPLOAD_FILE) {

            String filepath = conf.Path.trim();
            String filename = filepath.substring(filepath.lastIndexOf("/") + 1, filepath.length());

            textFieldPath.setReadOnly(false); // allow value settings
            textFieldPath.setValue(filename.trim()); // set value
            textFieldPath.setReadOnly(true); // forbid

        } else {
            textFieldPath.setValue(conf.Path.trim());
        }

        if (extractType == FileExtractType.PATH_TO_DIRECTORY || extractType == FileExtractType.PATH_TO_DIRECTORY_SKIP_PROBLEM_FILES) {

            textFieldOnly.setValue(conf.FileSuffix.trim());
        }

        comboBoxFormat.setValue(conf.RDFFormatValue);
        useHandler.setValue(conf.UseStatisticalHandler);
        failWhenErrors.setValue(conf.failWhenErrors);

        textFieldTargetPath.setValue(conf.TargetRDF);
        textFieldDebugProcessOnlyNItems.setValue(String.valueOf(conf.DebugProcessOnlyNItems));
        textFieldBatchSize.setValue(String.valueOf(conf.BatchSize));

    }

    @Override
    public String getDescription() {
        String path;
        if (extractType == FileExtractType.UPLOAD_FILE) {
            path = FileUploadReceiver.path + "/" + textFieldPath.getValue().trim();
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
     * @return mainLayout GridLayout with all components of configuration dialog.
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
     * Return message to {@link #pathType} in accordance with file extract type of the item.
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
     * Builds layout contains Core tab components of {@link #tabSheet}. Calls from {@link #buildMainLayout}
     * 
     * @return gridLayoutCore. GridLayout with components located at the Core tab.
     */
    private GridLayout buildGridLayoutCore() {
        // common part: create layout
        gridLayoutCore = new GridLayout(1, 7);
        gridLayoutCore.setImmediate(false);
        gridLayoutCore.setWidth("100%");
        gridLayoutCore.setHeight("100%");
        gridLayoutCore.setMargin(true);
        gridLayoutCore.setSpacing(true);

        // OptionGroup for path type definition
        pathType = new OptionGroup();
        pathType.setImmediate(true);
        pathType.setWidth("-1px");
        pathType.setHeight("-1px");
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

                            if (extractType == FileExtractType.HTTP_URL) {
                                if (!stringValue.toLowerCase().startsWith("http://")) {

                                    String message = getValidMessageByFileExtractType(extractType);
                                    ex = new InvalidValueException(message);
                                    throw ex;
                                }
                            } else {
                                if (stringValue.isEmpty()) {

                                    String message = getValidMessageByFileExtractType(extractType);
                                    ex = new EmptyValueException(message);
                                    throw ex;
                                }
                            }
                        } else {
                            ex = new InvalidValueException("Value is not string type");
                            throw ex;
                        }
                    }
                });

                // If selected "Extract uploaded file" option
                if (event.getProperty().getValue().equals(FileExtractType.getDescriptionByType(FileExtractType.UPLOAD_FILE))) {

                    extractType = FileExtractType.UPLOAD_FILE;
                    fileUploadReceiver = new FileUploadReceiver();

                    // Upload component
                    fileUpload = new Upload(null, fileUploadReceiver);
                    fileUpload.setImmediate(true);
                    fileUpload.setButtonCaption("Choose file");
                    // Upload started event listener
                    fileUpload.addStartedListener(new StartedListener() {
                        @Override
                        public void uploadStarted(final StartedEvent event) {

                            if (uploadInfoWindow.getParent() == null) {
                                UI.getCurrent().addWindow(uploadInfoWindow);
                            }
                            uploadInfoWindow.setClosable(false);

                        }
                    });
                    // Upload received event listener.
                    fileUpload.addFinishedListener(new Upload.FinishedListener() {
                        @Override
                        public void uploadFinished(final FinishedEvent event) {

                            uploadInfoWindow.setClosable(true);
                            uploadInfoWindow.close();
                            // If upload wasn't interrupt by user
                            if (fl == 0) {
                                textFieldPath.setReadOnly(false);
                                // File was upload to the temp folder.
                                // Path to this file is setting to the textFieldPath field
                                textFieldPath.setValue(FileUploadReceiver.fileName.toString());
                                textFieldPath.setReadOnly(true);
                            } // If upload was interrupt by user
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

                    // Adding uploading component
                    gridLayoutCore.addComponent(uploadFileLayout, 0, 1);

                    // If selected "Extract file based on the path to file" option
                } else if (event.getProperty().getValue().equals(FileExtractType.getDescriptionByType(FileExtractType.PATH_TO_FILE))) {

                    extractType = FileExtractType.PATH_TO_FILE;

                    textFieldPath.setInputPrompt("C:\\ted\\test.ttl");

                    // Adding component for specify path to file
                    gridLayoutCore.addComponent(textFieldPath, 0, 1);
                    // TODO to extend this functionality

                    // If selected "Extract file based on the path to the directory" option
                    // } else if (event.getProperty().getValue().equals(FileExtractType.getDescriptionByType(FileExtractType.PATH_TO_DIRECTORY))) {
                    //
                    // extractType = FileExtractType.PATH_TO_DIRECTORY;
                    // prepareDirectoryForm();
                    //
                    // } else if
                    // (event.getProperty().getValue().equals(FileExtractType.getDescriptionByType(FileExtractType.PATH_TO_DIRECTORY_SKIP_PROBLEM_FILES))) {
                    //
                    // extractType = FileExtractType.PATH_TO_DIRECTORY_SKIP_PROBLEM_FILES;
                    // prepareDirectoryForm();
                    //
                    // If selected "Extract file from the given HTTP URL" option
                } else if (event.getProperty().getValue().equals(FileExtractType.getDescriptionByType(FileExtractType.HTTP_URL))) {

                    extractType = FileExtractType.HTTP_URL;

                    textFieldPath.setInputPrompt("http://");

                    // Adding component for specify HTTP URL
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

        FormLayout fl = new FormLayout();
        fl.setWidth("100%");

        textFieldTargetPath = new TextField("RDF Format save to directory:");
        textFieldTargetPath.setInputPrompt("C:\\");
        textFieldTargetPath.setNullRepresentation("");
        textFieldTargetPath.setWidth("100%");
        textFieldTargetPath.setHeight("-1px");

        textFieldDebugProcessOnlyNItems = new TextField("Process only N items");
        textFieldDebugProcessOnlyNItems.setInputPrompt("10");
        textFieldDebugProcessOnlyNItems.setWidth("100%");
        textFieldDebugProcessOnlyNItems.setHeight("-1px");

        textFieldBatchSize = new TextField("Size of batch");
        textFieldBatchSize.setInputPrompt("10");
        textFieldBatchSize.setWidth("100%");
        textFieldBatchSize.setHeight("-1px");

        fl.addComponent(textFieldTargetPath);
        fl.addComponent(textFieldDebugProcessOnlyNItems);
        fl.addComponent(textFieldBatchSize);
        gridLayoutCore.addComponent(fl, 0, 4);
        return gridLayoutCore;
    }

    private void prepareDirectoryForm() {
        textFieldPath.setInputPrompt("C:\\ted\\");

        // Adding component for specify path to directory
        gridLayoutCore.addComponent(textFieldPath, 0, 1);

        // layoutOnly

        horizontalLayoutOnly = new HorizontalLayout();
        horizontalLayoutOnly.setImmediate(false);
        horizontalLayoutOnly.setSpacing(true);

        horizontalLayoutOnly.addComponent(new Label("If directory, process only files with extension:"));

        // textFieldOnly
        textFieldOnly = new TextField("");
        textFieldOnly.setImmediate(false);
        textFieldOnly.setWidth("50px");
        textFieldOnly.setInputPrompt(".ttl");
        horizontalLayoutOnly.addComponent(textFieldOnly);
        horizontalLayoutOnly.setComponentAlignment(textFieldOnly, Alignment.TOP_RIGHT);

        // Adding component for specify file extension
        gridLayoutCore.addComponent(horizontalLayoutOnly, 0, 2);

    }

    /**
     * Builds layout contains Details tab components of {@link #tabSheet}. Calls from {@link #buildMainLayout}
     * 
     * @return verticalLayoutDetails. VerticalLayout with components located at the Details tab.
     */
    private VerticalLayout buildVerticalLayoutDetails() {
        // common part: create layout
        verticalLayoutDetails = new VerticalLayout();
        verticalLayoutDetails.setImmediate(false);
        verticalLayoutDetails.setWidth("100%");
        verticalLayoutDetails.setHeight("-1px");
        verticalLayoutDetails.setMargin(true);
        verticalLayoutDetails.setSpacing(true);

        // Statistical handler
        useHandler = new CheckBox("Use statistical and error handler");
        useHandler.setValue(true);
        useHandler.setWidth("-1px");
        useHandler.setHeight("-1px");
        useHandler.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                if (failWhenErrors != null) {
                    failWhenErrors.setVisible(useHandler.getValue());
                }
            }
        });

        // How to solve errors for Statistical handler
        failWhenErrors = new CheckBox("If there is an error in some of the extracted triples," + " extractor ends with an error");
        failWhenErrors.setValue(false);
        failWhenErrors.setWidth("-1px");
        failWhenErrors.setHeight("-1px");
        failWhenErrors.setVisible(useHandler.getValue());

        verticalLayoutDetails.addComponent(useHandler);
        verticalLayoutDetails.addComponent(failWhenErrors);

        return verticalLayoutDetails;
    }
}

// TODO: Petyr move the next two classes: UploadInfoWindow and FileUploadReceiver to
// commons-web for enable to use it also from fronted
/**
 * Dialog for uploading status. Appear automatically after file upload start.
 * 
 * @author Maria Kukhar
 * 
 */
class UploadInfoWindow extends Window implements Upload.StartedListener, Upload.ProgressListener, Upload.FinishedListener {

    private static final long serialVersionUID = 1L;

    private final Label state = new Label();

    private final Label fileName = new Label();

    private final Label textualProgress = new Label();

    private final ProgressIndicator pi = new ProgressIndicator();

    private final Button cancelButton;

    private final Upload upload;

    /**
     * Basic constructor
     * 
     * @param nextUpload
     *            Upload component
     */
    public UploadInfoWindow(Upload nextUpload) {

        super("Status");
        this.upload = nextUpload;
        this.cancelButton = new Button("Cancel");

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
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                upload.interruptUpload();
                CsvPoliticalExtractorDialog.fl = 1;
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

        // progress indicator
        pi.setCaption("Progress");
        pi.setVisible(false);
        formLayout.addComponent(pi);

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
        pi.setVisible(false);
        textualProgress.setVisible(false);
        cancelButton.setVisible(false);

    }

    /**
     * this method gets called immediately after upload is started
     */
    @Override
    public void uploadStarted(final StartedEvent event) {

        pi.setValue(0f);
        pi.setVisible(true);
        pi.setPollingInterval(500); // hit server frequantly to get
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
        pi.setValue(new Float(readBytes / (float) contentLength));
        textualProgress.setValue("Processed " + (readBytes / 1024) + " k bytes of " + (contentLength / 1024) + " k");
    }
}

/**
 * Upload selected file to template directory
 * 
 * @author Maria Kukhar
 * 
 */
class FileUploadReceiver implements Receiver {

    private static final long serialVersionUID = 5099459605355200117L;

    private static final int searchedByte = '\n';

    private static int total = 0;

    private boolean sleep = false;

    public static String fileName;

    public static File file;

    public static Path path;

    /**
     * return an OutputStream
     */
    @Override
    public OutputStream receiveUpload(final String filename, final String MIMEType) {
        fileName = filename;

        try {
            // create template directory
            path = Files.createTempDirectory("Upload");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        file = new File("/" + path + "/" + filename); // path for upload file in temp directory

        OutputStream fos = null;

        try {
            final FileOutputStream fstream = new FileOutputStream(file);

            fos = new OutputStream() {
                @Override
                public void write(final int b) throws IOException {
                    total++;

                    fstream.write(b);

                }

                @Override
                public void write(byte b[], int off, int len) throws IOException {
                    if (b == null) {
                        throw new NullPointerException();
                    } else if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0)) {
                        throw new IndexOutOfBoundsException();
                    } else if (len == 0) {
                        return;
                    }
                    fstream.write(b, off, len);
                    total += len;

                }

                @Override
                public void close() throws IOException {
                    fstream.close();
                    super.close();
                }
            };

        } catch (FileNotFoundException e) {
            new Notification("Could not open file<br/>", e.getMessage(), Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
        } finally {
            return fos;

        }

    }
}
