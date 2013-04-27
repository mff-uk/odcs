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
    private TextArea txtQuery;
    private Label labelUpQuer;

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
        //   config.setValue(Config.NameDPU.name(), textFieldName.getValue());
        //   config.setValue(Config.Description.name(), textAreaDescr.getValue());
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

            //      textFieldName.setValue((String) conf.getValue(Config.NameDPU.name()));
            //      textAreaDescr.setValue((String) conf.getValue(Config.Description.name()));
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
        mainLayout = new GridLayout(2, 1);
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");
        mainLayout.setMargin(false);
        //mainLayout.setSpacing(true);

        // top-level component properties
        setWidth("100%");
        setHeight("100%");

        // labelUpQuer
        labelUpQuer = new Label();
        labelUpQuer.setImmediate(false);
        labelUpQuer.setWidth("68px");
        labelUpQuer.setHeight("-1px");
        labelUpQuer.setValue("SPARQL  Update Query");
        mainLayout.addComponent(labelUpQuer, 0, 0);

        // textAreaUpQuer
        txtQuery = new TextArea();
        txtQuery.setImmediate(false);
        txtQuery.setWidth("100%");
        txtQuery.setHeight("211px");
        txtQuery.setInputPrompt("PREFIX br:<http://purl.org/business-register#>\nMODIFY\nDELETE { ?s pc:contact ?o}\nINSERT { ?s br:contact ?o}\nWHERE {\n\t     ?s a gr:BusinessEntity .\n\t      ?s pc:contact ?o\n}");
        mainLayout.addComponent(txtQuery, 1, 0);
        mainLayout.setColumnExpandRatio(0, 0.00001f);
        mainLayout.setColumnExpandRatio(1, 0.99999f);

        return mainLayout;
    }
}
