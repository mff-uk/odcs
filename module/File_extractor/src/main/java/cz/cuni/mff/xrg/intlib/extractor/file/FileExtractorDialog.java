package cz.cuni.mff.xrg.intlib.extractor.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.Upload.SucceededEvent;

import static cz.cuni.mff.xrg.intlib.extractor.file.FileExtractType.*;
import cz.cuni.xrg.intlib.commons.configuration.*;
import cz.cuni.xrg.intlib.commons.web.AbstractConfigDialog;

/**
 * FileExtractorConfig dialog.
 *
 * @author Maria
 * @author Jiri Tomes
 *
 *
 */
public class FileExtractorDialog extends AbstractConfigDialog<FileExtractorConfig> {

	private GridLayout mainLayout;

	private ComboBox comboBoxFormat; //RDFFormat

	private CheckBox useHandler;  //Statistical handler

	private Label labelFormat;

	private TextField textFieldOnly;

	private Label labelOnly;

	private TextField textFieldPath; //Path

	private HorizontalLayout horizontalLayoutOnly;

	private HorizontalLayout horizontalLayoutFormat;

	private OptionGroup pathType; //OptionGroup for path type definition

	private FileExtractType extractType;

	private InvalidValueException ex;

	private LineBreakCounter lineBreakCounter;

	private Upload fileUpload;

	private UploadInfoWindow uploadInfoWindow;

	static int fl = 0;

	private TabSheet tabSheet;

	private GridLayout gridLayoutCore;

	private VerticalLayout verticalLayoutDetails;

	public FileExtractorDialog() {
		inicialize();
		buildMainLayout();
		setCompositionRoot(mainLayout);
		mapData();
	}

	private void inicialize() {
		extractType = FileExtractType.UPLOAD_FILE;
		ex = new InvalidValueException("Valid");
	}

	private void mapData() {

		comboBoxFormat.addItem("AUTO");
		comboBoxFormat.addItem("TTL");
		comboBoxFormat.addItem("RDF/XML");
		comboBoxFormat.addItem("N3");
		comboBoxFormat.addItem("TriG");

		comboBoxFormat.setValue("AUTO");

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

	@Override
	public FileExtractorConfig getConfiguration() throws ConfigException {

		if (!textFieldPath.isValid()) {
			throw new ConfigException(ex.getMessage(), ex);
		} else {
			FileExtractorConfig conf = new FileExtractorConfig();

			conf.Path = textFieldPath.getValue();

			if (extractType == FileExtractType.PATH_TO_DIRECTORY) {
				conf.FileSuffix = textFieldOnly.getValue();

				if (textFieldOnly.getValue().isEmpty()) {
					conf.OnlyThisSuffix = false;
				} else {
					conf.OnlyThisSuffix = true;
				}
			} else {
				conf.FileSuffix = "";
				conf.OnlyThisSuffix = false;
			}

			conf.RDFFormatValue = (String) comboBoxFormat.getValue();
			conf.UseStatisticalHandler = useHandler.getValue();

			conf.fileExtractType = extractType;

			return conf;
		}
	}

	@Override
	public void setConfiguration(FileExtractorConfig conf) {

		extractType = conf.fileExtractType;
		pathType.setValue(FileExtractType.getDescriptionByType(
				extractType));

		textFieldPath.setValue(conf.Path);

		if (extractType == FileExtractType.PATH_TO_DIRECTORY) {
			textFieldOnly.setValue(conf.FileSuffix);
		}

		comboBoxFormat.setValue(conf.RDFFormatValue);
		useHandler.setValue(conf.UseStatisticalHandler);

	}

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

		// common part: create layout
		tabSheet = new TabSheet();
		tabSheet.setImmediate(true);
		tabSheet.setWidth("100%");
		tabSheet.setHeight("100%");

		// verticalLayoutCore
		gridLayoutCore = buildGridLayoutCore();
		tabSheet.addTab(gridLayoutCore, "Core", null);

		// verticalLayoutDetails
		verticalLayoutDetails = buildVerticalLayoutDetails();
		tabSheet.addTab(verticalLayoutDetails, "Details", null);

		mainLayout.addComponent(tabSheet, 0, 0);


		return mainLayout;
	}

