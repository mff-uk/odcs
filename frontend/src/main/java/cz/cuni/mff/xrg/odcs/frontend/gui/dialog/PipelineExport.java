package cz.cuni.mff.xrg.odcs.frontend.gui.dialog;

import com.vaadin.server.FileDownloader;
import com.vaadin.ui.*;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.Pipeline;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer.ExportException;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer.ExportService;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer.ExportSetting;
import cz.cuni.mff.xrg.odcs.commons.app.pipeline.transfer.DpuItem;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.download.OnDemandFileDownloader;
import cz.cuni.mff.xrg.odcs.frontend.auxiliaries.download.OnDemandStreamResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.TreeSet;

/**
 * @author Škoda Petr
 */
public class PipelineExport extends Window {

    private static final Logger LOG = LoggerFactory.getLogger(
            PipelineExport.class);

    private CheckBox chbExportDPUData;

    private CheckBox chbExportJars;

    private CheckBox chbExportSchedule;

    private Label usedJarsLabel;

    private Label usedJarsText;

    /**
     * Export service.
     */
    private final ExportService exportService;

    /**
     * Pipeline to export.
     */
    private Pipeline pipeline;

    public PipelineExport(ExportService exportService, Pipeline pipeline) {
        this.exportService = exportService;
        this.pipeline = pipeline;
        init();
    }

    private void init() {
        this.setResizable(false);
        this.setModal(true);
        this.setWidth("500px");
        this.setHeight("350px");
        this.setCaption("Pipeline export");

        final VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setMargin(true);
        mainLayout.setSizeFull();

        final VerticalLayout detailLayout = new VerticalLayout();
        detailLayout.setWidth("100%");

        chbExportDPUData = new CheckBox("Export DPU data");
        chbExportDPUData.setWidth("100%");
        chbExportDPUData.setValue(false);
        detailLayout.addComponent(chbExportDPUData);

        chbExportJars = new CheckBox("Export DPUs JARs");
        chbExportJars.setWidth("100%");
        chbExportJars.setValue(false);
        detailLayout.addComponent(chbExportJars);

        chbExportSchedule = new CheckBox("Export pipeline's schedule");
        chbExportSchedule.setWidth("100%");
        chbExportSchedule.setValue(false);
        detailLayout.addComponent(chbExportSchedule);

        final VerticalLayout usedJarsLayout = new VerticalLayout();
        usedJarsLayout.setWidth("100%");

        Panel panel = new Panel("Used dpus:");
        panel.setWidth("100%");
        panel.setHeight("150px");

        TreeSet<DpuItem> usedDpus = exportService.getDpusInformation(pipeline);

        Table table = new Table();
        table.addContainerProperty("DPU template", String.class,  null);
        table.addContainerProperty("DPU jar's name",  String.class,  null);
        table.addContainerProperty("Version",  String.class,  null);
        table.setWidth("100%");
        table.setHeight("130px");
        //add dpu's information to table
        for (DpuItem entry : usedDpus) {
            table.addItem(new Object[]{entry.getDpuName(), entry.getJarName(), entry.getVersion()}, null);
        }

        panel.setContent(table);
        usedJarsLayout.addComponent(panel);

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
        mainLayout.addComponent(usedJarsLayout);
        mainLayout.addComponent(buttonLayout);
        mainLayout.setComponentAlignment(buttonLayout, Alignment.BOTTOM_CENTER);
        setContent(mainLayout);

        FileDownloader fileDownloader = new OnDemandFileDownloader(new OnDemandStreamResource() {

            @Override
            public String getFilename() {
                return pipeline.getName() + ".zip";
            }

            @Override
            public InputStream getStream() {
                ExportSetting setting = new ExportSetting(chbExportDPUData.getValue(), chbExportJars.getValue(), chbExportSchedule.getValue());
                LOG.debug("Exporting DPU date: {}", setting.isExportDPUUserData());
                LOG.debug("Exporting DPU's jars: {}", setting.isExportJars());
                LOG.debug("Exporting DPU's schedule: {}", setting.isChbExportSchedule());

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


}
