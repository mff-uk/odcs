package cz.cuni.mff.xrg.intlib.extractor.silklinker;

import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;
import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.dpu.DPUContext;
import cz.cuni.xrg.intlib.commons.module.dialog.BaseConfigDialog;
import cz.cuni.xrg.intlib.commons.module.file.FileManager;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 *
 */
public class SilkLinkerDialog extends BaseConfigDialog<SilkLinkerConfig> {

    private static final Logger logger = LoggerFactory.getLogger(SilkLinkerDialog.class);
    private GridLayout mainLayout;
    
    private TextArea silkConfigTextArea;
    private UploadInfoWindow uploadInfoWindow;
    
    static int fl = 0;
    
    //private DPUContext context;

    public SilkLinkerDialog() {


        super(SilkLinkerConfig.class);
        
        buildMainLayout();
        setCompositionRoot(mainLayout);
    }
    
    private GridLayout buildMainLayout() {

        // common part: create layout
        mainLayout = new GridLayout(1, 2);
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");
        mainLayout.setMargin(false);
        //mainLayout.setSpacing(true);

        // top-level component properties
        setWidth("100%");
        setHeight("100%");

        //***************
        //FILE UPLOADER
        //***************


        final FileUploadReceiver fileUploadReceiver = new FileUploadReceiver();

        //Upload component
        Upload fileUpload = new Upload("Uploading Silk config file", fileUploadReceiver);
        fileUpload.setImmediate(true);
        fileUpload.setButtonCaption("Choose");
        //Upload started event listener
        fileUpload.addStartedListener(new Upload.StartedListener() {
            @Override
            public void uploadStarted(final Upload.StartedEvent event) {

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
            public void uploadFinished(final Upload.FinishedEvent event) {

                uploadInfoWindow.setClosable(true);
                uploadInfoWindow.close();
                //If upload wasn't interrupt by user
                if (fl == 0) {
                    //textFieldPath.setReadOnly(false);
                    //File was upload to the temp folder. 
                    //Path to this file is setting to the textFieldPath field
                    String configText = fileUploadReceiver.getOutputStream().toString();
                    silkConfigTextArea.setValue(configText);

//                   silkConfigTextArea.setValue(
//                            FileUploadReceiver.file
//                            .toString());
                    //textFieldPath.setReadOnly(true);
                } //If upload was interrupt by user
                else {
                    //textFieldPath.setReadOnly(false);
                    silkConfigTextArea.setValue("");
                    //textFieldPath.setReadOnly(true);
                    fl = 0;
                }
            }

           
        });

        // The window with upload information
        uploadInfoWindow = new UploadInfoWindow(fileUpload);


//        HorizontalLayout uploadFileLayout = new HorizontalLayout();
//        uploadFileLayout.setWidth("100%");
//        uploadFileLayout.setSpacing(true);
//
////        textFieldPath.setReadOnly(true);
//        uploadFileLayout.addComponent(fileUpload);
//        //uploadFileLayout.addComponent(silkConfigTextArea);
//        uploadFileLayout.setExpandRatio(fileUpload, 0.2f);
//        //uploadFileLayout.setExpandRatio(silkConfigTextArea, 0.8f);
//
//        //Adding uploading component
//        mainLayout.addComponent(uploadFileLayout, 0, 1);
        
         mainLayout.addComponent(fileUpload);

        //***************
        // TEXT AREA
        //***************

         silkConfigTextArea = new TextArea();

//	

        silkConfigTextArea.setNullRepresentation("");
        silkConfigTextArea.setImmediate(false);
        silkConfigTextArea.setWidth("100%");
        silkConfigTextArea.setHeight("300px");
//		silkConfigTextArea.setInputPrompt(
//				"PREFIX br:<http://purl.org/business-register#>\nMODIFY\nDELETE { ?s pc:contact ?o}\nINSERT { ?s br:contact ?o}\nWHERE {\n\t     ?s a gr:BusinessEntity .\n\t      ?s pc:contact ?o\n}");

        mainLayout.addComponent(silkConfigTextArea);
        mainLayout.setColumnExpandRatio(0, 0.00001f);
        mainLayout.setColumnExpandRatio(1, 0.99999f);


        


      

        return mainLayout;
    }
    
//     private void fillTextAreaWithConfig(File f) {
//
//                //read file, display it in 
//                String configText = null;
//                try {
//                    configText = SilkLinkerDialog.readFile(f.getCanonicalPath(), StandardCharsets.UTF_8);
//                } catch (IOException ex) {
//                    java.util.logging.Logger.getLogger(SilkLinkerDialog.class.getName()).log(Level.SEVERE, null, ex);
//                }
//
//                silkConfigTextArea.setValue(configText);
//
//
//            }

    @Override
    public void setConfiguration(SilkLinkerConfig conf) throws ConfigException {
    //fill the textarea 
        
        if (conf.getSilkConf() != null) {
            silkConfigTextArea.setValue(conf.getSilkConf());
        }
        else {
             silkConfigTextArea.setValue("");
        }
        

    }

    @Override
    public SilkLinkerConfig getConfiguration() throws ConfigException {
    //get the conf from textArea
       SilkLinkerConfig conf = null;
        
       if (silkConfigTextArea.getValue().trim().isEmpty()) {
           //no config!
           conf = new SilkLinkerConfig();
       }
       else {
        conf = new SilkLinkerConfig(silkConfigTextArea.getValue());
        }
       
        return conf;
    }

    static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return encoding.decode(ByteBuffer.wrap(encoded)).toString();
    }

}




    

