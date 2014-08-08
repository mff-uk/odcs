package cz.cuni.mff.xrg.odcs.frontend.gui.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer.DpuItem;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.*;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer.ImportException;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer.ImportService;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer.ImportedFileInformation;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.FileUploadReceiver;
import cz.cuni.mff.xrg.odcs.frontend.gui.components.UploadInfoWindow;

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

    private List<DpuItem> usedDpus = new ArrayList<>();
    private  TreeMap<String, DpuItem> missingDpus = new TreeMap<>();

    private Table usedDpusTable = new Table();

    private Table missingDpusTable = new Table();

    private Button btnImport = new Button();

    private Panel panelMissingDpus = new Panel();
    
    private CheckBox chbExportDPUData = new CheckBox("Import user's data");

    private CheckBox chbExportSchedule = new CheckBox("Import pipeline's schedule");

    
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
        this.setWidth("500px");
        this.setHeight("520px");
        this.markAsDirtyRecursive();

        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setMargin(true);
        mainLayout.setSizeFull();

        // upload settings
        final GridLayout detailLayout = new GridLayout(2, 3);
        detailLayout.setMargin(false);
        detailLayout.setSpacing(true);
        detailLayout.setSizeFull();

        detailLayout.setRowExpandRatio(0, 0);
        detailLayout.setRowExpandRatio(1, 1);

        detailLayout.setColumnExpandRatio(0, 0);
        detailLayout.setColumnExpandRatio(1, 1);

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
        detailLayout.setComponentAlignment(txtUploadFile, Alignment.MIDDLE_LEFT);

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
                missingDpusTable.removeAllItems();
                usedDpusTable.removeAllItems();

                if (ext.compareToIgnoreCase("zip") != 0) {
                    upload.interruptUpload();
                    Notification.show("Selected file is not zip file",
                            Notification.Type.ERROR_MESSAGE);
                    btnImport.setEnabled(false);
                } else {
                    // show upload process dialog
                    if (uploadInfoWindow.getParent() == null) {
                        UI.getCurrent().addWindow(uploadInfoWindow);
                    }
                    btnImport.setEnabled(true);
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
                File zippedFile = fileUploadReceiver.getFile();
                try {
                    ImportedFileInformation result = importService.getImportedInformation(zippedFile);
                    usedDpus = result.getUsedDpus();
                    missingDpus = result.getMissingDpus();

                    chbExportDPUData.setValue(false);
                    chbExportSchedule.setValue(false);

                    if (result.isUserDataFile()) {
                        chbExportDPUData.setEnabled(true);
                    } else {
                        chbExportDPUData.setEnabled(false);
                    }

                    if (result.isScheduleFile()) {
                        chbExportSchedule.setEnabled(true);
                    } else {
                        chbExportSchedule.setEnabled(false);
                    }

                    if (usedDpus == null) {
                        String msg = "It is not possible to read the file [used_dpu.xml]\nwhere used dpus are.";
                        LOG.warn(msg);
                        Notification.show(msg, Notification.Type.WARNING_MESSAGE);
                    } else {
                        // show result on table  these dpus which are in use
                        for (DpuItem entry : usedDpus) {
                            usedDpusTable.addItem(new Object[]{entry.getDpuName(), entry.getJarName(), entry.getVersion()}, null);
                        }
                    }

                    if (missingDpus.size() > 0) {
                        btnImport.setEnabled(false);
                        Notification.show("It is not possible to import pipeline due to missing DPUs.\n" +
                                "Please install DPUs from table and then run import again", Notification.Type.ERROR_MESSAGE);

                    } else {
                        btnImport.setEnabled(true);

                    }

                    // show result on table - these dpus which are missing
                    for (Map.Entry<String, DpuItem> entry : missingDpus.entrySet()) {
                        String key = entry.getKey();
                        DpuItem value = entry.getValue();
                        missingDpusTable.addItem(new Object[]{value.getDpuName(), value.getJarName(), value.getVersion()}, null);
                    }


                } catch (Exception e) {
                    LOG.error("reading of pipeline from zip: {} failed", zippedFile, e);
                }
            }
        });

        detailLayout.addComponent(upload, 1, 1);
        detailLayout.setComponentAlignment(upload, Alignment.TOP_LEFT);
        final  HorizontalLayout checkBoxesLayout = new HorizontalLayout();
        checkBoxesLayout.setWidth("100%");
        checkBoxesLayout.setMargin(true);
        chbExportSchedule.setEnabled(false);
        chbExportDPUData.setEnabled(false);
        checkBoxesLayout.addComponent(chbExportSchedule);
        checkBoxesLayout.setComponentAlignment(chbExportSchedule, Alignment.MIDDLE_LEFT);
        checkBoxesLayout.addComponent(chbExportDPUData);
        checkBoxesLayout.setComponentAlignment(chbExportDPUData, Alignment.MIDDLE_RIGHT);


        final VerticalLayout usedJarsLayout = new VerticalLayout();
        usedJarsLayout.setWidth("100%");

        Panel panel = new Panel("The DPUs which are used in imported pipeline:");
        panel.setWidth("100%");
        panel.setHeight("150px");

        usedDpusTable.addContainerProperty("DPU template", String.class,  null);
        usedDpusTable.addContainerProperty("DPU jar's name",  String.class,  null);
        usedDpusTable.addContainerProperty("Version",  String.class,  null);

        usedDpusTable.setWidth("100%");
        usedDpusTable.setHeight("130px");

        panel.setContent(usedDpusTable);
        usedJarsLayout.addComponent(panel);

        
        final VerticalLayout missingJarsLayout = new VerticalLayout();
        missingJarsLayout.setWidth("100%");

        panelMissingDpus = new Panel("The DPUs which are missing in system. Install them before import:");
        panelMissingDpus.setWidth("100%");
        panelMissingDpus.setHeight("150px");

        missingDpusTable.addContainerProperty("DPU template", String.class,  null);
        missingDpusTable.addContainerProperty("DPU jar's name",  String.class,  null);
        missingDpusTable.addContainerProperty("version",  String.class,  null);

        missingDpusTable.setWidth("100%");
        missingDpusTable.setHeight("130px");
        panelMissingDpus.setContent(missingDpusTable);
        missingJarsLayout.addComponent(panelMissingDpus);


        // bottom buttons
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidth("100%");

        btnImport = new Button("Import", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (!txtUploadFile.isValid()) {
                    Notification.show("No archive selected.",
                            Notification.Type.ERROR_MESSAGE);
                } else {
                    // import
                    final File zipFile = fileUploadReceiver.getFile();
                    try {
                        importedPipeline = importService.importPipeline(zipFile, chbExportDPUData.getValue(), chbExportSchedule.getValue());
                        close();
                    } catch (ImportException ex) {
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
        mainLayout.addComponent(checkBoxesLayout);
        mainLayout.setExpandRatio(checkBoxesLayout, 1);

        mainLayout.addComponent(usedJarsLayout);
        mainLayout.setExpandRatio(usedJarsLayout, 3);
        mainLayout.addComponent(missingJarsLayout);
        mainLayout.setExpandRatio(missingJarsLayout, 3);
        mainLayout.addComponent(buttonLayout);
        mainLayout.setExpandRatio(buttonLayout, 0);
        setContent(mainLayout);
    }

    /**
     * Return imported pipeline or null if no pipeline has been imported.
     * 
     * @return
     */
    public Pipeline getImportedPipeline() {
        return importedPipeline;
    }

}
