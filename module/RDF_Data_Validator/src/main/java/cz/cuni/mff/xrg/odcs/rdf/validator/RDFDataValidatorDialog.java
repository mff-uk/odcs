package cz.cuni.mff.xrg.odcs.rdf.validator;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.VerticalLayout;

import eu.unifiedviews.dpu.config.DPUConfigException;
import eu.unifiedviews.helpers.dpu.config.BaseConfigDialog;

/**
 * DPU's configuration dialog for setting directory path and others parameters
 * for saving validation report.
 * 
 * @author Petyr
 * @author Jiri Tomes
 */
public class RDFDataValidatorDialog extends BaseConfigDialog<RDFDataValidatorConfig> {

    private VerticalLayout verticalLayoutCore;

    /**
     * CheckBox to set if pipeline execution fail in case of invalid data or
     * not.
     */
    private CheckBox failExecution;

    private CheckBox createOutput;

    public RDFDataValidatorDialog() {
        super(RDFDataValidatorConfig.class);
        buildMainLayout();
    }

    /**
     * Builds configuration dialog.
     */
    private void buildMainLayout() {
        // top-level component properties
        setWidth("100%");
        setHeight("100%");

        // Core tab
        verticalLayoutCore = buildVerticalLayoutCore();
        verticalLayoutCore.setImmediate(false);
        verticalLayoutCore.setSizeUndefined();

        setCompositionRoot(verticalLayoutCore);
    }

    /**
     * Builds layout contains Core tab components of {@link #tabSheet}. Calls
     * from {@link #buildMainLayout}
     * 
     * @return verticalLayoutCore. VerticalLayout with components located at the
     *         Core tab.
     */
    private VerticalLayout buildVerticalLayoutCore() {
        // common part: create layout
        verticalLayoutCore = new VerticalLayout();
        verticalLayoutCore.setImmediate(false);
        verticalLayoutCore.setSizeUndefined();
        verticalLayoutCore.setMargin(true);
        verticalLayoutCore.setSpacing(true);

        // CheckBox selected for each pipeline execution generates a different name
        failExecution = new CheckBox(
                "if invalid data find out, pipeline execution fails immediately");
        failExecution.setImmediate(false);
        failExecution.setSizeUndefined();
        verticalLayoutCore.addComponent(failExecution);

        createOutput = new CheckBox(
                "Add triples to report output only if some data are invalid");
        createOutput.setImmediate(false);
        failExecution.setSizeUndefined();
        verticalLayoutCore.addComponent(createOutput);

        return verticalLayoutCore;
    }

    /**
     * Load values from configuration object implementing {@link DPUConfigObject} interface and configuring DPU into the dialog
     * where the configuration object may be edited.
     * 
     * @throws DPUConfigException
     *             Exception which might be thrown when components
     *             are in read-only mode or when values loading to
     *             this fields could not be converted. Also when
     *             requested operation is not supported.
     * @param conf
     *            Object holding configuration which is used to initialize
     *            fields in the configuration dialog.
     */
    @Override
    public void setConfiguration(RDFDataValidatorConfig conf) throws DPUConfigException {
        failExecution.setValue(conf.canStopExecution());
        createOutput.setValue(conf.hasSometimesOutput());
    }

    /**
     * Set values from from dialog where the configuration object may be edited
     * to configuration object implementing {@link DPUConfigObject} interface
     * and configuring DPU
     * 
     * @throws DPUConfigException
     *             Exception which might be thrown when some of
     *             fields contains null value.
     * @return config object holding configuration which is used in {@link #setConfiguration} to initialize fields in the
     *         configuration dialog.
     */
    @Override
    public RDFDataValidatorConfig getConfiguration() throws DPUConfigException {
        boolean stopExecution = failExecution.getValue();
        boolean sometimesOutput = createOutput.getValue();

        RDFDataValidatorConfig conf = new RDFDataValidatorConfig(stopExecution,
                sometimesOutput);
        return conf;

    }
}
