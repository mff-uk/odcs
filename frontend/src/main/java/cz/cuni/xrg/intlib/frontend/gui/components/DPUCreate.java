package cz.cuni.xrg.intlib.frontend.gui.components;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.Validator;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Upload.StartedEvent;

import cz.cuni.xrg.intlib.commons.app.conf.AppConfig;
import cz.cuni.xrg.intlib.commons.app.conf.ConfigProperty;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUInstanceRecord;
import cz.cuni.xrg.intlib.commons.app.dpu.DPURecord;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUTemplateRecord;
import cz.cuni.xrg.intlib.commons.app.dpu.DPUType;
import cz.cuni.xrg.intlib.commons.app.dpu.VisibilityType;
import cz.cuni.xrg.intlib.commons.app.module.BundleInstallFailedException;
import cz.cuni.xrg.intlib.commons.app.module.ClassLoadFailedException;
import cz.cuni.xrg.intlib.commons.app.module.ModuleException;
import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.extractor.Extract;
import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.commons.transformer.Transform;
import cz.cuni.xrg.intlib.frontend.AppEntry;
import cz.cuni.xrg.intlib.frontend.auxiliaries.App;
import cz.cuni.xrg.intlib.frontend.auxiliaries.dpu.DPUInstanceWrap;

public class DPUCreate extends Window {

	private TextField dpuName;

	private TextArea dpuDescription;
	private OptionGroup groupVisibility;
	private Upload selectFile;
	private LineBreakCounter lineBreakCounter;
	private UploadInfoWindow uploadInfoWindow;
	private GridLayout dpuGeneralSettingsLayout;
	private DPUTemplateRecord dpuTemplate;
	private TextField uploadFile;
	public static int fl=0;

