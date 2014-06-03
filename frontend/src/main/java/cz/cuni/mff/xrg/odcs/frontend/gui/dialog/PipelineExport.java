package cz.cuni.mff.xrg.odcs.frontend.gui.dialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.FileDownloader;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer.ExportException;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer.ExportService;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer.ExportSetting;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.download.OnDemandFileDownloader;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.download.OnDemandStreamResource;

/**
 * @author Škoda Petr
 */
public class PipelineExport extends Window {

    private static final Logger LOG = LoggerFactory.getLogger(
            PipelineExport.class);

    private CheckBox chbExportDPUData;

    /**
     * Export service.
     */
    private final ExportService exportService;

    /**
     * Pipeline to export.
     */
    private Pipeline pipeline;

    public PipelineExport(ExportService exportService) {
        this.exportService = exportService;
        init();
    }

    private void init() {
        this.setResizable(false);
        this.setModal(true);
        this.setWidth("320px");
        this.setHeight("320px");
        this.setCaption("Pipeline export");

        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setMargin(true);
        mainLayout.setSizeFull();

        final VerticalLayout detailLayout = new VerticalLayout();

        chbExportDPUData = new CheckBox("Export DPU data:");
        chbExportDPUData.setWidth("100%");
        chbExportDPUData.setValue(true);
        detailLayout.addComponent(chbExportDPUData);

        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidth("100%");

        Button btnExport = new Button("export");
        buttonLayout.addComponent(btnExport);
        buttonLayout.setComponentAlignment(btnExport, Alignment.MIDDLE_LEFT);
        Button btnCancel = new Button("close", new Button.ClickListener() {
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

        FileDownloader fileDownloader = new OnDemandFileDownloader(new OnDemandStreamResource() {

            @Override
            public String getFilename() {
                return pipeline.getName() + ".zip";
            }

            @Override
            public InputStream getStream() {
                ExportSetting setting = new ExportSetting(chbExportDPUData.getValue());
                LOG.debug("Exporting DPU date: {}", setting.isExportDPUUserData());

                // TODO we should add some waiting dialog here, or 
                //	we can split the action -> prepare download, download
                LOG.debug("Constructing output stream.");
                File pplFile;
                try {
                    pplFile = exportService.exportPipeline(pipeline, setting);
                } catch (ExportException ex) {
                    LOG.error("Faield to export pipeline", ex);
                    Notification.show("Failed to export pipeline.", Notification.Type.ERROR_MESSAGE);
                    return null;
                }
                try {
                    return new FileInputStream(pplFile);
                } catch (FileNotFoundException ex) {
                    LOG.error("Faield to load file with pipeline", ex);
                    Notification.show("Failed to export pipeline.", Notification.Type.ERROR_MESSAGE);
                    return null;
                }
            }

        });
        fileDownloader.extend(btnExport);
    }

    public void setData(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

}
