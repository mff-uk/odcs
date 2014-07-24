package cz.cuni.mff.xrg.odcs.frontend.gui.components;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.FinishedListener;
import com.vaadin.ui.Upload.ProgressListener;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.Window;

/**
 * Dialog for uploading status. Appear automatically after file upload start.
 * 
 * @author Maria Kukhar
 */
public class UploadInfoWindow extends Window implements StartedListener,
        ProgressListener, FinishedListener {

    private Label state;

    private Label fileName;

    private Label textualProgress;

    private ProgressBar progressBar;

    private Button cancelButton;

    private Upload upload;

    /**
     * Basic constructor
     * 
     * @param upload
     *            Upload component that called this method
     */
    public UploadInfoWindow(final Upload upload) {
        super("Status");
        this.upload = upload;
        this.state = new Label();
        this.fileName = new Label();
        this.textualProgress = new Label();
        this.progressBar = new ProgressBar();
        cancelButton = new Button("Cancel");

        setParameters();
    }

    private void setParameters() {
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
                DPUCreate.setFl(1);
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
        progressBar.setCaption("Progress");
        progressBar.setVisible(false);
        formLayout.addComponent(progressBar);

        textualProgress.setVisible(false);
        formLayout.addComponent(textualProgress);

        upload.addStartedListener(this);
        upload.addProgressListener(this);
        upload.addFinishedListener(this);

    }

    /**
     * This method gets called immediately after upload is finished
     * 
     * @param event
     *            the Upload finished event.
     */
    @Override
    public void uploadFinished(final FinishedEvent event) {
        state.setValue("Idle");
        progressBar.setVisible(false);
        textualProgress.setVisible(false);
        cancelButton.setVisible(false);

    }

    /**
     * This method gets called immediately after upload is started
     * 
     * @param event
     *            the Upload finished event.
     */
    @Override
    public void uploadStarted(final StartedEvent event) {

        progressBar.setValue(0f);
        progressBar.setVisible(true);
        if (progressBar.getUI() != null) {
            progressBar.getUI().setPollInterval(500); // hit server frequently to get
        }
        textualProgress.setVisible(true);
        // updates to client
        state.setValue("Uploading");
        fileName.setValue(event.getFilename());

        cancelButton.setVisible(true);
    }

    /**
     * This method shows update progress
     * 
     * @param readBytes
     *            bytes transferred
     * @param contentLength
     *            total size of file currently being uploaded, -1 if
     *            unknown
     */
    @Override
    public void updateProgress(final long readBytes, final long contentLength) {
        progressBar.setValue(new Float(readBytes / (float) contentLength));
        textualProgress.setValue(
                "Processed " + (readBytes / 1024) + " k bytes of "
                        + (contentLength / 1024) + " k");
        if (progressBar.getValue() == 1.0 && progressBar.getUI() != null) {
        	progressBar.getUI().setPollInterval(-1); // disabling
        }
    }
}
