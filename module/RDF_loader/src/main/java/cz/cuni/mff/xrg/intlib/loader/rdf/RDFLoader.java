package cz.cuni.mff.xrg.intlib.loader.rdf;

import cz.cuni.xrg.intlib.commons.configuration.ConfigException;
import cz.cuni.xrg.intlib.commons.configuration.Configurable;
import cz.cuni.xrg.intlib.commons.data.DataUnit;
import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.commons.loader.LoadContext;
import cz.cuni.xrg.intlib.commons.loader.LoadException;
import cz.cuni.xrg.intlib.commons.web.*;
import cz.cuni.xrg.intlib.rdf.enums.WriteGraphType;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataRepository;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * @author Jiri Tomes
 * @author Petyr
 */
public class RDFLoader implements Load,
	Configurable<RDFLoaderConfig>, ConfigDialogProvider<RDFLoaderConfig> {

    /**
     * DPU configuration.
     */
    private RDFLoaderConfig config = null;

    public RDFLoader() { }

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

        final String endpoint = config.SPARQL_endpoint;
        URL endpointURL = null;
        try {
            endpointURL = new URL(endpoint);
        } catch (MalformedURLException ex) {

            throw new LoadException(ex);
        }

        final List<String> defaultGraphsURI = config.GraphsUri;
        final String hostName = config.Host_name;
        final String password = config.Password;
        final WriteGraphType graphType = config.Options;

        repository.loadtoSPARQLEndpoint(endpointURL, defaultGraphsURI, hostName, password, graphType);

    }

	@Override
	public AbstractConfigDialog<RDFLoaderConfig> getConfigurationDialog() {		
		return new RDFLoaderDialog();
	}

	@Override
	public void configure(RDFLoaderConfig c) throws ConfigException {
		config = c;
		
	}

	@Override
	public RDFLoaderConfig getConfiguration() {
		return config;
	}
}
