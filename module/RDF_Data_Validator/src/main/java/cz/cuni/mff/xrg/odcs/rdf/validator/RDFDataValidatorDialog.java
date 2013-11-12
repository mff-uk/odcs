package cz.cuni.mff.xrg.odcs.rdf.validator;

import com.vaadin.data.Validator;
import com.vaadin.ui.*;
import cz.cuni.mff.xrg.odcs.commons.configuration.ConfigException;
import cz.cuni.mff.xrg.odcs.commons.module.dialog.BaseConfigDialog;
import java.io.File;

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

	private CheckBox createFile;

	/**
	 * TextField to set directory path to the where the file should be stored.
	 */
	private TextField textFieldDirPath;

	private Validator.InvalidValueException ex;

	public RDFDataValidatorDialog() {
		super(RDFDataValidatorConfig.class);
		inicialize();
		buildMainLayout();
		setCompositionRoot(mainLayout);

	}

	private void inicialize() {
		ex = new Validator.InvalidValueException("Valid");
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


		//Directory TextField
		textFieldDirPath = new TextField();
		textFieldDirPath.setNullRepresentation("");
		textFieldDirPath.setCaption("Directory path for errors report:");
		textFieldDirPath.setImmediate(true);
		textFieldDirPath.setWidth("100%");
		textFieldDirPath.setHeight("-1px");
		textFieldDirPath.setInputPrompt("C:\\validator\\errors");
		textFieldDirPath.addValidator(new Validator() {
			@Override
			public void validate(Object value) throws Validator.InvalidValueException {

				String directory = value.toString();

				if (directory.isEmpty()) {
					ex = new Validator.EmptyValueException(
							"Directory path must be filled!");
					throw ex;
				} else {
					File dir = new File(directory);
					if (dir.exists() && !dir.isDirectory()) {
						ex = new InvalidValueException(
								"Given path is not path to existing directory");
						throw ex;
					}
				}
			}
		});
		verticalLayoutCore.addComponent(textFieldDirPath);


		// CheckBox selected for each pipeline execution generates a different name
		failExecution = new CheckBox(
				"if invalid data find out, pipeline execution fails immediately");
		failExecution.setImmediate(false);
		failExecution.setWidth("-1px");
		failExecution.setHeight("-1px");
		verticalLayoutCore.addComponent(failExecution);

		createFile = new CheckBox(
				"Create report file only if some data are invalid");
		createFile.setImmediate(false);
		createFile.setWidth("-1px");
		createFile.setHeight("-1px");
		verticalLayoutCore.addComponent(createFile);

		return verticalLayoutCore;
	}

	@Override
	public void setConfiguration(RDFDataValidatorConfig conf) throws ConfigException {
		textFieldDirPath.setValue(conf.directoryPath);
		failExecution.setValue(conf.stopExecution);
		createFile.setValue(conf.sometimesFile);


	}

	@Override
	public RDFDataValidatorConfig getConfiguration() throws ConfigException {

		if (!textFieldDirPath.isValid()) {
			throw new ConfigException(ex.getMessage(), ex);
		} else {
			RDFDataValidatorConfig conf = new RDFDataValidatorConfig();
			conf.directoryPath = textFieldDirPath.getValue().trim();
			conf.stopExecution = failExecution.getValue();
			conf.sometimesFile = createFile.getValue();

			return conf;
		}
	}

	@Override
	public String getDescription() {
		String path = textFieldDirPath.getValue().trim();
		// create description
		StringBuilder description = new StringBuilder();
		description.append("Error report about validation load to file: ");
		description.append(path);
		return description.toString();
	}
}