	public DPUCreate() {

		this.setResizable(false);
		this.setModal(true);
		this.setCaption("DPU Creation");

		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setStyleName("dpuDetailMainLayout");
		mainLayout.setMargin(true);

		dpuGeneralSettingsLayout = new GridLayout(2, 6);
		dpuGeneralSettingsLayout.setSpacing(true);
		dpuGeneralSettingsLayout.setWidth("400px");
		dpuGeneralSettingsLayout.setColumnExpandRatio(0, 0.5f);
		dpuGeneralSettingsLayout.setColumnExpandRatio(1, 0.5f);

		Label nameLabel = new Label("Name");
		nameLabel.setImmediate(false);
		nameLabel.setWidth("-1px");
		nameLabel.setHeight("-1px");
		dpuGeneralSettingsLayout.addComponent(nameLabel, 0, 0);

		dpuName = new TextField();
		dpuName.setImmediate(false);
		dpuName.setWidth("310px");
		dpuName.setHeight("-1px");
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

		Label descriptionLabel = new Label("Description");
		descriptionLabel.setImmediate(false);
		descriptionLabel.setWidth("-1px");
		descriptionLabel.setHeight("-1px");
		dpuGeneralSettingsLayout.addComponent(descriptionLabel, 0, 1);

		dpuDescription = new TextArea();
		dpuDescription.setImmediate(false);
		dpuDescription.setWidth("310px");
		dpuDescription.setHeight("60px");
		dpuDescription.addValidator(new Validator() {

			@Override
			public void validate(Object value) throws InvalidValueException {
				if (value.getClass() == String.class
						&& !((String) value).isEmpty()) {
					return;
				}
				throw new InvalidValueException("Description must be filled!");

			}
		});
		dpuGeneralSettingsLayout.addComponent(dpuDescription, 1, 1);

		Label visibilityLabel = new Label("Visibility");
		descriptionLabel.setImmediate(false);
		descriptionLabel.setWidth("-1px");
		descriptionLabel.setHeight("-1px");
		dpuGeneralSettingsLayout.addComponent(visibilityLabel, 0, 2);

		groupVisibility = new OptionGroup();
		groupVisibility.addStyleName("horizontalgroup");
		groupVisibility.addItem(VisibilityType.PRIVATE);
		groupVisibility.addItem(VisibilityType.PUBLIC);

		dpuGeneralSettingsLayout.addComponent(groupVisibility, 1, 2);

		Label selectLabel = new Label("Select .jar file");
		selectLabel.setImmediate(false);
		selectLabel.setWidth("-1px");
		selectLabel.setHeight("-1px");
		dpuGeneralSettingsLayout.addComponent(selectLabel, 0, 3);

		lineBreakCounter = new LineBreakCounter();
		lineBreakCounter.setSlow(true);

		selectFile = new Upload(null, lineBreakCounter);
		selectFile.addStyleName("horizontalgroup");

		selectFile.addStartedListener(new StartedListener() {

			/**
			 * 
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
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void uploadFinished(final FinishedEvent event) {
				
				uploadInfoWindow.setClosable(true);
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

		uploadInfoWindow = new UploadInfoWindow(selectFile, lineBreakCounter);

		dpuGeneralSettingsLayout.addComponent(selectFile, 1, 3);

		Label uploadLabel = new Label("Upload file");
		uploadLabel.setImmediate(false);
		uploadLabel.setWidth("-1px");
		uploadLabel.setHeight("-1px");
		dpuGeneralSettingsLayout.addComponent(uploadLabel, 0, 4);

		uploadFile = new TextField();
		uploadFile.setWidth("310px");
		uploadFile.setReadOnly(true);

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
		dpuGeneralSettingsLayout.addComponent(uploadFile, 1, 4);

		dpuGeneralSettingsLayout.setMargin(new MarginInfo(false, false, true,
				false));
		mainLayout.addComponent(dpuGeneralSettingsLayout);

		HorizontalLayout buttonBar = new HorizontalLayout();
		buttonBar.setStyleName("dpuDetailButtonBar");
		buttonBar.setMargin(new MarginInfo(true, false, false, false));

		Button saveButton = new Button("Save");
		saveButton.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if ((!dpuName.isValid()) || (!dpuDescription.isValid())
						|| (!uploadFile.isValid())) {
					Notification.show("Failed to save DPURecord",
							"Mandatory fields should be filled",
							Notification.Type.ERROR_MESSAGE);
					return;
				}

				if (LineBreakCounter.path != null) {
					String pojPath = App.getApp().getAppConfiguration()
							.getString(ConfigProperty.MODULE_PATH);
					File srcFile = new File(LineBreakCounter.file.toString());
					File destFile = new File(pojPath + LineBreakCounter.fName);


					boolean exists = destFile.exists();
					if (!exists) {
						try {
							copyFile(srcFile, destFile);
						} catch (IOException e) {
							e.printStackTrace();
							// error, just exit
							System.exit(0);
						}
					} else {
						Notification.show(
								"File " + LineBreakCounter.fName +" already exist",
								Notification.Type.ERROR_MESSAGE);
						return;

					}

					String relativePath = LineBreakCounter.fName;
					// we try to load new DPU .. to find out more about it,
					// start by obtaining ModuleFacade
					Object dpuObject = null;
					try {
						dpuObject = App.getApp().getModules()
								.getObject(relativePath);
					} catch (BundleInstallFailedException
							| ClassLoadFailedException | FileNotFoundException e) {
						// for some reason we can't load bundle .. delete dpu
						// and show message to the user
						destFile.delete();
						uploadFile.setReadOnly(false);
						uploadFile.setValue("");
						uploadFile.setReadOnly(true);
						Notification.show(
								"Can't load bundle because of exception:",
								e.getMessage(), Notification.Type.ERROR_MESSAGE);
						return;

					}
					String jarDescription = App.getApp().getModules()
							.getJarDescription(relativePath);
					if (jarDescription == null) {
						// failed to read description .. use empty string ?
						jarDescription = "";
					}

					// check type ..
					DPUType dpuType = null;
					if (dpuObject instanceof Extract) {
						dpuType = DPUType.Extractor;
					} else if (dpuObject instanceof Transform) {
						dpuType = DPUType.Transformer;
					} else if (dpuObject instanceof Load) {
						dpuType = DPUType.Loader;
					} else {
						// unknown type .. delete dpu and throw error
						destFile.delete();
						uploadFile.setReadOnly(false);
						uploadFile.setValue("");
						uploadFile.setReadOnly(true);
						Notification.show("Unknown DPURecord type.",
								"Upload another file",
								Notification.Type.ERROR_MESSAGE);
						return;

					}

					// now we know all what we need create record in Database
					dpuTemplate = new DPUTemplateRecord(dpuName.getValue(),
							dpuType);
					dpuTemplate.setDescription(dpuDescription.getValue());
					dpuTemplate.setVisibility((VisibilityType) groupVisibility
							.getValue());
					dpuTemplate.setJarPath(LineBreakCounter.fName);
					dpuTemplate.setJarDescription(jarDescription);

					// TODO Petyr, Maria: Load the "default" configuration

					App.getDPUs().save(dpuTemplate);
					uploadInfoWindow.close();
					close();

				}
			}

		});
		buttonBar.addComponent(saveButton);

		Button cancelButton = new Button("Cancel", new Button.ClickListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(Button.ClickEvent event) {
				close();
			}
		});
		buttonBar.addComponent(cancelButton);

		mainLayout.addComponent(buttonBar);

		this.setContent(mainLayout);
		setSizeUndefined();
	}

	public static void copyFile(File src, File dest) throws IOException {

		InputStream inStream = null;
		OutputStream outStream = null;
		try {

			inStream = new FileInputStream(src);
			outStream = new FileOutputStream(dest); 

			byte[] buffer = new byte[1024];

			int length;
			while ((length = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, length);
			}

			if (inStream != null)
				inStream.close();
			if (outStream != null)
				outStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

class UploadInfoWindow extends Window implements Upload.StartedListener,
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

class LineBreakCounter implements Receiver {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5099459605355200117L;
	private int counter;
	private int total;
	private boolean sleep;
	public static File file;
	private FileOutputStream fstream = null;
	public static Path path;
	public static String fName;

	/**
	 * return an OutputStream that simply counts lineends
	 */
	public OutputStream receiveUpload(final String filename,
			final String MIMEType) {
		counter = 0;
		total = 0;
		fName = filename;

		OutputStream fos = null;

		try {
			path = Files.createTempDirectory("jarDPU");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			file = new File("/" + path + "/" + filename);
			fstream = new FileOutputStream(file);
			fos = new OutputStream() {
				private static final int searchedByte = '\n';

				@Override
				public void write(final int b) throws IOException {
					total++;
					if (b == searchedByte) {
						counter++;
					}
					fstream.write(b);
					if (sleep && total % 1000 == 0) {
						try {
							Thread.sleep(100);
						} catch (final InterruptedException e) {
							e.printStackTrace();
						}
					}

				}

				@Override
				public void close() throws IOException {
					fstream.close();
					super.close();
				}
			};

		} catch (FileNotFoundException e) {
			new Notification("Could not open file<br/>", e.getMessage(),
					Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
			return null;
		}
		return fos;
	}

	public int getLineBreakCount() {
		return counter;
	}

	public void setSlow(final boolean value) {
		sleep = value;
	}

}
