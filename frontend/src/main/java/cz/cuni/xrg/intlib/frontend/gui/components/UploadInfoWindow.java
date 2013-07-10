package cz.cuni.xrg.intlib.frontend.gui.components;

import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;

public class UploadInfoWindow extends Window implements Upload.StartedListener,
Upload.ProgressListener, Upload.FailedListener,
Upload.SucceededListener, Upload.FinishedListener {
/**
* 
*/
private static final long serialVersionUID = 1L;
private final Label state = new Label();
private final Label result = new Label();
private final Label fileName = new Label();
private final Label textualProgress = new Label();

private final ProgressIndicator pi = new ProgressIndicator();
private final Button cancelButton;
private final LineBreakCounter counter;

public UploadInfoWindow(final Upload upload,
	final LineBreakCounter lineBreakCounter) {
super("Status");
this.counter = lineBreakCounter;

addStyleName("upload-info");

setResizable(false);
setDraggable(false);

final FormLayout l = new FormLayout();
setContent(l);
l.setMargin(true);

final HorizontalLayout stateLayout = new HorizontalLayout();
stateLayout.setSpacing(true);
stateLayout.addComponent(state);

cancelButton = new Button("Cancel");
cancelButton.addClickListener(new Button.ClickListener() {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void buttonClick(final ClickEvent event) {
		upload.interruptUpload();
		DPUCreate.fl=1;
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

result.setCaption("Line breaks counted");
l.addComponent(result);

pi.setCaption("Progress");
pi.setVisible(false);
l.addComponent(pi);

textualProgress.setVisible(false);
l.addComponent(textualProgress);

upload.addStartedListener(this);
upload.addProgressListener(this);
upload.addFailedListener(this);
upload.addSucceededListener(this);
upload.addFinishedListener(this);

}

@Override
public void uploadFinished(final FinishedEvent event) {
state.setValue("Idle");
pi.setVisible(false);
textualProgress.setVisible(false);
cancelButton.setVisible(false);

}

@Override
public void uploadStarted(final StartedEvent event) {
// this method gets called immediatedly after upload is
// started
pi.setValue(0f);
pi.setVisible(true);
pi.setPollingInterval(500); // hit server frequantly to get
textualProgress.setVisible(true);
// updates to client
state.setValue("Uploading");
fileName.setValue(event.getFilename());

cancelButton.setVisible(true);
}

@Override
public void updateProgress(final long readBytes, final long contentLength) {
// this method gets called several times during the update
pi.setValue(new Float(readBytes / (float) contentLength));
textualProgress.setValue("Processed " + readBytes + " bytes of "
		+ contentLength);
result.setValue(counter.getLineBreakCount() + " (counting...)");
}

@Override
public void uploadSucceeded(final SucceededEvent event) {
result.setValue(counter.getLineBreakCount() + " (total)");

}

@Override
public void uploadFailed(final FailedEvent event) {
result.setValue(counter.getLineBreakCount()
		+ " (counting interrupted at "
		+ Math.round(100 * pi.getValue()) + "%)");
}

}