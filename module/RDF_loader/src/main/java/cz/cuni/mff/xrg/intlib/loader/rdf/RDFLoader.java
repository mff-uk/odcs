package cz.cuni.mff.xrg.intlib.loader.rdf;

import cz.cuni.xrg.intlib.commons.data.DataUnitException;
import cz.cuni.xrg.intlib.commons.dpu.annotation.InputDataUnit;
import cz.cuni.xrg.intlib.commons.loader.Load;
import cz.cuni.xrg.intlib.commons.loader.LoadContext;
import cz.cuni.xrg.intlib.commons.loader.LoadException;
import cz.cuni.xrg.intlib.commons.module.dpu.ConfigurableBase;
import cz.cuni.xrg.intlib.commons.web.*;
import cz.cuni.xrg.intlib.rdf.enums.InsertType;
import cz.cuni.xrg.intlib.rdf.enums.WriteGraphType;
import cz.cuni.xrg.intlib.rdf.exceptions.RDFDataUnitException;
import cz.cuni.xrg.intlib.rdf.interfaces.RDFDataUnit;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * @author Jiri Tomes
 * @author Petyr
 */
public class RDFLoader extends ConfigurableBase<RDFLoaderConfig>
		implements Load, ConfigDialogProvider<RDFLoaderConfig> {

	@InputDataUnit
	public RDFDataUnit rdfDataUnit;

	public RDFLoader() {
		super(RDFLoaderConfig.class);
	}

	@Override
	public void load(LoadContext context)
			throws LoadException,
			DataUnitException {

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
		final WriteGraphType graphType = config.graphOption;
		final InsertType insertType = config.insertOption;

		try {
			
			rdfDataUnit.loadToSPARQLEndpoint(endpointURL, defaultGraphsURI,
					hostName, password, graphType,insertType);
		} catch (RDFDataUnitException ex) {
			throw new LoadException(ex.getMessage(), ex);
		}
	}

	@Override
	public AbstractConfigDialog<RDFLoaderConfig> getConfigurationDialog() {
		return new RDFLoaderDialog();
	}
}