	private HorizontalLayout buildHorizontalLayoutOnly() {
		// common part: create layout
		horizontalLayoutOnly = new HorizontalLayout();
		horizontalLayoutOnly.setImmediate(false);
		horizontalLayoutOnly.setWidth("-1px");
		horizontalLayoutOnly.setHeight("-1px");
		horizontalLayoutOnly.setMargin(false);
		horizontalLayoutOnly.setSpacing(true);

		// labelOnly
		labelOnly = new Label();
		labelOnly.setImmediate(false);
		labelOnly.setWidth("240px");
		labelOnly.setHeight("-1px");
		labelOnly.setValue("If directory, process only files with extension:");
		horizontalLayoutOnly.addComponent(labelOnly);

		// textFieldOnly
		textFieldOnly = new TextField("");
		textFieldOnly.setImmediate(false);
		textFieldOnly.setWidth("50px");
		textFieldOnly.setHeight("-1px");
		textFieldOnly.setInputPrompt(".ttl");
		horizontalLayoutOnly.addComponent(textFieldOnly);
		horizontalLayoutOnly.setComponentAlignment(textFieldOnly,
				Alignment.TOP_RIGHT);

		return horizontalLayoutOnly;
	}

	private HorizontalLayout buildHorizontalLayoutFormat() {
		// common part: create layout
		horizontalLayoutFormat = new HorizontalLayout();
		horizontalLayoutFormat.setImmediate(false);
		horizontalLayoutFormat.setWidth("-1px");
		horizontalLayoutFormat.setHeight("-1px");
		horizontalLayoutFormat.setMargin(false);
		horizontalLayoutFormat.setSpacing(true);

		// labelFormat
		labelFormat = new Label();
		labelFormat.setImmediate(false);
		labelFormat.setWidth("74px");
		labelFormat.setHeight("-1px");
		labelFormat.setValue("RDF Format:");
		horizontalLayoutFormat.addComponent(labelFormat);

		// comboBoxFormat
		comboBoxFormat = new ComboBox();
		comboBoxFormat.setImmediate(true);
		comboBoxFormat.setWidth("-1px");
		comboBoxFormat.setHeight("-1px");
		comboBoxFormat.setNewItemsAllowed(false);
		comboBoxFormat.setNullSelectionAllowed(false);
		horizontalLayoutFormat.addComponent(comboBoxFormat);


		return horizontalLayoutFormat;
	}

