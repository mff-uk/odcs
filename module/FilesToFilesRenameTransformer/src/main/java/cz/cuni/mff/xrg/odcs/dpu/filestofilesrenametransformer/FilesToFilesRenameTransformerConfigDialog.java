package cz.cuni.mff.xrg.odcs.dpu.filestofilesrenametransformer;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.ObjectProperty;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 */
public class FilesToFilesRenameTransformerConfigDialog extends
        BaseConfigDialog<FilesToFilesRenameTransformerConfig> {

    /**
     * 
     */
    private static final long serialVersionUID = 63148374398039L;

    private static final Logger log = LoggerFactory
            .getLogger(FilesToFilesRenameTransformerConfigDialog.class);

    private static final String SKIP_ON_ERROR_LABEL = "Skip file on error";

    private Label lFileName;

    private VerticalLayout mainLayout;

    private TextArea taXSLTemplate;

    private UploadInfoWindow uploadInfoWindow;

    private ObjectProperty<Boolean> skipOnError = new ObjectProperty<Boolean>(
            false);

    // TODO refactor
    static int fl = 0;

    public FilesToFilesRenameTransformerConfigDialog() {
        super(FilesToFilesRenameTransformerConfig.class);
        buildMainLayout();
        Panel panel = new Panel();
        panel.setSizeFull();
        panel.setContent(mainLayout);
        setCompositionRoot(panel);
        // setCompositionRoot(mainLayout);
        // setCompositionRoot(p);
    }

    private VerticalLayout buildMainLayout() {
        // common part: create layout
        mainLayout = new VerticalLayout();
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("-1px");
        mainLayout.setMargin(false);
        mainLayout.setSpacing(true);

        mainLayout.addComponent(new CheckBox(SKIP_ON_ERROR_LABEL, skipOnError));

        // top-level component properties
        setWidth("100%");
        setHeight("100%");

        // upload
        final FileUploadReceiver fileUploadReceiver = new FileUploadReceiver();

        // Upload component
        Upload fileUpload = new Upload("XSLT Template: ", fileUploadReceiver);
        fileUpload.setImmediate(true);
        fileUpload.setButtonCaption("Upload");
        // Upload started event listener
        fileUpload.addStartedListener(new Upload.StartedListener() {
            /**
             * 
             */
            private static final long serialVersionUID = -4167203924388153623L;

            @Override
            public void uploadStarted(final Upload.StartedEvent event) {

                if (uploadInfoWindow.getParent() == null) {
                    UI.getCurrent().addWindow(uploadInfoWindow);
                }
                uploadInfoWindow.setClosable(false);

            }
        });
        // Upload received event listener.
        fileUpload.addFinishedListener(new Upload.FinishedListener() {
            /**
                     * 
                     */
            private static final long serialVersionUID = -7276225240612908058L;

            @Override
            public void uploadFinished(final Upload.FinishedEvent event) {

                uploadInfoWindow.setClosable(true);
                uploadInfoWindow.close();
                // If upload wasn't interrupt by user
                if (fl == 0) {

                    String configText = null;
                    try {
                        configText = ((ByteArrayOutputStream) fileUploadReceiver
                                .getOutputStream()).toString("UTF-8");
                    } catch (UnsupportedEncodingException ex) {
                        log.error("Error", ex);
                    }
                    if (configText == null) {
                        log.error("Cannot save XSLT template with UTF-8 encoding");
                        return;
                    }

                    taXSLTemplate.setValue(configText);

                    // to get the current date:
                    DateFormat dateFormat = new SimpleDateFormat(
                            "yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();

                    lFileName.setValue("File "
                            + fileUploadReceiver.getFileName()
                            + " was successfully uploaded on: "
                            + dateFormat.format(date));

                    //
                } else {
                    // textFieldPath.setReadOnly(false);
                    taXSLTemplate.setValue("");
                    // textFieldPath.setReadOnly(true);
                    fl = 0;
                }
            }

        });

        // The window with upload information
        uploadInfoWindow = new UploadInfoWindow(fileUpload);

        mainLayout.addComponent(fileUpload);

        // label for xslt filename
        lFileName = new Label("File not uploaded");
        mainLayout.addComponent(lFileName);

        Label lInput = new Label();
        lInput.setValue("Input:");
        mainLayout.addComponent(lInput);

        // ***************
        // TEXT AREA
        // ***************
        // //empty line
        // Label emptyLabel5 = new Label("");
        // emptyLabel4.setHeight("1em");
        // mainLayout.addComponent(emptyLabel5);

        taXSLTemplate = new TextArea();

        //
        taXSLTemplate.setNullRepresentation("");
        taXSLTemplate.setImmediate(false);
        taXSLTemplate.setWidth("100%");
        taXSLTemplate.setHeight("300px");
        taXSLTemplate.setVisible(true);
        // silkConfigTextArea.setInputPrompt(
        // "PREFIX br:<http://purl.org/business-register#>\nMODIFY\nDELETE { ?s pc:contact ?o}\nINSERT { ?s br:contact ?o}\nWHERE {\n\t     ?s a gr:BusinessEntity .\n\t      ?s pc:contact ?o\n}");

        mainLayout.addComponent(taXSLTemplate);
        // mainLayout.setColumnExpandRatio(0, 0.00001f);
        // mainLayout.setColumnExpandRatio(1, 0.99999f);

        return mainLayout;
    }

    @Override
    public void setConfiguration(FilesToFilesRenameTransformerConfig conf)
            throws DPUConfigException {
        // get configuration from the CONFIG object to dialog

        if (!conf.getXslTemplate().isEmpty()) {
            taXSLTemplate.setValue(conf.getXslTemplate());
            lFileName.setValue(conf.getXslTemplateFileNameShownInDialog());
        }
        skipOnError.setValue(conf.isSkipOnError());
    }

    @Override
    public FilesToFilesRenameTransformerConfig getConfiguration()
            throws DPUConfigException {
        // get the conf from the dialog

        // check that certain xslt was uploaded
        if (taXSLTemplate.getValue().trim().isEmpty()) {
            // no config!
            throw new DPUConfigException("No configuration file uploaded");

        }

        // prepare output type:
        // TODO storing the textarea content not needed - not readed when the
        // configuration is shown
        FilesToFilesRenameTransformerConfig conf = new FilesToFilesRenameTransformerConfig();
        conf.setXslTemplate(taXSLTemplate.getValue());
        conf.setXslTemplateFileNameShownInDialog(lFileName.getValue().trim());
        conf.setSkipOnError(skipOnError.getValue());
        return conf;

    }
}

/**
 * Upload selected file to template directory
 */
class FileUploadReceiver implements Upload.Receiver {

    private static final long serialVersionUID = 5099459605355200117L;

    // private static final int searchedByte = '\n';
    // private static int total = 0;
    // private boolean sleep = false;
    // public static String fileName;
    // public static File file;
    // public static Path path;
    // private DPUContext context;

    private String fileName;

    private OutputStream fos;

    public String getFileName() {
        return fileName;
    }

    public OutputStream getOutputStream() {
        return fos;
    }

    /**
     * return an OutputStream
     */
    @Override
    public OutputStream receiveUpload(final String filename,
            final String MIMEType) {

        this.fileName = filename;
        fos = new ByteArrayOutputStream();
        return fos;

    }

}

/**
 * Dialog for uploading status. Appear automatically after file upload start.
 * 
 * @author tknap
 */
class UploadInfoWindow extends Window implements Upload.StartedListener,
        Upload.ProgressListener, Upload.FinishedListener {

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
     * @param upload
     *            . Upload component
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
            public void buttonClick(final Button.ClickEvent event) {
                upload.interruptUpload();
                FilesToFilesRenameTransformerConfigDialog.fl = 1;
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
    public void uploadFinished(final Upload.FinishedEvent event) {
        state.setValue("Idle");
        pi.setVisible(false);
        textualProgress.setVisible(false);
        cancelButton.setVisible(false);

    }

    /**
     * this method gets called immediately after upload is started
     */
    @Override
    public void uploadStarted(final Upload.StartedEvent event) {

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
        textualProgress.setValue("Processed " + (readBytes / 1024)
                + " k bytes of " + (contentLength / 1024) + " k");
    }
}
