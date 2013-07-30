package cz.cuni.mff.xrg.intlib.extractor.silklinker;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.web.AbstractConfigDialog;

/**
 * DPU's configuration dialog. User can use this dialog to configure DPU
 * configuration.
 *
 */
public class SilkLinkerDialog extends AbstractConfigDialog<SilkLinkerConfig> {

    private GridLayout mainLayout;
    private TextField confPath; //Path

    public SilkLinkerDialog() {
        buildMainLayout();
        setCompositionRoot(mainLayout);
    }

    private GridLayout buildMainLayout() {
        // common part: create layout
        mainLayout = new GridLayout(1, 2);
        mainLayout.setImmediate(false);
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");
        mainLayout.setMargin(false);
        //mainLayout.setSpacing(true);

        // top-level component properties
        setWidth("100%");
        setHeight("100%");
        
        confPath = new TextField();
        confPath.setNullRepresentation("");
        confPath.setCaption("Path to conf file:");
        confPath.setImmediate(false);
        confPath.setWidth("100%");
        confPath.setHeight("-1px");
        //confPath.setInputPrompt("/tmp/silkScripts/be-sameAs.xml");
        confPath.setValue("/tmp/silkScripts/be-sameAs.xml");
        confPath.addValidator(new Validator() {
            @Override
            public void validate(Object value) throws Validator.InvalidValueException {
                if (value.getClass() == String.class && !((String) value).isEmpty()) {
                    return;
                }
                throw new Validator.InvalidValueException("Path must be filled!");
            }
        });
        mainLayout.addComponent(confPath) ;


        return mainLayout;
    }

    @Override
    public void setConfiguration(SilkLinkerConfig conf) throws ConfigException {
        try {
            confPath.setValue(conf.getSilkConf());
            
        } catch (Exception ex) {
            // throw setting exception
            throw new ConfigException();
        }

    }

    @Override
    public SilkLinkerConfig getConfiguration() throws ConfigException {
        if (!confPath.isValid()) {
            throw new ConfigException();
        } else {
            SilkLinkerConfig conf = new SilkLinkerConfig(confPath.getValue().trim());
            return conf;
        }
    }
}
