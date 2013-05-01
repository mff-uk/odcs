package module;

import gui.ConfigDialog;

import com.vaadin.ui.CustomComponent;

import cz.cuni.xrg.intlib.commons.Type;
import cz.cuni.xrg.intlib.commons.configuration.Configuration;
import cz.cuni.xrg.intlib.commons.configuration.ConfigurationException;
import cz.cuni.xrg.intlib.commons.loader.LoadContext;
import cz.cuni.xrg.intlib.commons.loader.LoadException;
import cz.cuni.xrg.intlib.commons.web.*;
import cz.cuni.xrg.intlib.commons.repository.LocalRepo;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * TODO Change super class to desired one, you can choose from the following:
 * GraphicalExtractor, GraphicalLoader, GraphicalTransformer
 */
public class RDF_loader implements GraphicalLoader {

    private LocalRepo repository = null;
    /**
     * Configuration component.
     */
    private gui.ConfigDialog configDialog = null;
    /**
     * DPU configuration.
     */
    private Configuration config = new Configuration();

    public RDF_loader() {
        // set initial configuration
        /**
         * TODO Set default (possibly empty but better valid) configuration for
         * your DPU.
         */
        this.config.setValue(Config.SPARQL_endpoint.name(), "http://");
        this.config.setValue(Config.Host_name.name(), "");
        this.config.setValue(Config.Password.name(), "");
        this.config.setValue(Config.GraphsUri.name(), new LinkedList<String>());
    }

    @Override
    public Type getType() {
        return Type.LOADER;

    }

    @Override
    public CustomComponent getConfigurationComponent() {
        // does dialog exist?
        if (this.configDialog == null) {
            // create it
            this.configDialog = new ConfigDialog();
            this.configDialog.setConfiguration(this.config);
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
    private String getSPARQLEndpointURLAsString() {
        String endpoint = (String) config.getValue(Config.SPARQL_endpoint.name());
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

    @Override
    public void load(LoadContext context) throws LoadException {
        final String endpoint = getSPARQLEndpointURLAsString();
        try {
            final URL endpointURL = new URL(endpoint);
            final List<String> defaultGraphsURI = getGraphsURI();
            final String hostName = getHostName();
            final String password = getPassword();

            repository.loadtoSPARQLEndpoint(endpointURL, defaultGraphsURI, hostName, password);
        } catch (MalformedURLException ex) {
            System.err.println("This URL not exists");
            System.err.println(ex.getMessage());
        }
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