	private String getValidMessageByFileExtractType(FileExtractType type) {

		String message = "";

		switch (type) {
			case HTTP_URL:
				message = "URL path must start with prefix http://";
				break;
			case PATH_TO_DIRECTORY:
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

	private GridLayout buildGridLayoutCore() {
		// common part: create layout
		gridLayoutCore = new GridLayout(1, 4);
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
				textFieldPath.setImmediate(false);
				textFieldPath.setWidth("100%");
				textFieldPath.setHeight("-1px");

				textFieldPath.addValidator(new Validator() {
					@Override
					public void validate(Object value) throws InvalidValueException {
						Class<?> myClass = value.getClass();

						if (myClass.equals(String.class)) {
							String stringValue = (String) value;

							if (extractType == FileExtractType.HTTP_URL) {
								if (!stringValue.toLowerCase().startsWith(
										"http://")) {

									String message = getValidMessageByFileExtractType(
											extractType);
									ex = new InvalidValueException(message);
									throw ex;
								}
							} else {
								if (stringValue.isEmpty()) {

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
					lineBreakCounter = new LineBreakCounter();
					lineBreakCounter.setSlow(true);

					//Upload component
					fileUpload = new Upload(null, lineBreakCounter);
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
								textFieldPath.setValue(LineBreakCounter.file
										.toString());
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
					uploadInfoWindow = new UploadInfoWindow(fileUpload,
							lineBreakCounter);


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

					textFieldPath.setInputPrompt("C:\\ted\\");

					//Adding component for specify path to directory
					gridLayoutCore.addComponent(textFieldPath, 0, 1);

					// layoutOnly
					horizontalLayoutOnly = buildHorizontalLayoutOnly();
					//Adding component for specify file extension
					gridLayoutCore.addComponent(horizontalLayoutOnly, 0, 2);

					//If selected "Extract file from the given HTTP URL" option
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
		horizontalLayoutFormat = buildHorizontalLayoutFormat();
		gridLayoutCore.addComponent(horizontalLayoutFormat, 0, 3);


		return gridLayoutCore;
	}

	private VerticalLayout buildVerticalLayoutDetails() {
		// common part: create layout
		verticalLayoutDetails = new VerticalLayout();
		verticalLayoutDetails.setImmediate(false);
		verticalLayoutDetails.setWidth("100%");
		verticalLayoutDetails.setHeight("-1px");
		verticalLayoutDetails.setMargin(true);
		verticalLayoutDetails.setSpacing(true);

		//Statistical handler
		useHandler = new CheckBox("Use statistical handler");
		useHandler.setWidth("-1px");
		useHandler.setHeight("-1px");
		verticalLayoutDetails.addComponent(useHandler);

		return verticalLayoutDetails;
	}
}

class UploadInfoWindow extends Window implements Upload.StartedListener,
		Upload.ProgressListener, Upload.FailedListener,
		Upload.SucceededListener, Upload.FinishedListener {

	private static final long serialVersionUID = 1L;

	private final Label state = new Label();

	private final Label fileName = new Label();

	private final Label textualProgress = new Label();

	private final ProgressIndicator pi = new ProgressIndicator();

	private final Button cancelButton;

	private final LineBreakCounter counter;

	private final Upload upload;

	public UploadInfoWindow(Upload nextUpload,
			final LineBreakCounter lineBreakCounter) {

		super("Status");
		this.counter = lineBreakCounter;
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

//		result.setCaption("Line breaks counted");
//		l.addComponent(result);

		pi.setCaption("Progress");
		pi.setVisible(false);
		formLayout.addComponent(pi);

		textualProgress.setVisible(false);
		formLayout.addComponent(textualProgress);

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
//		result.setValue(counter.getLineBreakCount() + " (counting...)");
	}

	@Override
	public void uploadSucceeded(final SucceededEvent event) {
//		result.setValue(counter.getLineBreakCount() + " (total)");
	}

	@Override
	public void uploadFailed(final FailedEvent event) {
//		result.setValue(counter.getLineBreakCount()
//				+ " (counting interrupted at "
//				+ Math.round(100 * pi.getValue()) + "%)");
	}
}

class LineBreakCounter implements Receiver {

	private static final long serialVersionUID = 5099459605355200117L;

	private static final int searchedByte = '\n';

	private static int total = 0;

	private int counter = 0;

	private boolean sleep = false;

	public static String fileName;

	public static File file;

	/**
	 * return an OutputStream that simply counts lineends
	 */
	@Override
	public OutputStream receiveUpload(final String filename,
			final String MIMEType) {

		counter = 0;
		fileName = filename;

		Path path;

		try {
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
					if (b == searchedByte) {
						counter++;
					}
					fstream.write(b);
					if (sleep && total % 1000 == 0) {
						try {
							Thread.sleep(100);
						} catch (final InterruptedException e) {
							e.fillInStackTrace();
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
		} finally {
			return fos;

		}

	}

	public int getLineBreakCount() {
		return counter;
	}

	public void setSlow(final boolean value) {
		sleep = value;
	}
}
