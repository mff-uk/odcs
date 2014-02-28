package cz.cuni.mff.xrg.odcs.rdf.validator;

import com.vaadin.ui.*;
import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.configuration.DPUConfigObject;
import cz.cuni.mff.xrg.odcs.commons.module.dialog.BaseConfigDialog;

/**
 * DPU's configuration dialog for setting directory path and others parameters
 * for saving validation report.
 */
public class RDFDataValidatorDialog extends BaseConfigDialog<RDFDataValidatorConfig> {

	private GridLayout mainLayout;

	/**
	 * TabSheet of Configuration dialog. Contains two tabs: Core and Details
	 */
	private TabSheet tabSheet;

	private VerticalLayout verticalLayoutDetails;

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
		setCompositionRoot(mainLayout);

	}

	/**
	 * Builds main layout contains {@link #tabSheet} with all dialog components.
	 *
	 * @return mainLayout GridLayout with all components of configuration
	 *         dialog.
	 */
	private GridLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new GridLayout(1, 1);
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");

		// top-level component properties
		setWidth("100%");
		setHeight("100%");

		// tabSheet
		tabSheet = new TabSheet();
		tabSheet.setImmediate(true);
		tabSheet.setWidth("100%");
		tabSheet.setHeight("100%");

		// Core tab
		verticalLayoutCore = buildVerticalLayoutCore();
		verticalLayoutCore.setImmediate(false);
		verticalLayoutCore.setWidth("100.0%");
		verticalLayoutCore.setHeight("100.0%");
		tabSheet.addTab(verticalLayoutCore, "Core", null);

		// Details tab
		verticalLayoutDetails = new VerticalLayout();
		verticalLayoutDetails.setImmediate(false);
		verticalLayoutDetails.setWidth("100.0%");
		verticalLayoutDetails.setHeight("100.0%");
		verticalLayoutDetails.setMargin(false);
		tabSheet.addTab(verticalLayoutDetails, "Details", null);

		mainLayout.addComponent(tabSheet, 0, 0);
		mainLayout.setComponentAlignment(tabSheet, Alignment.TOP_LEFT);

		return mainLayout;
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
		verticalLayoutCore.setWidth("100.0%");
		verticalLayoutCore.setHeight("100.0%");
		verticalLayoutCore.setMargin(true);
		verticalLayoutCore.setSpacing(true);

		// CheckBox selected for each pipeline execution generates a different name
		failExecution = new CheckBox(
				"if invalid data find out, pipeline execution fails immediately");
		failExecution.setImmediate(false);
		failExecution.setWidth("-1px");
		failExecution.setHeight("-1px");
		verticalLayoutCore.addComponent(failExecution);

		createOutput = new CheckBox(
				"Add triples to report output only if some data are invalid");
		createOutput.setImmediate(false);
		createOutput.setWidth("-1px");
		createOutput.setHeight("-1px");
		verticalLayoutCore.addComponent(createOutput);

		return verticalLayoutCore;
	}

	/**
	 * Load values from configuration object implementing
	 * {@link DPUConfigObject} interface and configuring DPU into the dialog
	 * where the configuration object may be edited.
	 *
	 * @throws ConfigException Exception which might be thrown when components
	 *                         are in read-only mode or when values loading to
	 *                         this fields could not be converted. Also when
	 *                         requested operation is not supported.
	 * @param conf Object holding configuration which is used to initialize
	 *             fields in the configuration dialog.
	 */
	@Override
	public void setConfiguration(RDFDataValidatorConfig conf) throws ConfigException {
		failExecution.setValue(conf.canStopExecution());
		createOutput.setValue(conf.hasSometimesOutput());
	}

	/**
	 * Set values from from dialog where the configuration object may be edited
	 * to configuration object implementing {@link DPUConfigObject} interface
	 * and configuring DPU
	 *
	 * @throws ConfigException Exception which might be thrown when some of
	 *                         fields contains null value.
	 * @return config object holding configuration which is used in
	 *         {@link #setConfiguration} to initialize fields in the
	 *         configuration dialog.
	 */
	@Override
	public RDFDataValidatorConfig getConfiguration() throws ConfigException {

		boolean stopExecution = failExecution.getValue();
		boolean sometimesOutput = createOutput.getValue();

		RDFDataValidatorConfig conf = new RDFDataValidatorConfig(stopExecution,
				sometimesOutput);
		return conf;

	}
}
