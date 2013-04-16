package module;

import gui.ConfigDialog;

import com.vaadin.ui.CustomComponent;

import cz.cuni.xrg.intlib.commons.Type;
import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import cz.cuni.xrg.intlib.commons.configuration.ConfigurationException;
import cz.cuni.xrg.intlib.commons.extractor.ExtractContext;
import cz.cuni.xrg.intlib.commons.extractor.ExtractException;
import cz.cuni.xrg.intlib.commons.module.*;
import cz.cuni.xrg.intlib.repository.LocalRepo;
import java.net.URL;
import java.util.List;


/**
 * TODO Change super class to desired one, you can choose from the following:
 * GraphicalExtractor, GraphicalLoader, GraphicalTransformer
 */
public class Module implements GraphicalExtractor {

    /**
     * Configuration component.
     */
    private gui.ConfigDialog configDialog = null;
    /**
     * DPU configuration.
     */
    private Configuration config = new Configuration();

    public Module() {
        // set initial configuration
        /**
         * TODO Set default (possibly empty but better valid) configuration for
         * your DPU.
         */
        this.config.setValue(Config.DPU_name.name(), "");
        this.config.setValue(Config.Description.name(), "");
        this.config.setValue(Config.SPARQL_endpoint.name(), "");
        this.config.setValue(Config.Host_name.name(), "");
        this.config.setValue(Config.Password.name(), "");
        this.config.setValue(Config.GraphsUri.name(), "");
        this.config.setValue(Config.SPARQL_query.name(), "CONSTRUCT {?s ?p ?o} where {?s ?p ?o}");
    }

    public Type getType() {
        return Type.EXTRACTOR;

    }

    public CustomComponent getConfigurationComponent() {
        // does dialog exist?
        if (this.configDialog == null) {
            // create it
            this.configDialog = new ConfigDialog();
            this.configDialog.setConfiguration(this.config);
        }
        return this.configDialog;
    }

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

    public void setSettings(Configuration configuration) {
        this.config = configuration;
        if (this.configDialog == null) {
        } else {
            // also set configuration for dialog
            this.configDialog.setConfiguration(this.config);
        }
    }

    private URL getSPARQLEndpoinURL() {
        URL endpoint = (URL) config.getValue(Config.SPARQL_endpoint.name());

        return endpoint;
    }

    private String getHostName() {
        String hostName = (String) config.getValue(Config.Host_name.name());

        return hostName;
    }

    private String getPassword() {
        String password = (String) config.getValue(Config.Password.name());

        return password;
    }

    private List<String> getGraphsURI() {
        List<String> graphs = (List<String>) config.getValue(Config.GraphsUri.name());

        return graphs;
    }

    private String getQuery() {
        String query = (String) config.getValue(Config.SPARQL_query.name());

        return query;
    }

    /**
     * Implementation of module functionality here.
     *
     */
    public void extract(ExtractContext context) throws ExtractException {

        final URL endpointURL = getSPARQLEndpoinURL();
        final String hostName = getHostName();
        final String password = getPassword();
        final List<String> defaultGraphsUri = getGraphsURI();
        final String query = getQuery();

        LocalRepo repository = LocalRepo.createLocalRepo();
        repository.extractfromSPARQLEndpoint(endpointURL, defaultGraphsUri, query, hostName, password);


    }
}
