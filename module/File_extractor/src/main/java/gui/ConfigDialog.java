package gui;

import module.Config;

import com.vaadin.ui.*;

import cz.cuni.xrg.intlib.commons.configuration.*;
import cz.cuni.xrg.intlib.commons.module.gui.AbstractConfigDialog;

/**
 * Configuration dialog.
 *
 * @author Maria
 *
 */
public class ConfigDialog extends AbstractConfigDialog {

    private static final long serialVersionUID = 1L;

    private GridLayout mainLayout;
    private ComboBox comboBoxFormat; //RDFFormat
    private Label labelFormat;
    private TextField textFieldOnly;
    private Label labelOnly;
 //   private CheckBox checkBoxWhole;
    private TextField textFieldPath; //Path
    private HorizontalLayout horizontalLayoutOnly;
    private HorizontalLayout horizontalLayoutFormat;

    public ConfigDialog() {
        buildMainLayout();
        setCompositionRoot(mainLayout);
        mapData();
    }

    private void mapData() {

        comboBoxFormat.addItem("TTL");
        comboBoxFormat.addItem("RDF/XML");
        comboBoxFormat.addItem("N3");
        comboBoxFormat.addItem("TriG");
        comboBoxFormat.setValue("TTL");
    }

    @Override
    public void getConfiguration(Configuration config) {
    	config.setValue(Config.OnlyThisText.name(), textFieldOnly.getValue());
        config.setValue(Config.Path.name(), textFieldPath.getValue());
        config.setValue(Config.FileSuffix.name(), (String)comboBoxFormat.getValue());
// TODO: read from dialog
        config.setValue(Config.OnlyThisSuffix.name(), false);
    }

    @Override
    public void setConfiguration(Configuration conf) {

        try {
        	
            textFieldPath.setValue((String) conf.getValue(Config.Path.name()));
            comboBoxFormat.setValue((String) conf.getValue(Config.FileSuffix.name()));
            textFieldOnly.setValue((String) conf.getValue(Config.OnlyThisText.name()));


        } catch (Exception ex) {
            // throw setting exception
            throw new ConfigurationException();
        }
    }

    private GridLayout buildMainLayout() {


        // common part: create layout
        mainLayout = new GridLayout(1, 3);
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");
        mainLayout.setMargin(false);
        mainLayout.setSpacing(true);

        // top-level component properties
        setWidth("100%");
        setHeight("100%");


        // textFieldPath
        textFieldPath = new TextField();
        textFieldPath.setCaption("HTTP URL/Path to file or directory on the server:");
        textFieldPath.setImmediate(false);
        textFieldPath.setWidth("100%");
        textFieldPath.setHeight("-1px");
        textFieldPath.setInputPrompt("http://example.org/test.ttl");
        mainLayout.addComponent(textFieldPath, 0, 0);


        // layoutOnly
        horizontalLayoutOnly = buildHorizontalLayoutOnly();
        mainLayout.addComponent(horizontalLayoutOnly, 0, 1);

        // horizontalLayoutFormat
        horizontalLayoutFormat = buildHorizontalLayoutFormat();
        mainLayout.addComponent(horizontalLayoutFormat, 0, 2);
   
        return mainLayout;
    }

    private HorizontalLayout buildHorizontalLayoutOnly() {
        // common part: create layout
        horizontalLayoutOnly = new HorizontalLayout();
        horizontalLayoutOnly.setImmediate(false);
        horizontalLayoutOnly.setWidth("-1px");
        horizontalLayoutOnly.setHeight("-1px");
        horizontalLayoutOnly.setMargin(false);
        horizontalLayoutOnly.setSpacing(true);

        // labelOnly
        labelOnly = new Label();
        labelOnly.setImmediate(false);
        labelOnly.setWidth("240px");
        labelOnly.setHeight("-1px");
        labelOnly.setValue("If directory, process only files with extension:");
        horizontalLayoutOnly.addComponent(labelOnly);

        // textFieldOnly
        textFieldOnly = new TextField();
        textFieldOnly.setImmediate(false);
        textFieldOnly.setWidth("50px");
        textFieldOnly.setHeight("-1px");
        textFieldOnly.setInputPrompt("*.ttl");
        horizontalLayoutOnly.addComponent(textFieldOnly);
        horizontalLayoutOnly.setComponentAlignment(textFieldOnly,Alignment.TOP_RIGHT);
        

        return horizontalLayoutOnly;
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
        labelFormat.setWidth("74px");
        labelFormat.setHeight("-1px");
        labelFormat.setValue("RDF Format:");
        horizontalLayoutFormat.addComponent(labelFormat);

        // comboBoxFormat
        comboBoxFormat = new ComboBox();
        comboBoxFormat.setImmediate(false);
        comboBoxFormat.setWidth("-1px");
        comboBoxFormat.setHeight("-1px");
        comboBoxFormat.setNewItemsAllowed(false);
		comboBoxFormat.setNullSelectionAllowed(false);
        horizontalLayoutFormat.addComponent(comboBoxFormat);
   //     horizontalLayoutFormat.setComponentAlignment(comboBoxFormat,Alignment.TOP_RIGHT);
        

        return horizontalLayoutFormat;
    }
}
