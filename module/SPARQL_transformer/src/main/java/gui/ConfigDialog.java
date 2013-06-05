package gui;

import com.vaadin.data.Property;
import module.Config;

import com.vaadin.ui.*;

import cz.cuni.xrg.intlib.commons.configuration.*;

/**
 * Config dialog.
 *
 * @author Maria
 *
 */
public class ConfigDialog extends CustomComponent {

    private static final long serialVersionUID = 1L;
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
     */
    public void getConfiguration(Config config) {
        config.setValue(Config.SPARQL_Update_Query.name(), txtQuery.getValue());
    }

    /**
     * Load values from configuration into dialog.
     *
     * @throws ConfigException
     * @param conf
     */
    public void setConfiguration(Config conf) {
        try {
            txtQuery.setValue((String) conf.getValue(Config.SPARQL_Update_Query.name()));

        } catch (Exception ex) {
            // throw setting exception
            throw new ConfigException();
        }
    }

    private GridLayout buildMainLayout() {
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
        
        txtQuery.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                final String query=txtQuery.getValue();
                
                
            }
        });
        
        txtQuery.setNullRepresentation("");
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
