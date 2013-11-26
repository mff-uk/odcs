package cz.cuni.mff.xrg.odcs.loader.file;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.converter.Converter;

import com.vaadin.ui.*;

import cz.cuni.mff.xrg.odcs.commons.configuration.*;
import cz.cuni.mff.xrg.odcs.commons.module.dialog.BaseConfigDialog;
import cz.cuni.mff.xrg.odcs.rdf.enums.RDFFormatType;

import java.util.List;

/**
 * Configuration dialog for DPU RDF File Loader.
 *
 * @author Maria
 *
 */
public class FileLoaderDialog extends BaseConfigDialog<FileLoaderConfig> {

	private static final long serialVersionUID = 1L;

	private GridLayout mainLayout;

	/**
	 * TabSheet of Configuration dialog. Contains two tabs: Core and Details
	 */
	private TabSheet tabSheet;

	private VerticalLayout verticalLayoutDetails;

	private VerticalLayout verticalLayoutCore;

	private HorizontalLayout horizontalLayoutFormat;

	/**
	 * ComboBox to set output RDF format (RDF/XML, TTL, TriG, N3)
	 */
	private ComboBox comboBoxFormat;

	private Label labelFormat;

	/**
	 * CheckBox to set that each run of the loader should generate a new file,
	 * e.g. {name}-00001.ttl, {name}-00002.ttl.
	 */
	private CheckBox checkBoxDiffName;

	/**
	 * TextField to set file path to the where the file should be stored.
	 */
	private TextField textFieldFilePath;

	private Validator.InvalidValueException ex;

	/**
	 * Basic constructor.
	 */
	public FileLoaderDialog() {
		super(FileLoaderConfig.class);
		inicialize();
		buildMainLayout();
		setCompositionRoot(mainLayout);
		mapData();
	}

	private void inicialize() {
		ex = new Validator.InvalidValueException("Valid");
	}

	/**
	 * Set format data to {@link #comboBoxFormat}
	 */
	private void mapData() {

		List<RDFFormatType> formatTypes = RDFFormatType.getListOfRDFType();

		for (RDFFormatType next : formatTypes) {
			String formatValue = RDFFormatType.getStringValue(next);
			comboBoxFormat.addItem(formatValue);
		}

		comboBoxFormat
				.setValue(RDFFormatType.getStringValue(RDFFormatType.AUTO));


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
		textFieldFilePath = new TextField();
		textFieldFilePath.setNullRepresentation("");
		textFieldFilePath.setCaption("File path:");
		textFieldFilePath.setImmediate(true);
		textFieldFilePath.setWidth("100%");
		textFieldFilePath.setHeight("-1px");
		textFieldFilePath.setInputPrompt("C:\\ted\\result.ttl");
		textFieldFilePath.addValidator(new Validator() {
			@Override
			public void validate(Object value) throws InvalidValueException {
				if (value.getClass() == String.class && !((String) value)
						.isEmpty()) {
					return;
				}
				ex = new InvalidValueException("File path must be filled!");
				throw ex;
			}
		});
		verticalLayoutCore.addComponent(textFieldFilePath);


		// CheckBox selected for each pipeline execution generates a different name
		checkBoxDiffName = new CheckBox();
		checkBoxDiffName
				.setCaption("Each pipeline execution generates a different name");
		checkBoxDiffName.setImmediate(false);
		checkBoxDiffName.setWidth("-1px");
		checkBoxDiffName.setHeight("-1px");
		verticalLayoutCore.addComponent(checkBoxDiffName);

		// horizontalLayoutFormat
		horizontalLayoutFormat = new HorizontalLayout();
		horizontalLayoutFormat.setImmediate(false);
		horizontalLayoutFormat.setWidth("-1px");
		horizontalLayoutFormat.setHeight("-1px");
		horizontalLayoutFormat.setMargin(false);
		horizontalLayoutFormat.setSpacing(true);

		// labelFormat
		labelFormat = new Label();
		labelFormat.setImmediate(false);
		labelFormat.setWidth("79px");
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

		verticalLayoutCore.addComponent(horizontalLayoutFormat);

		return verticalLayoutCore;
	}

	/**
	 * Set values from from dialog where the configuration object may be edited
	 * to configuration object implementing {@link DPUConfigObject} interface
	 * and configuring DPU
	 *
	 * @throws ConfigException Exception which might be thrown when fields
	 *                         {@link #textFieldFileName} and
	 *                         {@link #textFieldFilePath} contain null value.
	 * @return config Object holding configuration which is used in
	 *         {@link #setConfiguration} to initialize fields in the
	 *         configuration dialog.
	 */
	@Override
	public FileLoaderConfig getConfiguration() throws ConfigException {
		if (!textFieldFilePath.isValid()) {
			throw new ConfigException(ex.getMessage(), ex);
		} else {
			FileLoaderConfig config = new FileLoaderConfig();
			config.DiffName = checkBoxDiffName.getValue();
			config.FilePath = textFieldFilePath.getValue().trim();

			String formatValue = (String) comboBoxFormat.getValue();
			config.RDFFileFormat = RDFFormatType.getTypeByString(formatValue);
			return config;
		}

	}

	/**
	 * Load values from configuration object implementing
	 * {@link DPUConfigObject} interface and configuring DPU into the dialog
	 * where the configuration object may be edited.
	 *
	 * @throws ConfigException Exception which might be thrown when components
	 *                         {@link #textFieldFileName}, {@link #textFieldFilePath}, {@link #checkBoxDiffName}, {@link #comboBoxFormat}
	 *                         in read-only mode or when values loading to this
	 *                         fields could not be converted.
	 * @param conf Object holding configuration which is used to initialize
	 *             fields in the configuration dialog.
	 */
	@Override
	public void setConfiguration(FileLoaderConfig conf) throws ConfigException {
		try {
			checkBoxDiffName.setValue(conf.DiffName);
			textFieldFilePath.setValue(conf.FilePath.trim());

			String formatValue = RDFFormatType
					.getStringValue(conf.RDFFileFormat);
			comboBoxFormat.setValue(formatValue);
		} catch (Property.ReadOnlyException | Converter.ConversionException e) {
			// throw setting exception
			throw new ConfigException(e.getMessage(), e);
		}
	}

	@Override
	public String getDescription() {
		String path = textFieldFilePath.getValue().trim();
		// create description
		StringBuilder description = new StringBuilder();
		description.append("Load to: ");
		description.append(path);
		return description.toString();
	}
}
