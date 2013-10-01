package cz.cuni.mff.xrg.odcs.frontend.gui.components;

import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.StartedEvent;

/**
 * Dialog for uploading status. Appear automatically after file upload start.
 *
 * @author Maria Kukhar
 *
 */
public class UploadInfoWindow extends Window implements Upload.StartedListener,
		Upload.ProgressListener, Upload.FinishedListener {

	private static final long serialVersionUID = 1L;
	private final Label state = new Label();
	private final Label fileName = new Label();
	private final Label textualProgress = new Label();
	private final ProgressIndicator pi = new ProgressIndicator();
	private final Button cancelButton;
	final FormLayout l;

	/**
	 * Basic constructor
	 *
	 * @param upload Upload component that called this method
	 */
	public UploadInfoWindow(final Upload upload) {
		super("Status");

		addStyleName("upload-info");

		setResizable(false);
		setDraggable(false);

		l = new FormLayout();
		setContent(l);
		l.setMargin(true);

		final HorizontalLayout stateLayout = new HorizontalLayout();
		stateLayout.setSpacing(true);
		stateLayout.addComponent(state);

		cancelButton = new Button("Cancel");
		cancelButton.addClickListener(new Button.ClickListener() {
			/**
			 * Upload interruption
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(final ClickEvent event) {
				upload.interruptUpload();
				DPUCreate.fl = 1;
			}
		});
		cancelButton.setVisible(false);
		cancelButton.setStyleName("small");
		stateLayout.addComponent(cancelButton);

		stateLayout.setCaption("Current state");
		state.setValue("Idle");
		l.addComponent(stateLayout);

		fileName.setCaption("File name");
		l.addComponent(fileName);

		//progress indicator
		pi.setCaption("Progress");
		pi.setVisible(false);
		l.addComponent(pi);

		textualProgress.setVisible(false);
		l.addComponent(textualProgress);

		upload.addStartedListener(this);
		upload.addProgressListener(this);
		upload.addFinishedListener(this);

	}

	/**
	 * This method gets called immediately after upload is finished
	 *
	 * @param event the Upload finished event.
	 */
	@Override
	public void uploadFinished(final FinishedEvent event) {
		state.setValue("Idle");
		pi.setVisible(false);
		textualProgress.setVisible(false);
		cancelButton.setVisible(false);

	}

	/**
	 * This method gets called immediately after upload is started
	 *
	 * @param event the Upload finished event.
	 */
	@Override
	public void uploadStarted(final StartedEvent event) {

		pi.setValue(0f);
		pi.setVisible(true);
		pi.setPollingInterval(500); // hit server frequently to get
		textualProgress.setVisible(true);
		// updates to client
		state.setValue("Uploading");
		fileName.setValue(event.getFilename());

		cancelButton.setVisible(true);
	}

	/**
	 * This method shows update progress
	 *
	 * @param readBytes bytes transferred
	 * @param contentLength total size of file currently being uploaded, -1 if
	 * unknown
	 */
	@Override
	public void updateProgress(final long readBytes, final long contentLength) {
		pi.setValue(new Float(readBytes / (float) contentLength));
		textualProgress.setValue("Processed " + (readBytes / 1024) + " k bytes of "
				+ (contentLength / 1024) + " k");

	}
}