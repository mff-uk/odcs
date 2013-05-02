package module;

import gui.ConfigDialog;

import com.vaadin.ui.CustomComponent;

import cz.cuni.xrg.intlib.commons.Type;
import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import cz.cuni.xrg.intlib.commons.configuration.ConfigurationException;
import cz.cuni.xrg.intlib.commons.web.*;
import cz.cuni.xrg.intlib.backend.transformer.events.TransformContext;
import cz.cuni.xrg.intlib.backend.transformer.events.TransformException;
import cz.cuni.xrg.intlib.commons.repository.LocalRepo;

public class SPARQL_transformer implements GraphicalTransformer {

    private LocalRepo repository = null;
    /**
     * Configuration component.
     */
    private gui.ConfigDialog configDialog = null;
    /**
     * DPU configuration.
     */
    private Configuration config = null;

    public SPARQL_transformer() {
    }

    @Override
    public void fillDefaultConfiguration(Configuration configuration) {
    	configuration.setValue(Config.SPARQL_Update_Query.name(), "SELECT {?s ?p ?o} where {?s ?p ?o}");  	
    }     
    
    @Override
    public Type getType() {
        return Type.TRANSFORMER;

    }

    @Override
    public CustomComponent getConfigurationComponent(Configuration configuration) {
        // does dialog exist?
        if (this.configDialog == null) {
            // create it
            this.configDialog = new ConfigDialog();
            this.configDialog.setConfiguration(configuration);
        }
        return this.configDialog;
    }

    @Override
    public Configuration getSettings() throws ConfigurationException {
        if (this.configDialog == null) {
        } else {
            // get configuration from dialog
            Configuration conf = this.configDialog.getConfiguration();
            if (conf == null) {
                // in dialog is invalid configuration .. 
                return null;
            } else {
                this.config = conf;
            }
        }
        return this.config;
    }

    @Override
    public void setSettings(Configuration configuration) {
        this.config = configuration;
        if (this.configDialog == null) {
        } else {
            // also set configuration for dialog
            this.configDialog.setConfiguration(this.config);
        }
    }

    /**
     * Implementation of module functionality here.
     *
     */
    private String getUpdateQuery() {
        String query = (String) config.getValue(Config.SPARQL_Update_Query.name());

        return query;
    }

    @Override
    public void transform(TransformContext context) throws TransformException {

        final String updateQuery = getUpdateQuery();

        repository.transformUsingSPARQL(updateQuery);
    }

    @Override
    public LocalRepo getLocalRepo() {
        return repository;
    }

    @Override
    public void setLocalRepo(LocalRepo localRepo) {
        repository = localRepo;
    }
}