/**
     * Upload selected file to template directory
     *
     * @author Maria Kukhar
     *
     */
    class FileUploadReceiver implements Upload.Receiver {

    private static final long serialVersionUID = 5099459605355200117L;
//    private static final int searchedByte = '\n';
//    private static int total = 0;
//    private boolean sleep = false;
//    public static String fileName;
//    public static File file;
//    public static Path path;
//    private DPUContext context;
    
    private OutputStream fos;
    
    public OutputStream getOutputStream() {
        return fos;
    }

//    public FileUploadReceiver(DPUContext c) {
//        context = c;
//    }

    /**
     * return an OutputStream
     */
    @Override
    public OutputStream receiveUpload(final String filename,
            final String MIMEType) {
//        fileName = filename;

//        //get the dpu id
//        context
        
        
        
//        // creates file manager
//        FileManager fileManager = new FileManager(context);
//        // obtains file in sub-directory in global directory
//        file = fileManager.getGlobal().directory("silkConfs").directory("x").file(filename);
        

//            File globalDirectory = context.getGlobalDirectory();
//
//            try {
//                //create template directory
//                path = Files.createTempDirectory("SilkConfUpload");
//            } catch (IOException e) {
//                throw new RuntimeException(e.getMessage(), e);
//            }



//            file = new File("/" + path + "/" + filename); // path for upload file in temp directory

      
        fos = new ByteArrayOutputStream();
        return fos;
        
    
        
//        OutputStream fos = null;
//
//        try {
//            final FileOutputStream fstream = new FileOutputStream(file);
//
//            fos = new OutputStream() {
//                @Override
//                public void write(final int b) throws IOException {
//                    total++;
//
//                    fstream.write(b);
//
//
//                }
//
//                @Override
//                public void write(byte b[], int off, int len) throws IOException {
//                    if (b == null) {
//                        throw new NullPointerException();
//                    } else if ((off < 0) || (off > b.length) || (len < 0)
//                            || ((off + len) > b.length) || ((off + len) < 0)) {
//                        throw new IndexOutOfBoundsException();
//                    } else if (len == 0) {
//                        return;
//                    }
//                    fstream.write(b, off, len);
//                    total += len;
//
//
//                }
//
//                @Override
//                public void close() throws IOException {
//                    fstream.close();
//                    super.close();
//                }
//            };
//
//        } catch (FileNotFoundException e) {
//            new Notification("Could not open file<br/>", e.getMessage(),
//                    Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
//        } finally {
//            return fos;
//
//        }

    }

// 
}

/**
 * Dialog for uploading status. Appear automatically after file upload start.
 *
 * @author Maria Kukhar
 *
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
     * @param upload. Upload component
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
                SilkLinkerDialog.fl = 1;
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
        textualProgress.setValue(
                "Processed " + (readBytes / 1024) + " k bytes of "
                + (contentLength / 1024) + " k");
    }
}


