package gui;

import module.Config;

import com.vaadin.ui.*;

import cz.cuni.xrg.intlib.commons.configuration.*;

/**
 * Configuration dialog.
 *
 * @author Petyr
 *
 */
public class ConfigDialog extends CustomComponent {

    private static final long serialVersionUID = 1L;
    /**
     * TODO Implement your own configuration component. You can use vaadin
     * visual editor if you like. Just remember don't use vaddin classes the ere
     * not located directly in package com.vaadi.ui;
     */
    private GridLayout mainLayout;
    private Button buttonCanc;
    private Button buttonSave;
    private Button buttonDev;
    private GridLayout gridLayoutName;
    private TextArea txtQuery;
    private Label labelUpQuer;
    private TextArea textAreaDescr;
    private Label labelDescr;
    private TextField textFieldName;
    private Label labelName;
    private HorizontalLayout horizontalLayoutButtons;

    public ConfigDialog() {
        buildMainLayout();
        setCompositionRoot(mainLayout);
    }

    /**
     * Return current configuration from dialog. Can return null, if current
     * configuration is invalid.
     *
     * @return current configuration or null
     */
    public Configuration getConfiguration() {
        Configuration config = new Configuration();
        /**
         * TODO Gather data from you dialog and store them into configuration.
         * You can use enum Config to make sure that you don't miss spell the
         * ids of values. Also remember that you can return null in case of
         * invalid configuration in dialog.
         */
        config.setValue(Config.NameDPU.name(), textFieldName.getValue());
        config.setValue(Config.Description.name(), textAreaDescr.getValue());
        config.setValue(Config.SPARQL_Update_Query.name(), txtQuery.getValue());


        return config;
    }

    /**
     * Load values from configuration into dialog.
     *
     * @throws ConfigurationException
     * @param conf
     */
    public void setConfiguration(Configuration conf) {
        /**
         * TODO Load configuration from conf into dialog components. You can use
         * enum Config to make sure that you don't miss spell the ids of values.
         * The ConfigurationException can be thrown in case of invalid
         * configuration.
         */
        try {

            textFieldName.setValue((String) conf.getValue(Config.NameDPU.name()));
            textAreaDescr.setValue((String) conf.getValue(Config.Description.name()));
            txtQuery.setValue((String) conf.getValue(Config.SPARQL_Update_Query.name()));

        } catch (Exception ex) {
            // throw setting exception
            throw new ConfigurationException();
        }
    }

    private GridLayout buildMainLayout() {
        /**
         * TODO Build your component here.
         */
        // common part: create layout
        mainLayout = new GridLayout(2, 4);
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");
        mainLayout.setMargin(true);

        // top-level component properties
        setWidth("100%");
        setHeight("100%");

        // gridLayoutName
        // gridLayoutName = buildGridLayoutName();
        // mainLayout.addComponent(gridLayoutName, "top:40.0px;left:40.0px;");

        // labelName
        labelName = new Label();
        labelName.setImmediate(false);
        labelName.setWidth("-1px");
        labelName.setHeight("-1px");
        labelName.setValue("Name:");
        mainLayout.addComponent(labelName, 0, 0);

        // textFieldName
        textFieldName = new TextField();
        textFieldName.setImmediate(false);
        textFieldName.setWidth("260px");
        textFieldName.setHeight("-1px");
        mainLayout.addComponent(textFieldName, 1, 0);

        // labelDescr
        labelDescr = new Label();
        labelDescr.setImmediate(false);
        labelDescr.setWidth("-1px");
        labelDescr.setHeight("-1px");
        labelDescr.setValue("Description:");
        mainLayout.addComponent(labelDescr, 0, 1);

        // textAreaDescr
        textAreaDescr = new TextArea();
        textAreaDescr.setImmediate(false);
        textAreaDescr.setWidth("260px");
        textAreaDescr.setHeight("36px");
        mainLayout.addComponent(textAreaDescr, 1, 1);

        // labelUpQuer
        labelUpQuer = new Label();
        labelUpQuer.setImmediate(false);
        labelUpQuer.setWidth("74px");
        labelUpQuer.setHeight("-1px");
        labelUpQuer.setValue("SPARQL  Update Query");
        mainLayout.addComponent(labelUpQuer, 0, 2);

        // txtQuery
        txtQuery = new TextArea();
        txtQuery.setImmediate(false);
        txtQuery.setWidth("260px");
        txtQuery.setHeight("211px");
        mainLayout.addComponent(txtQuery, 1, 2);

        // buttonDev
        buttonDev = new Button();
        buttonDev.setCaption("Develop");
        buttonDev.setImmediate(true);
        buttonDev.setWidth("-1px");
        buttonDev.setHeight("-1px");
        mainLayout.addComponent(buttonDev, 0, 3);


        horizontalLayoutButtons = buildHorizontalLayout();
        mainLayout.addComponent(horizontalLayoutButtons, 1, 3);


        return mainLayout;
    }

    private HorizontalLayout buildHorizontalLayout() {
        // common part: create layout
        horizontalLayoutButtons = new HorizontalLayout();
        horizontalLayoutButtons.setImmediate(false);
        horizontalLayoutButtons.setWidth("240px");
        horizontalLayoutButtons.setHeight("1px");
        horizontalLayoutButtons.setMargin(false);
        horizontalLayoutButtons.setSpacing(true);


        // buttonSave
        buttonSave = new Button();
        buttonSave.setCaption("Save & Commit");
        buttonSave.setImmediate(true);
        buttonSave.setWidth("-1px");

        buttonSave.setHeight("-1px");
        horizontalLayoutButtons.addComponent(buttonSave);

        // buttonCanc
        buttonCanc = new Button();
        buttonCanc.setCaption("Cancel");
        buttonCanc.setImmediate(true);
        buttonCanc.setWidth("-1px");
        buttonCanc.setHeight("-1px");
        horizontalLayoutButtons.addComponent(buttonCanc);

        return horizontalLayoutButtons;
    }
}
