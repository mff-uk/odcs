package cz.cuni.mff.xrg.odcs.frontend.gui.dialog;

import com.vaadin.ui.*;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer.ImportException;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer.ImportService;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.FileUploadReceiver;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.UploadInfoWindow;
import java.io.File;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dialog for pipeline import.
 * 
 * @author Škoda Petr
 */
public class PipelineImport extends Window {

	private static final Logger LOG = LoggerFactory.getLogger(
			PipelineImport.class);
	
	private TextField txtUploadFile;
	
	private Pipeline importedPipeline = null;
	
	/**
	 * Receive uploaded file.
	 */
	private FileUploadReceiver fileUploadReceiver;
	
	/**
	 * Dialog with information about file upload process.
	 */
	private UploadInfoWindow uploadInfoWindow;
	
	/**
	 * Service used to import pipelines.
	 */
	private final ImportService importService;
	
	public PipelineImport(ImportService importService) {
		super("Pipeline import");		
		this.importService = importService;		
		init();
	}
		
	/**
	 * Initialise user interface.
	 */
	private void init() {
		this.setResizable(false);
		this.setModal(true);
		this.setWidth("320px");
		this.setHeight("320px");
		
		final VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setMargin(true);
		mainLayout.setSizeFull();
		
		// upload settings
		final GridLayout detailLayout = new GridLayout(2, 2);
		detailLayout.setMargin(false);
		detailLayout.setSpacing(true);
		
		{
			Label lbl = new Label("Zip archive:");
			lbl.setWidth("-1px");
			detailLayout.addComponent(lbl, 0, 0);
		}
		
		txtUploadFile = new TextField();
		txtUploadFile.setWidth("100%");
		txtUploadFile.setReadOnly(true);
		txtUploadFile.setRequired(true);
		detailLayout.addComponent(txtUploadFile, 1, 0);
					
		fileUploadReceiver = new FileUploadReceiver();
		final Upload upload = new Upload(null, fileUploadReceiver);
		upload.setImmediate(true);
		upload.setButtonCaption("Upload file");
		// modify the look so the upload component is more user friendly
		upload.addStyleName("horizontalgroup");
		
		// create dialog for upload process
		uploadInfoWindow = new UploadInfoWindow(upload);
		
		// assign action to upload
		upload.addStartedListener(new Upload.StartedListener() {

			@Override
			public void uploadStarted(Upload.StartedEvent event) {
				String ext = FilenameUtils.getExtension(event.getFilename());
				
				if (ext.compareToIgnoreCase("zip") != 0) {
					upload.interruptUpload();
					Notification.show("Selected file is not zip file", 
							Notification.Type.ERROR_MESSAGE);
				} else { 
					// show upload process dialog
					if(uploadInfoWindow.getParent() == null) {
						UI.getCurrent().addWindow(uploadInfoWindow);
					}
					uploadInfoWindow.setClosable(false);
				}
			}
		});
		upload.addFailedListener(new Upload.FailedListener() {

			@Override
			public void uploadFailed(Upload.FailedEvent event) {
				txtUploadFile.setReadOnly(false);
				txtUploadFile.setValue("");
				txtUploadFile.setReadOnly(true);
				// close upload info dialog
				uploadInfoWindow.setClosable(true);
				uploadInfoWindow.close();
			}
		});
		upload.addFinishedListener(new Upload.FinishedListener() {

			@Override
			public void uploadFinished(Upload.FinishedEvent event) {
				txtUploadFile.setReadOnly(false);
				txtUploadFile.setValue(event.getFilename());
				txtUploadFile.setReadOnly(true);
				// close upload info dialog
				uploadInfoWindow.setClosable(true);
				uploadInfoWindow.close();
				// hide uploader
				upload.setVisible(false);
			}
		});
		
		detailLayout.addComponent(upload, 1, 1);
		
		// bottom buttons
		
		HorizontalLayout buttonLayout = new HorizontalLayout();		
		buttonLayout.setWidth("100%");
		
		Button btnImport = new Button("Import", new Button.ClickListener() {

			@Override
			public void buttonClick(Button.ClickEvent event) {
				if (!txtUploadFile.isValid()) {
					Notification.show("No archive selected.", 
							Notification.Type.ERROR_MESSAGE);
				} else {
					// import
					final File zipFile = fileUploadReceiver.getFile();
					try {
						importedPipeline = importService.importPipeline(zipFile);
						close();
					} catch(ImportException ex) {
						LOG.error("Import failed.", ex);
						Notification.show("Import failed. " + ex.getMessage(),
								Notification.Type.ERROR_MESSAGE);
					}
				}
			}
		});
		buttonLayout.addComponent(btnImport);
		buttonLayout.setComponentAlignment(btnImport, Alignment.MIDDLE_LEFT);
		
		Button btnCancel = new Button("Cancel", new Button.ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				close();
			}
		});
		buttonLayout.addComponent(btnCancel);
		buttonLayout.setComponentAlignment(btnCancel, Alignment.MIDDLE_RIGHT);
		
		// add to the main layout
		mainLayout.addComponent(detailLayout);
		mainLayout.setExpandRatio(detailLayout, 1);
		mainLayout.addComponent(buttonLayout);
		mainLayout.setExpandRatio(buttonLayout, 0);
		setContent(mainLayout);
	}
	
	/**
	 * Return imported pipeline or null if no pipeline has been imported.
	 * @return 
	 */
	public Pipeline getImportedPipeline() {
		return importedPipeline;
	}
	
}
