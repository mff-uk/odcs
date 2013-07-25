package cz.cuni.mff.xrg.intlib.loader.file;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.converter.Converter;

import com.vaadin.ui.*;

import cz.cuni.xrg.intlib.commons.configuration.*;
import cz.cuni.xrg.intlib.commons.web.AbstractConfigDialog;
import cz.cuni.xrg.intlib.rdf.enums.RDFFormatType;
import java.util.List;

/**
 * Configuration dialog for DPU RDF File Loader. 
 *
 * @author Maria
 *
 */
public class FileLoaderDialog extends AbstractConfigDialog<FileLoaderConfig> {

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
	 * TextField to set name of the file (without extension).
	 */
	private TextField textFieldFileName; 

	/**
	 * TextField to set path to the local directory to which the file should be stored.
	 */
	private TextField textFieldDir;	

	/**
	 *  Basic constructor.
	 */
	public FileLoaderDialog() {
		buildMainLayout();
		setCompositionRoot(mainLayout);
		mapData();
	}

	
	/**
	 * Set format data to {@link #comboBoxFormat}
	 */
	private void mapData() {

		List<RDFFormatType> formatTypes = RDFFormatType.getListOfRDFType();

		for (RDFFormatType next : formatTypes) {
			comboBoxFormat.addItem(next);
		}

		comboBoxFormat.setValue(RDFFormatType.AUTO);


	}

	/**
	 * Builds main layout contains {@link #tabSheet} with all dialog components.
	 * 
	 * @return mainLayout GridLayout with all components of configuration dialog.
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
	 * Builds layout contains Core tab components of {@link #tabSheet}.
	 * Calls from {@link #buildMainLayout}

	 * @return verticalLayoutCore. VerticalLayout with components located at the Core tab.
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
		textFieldDir = new TextField();
		textFieldDir.setNullRepresentation("");
		textFieldDir.setCaption("Directory:");
		textFieldDir.setImmediate(false);
		textFieldDir.setWidth("100%");
		textFieldDir.setHeight("-1px");
		textFieldDir.setInputPrompt("C:\\ted\\");
		textFieldDir.addValidator(new Validator() {
			@Override
			public void validate(Object value) throws InvalidValueException {
				if (value.getClass() == String.class && !((String) value)
						.isEmpty()) {
					return;
				}
				throw new InvalidValueException("Directory must be filled!");
			}
		});
		verticalLayoutCore.addComponent(textFieldDir);

		//File name TextField
		textFieldFileName = new TextField();
		textFieldFileName.setNullRepresentation("");
		textFieldFileName.setCaption("File name:");
		textFieldFileName.setImmediate(false);
		textFieldFileName.setWidth("100%");
		textFieldFileName.setHeight("-1px");
		textFieldFileName.setInputPrompt("test-ted.ttl");
		textFieldFileName.addValidator(new Validator() {
			@Override
			public void validate(Object value) throws InvalidValueException {
				if (value.getClass() == String.class && !((String) value)
						.isEmpty()) {
					return;
				}
				throw new InvalidValueException("File name must be filled!");
			}
		});
		verticalLayoutCore.addComponent(textFieldFileName);

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
	 * to configuration object implementing {@link Config} interface and 
	 * configuring DPU
	 * 
	 * @throws ConfigException Exception which might be thrown when fields {@link #textFieldFileName} 
	 * and {@link #textFieldDir} contain null value.
	 * @return config Object holding configuration which is used in {@link #setConfiguration} 
	 * to initialize fields in the configuration dialog.
	 */
	
	@Override
	public FileLoaderConfig getConfiguration() throws ConfigException {
		if ((!textFieldDir.isValid()) || (!textFieldFileName.isValid())) {
			throw new ConfigException();
		} else {
			FileLoaderConfig config = new FileLoaderConfig();
			config.DiffName = checkBoxDiffName.getValue();
			config.DirectoryPath = textFieldDir.getValue().trim();
			config.FileName = textFieldFileName.getValue().trim();
			config.RDFFileFormat = (RDFFormatType) comboBoxFormat.getValue();
			return config;
		}

	}

    /**
    * Load values from configuration object implementing {@link Config} interface and 
    * configuring DPU into the dialog where the configuration object may be edited.
    * 
    * @throws ConfigException Exception which might be thrown when components 
    * {@link #textFieldFileName}, {@link #textFieldDir}, {@link #checkBoxDiffName}, {@link #comboBoxFormat} 
    * in read-only mode or when values loading to this fields could not be converted.
    * @param conf Object holding configuration which is used to initialize fields in the configuration dialog.
    */
	@Override
	public void setConfiguration(FileLoaderConfig conf) throws ConfigException {
		try {
			checkBoxDiffName.setValue(conf.DiffName);
			textFieldDir.setValue(conf.DirectoryPath.trim());
			textFieldFileName.setValue(conf.FileName.trim());
			comboBoxFormat.setValue(conf.RDFFileFormat);
		} catch (Property.ReadOnlyException | Converter.ConversionException ex) {
			// throw setting exception
			throw new ConfigException(ex.getMessage(), ex);
		}
	}
}
