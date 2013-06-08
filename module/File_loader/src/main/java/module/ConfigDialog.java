package module;

import com.vaadin.data.Property;
import com.vaadin.data.util.converter.Converter;

import com.vaadin.ui.*;

import cz.cuni.xrg.intlib.commons.configuration.*;
import cz.cuni.xrg.intlib.commons.data.rdf.RDFFormatType;
import cz.cuni.xrg.intlib.commons.web.AbstractConfigDialog;

/**
 * FileLoaderConfig dialog.
 *
 * @author Maria
 *
 */
public class ConfigDialog extends AbstractConfigDialog<FileLoaderConfig> {

    private static final long serialVersionUID = 1L;
    private GridLayout mainLayout;
    private TabSheet tabSheet;
    private VerticalLayout verticalLayoutDetails;
    private VerticalLayout verticalLayoutCore;
    private HorizontalLayout horizontalLayoutFormat;
    private ComboBox comboBoxFormat; //RDFformat
    private Label labelFormat;
    private CheckBox checkBoxDiffName;
    private TextField textFieldFileName; // FileName
    private TextField textFieldDir;	//Directory

    public ConfigDialog() {
        buildMainLayout();
        setCompositionRoot(mainLayout);
        mapData();
    }

    private void mapData() {

        comboBoxFormat.addItem(RDFFormatType.AUTO);
        comboBoxFormat.addItem(RDFFormatType.TTL);
        comboBoxFormat.addItem(RDFFormatType.RDFXML);
        comboBoxFormat.addItem(RDFFormatType.N3);
        comboBoxFormat.addItem(RDFFormatType.TRIG);

        comboBoxFormat.setValue(RDFFormatType.AUTO);


    }

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
        tabSheet = buildTabSheet();
        mainLayout.addComponent(tabSheet, 0, 0);
        mainLayout.setComponentAlignment(tabSheet, Alignment.TOP_LEFT);

        return mainLayout;
    }

    private TabSheet buildTabSheet() {
        // common part: create layout
        tabSheet = new TabSheet();
        tabSheet.setImmediate(true);
        tabSheet.setWidth("100%");
        tabSheet.setHeight("100%");

        // verticalLayoutCore
        verticalLayoutCore = buildVerticalLayoutCore();
        verticalLayoutCore.setImmediate(false);
        verticalLayoutCore.setWidth("100.0%");
        verticalLayoutCore.setHeight("100.0%");
        tabSheet.addTab(verticalLayoutCore, "Core", null);

        // verticalLayoutDetails
        verticalLayoutDetails = new VerticalLayout();
        verticalLayoutDetails.setImmediate(false);
        verticalLayoutDetails.setWidth("100.0%");
        verticalLayoutDetails.setHeight("100.0%");
        verticalLayoutDetails.setMargin(false);
        tabSheet.addTab(verticalLayoutDetails, "Details", null);

        return tabSheet;
    }

    private VerticalLayout buildVerticalLayoutCore() {
        // common part: create layout
        verticalLayoutCore = new VerticalLayout();
        verticalLayoutCore.setImmediate(false);
        verticalLayoutCore.setWidth("100.0%");
        verticalLayoutCore.setHeight("100.0%");
        verticalLayoutCore.setMargin(true);
        verticalLayoutCore.setSpacing(true);


        // textFieldDir
        textFieldDir = new TextField();
        textFieldDir.setNullRepresentation("");
        textFieldDir.setCaption("Directory:");
        textFieldDir.setImmediate(false);
        textFieldDir.setWidth("100%");
        textFieldDir.setHeight("-1px");
        textFieldDir.setInputPrompt("C:\\ted\\");
        verticalLayoutCore.addComponent(textFieldDir);

        // textFieldFileName
        textFieldFileName = new TextField();
        textFieldFileName.setNullRepresentation("");
        textFieldFileName.setCaption("File name:");
        textFieldFileName.setImmediate(false);
        textFieldFileName.setWidth("100%");
        textFieldFileName.setHeight("-1px");
        textFieldFileName.setInputPrompt("test-ted.ttl");
        verticalLayoutCore.addComponent(textFieldFileName);

        // checkBoxDiffName
        checkBoxDiffName = new CheckBox();
        checkBoxDiffName
                .setCaption("Each pipeline execution generates a different name");
        checkBoxDiffName.setImmediate(false);
        checkBoxDiffName.setWidth("-1px");
        checkBoxDiffName.setHeight("-1px");
        verticalLayoutCore.addComponent(checkBoxDiffName);

        // horizontalLayoutFormat
        horizontalLayoutFormat = buildHorizontalLayoutFormat();
        verticalLayoutCore.addComponent(horizontalLayoutFormat);

        return verticalLayoutCore;
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
        labelFormat.setWidth("79px");
        labelFormat.setHeight("-1px");
        labelFormat.setValue("RDF Format:");
        horizontalLayoutFormat.addComponent(labelFormat);

        // comboBoxFormat
        comboBoxFormat = new ComboBox();
//        comboBoxFormat.setNullSelectionItemId(RDFFormatType.AUTO);
        comboBoxFormat.setImmediate(true);
        comboBoxFormat.setWidth("-1px");
        comboBoxFormat.setHeight("-1px");
        comboBoxFormat.setNewItemsAllowed(false);
        comboBoxFormat.setNullSelectionAllowed(false);
        horizontalLayoutFormat.addComponent(comboBoxFormat);

        return horizontalLayoutFormat;
    }

	@Override
	public FileLoaderConfig getConfiguration() throws ConfigException {
		FileLoaderConfig config = new FileLoaderConfig();
		config.DiffName = checkBoxDiffName.getValue();
		config.DirectoryPath = textFieldDir.getValue();
		config.FileName = textFieldFileName.getValue();
		config.RDFFileFormat = (RDFFormatType) comboBoxFormat.getValue();
		return null;
	}
	
    /**
     * Load values from configuration into dialog.
     *
     * @throws ConfigException
     * @param conf
     */
	@Override
    public void setConfiguration(FileLoaderConfig conf) throws ConfigException {
        try {
            checkBoxDiffName.setValue(conf.DiffName);
            textFieldDir.setValue(conf.DirectoryPath);
            textFieldFileName.setValue(conf.FileName);
            comboBoxFormat.setValue(conf.RDFFileFormat);
        } catch (Property.ReadOnlyException | Converter.ConversionException ex) {
            // throw setting exception
            throw new ConfigException();
        }
    }	
}
