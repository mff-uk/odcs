package module;

import cz.cuni.xrg.intlib.commons.data.rdf.WriteGraphType;
import gui.ConfigDialog;

import com.vaadin.ui.CustomComponent;

import cz.cuni.xrg.intlib.commons.DPUExecutive;
import cz.cuni.xrg.intlib.commons.DpuType;
import cz.cuni.xrg.intlib.commons.configuration.Config;
import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.data.rdf.RDFDataRepository;
import cz.cuni.xrg.intlib.commons.loader.LoadContext;
import cz.cuni.xrg.intlib.commons.loader.LoadException;
import cz.cuni.xrg.intlib.commons.web.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Jiri Tomes
 * @author Petyr
 */
public class RDF_loader implements GraphicalLoader, DPUExecutive {

    /**
     * Config component.
     */
    private gui.ConfigDialog configDialog = null;
    /**
     * DPU configuration.
     */
    private Config config = null;

    public RDF_loader() {
    }

    @Override
    public void saveConfigurationDefault(Config configuration) {
        configuration.setValue(Config.SPARQL_endpoint.name(), "http://");
        configuration.setValue(Config.Host_name.name(), "");
        configuration.setValue(Config.Password.name(), "");
        configuration.setValue(Config.GraphsUri.name(), new LinkedList<String>());
    }

    @Override
    public DpuType getType() {
        return DpuType.LOADER;

    }

    @Override
    public CustomComponent getConfigurationComponent(Config configuration) {
        // does dialog exist?
        if (this.configDialog == null) {
            // create it
            this.configDialog = new ConfigDialog();
            this.configDialog.setConfiguration(configuration);
        }
        return this.configDialog;
    }

    @Override
    public void loadConfiguration(Config configuration)
            throws ConfigException {
        // 
        if (this.configDialog == null) {
        } else {
            // get configuration from dialog
            this.configDialog.setConfiguration(configuration);
        }
    }

    @Override
    public void saveConfiguration(Config configuration) {
        this.config = configuration;
        if (this.configDialog == null) {
        } else {
            // also set configuration for dialog
            this.configDialog.getConfiguration(this.config);
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
        if (hostName == null) {
            hostName = "";
        }
        return hostName;
    }

    private String getPassword() {
        String password = (String) config.getValue(Config.Password.name());
        if (password == null) {
            password = "";
        }
        return password;
    }

    private List<String> getGraphsURI() {
        List<String> graphs = (List<String>) config.getValue(Config.GraphsUri.name());
        if (graphs==null)
        {
            graphs=new LinkedList<>();
        }
        return graphs;
    }

    private WriteGraphType getWriteGraphType() {
        WriteGraphType graphType = (WriteGraphType) config.getValue(Config.Options.name());
        return graphType;
    }

    @Override
    public void load(LoadContext context) throws LoadException {
        RDFDataRepository repository = null;
        // get repository
        if (context.getInputs().isEmpty()) {
            throw new LoadException("Missing inputs!");
        }

        DataUnit dataUnit = context.getInputs().get(0);

        if (dataUnit instanceof RDFDataRepository) {
            repository = (RDFDataRepository) dataUnit;
        } else {

            throw new LoadException("Wrong input type " + dataUnit.getType().toString() + " instead of RDF.");
        }

        final String endpoint = getSPARQLEndpointURLAsString();

        URL endpointURL = null;

        try {
            endpointURL = new URL(endpoint);
        } catch (MalformedURLException ex) {

            throw new LoadException(ex);
        }

        final List<String> defaultGraphsURI = getGraphsURI();
        final String hostName = getHostName();
        final String password = getPassword();
        final WriteGraphType graphType = getWriteGraphType();

        repository.loadtoSPARQLEndpoint(endpointURL, defaultGraphsURI, hostName, password, graphType);

    }
}
